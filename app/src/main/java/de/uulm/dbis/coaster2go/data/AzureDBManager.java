package de.uulm.dbis.coaster2go.data;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.util.Log;

import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.MobileServiceException;
import com.microsoft.windowsazure.mobileservices.http.OkHttpClientFactory;
import com.microsoft.windowsazure.mobileservices.table.MobileServiceTable;
import com.microsoft.windowsazure.mobileservices.table.query.QueryOrder;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import com.squareup.okhttp.OkHttpClient;

/** Class handels all Connections between the App and the Azure SQL Data tables.
 *
 * TODO: Find a better solution for the date problems
 *
 */
public class AzureDBManager {

    private static final int CACHE_MINUTES = 5;
    private static final String SHARED_PARKS_DATE_KEY = "parks_date";
    private static final String SHARED_ATTRACTIONS_DATE_KEY = "_attractions_date";

    private Context context;
    private SharedPreferences sharedPref;
    private JsonManager jsonManager;
    private MobileServiceClient mClient;

    public AzureDBManager(Context context) {
        this.context = context;
        sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        jsonManager = new JsonManager(context);

        if (mClient == null) {
            try {
                mClient = new MobileServiceClient(
                        "https://coaster2go.azurewebsites.net",
                        context
                );

                // Extend timeout from default of 10s to 30s
                mClient.setAndroidHttpClientFactory(new OkHttpClientFactory() {
                    @Override
                    public OkHttpClient createOkHttpClient() {
                        OkHttpClient client = new OkHttpClient();
                        client.setReadTimeout(30, TimeUnit.SECONDS);
                        client.setWriteTimeout(30, TimeUnit.SECONDS);
                        return client;
                    }
                });

            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }
    }

    /** Method checks, if the device has an internet connection
     *
     * @return true if davice is connected
     */
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null;
    }

    /** Method checks, if there is an internet connection and if Azure can be accessed
     *
     * @return true if Azure Website can get accessed
     */
    public boolean hasActiveInternetConnection() {
        if (isNetworkAvailable()) {
            try {
                HttpURLConnection urlc = (HttpURLConnection) (new URL("https://coaster2go.azurewebsites.net").openConnection());
                urlc.setRequestProperty("Connection", "close");
                urlc.setConnectTimeout(3000);
                urlc.connect();
                return (urlc.getResponseCode() == 200);
            } catch (IOException e) {
                Log.e("Internet Connection", "Error checking internet connection", e);
                e.printStackTrace();
            }
        } else {
            Log.d("Internet Connection", "No network available!");
        }
        return false;
    }

    //--------------------------Parks---------------------------------------------

    /** Writes the given Park to the database and returns it with it's id.
     *
     * @param park New Park
     * @return created Park.
     */
    public Park createPark(Park park){
        if(!hasActiveInternetConnection()){
            return null;
        }
        MobileServiceTable<Park> mParkTable = mClient.getTable(Park.class);

        Park resultPark = null;
        try {
            resultPark = mParkTable.insert(park).get();
            //Write new ParkList into Internal Storage:
            getParkListOnline();
        } catch (InterruptedException e) {
            e.printStackTrace();
            return null;
        } catch (ExecutionException e) {
            e.printStackTrace();
            return null;
        }
        return resultPark;
    }

    /** Updates the given Park to the database and returns it with it's id.
     * The Park Object needs an id.
     *
     * @param park Object with id
     * @return updated Park.
     */
    public Park updatePark(Park park){
        if(!hasActiveInternetConnection()){
            return null;
        }
        MobileServiceTable<Park> mParkTable = mClient.getTable(Park.class);

        Park resultPark = null;
        try {
            resultPark = mParkTable.update(park).get();
            //Write new ParkList into Internal Storage:
            getParkListOnline();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return resultPark;
    }

    /** Returns the searched Parkobject or null
     *
     * @param parkId .
     * @return the searched Parkobject or null.
     */
    public Park getParkById(String parkId){
        //Try to load the Park of the Internal Storage first:
        try{
            Date lastUpdated = new Date(sharedPref.getString(SHARED_PARKS_DATE_KEY, "Mon May 01 00:00:00 GMT+02:00 2000"));
            Date now = new Date();
            long difference = now.getTime() - lastUpdated.getTime();
            //If the Data did already got Updated in the Last Minutes then return the saved Data
            if(difference < 60000*CACHE_MINUTES){
                return jsonManager.getParkById(parkId);
            }
        }catch(Exception e){
            //Probably no Data in the Internal Storage
            e.printStackTrace();
        }

        if(!hasActiveInternetConnection()){
            //If there is no Internet Connection then return the saved DATA
            return jsonManager.getParkById(parkId);
        }

        //Else return real online data
        //return getParkByIdOnline(parkId);
        //Download all Parks again and load the one local
        getParkListOnline();
        return jsonManager.getParkById(parkId);
    }

    /** Returns the searched Parkobject or null
     *
     * @param parkId .
     * @return the searched Parkobject or null.
     */
    private Park getParkByIdOnline(String parkId){
        MobileServiceTable<Park> mParkTable = mClient.getTable(Park.class);

        List<Park> resultPark = null;
        try {
            resultPark = mParkTable.where().field("id").eq(parkId).execute().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        if(resultPark == null || resultPark.isEmpty()){
            return jsonManager.getParkById(parkId);
        }else{
            return resultPark.get(0);
        }
    }

    /** Returns a List with all Parks in the database ordered by the Parkname.
     *
     * @return List with all Parks
     */
    public List<Park> getParkList(){
        //Try to load the Parklist of the Internal Storage first:
        try{
            System.out.println("Parks: Check Time Difference");
            Date lastUpdated = new Date(sharedPref.getString(SHARED_PARKS_DATE_KEY, "Mon May 01 00:00:00 GMT+02:00 2000"));
            Date now = new Date();
            long difference = now.getTime() - lastUpdated.getTime();
            //If the Data did already get Updated in the Last Minutes then return the saved Data
            System.out.println("TIME DIFFERENCE: "+difference);
            if(difference < 60000*CACHE_MINUTES){
                return jsonManager.getParkList();
            }
        }catch(Exception e){
            //Probably no Data in the Internal Storage
            e.printStackTrace();
        }

        System.out.println("Parks: Check internet connection");
        if(!hasActiveInternetConnection()){
            //If there is no Internet Connection then return the saved DATA
            return jsonManager.getParkList();
        }

        //Else return real online data
        System.out.println("Parks: Load Online Data");
        return getParkListOnline();
    }

    /** Returns a List with all Parks in the database ordered by the Parkname.
     *
     * @return List with all Parks
     */
    private List<Park> getParkListOnline(){
        MobileServiceTable<Park> mParkTable = mClient.getTable(Park.class);

        List<Park> parkList = null;
        try {
            parkList = mParkTable.orderBy("name", QueryOrder.Ascending).execute().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        if(parkList == null || parkList.isEmpty()){
            return jsonManager.getParkList();
        }
        //Load New ParkList into Internal Storage:
        jsonManager.writeParkList(parkList);
        SharedPreferences.Editor editor = sharedPref.edit();
        Date now = new Date();
        editor.putString(SHARED_PARKS_DATE_KEY, now.toString());
        System.out.println("Trying to save new Date "+now.toString());
        editor.commit();

        return parkList;
    }

    //-----------------------Attractions----------------------------------------

    /** Writes the given Attraction to the database and returns it with it's id.
     *
     * @param attraction Attraction
     * @return created Park.
     */
    public Attraction createAttraction(Attraction attraction){
        if(!hasActiveInternetConnection()){
            return null;
        }
        MobileServiceTable<Attraction> mAttractionTable = mClient.getTable(Attraction.class);

        Attraction resultAttraction = null;
        try {
            resultAttraction = mAttractionTable.insert(attraction).get();
            //Update the Attractions in the Internal Storage:
            getAttractionListOnline(resultAttraction.getParkId());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return resultAttraction;
    }

    /** Updates the given Attraction to the database and returns it with it's id.
     * The Attraction Object needs an id.
     *
     * @param attraction Object with id
     * @return updated Attraction.
     */
    public Attraction updateAttraction(Attraction attraction){
        if(!hasActiveInternetConnection()){
            return null;
        }
        MobileServiceTable<Attraction> mAttractionTable = mClient.getTable(Attraction.class);

        Attraction resultAttraction = null;
        try {
            resultAttraction = mAttractionTable.update(attraction).get();
            //Update the Attractions in the Internal Storage:
            getAttractionListOnline(resultAttraction.getParkId());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return resultAttraction;
    }

    /** Returns the Attraction object or null.
     *
     * @param attractionId .
     * @return TheAttraction object matching the given id or null.
     */
    public Attraction getAttractionById(String attractionId){
        if(!hasActiveInternetConnection()){
            //If there is no internet Connection, read the Park from the Internal Storage
            return jsonManager.getAttractionById(attractionId);
        }
        //TODO Find a solution to always load the Attraction out of the Internal Storage
        //The Park Id is needed to find out if the Attraction is already cached

        MobileServiceTable<Attraction> mAttractionTable = mClient.getTable(Attraction.class);

        List<Attraction> resultAttraction = null;
        try {
            resultAttraction = mAttractionTable.where().field("id").eq(attractionId).execute().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        if(resultAttraction == null || resultAttraction.isEmpty()){
            return jsonManager.getAttractionById(attractionId);
        }else{
            return resultAttraction.get(0);
        }
    }

    /** Returns a List with all Attractions in one Park ordered by the Parkname.
     *
     * @param parkId Id of the Park
     * @return List with all Attractions of the park
     */
    public List<Attraction> getAttractionList(String parkId){
        //Try to load the Attractionlist of the Internal Storage first:
        try{
            Date lastUpdated = new Date(sharedPref.getString(parkId+SHARED_ATTRACTIONS_DATE_KEY, "Mon May 01 00:00:00 GMT+02:00 2000"));
            Date now = new Date();
            long difference = now.getTime() - lastUpdated.getTime();
            //If the Data did already get Updated in the Last Minutes then return the saved Data
            System.out.println("TIME DIFFERENCE: "+difference);
            if(difference < 60000*CACHE_MINUTES){
                return jsonManager.getAttractionList(parkId);
            }
        }catch(Exception e){
            //Probably no Data in the Internal Storage
            e.printStackTrace();
        }

        if(!hasActiveInternetConnection()){
            //If there is no Internet Connection then return the saved DATA
            return jsonManager.getAttractionList(parkId);
        }
        //Else return real Online Data
        return getAttractionListOnline(parkId);
    }

    /** Returns a List with all Attractions in one Park ordered by the Parkname.
     *
     * @param parkId Id of the Park
     * @return List with all Attractions of the park
     */
    private List<Attraction> getAttractionListOnline(String parkId){
        MobileServiceTable<Attraction> mAttractionTable = mClient.getTable(Attraction.class);
        System.out.println("LADE ATTRACTION DATEN ONLINE");
        List<Attraction> attractionList = null;
        try {
            attractionList = mAttractionTable.where().field("parkId").eq(parkId).orderBy("name", QueryOrder.Ascending).execute().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        if(attractionList == null || attractionList.isEmpty()){
            return jsonManager.getAttractionList(parkId);
        }
        //Load New AttractionList into Internal Storage:
        jsonManager.writeAttractionList(attractionList, parkId);
        SharedPreferences.Editor editor = sharedPref.edit();
        Date now = new Date();
        editor.putString(parkId+SHARED_ATTRACTIONS_DATE_KEY, now.toString());
        editor.commit();
        System.out.println("Trying to save new Date "+now.toString());
        return attractionList;
    }

    //----------------------Reviews--------------------------------------------

    /** Inserts the given Review into the Database AND UPDATES the Park or Attraction review flags.
     *
     * @param review Review Object which needs to be inserted, reviewedId has to be set!
     * @param attraction BOOLEAN: TRUE if Attraction, FALSE if Park
     * @return Review Object with it's id.
     */
    public Review createReview(Review review, boolean attraction){
        if(!hasActiveInternetConnection()){
            return null;
        }
        MobileServiceTable<Review> mReviewTable = mClient.getTable(Review.class);

        Review resultReview = null;
        try {
            //Insert Review
            resultReview = mReviewTable.insert(review).get();
            String reviewedId = resultReview.getReviewedId();

            //Update Park/Attraction:
            if(attraction){
                Attraction updateAttraction = getAttractionById(reviewedId);
                if(updateAttraction == null){
                    return null;
                }
                int newNumberOfReviews = updateAttraction.getNumberOfReviews()+1;
                double newAverageReview = (updateAttraction.getAverageReview()*updateAttraction.getNumberOfReviews()
                        +resultReview.getNumberOfStars())/newNumberOfReviews;
                updateAttraction.setNumberOfReviews(newNumberOfReviews);
                updateAttraction.setAverageReview(newAverageReview);

                MobileServiceTable<Attraction> mAttractionTable = mClient.getTable(Attraction.class);
                mAttractionTable.update(updateAttraction);
                //Update the Attractions in the Internal Storage:
                getAttractionListOnline(updateAttraction.getParkId());

            }else{
                Park updatePark = getParkByIdOnline(reviewedId);
                if(updatePark == null){
                    return null;
                }
                int newNumberOfReviews = updatePark.getNumberOfReviews()+1;
                double newAverageReview = (updatePark.getAverageReview()*updatePark.getNumberOfReviews()
                        +resultReview.getNumberOfStars())/newNumberOfReviews;
                updatePark.setNumberOfReviews(newNumberOfReviews);
                updatePark.setAverageReview(newAverageReview);

                MobileServiceTable<Park> mParkTable = mClient.getTable(Park.class);
                mParkTable.update(updatePark);
                //Write new ParkList into Internal Storage:
                getParkListOnline();
            }


        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        return resultReview;
    }

    /** Updates the given Review in the Database AND UPDATES the Park or Attraction.
     *
     * @param review Review Object which needs to be updated, reviewedId and id has to be set!
     * @param attraction BOOLEAN: TRUE if Attraction, FALSE if Park
     * @return Review Object with it's id.
     */
    public Review updateReview(Review review, boolean attraction){
        if(!hasActiveInternetConnection()){
            return null;
        }
        MobileServiceTable<Review> mReviewTable = mClient.getTable(Review.class);

        Review resultReview = null;
        try {
            //Insert Review
            resultReview = mReviewTable.update(review).get();
            String reviewedId = resultReview.getReviewedId();

            //Update Park/Attraction:
            if(attraction){
                Attraction updateAttraction = getAttractionById(reviewedId);
                if(updateAttraction == null){
                    return null;
                }
                double newAverageReview = (updateAttraction.getAverageReview()*(updateAttraction.getNumberOfReviews()-1)
                        +resultReview.getNumberOfStars())/updateAttraction.getNumberOfReviews();
                updateAttraction.setAverageReview(newAverageReview);

                MobileServiceTable<Attraction> mAttractionTable = mClient.getTable(Attraction.class);
                mAttractionTable.update(updateAttraction);
                //Update the Attractions in the Internal Storage:
                getAttractionListOnline(updateAttraction.getParkId());

            }else{
                Park updatePark = getParkByIdOnline(reviewedId);
                if(updatePark == null){
                    return null;
                }
                double newAverageReview = (updatePark.getAverageReview()*(updatePark.getNumberOfReviews()-1)
                        +resultReview.getNumberOfStars())/updatePark.getNumberOfReviews();
                updatePark.setAverageReview(newAverageReview);

                MobileServiceTable<Park> mParkTable = mClient.getTable(Park.class);
                mParkTable.update(updatePark);
                //Write new ParkList into Internal Storage:
                getParkListOnline();
            }


        } catch (InterruptedException e) {
            e.printStackTrace();
            return null;
        } catch (ExecutionException e) {
            e.printStackTrace();
            return null;
        }

        return resultReview;
    }

    /** Returns a List with all Reviews of a Park or Atraction.
     * Statt der Methode kann auch getPartOfReviewList verwendet werden, welche
     * nur jewiles die ersten 5, nächsten 5,... Elemente der Liste zurück gibt
     *
     * @param reviewedId Id of the Park or Attraction
     * @return List with all Reviews ordered by Date
     */
    public List<Review> getReviewList(String reviewedId){
        if(!hasActiveInternetConnection()){
            return null;
        }
        MobileServiceTable<Review> mReviewTable = mClient.getTable(Review.class);

        List<Review> reviewList = null;
        try {
            reviewList = mReviewTable.where().field("reviewedId").eq(reviewedId).orderBy("createdAt", QueryOrder.Descending).execute().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        return reviewList;
    }

    /** Returns the next 5 Review Elements in a List of a Park or Atraction.
     * Counter: Start wth Element counter*5+1 and end with Element (counter+1)*5
     *
     * @param reviewedId Id of the Park or Attraction
     * @param counter, see description
     * @return List with 5 Reviews ordered by Date
     */
    public List<Review> getPartOfReviewList(String reviewedId, int counter){
        if(!hasActiveInternetConnection()){
            return null;
        }
        MobileServiceTable<Review> mReviewTable = mClient.getTable(Review.class);
        int skip = counter*5;
        int lastElement = (counter+1)*5;

        List<Review> reviewList = null;
        try {
            reviewList = mReviewTable
                    .where().field("reviewedId").eq(reviewedId)
                    .orderBy("createdAt", QueryOrder.Descending)
                    .skip(skip).top(lastElement).execute().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        return reviewList;
    }

    /** Returns the Review Object of a specific User of a Park or Attraction
     * or null if there is none so far
     *
     * @param reviewedId Id of the Park/Attraction
     * @param userId Id of the User
     * @return Review Object or null
     */
    public Review getReviewOfUser(String reviewedId, String userId){
        if(!hasActiveInternetConnection()){
            return null;
        }
        MobileServiceTable<Review> mReviewTable = mClient.getTable(Review.class);

        List<Review> reviewList = null;
        try {
            reviewList = mReviewTable.where().field("reviewedId").eq(reviewedId).and().field("userId").eq(userId).execute().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        if(reviewList == null || reviewList.isEmpty()){
            return null;
        }else{
            return reviewList.get(0);
        }
    }

    //getSingleReview(String id) not necessary so far

    //----------------------WaitingTimes--------------------------------------

    /** Returns a List with all WaitingTimes of an Atraction.
     * Statt der Methode kann auch getPartOfWaitingTimeList verwendet werden, welche
     * nur jewiles die ersten 5, nächsten 5,... Elemente der Liste zurück gibt
     *
     * @param attractionId Id of the Attraction
     * @return List with all WaitingTimes ordered by Date
     */
    public List<WaitingTime> getWaitingTimeList(String attractionId){
        if(!hasActiveInternetConnection()){
            return null;
        }
        MobileServiceTable<WaitingTime> mWaitTable = mClient.getTable(WaitingTime.class);

        List<WaitingTime> waitList = null;
        try {
            waitList = mWaitTable.where().field("attractionId").eq(attractionId).orderBy("createdAt", QueryOrder.Descending).execute().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        return waitList;
    }

    /** Returns the next 5 WaitingTime Elements in a List of an Attraction.
     * Counter: Start wth Element counter*5+1 and end with Element (counter+1)*5
     *
     * @param attractionId Id of the Attraction
     * @param counter, see description
     * @return List with 5 WaitingTimes ordered by Date
     */
    public List<WaitingTime> getPartOfWaitingTimeList(String attractionId, int counter){
        if(!hasActiveInternetConnection()){
            return null;
        }
        MobileServiceTable<WaitingTime> mWaitTable = mClient.getTable(WaitingTime.class);
        int skip = counter*5;
        int lastElement = (counter+1)*5;

        List<WaitingTime> waitList = null;
        try {
            waitList = mWaitTable.where().field("attractionId").eq(attractionId)
                    .orderBy("createdAt", QueryOrder.Descending)
                    .skip(skip).top(lastElement).execute().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        return waitList;
    }

    /** Returns a List with all TodayWaitingTimes of an Attraction.
     *
     *
     * @param attractionId Id of the Attraction
     * @return List with all WaitingTimes ordered by Date
     */
    public List<WaitingTime> getTodaysWaitingTimeList(String attractionId){
        if(!hasActiveInternetConnection()){
            return null;
        }
        MobileServiceTable<WaitingTime> mWaitTable = mClient.getTable(WaitingTime.class);

        List<WaitingTime> waitList = null;
        try {
            Date todayDate = new Date(); //Maybe use something different then the Date Object later...
            /* //TODO working with the sql Date functions seem like they do not work...
            waitList = mWaitTable.where().field("attractionId").eq(attractionId)
                    .and().year("due").eq(todayDate.getYear())
                    .and().month("due").eq(todayDate.getMonth())
                    .and().day("due").eq(todayDate.getDay())
                    .execute().get();

             */
            //Ersatzlösung, die aber genauso funktioniert:
            waitList = getWaitingTimeList(attractionId);
            int counter = 0;
            int i = 0;
            if(waitList != null && !waitList.isEmpty()){
                for(i = 0; i < waitList.size(); i++){
                    if(waitList.get(i).getCreatedAt().getYear()==todayDate.getYear()
                            && waitList.get(i).getCreatedAt().getMonth()==todayDate.getMonth()
                            && waitList.get(i).getCreatedAt().getDay()==todayDate.getDay()){
                        counter++;
                    }else{
                        i = waitList.size();
                    }
                }
                if(counter >= waitList.size()){
                    return waitList;
                }else{
                    waitList = waitList.subList(0, counter);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return waitList;
    }

    /** Inserts the given WaitingTime Object into the Database
     * AND UPDATES all the WaitingTime flags in the Attraction.
     * The CurrentWaitingTime is calculated by the last 3 waiting times.
     *
     * @param waitTime WaitingTime Object which needs to be inserted, attractionId has to be set!
     * @return WaitingTime Object with it's id.
     */
    public WaitingTime createWaitingTime(WaitingTime waitTime){
        if(!hasActiveInternetConnection()){
            return null;
        }
        MobileServiceTable<WaitingTime> mWaitTable = mClient.getTable(WaitingTime.class);

        WaitingTime resultTime = null;
        try {
            //Get Attraction:
            String attractionId = waitTime.getAttractionId();
            Attraction updateAttraction = getAttractionById(attractionId);
            if(updateAttraction == null){
                return null;
            }

            //1. Calculate and update new AverageWaitingTime
            int newNumberOfWaitingTimes = updateAttraction.getNumberOfWaitingTimes()+1;
            int newAverageWaitingTime = (int)((updateAttraction.getAverageWaitingTime()*updateAttraction.getNumberOfWaitingTimes()
                    +waitTime.getMinutes())/newNumberOfWaitingTimes);
            updateAttraction.setNumberOfWaitingTimes(newNumberOfWaitingTimes);
            updateAttraction.setAverageWaitingTime(newAverageWaitingTime);

            //2. Calculate and update new TodayAverageWaitingTime
            int newNumberOfTodayWaitingTimes = 0;
            int newAverageTodayWaitingTime = 0;
            List<WaitingTime> todayList = getTodaysWaitingTimeList(attractionId);

            if((todayList == null || todayList.isEmpty())){
                //Heute noch kein Eintrag bisher:
                newNumberOfTodayWaitingTimes = 1;
                newAverageTodayWaitingTime = waitTime.getMinutes();
            }else{
                //neuen heutigen Durchschnitt berechnen:
                newNumberOfTodayWaitingTimes = todayList.size()+1;
                for(WaitingTime w : todayList){
                    newAverageTodayWaitingTime = newAverageTodayWaitingTime + w.getMinutes();
                }
                newAverageTodayWaitingTime = newAverageTodayWaitingTime + waitTime.getMinutes();
                //System.out.println("Anzahl heute: "+todayList.size());
                //System.out.println("Gesamtminuten heute: "+newAverageTodayWaitingTime);
                newAverageTodayWaitingTime = newAverageTodayWaitingTime/newNumberOfTodayWaitingTimes;
            }
            updateAttraction.setNumberOfTodayWaitingTimes(newNumberOfTodayWaitingTimes);
            updateAttraction.setAverageTodayWaitingTime(newAverageTodayWaitingTime);

            //3. Calculate and Update new CurrentWaitingTime by the last 3 or less WaitingTimes
            int newCurrentTime = waitTime.getMinutes();
            List<WaitingTime> lastTwoTimes = mWaitTable.where().field("attractionId").eq(attractionId)
                    .orderBy("createdAt", QueryOrder.Descending).top(2).execute().get();
            if(lastTwoTimes != null && !lastTwoTimes.isEmpty()){
                for(WaitingTime w: lastTwoTimes){
                    newCurrentTime = newCurrentTime + w.getMinutes();
                }
                newCurrentTime = newCurrentTime/(lastTwoTimes.size()+1);
            }
            updateAttraction.setCurrentWaitingTime(newCurrentTime);

            //Update Attraction
            MobileServiceTable<Attraction> mAttractionTable = mClient.getTable(Attraction.class);
            mAttractionTable.update(updateAttraction);

            //Insert WaitingTime
            resultTime = mWaitTable.insert(waitTime).get();

            //Update the Attractions in the Internal Storage:
            getAttractionListOnline(updateAttraction.getParkId());


        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        return resultTime;
    }

    /** Method checks, if a User has already posted a WaitingTime for an Attraction in the current hour.
     *  If so, the method responds with false. If the User is allowed to post a WaitingTime,
     *  it responds with true.
     *
     * @param attractionId Id of the Attraction
     * @param userId Id of the USer
     * @return boolean if User can post a WaitingTime
     */
    public boolean isCreateWaitingTimeAllowed(String attractionId, String userId){
        if(!hasActiveInternetConnection()){
            return false;
        }
        List<WaitingTime> todayList = getTodaysWaitingTimeList(attractionId);
        if(todayList == null || todayList.isEmpty()){
            return true;
        }
        Date date = new Date(); //Maybe use something different then the Date Object later...
        int currentHour = date.getHours();
        System.out.println("current hour: "+currentHour);
        for(WaitingTime w : todayList){
            System.out.println(w.getCreatedAt().getHours());
            //For now the method only checks if the User did already post a WaitingTime in the
            // current hour. A better cariant would probably be to check if he did post a
            // WaitingTime during the last 60 minutes... //TODO ?
            if(w.getUserId().equals(userId) && w.getCreatedAt().getHours()==currentHour){
                return false;
            }
        }
        return true;
    }

    /** This method returns the statistics for the WaitingTime per Hours. It gives back a
     * HashMap< key, value>  where the key is a hour (only between 8 and 20) (Integer) and the value
     * is the average waiting time for this hour (Integer).
     *
     * @param attractionId Id of the Attraction.
     * @return see above
     */
    @SuppressLint("all")
    public HashMap<Integer, Integer> waitTimeHourStatistic(String attractionId){
        if(!hasActiveInternetConnection()){
            return null;
        }
        HashMap<Integer, Integer> numbers = new HashMap<>();
        HashMap<Integer, Integer> minutes = new HashMap<>();
        HashMap<Integer, Integer> result = new HashMap<>();
        for(int i = 0; i < 27; i++){
            numbers.put(i, 0); minutes.put(i, 0);
        }

        List<WaitingTime> waitList = getWaitingTimeList(attractionId);
        if(waitList != null && !waitList.isEmpty()){
            for(WaitingTime w: waitList){
                int hour = w.getCreatedAt().getHours();
                int time = w.getMinutes();
                numbers.put(hour, numbers.get(hour)+1);
                minutes.put(hour, minutes.get(hour)+time);
            }
        }

        //Let's only use the Waiting Times from 8:00 to 20:00 for the statistics
        for(int i = 8; i < 21; i++){
            if(numbers.get(i) > 0){
                result.put(i, minutes.get(i)/numbers.get(i));
            }else{
                result.put(i, 0);
            }
        }

        return result;
    }

}
