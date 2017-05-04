package de.uulm.dbis.coaster2go.data;


import com.microsoft.windowsazure.mobileservices.MobileServiceException;
import com.microsoft.windowsazure.mobileservices.table.MobileServiceTable;
import com.microsoft.windowsazure.mobileservices.table.query.QueryOrder;

import java.util.List;
import java.util.concurrent.ExecutionException;

/** Class handels all Connections between the App and the Azure SQL Data tables.
 *
 */
public class AzureDBManager {

    //--------------------------Parks---------------------------------------------

    /** Writes the given Park to the database and returns it with it's id.
     *
     * @param mParkTable .
     * @param park New Park
     * @return created Park.
     */
    public static Park createPark(MobileServiceTable<Park> mParkTable, Park park){
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
     * @param mParkTable .
     * @param park Object with id
     * @return updated Park.
     */
    public static Park editPark(MobileServiceTable<Park> mParkTable, Park park){
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
     * @param mParkTable .
     * @param parkId .
     * @return List with only one Parkobject matching the given id.
     */
    public static List<Park> getParkById(MobileServiceTable<Park> mParkTable, String parkId){
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
     * @param mParkTable .
     * @return List with all Parks
     */
    public static List<Park> getParkList(MobileServiceTable<Park> mParkTable){
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
