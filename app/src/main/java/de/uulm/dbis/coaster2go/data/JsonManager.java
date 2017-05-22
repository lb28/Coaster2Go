package de.uulm.dbis.coaster2go.data;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Attr;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/** Class reads and writes Data from the local Storage into JSON Files.
 * It saves Parklists and AttractionLists
 *
 */

public class JsonManager {
    private static final String JSON_FILENAME_PARKS = "parks.json";
    private static final String JSON_FILENAME_ATTRACTIONS = "_attractions.json";
    private Context context;

    public JsonManager(Context context){
        this.context = context;
    }

    /** Writes the ParkList into as a JSON File
     *
     * @param parkList ParkList
     * @return true if successful
     */
    public boolean writeParkList(List<Park> parkList){
        try {
            JSONObject jsonObj = new JSONObject();
            JSONArray parks = new JSONArray();
            for (Park p : parkList) {
                parks.put(p.toJSON());
            }
            jsonObj.put("parks", parks);
            // save the created json object
            return saveJSONToFile(jsonObj, JSON_FILENAME_PARKS);
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }
    }

    /** Returns a List with all Parks in the database ordered by the Parkname.
     *
     * @return List with all Parks
     */
    public List<Park> getParkList() {
        List<Park> parkList = new ArrayList<Park>();
        JSONObject jsonObj;
        JSONArray parks;

        try {
            jsonObj = readParkJSONFromFile(JSON_FILENAME_PARKS);
            if (jsonObj == null) return null;
            parks = jsonObj.getJSONArray("parks");

            for (int i = 0; i < parks.length(); i++) {
                JSONObject tmpPark = parks.getJSONObject(i);
                String id = tmpPark.getString("id");
                String name = tmpPark.getString("name");
                String location = tmpPark.getString("location");
                String description = tmpPark.getString("description");
                double lat = tmpPark.getDouble("lat");
                double lon = tmpPark.getDouble("lon");
                String image = tmpPark.getString("image");
                int numberOfReviews = tmpPark.getInt("numberOfReviews");
                double averageReview = tmpPark.getDouble("averageReview");
                String admin = tmpPark.getString("admin");

                Park newPark = new Park(id, name, location, description, lat, lon, image,
                        numberOfReviews, averageReview, admin);
                parkList.add(newPark);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return parkList;
    }

    /** Returns the searched Parkobject or null
     *
     * @param parkId .
     * @return the searched Parkobject or null.
     */
    public Park getParkById(String parkId){
        List<Park> parkList = getParkList();
        if(parkList == null || parkList.isEmpty()){
            return null;
        }
        for(Park p : parkList){
            if(p.getId().equals(parkId)){
                return p;
            }
        }
        return null;
    }

    /** Writes the AttractionList into as a JSON File
     *
     * @param attractionList AttractionList
     * @param parkId parkId
     * @return true if successful
     */
    public boolean writeAttractionList(List<Attraction> attractionList, String parkId){
        try {
            JSONObject jsonObj = new JSONObject();
            JSONArray attractions = new JSONArray();
            for (Attraction a : attractionList) {
                attractions.put(a.toJSON());
            }
            jsonObj.put("attractions", attractions);
            // save the created json object
            return saveJSONToFile(jsonObj, parkId+JSON_FILENAME_ATTRACTIONS);
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }
    }

    /** Returns a List with all Attractions of an Park in the database ordered by the Parkname.
     *
     * @param parkId Id of the park
     * @return List with all Attractions of the Park
     */
    public List<Attraction> getAttractionList(String parkId) {
        List<Attraction> attractionList = new ArrayList<Attraction>();
        JSONObject jsonObj;
        JSONArray attractions;

        try {
            jsonObj = readParkJSONFromFile(parkId+JSON_FILENAME_ATTRACTIONS);
            if (jsonObj == null) return null;
            attractions = jsonObj.getJSONArray("attractions");

            for (int i = 0; i < attractions.length(); i++) {
                JSONObject tmpAttraction = attractions.getJSONObject(i);
                String id = tmpAttraction.getString("id");
                String name = tmpAttraction.getString("name");
                String type = tmpAttraction.getString("type");
                String description = tmpAttraction.getString("description");
                double lat = tmpAttraction.getDouble("lat");
                double lon = tmpAttraction.getDouble("lon");
                String image = tmpAttraction.getString("image");
                int numberOfReviews = tmpAttraction.getInt("numberOfReviews");
                double averageReview = tmpAttraction.getDouble("averageReview");
                int numberOfWaitingTimes = tmpAttraction.getInt("numberOfWaitingTimes");
                int averageWaitingTime = tmpAttraction.getInt("averageWaitingTime");
                int numberOfTodayWaitingTimes = tmpAttraction.getInt("numberOfTodayWaitingTimes");
                int averageTodayWaitingTime = tmpAttraction.getInt("averageTodayWaitingTime");
                int currentWaitingTime = tmpAttraction.getInt("currentWaitingTime");
                //String parkId = tmpAttraction.getString("parkId");

                Attraction newAttraction = new Attraction(id, name, type, description, lat, lon,
                        image, numberOfReviews, averageReview, numberOfWaitingTimes,
                        averageWaitingTime, numberOfTodayWaitingTimes, averageTodayWaitingTime,
                        currentWaitingTime, parkId);
                attractionList.add(newAttraction);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return attractionList;
    }

    /** Returns the searched Attraction Object or null.
     * Method has a lot of overhead so far (reading through all Parks and all Attractions)
     * So it might be better to only use it
     * if there is really no internet connection
     *
     * @param attractionId .
     * @return the searched Attraction Object or null.
     */
    public Attraction getAttractionById(String attractionId){
        List<Park> parkList = getParkList();
        for(int i = 0; i < parkList.size(); i++){
            List<Attraction> attractionList = getAttractionList(parkList.get(i).getId());
            for(Attraction a : attractionList){
                if(a.getId().equals(attractionId)){
                    return a;
                }
            }
        }

        return null;
    }



    /** Reads the JSON File from the internal Storage and returns it as JSON-Object
     *
     * @return JSONObject
     */
    private JSONObject readParkJSONFromFile(String jsonFileName){
        // read the json object from the internal storage
        try {
            FileInputStream fin = context.openFileInput(
                    jsonFileName);
            BufferedInputStream in = new BufferedInputStream(fin);

            String jsonString = "";
            byte[] buffer = new byte[1024];
            int bytesRead = 0;
            while((bytesRead = in.read(buffer)) != -1) {
                jsonString += new String(buffer, 0, bytesRead);
            }
            if (jsonString.isEmpty()) return null;
            return new JSONObject(jsonString);
        } catch (IOException | JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * saves a json object to a file in internal storage, overwriting any older json file
     * with the same filename.
     * @param jsonObject the json object to save
     */
    private boolean saveJSONToFile(JSONObject jsonObject, String filename) {
        FileOutputStream outputStream;
        try {
            String jsonString = jsonObject.toString();

            outputStream = context.openFileOutput(
                    filename, Context.MODE_PRIVATE);
            outputStream.write(jsonString.getBytes());
            outputStream.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

}
