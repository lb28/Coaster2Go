package de.uulm.dbis.coaster2go.data;


import android.content.Context;

import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.MobileServiceException;
import com.microsoft.windowsazure.mobileservices.http.OkHttpClientFactory;
import com.microsoft.windowsazure.mobileservices.table.MobileServiceTable;
import com.microsoft.windowsazure.mobileservices.table.query.QueryOrder;

import java.net.MalformedURLException;
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

    /** Returns a List with the searched Parkobject or null
     *
     * @param parkId .
     * @return List with only one Parkobject matching the given id.
     */
    public List<Park> getParkById(String parkId){
        MobileServiceTable<Park> mParkTable = mClient.getTable(Park.class);

        List<Park> resultPark = null;
        try {
            resultPark = mParkTable.where().field("id").eq(parkId).execute().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return resultPark;
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

    /** Returns a List with the searched Attraction object or null
     *
     * @param attractionId .
     * @return List with only one Attraction object matching the given id.
     */
    public List<Attraction> getAttractionById(String attractionId){
        MobileServiceTable<Attraction> mAttractionTable = mClient.getTable(Attraction.class);

        List<Attraction> resultAttraction = null;
        try {
            resultAttraction = mAttractionTable.where().field("id").eq(attractionId).execute().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return resultAttraction;
    }

    /** Returns a List with all Attractions in one Park ordered by the Parkname.
     *
     * @param parkId Id of the Park
     * @return List with all Attractions of the park
     */
    public List<Attraction> getParkList(String parkId){
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

    /** Inserts the given Review into the Database AND UPDATES the Park or Attraction
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
                Attraction updateAttraction = getAttractionById(reviewedId).get(0);
                int newNumberOfReviews = updateAttraction.getNumberOfReviews()+1;
                double newAverageReview = (updateAttraction.getAverageReview()*updateAttraction.getNumberOfReviews()
                        +resultReview.getNumberOfStars())/newNumberOfReviews;
                updateAttraction.setNumberOfReviews(newNumberOfReviews);
                updateAttraction.setAverageReview(newAverageReview);

                MobileServiceTable<Attraction> mAttractionTable = mClient.getTable(Attraction.class);
                mAttractionTable.update(updateAttraction);

            }else{
                Park updatePark = getParkById(reviewedId).get(0);
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
                Attraction updateAttraction = getAttractionById(reviewedId).get(0);
                double newAverageReview = (updateAttraction.getAverageReview()*(updateAttraction.getNumberOfReviews()-1)
                        +resultReview.getNumberOfStars())/updateAttraction.getNumberOfReviews();
                updateAttraction.setAverageReview(newAverageReview);

                MobileServiceTable<Attraction> mAttractionTable = mClient.getTable(Attraction.class);
                mAttractionTable.update(updateAttraction);

            }else{
                Park updatePark = getParkById(reviewedId).get(0);
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
     *
     * @param reviewedId Id of the Park or Attraction
     * @return List with all Reviews ordered by Date
     */
    public List<Review> getReviewList(String reviewedId){
        //TODO ggf. könnte auch eine Methode geschrieben werden, welche nur die ersten 5, 5-10, 10-15
        // Reviews zurück gibt geschrieben werden
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

    /** Returns a List, where the first Object is the Review of a specific User of a Park or Attraction
     * or an empty List/Null (??) if there is none so far
     *
     * @param reviewedId Id of the Park/Attraction
     * @param userId Id of the User
     * @return Review Object in a List
     */
    public List<Review> getReviewOfUser(String reviewedId, String userId){
        MobileServiceTable<Review> mReviewTable = mClient.getTable(Review.class);

        List<Review> reviewList = null;
        try {
            reviewList = mReviewTable.where().field("reviewedId").eq(reviewedId).and().field("userId").eq(userId).execute().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return reviewList;
    }

    //getSingleReview(String id) not necessary so far

    //----------------------WaitingTimes--------------------------------------
    //TODO



    //test methods:
    public static void test(){
        System.out.println("--------------test");
    }

}
