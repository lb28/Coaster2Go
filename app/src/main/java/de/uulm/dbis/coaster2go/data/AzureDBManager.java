package de.uulm.dbis.coaster2go.data;


import android.content.Context;

import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.MobileServiceException;
import com.microsoft.windowsazure.mobileservices.http.OkHttpClientFactory;
import com.microsoft.windowsazure.mobileservices.table.MobileServiceTable;
import com.microsoft.windowsazure.mobileservices.table.query.QueryOrder;

import java.net.MalformedURLException;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import com.squareup.okhttp.OkHttpClient;

/** Class handels all Connections between the App and the Azure SQL Data tables.
 *
 */
public class AzureDBManager {

    private MobileServiceClient mClient;

    public AzureDBManager(Context context) {
        if (mClient == null) {
            try {
                mClient = new MobileServiceClient(
                        "https://coaster2go.azurewebsites.net",
                        context
                );

                // Extend timeout from default of 10s to 20s
                mClient.setAndroidHttpClientFactory(new OkHttpClientFactory() {
                    @Override
                    public OkHttpClient createOkHttpClient() {
                        OkHttpClient client = new OkHttpClient();
                        client.setReadTimeout(20, TimeUnit.SECONDS);
                        client.setWriteTimeout(20, TimeUnit.SECONDS);
                        return client;
                    }
                });

            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }
    }


    //--------------------------Parks---------------------------------------------

    /** Writes the given Park to the database and returns it with it's id.
     *
     * @param park New Park
     * @return created Park.
     */
    public Park createPark(Park park){
        MobileServiceTable<Park> mParkTable = mClient.getTable(Park.class);

        Park resultPark = null;
        try {
            resultPark = mParkTable.insert(park).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
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
        MobileServiceTable<Park> mParkTable = mClient.getTable(Park.class);

        Park resultPark = null;
        try {
            resultPark = mParkTable.update(park).get();
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
            return null;
        }else{
            return resultPark.get(0);
        }
    }

    /** Returns a List with all Parks in the database ordered by the Parkname.
     *
     * @return List with all Parks
     */
    public List<Park> getParkList(){
        MobileServiceTable<Park> mParkTable = mClient.getTable(Park.class);

        List<Park> parkList = null;
        try {
            parkList = mParkTable.orderBy("name", QueryOrder.Ascending).execute().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return parkList;
    }

    //-----------------------Attractions----------------------------------------

    /** Writes the given Attraction to the database and returns it with it's id.
     *
     * @param attraction Attraction
     * @return created Park.
     */
    public Attraction createAttraction(Attraction attraction){
        MobileServiceTable<Attraction> mAttractionTable = mClient.getTable(Attraction.class);

        Attraction resultAttraction = null;
        try {
            resultAttraction = mAttractionTable.insert(attraction).get();
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
        MobileServiceTable<Attraction> mAttractionTable = mClient.getTable(Attraction.class);

        Attraction resultAttraction = null;
        try {
            resultAttraction = mAttractionTable.update(attraction).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return resultAttraction;
    }

    /** Returns the Attraction object or null
     *
     * @param attractionId .
     * @return TheAttraction object matching the given id or null.
     */
    public Attraction getAttractionById(String attractionId){
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
            return null;
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
        MobileServiceTable<Attraction> mAttractionTable = mClient.getTable(Attraction.class);

        List<Attraction> attractionList = null;
        try {
            attractionList = mAttractionTable.where().field("parkId").eq(parkId).orderBy("name", QueryOrder.Ascending).execute().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return attractionList;
    }

    //----------------------Reviews--------------------------------------------

    /** Inserts the given Review into the Database AND UPDATES the Park or Attraction review flags
     *
     * @param review Review Object which needs to be inserted, reviewedId has to be set!
     * @param attraction BOOLEAN: TRUE if Attraction, FALSE if Park
     * @return Review Object with it's id.
     */
    public Review createReview(Review review, boolean attraction){
        MobileServiceTable<Review> mReviewTable = mClient.getTable(Review.class);

        Review resultReview = null;
        try {
            //Insert Review
            resultReview = mReviewTable.insert(review).get();
            String reviewedId = resultReview.getReviewedId();

            //Update Park/Attraction:
            if(attraction){
                Attraction updateAttraction = getAttractionById(reviewedId);
                int newNumberOfReviews = updateAttraction.getNumberOfReviews()+1;
                double newAverageReview = (updateAttraction.getAverageReview()*updateAttraction.getNumberOfReviews()
                        +resultReview.getNumberOfStars())/newNumberOfReviews;
                updateAttraction.setNumberOfReviews(newNumberOfReviews);
                updateAttraction.setAverageReview(newAverageReview);

                MobileServiceTable<Attraction> mAttractionTable = mClient.getTable(Attraction.class);
                mAttractionTable.update(updateAttraction);

            }else{
                Park updatePark = getParkById(reviewedId);
                int newNumberOfReviews = updatePark.getNumberOfReviews()+1;
                double newAverageReview = (updatePark.getAverageReview()*updatePark.getNumberOfReviews()
                        +resultReview.getNumberOfStars())/newNumberOfReviews;
                updatePark.setNumberOfReviews(newNumberOfReviews);
                updatePark.setAverageReview(newAverageReview);

                MobileServiceTable<Park> mParkTable = mClient.getTable(Park.class);
                mParkTable.update(updatePark);
            }


        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        return resultReview;
    }

    /** Updates the given Review in the Database AND UPDATES the Park or Attraction
     *
     * @param review Review Object which needs to be updated, reviewedId and id has to be set!
     * @param attraction BOOLEAN: TRUE if Attraction, FALSE if Park
     * @return Review Object with it's id.
     */
    public Review updateReview(Review review, boolean attraction){
        MobileServiceTable<Review> mReviewTable = mClient.getTable(Review.class);

        Review resultReview = null;
        try {
            //Insert Review
            resultReview = mReviewTable.update(review).get();
            String reviewedId = resultReview.getReviewedId();

            //Update Park/Attraction:
            if(attraction){
                Attraction updateAttraction = getAttractionById(reviewedId);
                double newAverageReview = (updateAttraction.getAverageReview()*(updateAttraction.getNumberOfReviews()-1)
                        +resultReview.getNumberOfStars())/updateAttraction.getNumberOfReviews();
                updateAttraction.setAverageReview(newAverageReview);

                MobileServiceTable<Attraction> mAttractionTable = mClient.getTable(Attraction.class);
                mAttractionTable.update(updateAttraction);

            }else{
                Park updatePark = getParkById(reviewedId);
                double newAverageReview = (updatePark.getAverageReview()*(updatePark.getNumberOfReviews()-1)
                        +resultReview.getNumberOfStars())/updatePark.getNumberOfReviews();
                updatePark.setAverageReview(newAverageReview);

                MobileServiceTable<Park> mParkTable = mClient.getTable(Park.class);
                mParkTable.update(updatePark);
            }


        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
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
     * Statt der Methode kann auch getPartOfReviewList verwendet werden, welche
     * nur jewiles die ersten 5, nächsten 5,... Elemente der Liste zurück gibt
     *
     * @param attractionId Id of the Attraction
     * @return List with all WaitingTimes ordered by Date
     */
    public List<WaitingTime> getWaitingTimeList(String attractionId){
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
     * @param attractionId Id of the Attraction
     * @return List with all WaitingTimes ordered by Date
     */
    public List<WaitingTime> getTodaysWaitingTimeList(String attractionId){
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
            //Ersatzlösung:
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
                    waitList = waitList.subList(0, counter+1);
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
        MobileServiceTable<WaitingTime> mWaitTable = mClient.getTable(WaitingTime.class);

        WaitingTime resultTime = null;
        try {
            //Get Attraction:
            String attractionId = waitTime.getAttractionId();
            Attraction updateAttraction = getAttractionById(attractionId);

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
                newAverageTodayWaitingTime = newAverageWaitingTime/newNumberOfTodayWaitingTimes;
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


        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        return resultTime;
    }



    //test methods:
    public static void test(){
        System.out.println("--------------test");
    }

}
