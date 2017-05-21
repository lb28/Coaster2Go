package de.uulm.dbis.coaster2go.data;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
    private static final String JSON_FILENAME_ATTRACTIONS = "attractions.json";
    private Context context;

    public JsonManager(Context context){
        this.context = context;
    }

    /**
     *
     * @param parkList ParkList
     * @return true if sucessfull
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

    /** Returns the searched Parkobject or null
     *
     * @param parkId .
     * @return the searched Parkobject or null.
     */
    public Park getParkById(String parkId){
        Park resultPark = null;
        List<Park> parkList = getParkList();
        if(parkList == null || parkList.isEmpty()){
            return null;
        }
        for(Park p : parkList){
            if(p.getId().equals(parkId)){
                return p;
            }
        }
        return resultPark;
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

    //TODO Attraction JSON Methods

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
