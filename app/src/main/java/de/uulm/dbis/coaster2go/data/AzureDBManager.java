package de.uulm.dbis.coaster2go.data;


import android.content.Context;

import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.MobileServiceException;
import com.microsoft.windowsazure.mobileservices.table.MobileServiceTable;
import com.microsoft.windowsazure.mobileservices.table.query.QueryOrder;

import java.net.MalformedURLException;
import java.util.List;
import java.util.concurrent.ExecutionException;

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
    public Park editPark(Park park){
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

    /** Returns a List with the given Parkobject or null
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


    //TODO ...

    //-----------------------Attractions----------------------------------------

    //----------------------Reviews--------------------------------------------

    //----------------------WaitingTimes--------------------------------------




    //test methods:
    public static void test(){
        System.out.println("--------------test");
    }

}
