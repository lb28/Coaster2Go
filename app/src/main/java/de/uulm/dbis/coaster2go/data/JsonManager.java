package de.uulm.dbis.coaster2go.data;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/** Class reads and writes Data from the local Storage into JSON Files.
 * It saves Parklists and AttractionLists
 *
 */

public class JsonManager {
    private static final String JSON_FILENAME_PARKS = "parks.json";
    private static final String JSON_FILENAME_ATTRACTIONS = "_attractions.json";
    public static final String JSON_FILENAME_PARKS_FAVORITES ="parks_favorites.json";
    public static final String JSON_FILENAME_ATTRACTIONS_FAVORITES ="attractions_favorites.json";
    private Context context;

    public JsonManager(Context context){
        this.context = context;
    }

    /**Returns true if the Park with the given Id is in the Favorites
     *
     * @param parkId .
     * @return true if Park is favorite.
     */
    public boolean isParkFavorite(String parkId){
        JSONObject jsonObj;
        JSONArray parks;
        try {
            jsonObj = readParkJSONFromFile(JSON_FILENAME_PARKS_FAVORITES);
            if (jsonObj == null) return false;
            parks = jsonObj.getJSONArray("parks");
            for (int i = 0; i < parks.length(); i++) {
                String id = parks.getString(i);
                if(id.equals(parkId)) return true;
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return false;
    }

    /** Puts a Park into the Fave List
     *
     * @param parkId .
     * @return true if successful
     */
    public boolean putIntoParkFavorites(String parkId){
        if(isParkFavorite(parkId)) return false;
        JSONObject jsonObj, resultObj;
        JSONArray parks;
        jsonObj = readParkJSONFromFile(JSON_FILENAME_PARKS_FAVORITES);
        try{
            if(jsonObj == null){
                jsonObj = new JSONObject();
                parks = new JSONArray();
                parks.put(parkId);
                jsonObj.put("parks", parks);
                return saveJSONToFile(jsonObj, JSON_FILENAME_PARKS_FAVORITES);
            }else{
                parks = jsonObj.getJSONArray("parks");
                parks.put(parkId);
                resultObj = new JSONObject();
                resultObj.put("parks", parks);
                return saveJSONToFile(resultObj, JSON_FILENAME_PARKS_FAVORITES);
            }
        }catch(JSONException e){
            e.printStackTrace();
        }


        return false;
    }

    /** Deletes a Park from the Fave List
     *
     * @param parkId .
     * @return true if successful
     */
    public boolean deleteParkFromFavorites(String parkId){
        JSONObject jsonObj, resultObj;
        JSONArray parks, resultParks;
        jsonObj = readParkJSONFromFile(JSON_FILENAME_PARKS_FAVORITES);
        try{
            if(jsonObj == null){
                return false;
            }else{
                parks = jsonObj.getJSONArray("parks");
                resultObj = new JSONObject();
                resultParks = new JSONArray();
                for(int i = 0; i < parks.length(); i++){
                    String id = parks.getString(i);
                    if(!id.equals((parkId))){
                        resultParks.put(id);
                    }
                }
                resultObj.put("parks", resultParks);
                return saveJSONToFile(resultObj, JSON_FILENAME_PARKS_FAVORITES);
            }
        }catch(JSONException e){
            e.printStackTrace();
        }
        return false;
    }

    /** Gives back a List with all Park Favorites of the User.
     *
     * @param parkList current ParkList
     * @return parkList of Favorites
     */
    public List<Park> getFavoriteParks(List<Park> parkList){
        List<Park> resultList = new ArrayList<Park>();
        if(parkList == null || parkList.isEmpty()){
            return resultList;
        }
        JSONObject jsonObj;
        JSONArray parks;
        try{
            jsonObj = readParkJSONFromFile(JSON_FILENAME_PARKS_FAVORITES);
            if(jsonObj == null) return resultList;
            parks = jsonObj.getJSONArray("parks");
            for(int i = 0; i < parks.length(); i++){
                String parkId = parks.getString(i);
                for(Park p : parkList){
                    if(p.getId().equals(parkId)){
                        resultList.add(p);
                    }
                }
            }

        }catch(Exception e){
            e.printStackTrace();
        }
        return resultList;
    }

    /**Returns true if the Attraction with the given Id is in the Favorites
     *
     * @param parkId .
     * @param attractionId .
     * @return true if Attraction is favorite.
     */
    public boolean isAttractionFavorite(String parkId, String attractionId){
        JSONObject jsonObj;
        JSONArray parks;
        try {
            jsonObj = readParkJSONFromFile(JSON_FILENAME_ATTRACTIONS_FAVORITES);
            if (jsonObj == null) return false;
            System.out.println("Attraction Favorites JSON FILE: "+jsonObj.toString());
            parks = jsonObj.getJSONArray("parks");
            for (int i = 0; i < parks.length(); i++) {
                JSONObject park = parks.getJSONObject(i);
                String id = park.getString("parkId");
                if(id.equals(parkId)){
                    JSONArray favorites = park.getJSONArray("favorites");
                    for(int j = 0; j < favorites.length(); j++){
                        if(favorites.getString(j).equals(attractionId)){
                            return true;
                        }
                    }
                }
            }
        }catch(JSONException e){
            e.printStackTrace();
            return false;
        }catch(Exception e){
            e.printStackTrace();
            return false;
        }
        return false;
    }

    /** Puts an Attraction into the Fave List
     *
     * @param parkId .
     * @param attractionId .
     * @return true if successful
     */
    public boolean putIntoAttractionFavorites(String parkId, String attractionId){
        if(isAttractionFavorite(parkId, attractionId)){
            return false;
        }else{
            JSONObject jsonObj;
            JSONArray parks;
            jsonObj = readParkJSONFromFile(JSON_FILENAME_ATTRACTIONS_FAVORITES);
            try{
                if(jsonObj == null){
                    //No Favorite so far
                    jsonObj = new JSONObject();
                    parks = new JSONArray();
                    JSONObject park = new JSONObject();
                    park.put("parkId", parkId);
                    JSONArray favorites = new JSONArray();
                    favorites.put(attractionId);
                    park.put("favorites", favorites);
                    parks.put(park);
                    jsonObj.put("parks", parks);
                    return saveJSONToFile(jsonObj, JSON_FILENAME_ATTRACTIONS_FAVORITES);
                }else{
                    //Favoritess of this park already there
                    parks = jsonObj.getJSONArray("parks");
                    for(int i = 0; i < parks.length(); i++){
                        JSONObject park = parks.getJSONObject(i);
                        if(park.getString("parkId").equals(parkId)){
                            JSONArray favorites = park.getJSONArray("favorites");
                            favorites.put(attractionId);

                            park.remove("favorites");
                            park.put("favorites", favorites);
                            JSONArray resultParks = new JSONArray();
                            for(int j = 0; j < parks.length(); j++){
                                if(i == j){
                                    resultParks.put(park);
                                }else{
                                    resultParks.put(parks.get(i));
                                }
                            }
                            jsonObj.remove("parks");
                            jsonObj.put("parks", parks);

                            return saveJSONToFile(jsonObj, JSON_FILENAME_ATTRACTIONS_FAVORITES);
                        }
                    }
                    //Favorites already there but not for this Park
                    JSONObject resultPark = new JSONObject();
                    resultPark.put("parkId", parkId);
                    JSONArray favorites = new JSONArray();
                    favorites.put(attractionId);
                    resultPark.put("favorites", favorites);
                    parks.put(resultPark);
                    jsonObj.remove("parks");
                    jsonObj.put("parks", parks);
                    return saveJSONToFile(jsonObj, JSON_FILENAME_ATTRACTIONS_FAVORITES);
                }
            }catch(JSONException e){
                e.printStackTrace();
                return false;
            }catch(Exception e){
                e.printStackTrace();
                return false;
            }
        }
    }

    /** Deletes an Attraction from the Fave List
     *
     * @param parkId .
     * @param attractionId .
     * @return true if successful
     */
    public boolean deleteAttractionFromFavorites(String parkId, String attractionId){
        JSONObject jsonObj;
        JSONArray parks, resultParks;
        jsonObj = readParkJSONFromFile(JSON_FILENAME_ATTRACTIONS_FAVORITES);
        try{
            if(jsonObj == null){
                return false;
            }else{
                parks = jsonObj.getJSONArray("parks");

                for(int i = 0; i < parks.length(); i++){
                    JSONObject park = parks.getJSONObject(i);
                    if(park.getString("parkId").equals(parkId)){
                        JSONArray favorites = park.getJSONArray("favorites");
                        JSONArray resultFavorites = new JSONArray();
                        for(int j = 0; j < favorites.length(); j++){
                            if(!favorites.getString(j).equals(attractionId)){
                                resultFavorites.put(favorites.getString(j));
                            }
                        }
                        park.remove("favorites");
                        park.put("favorites", resultFavorites);

                        resultParks = new JSONArray();
                        for(int j = 0; j < parks.length(); j++){
                            if(i == j){
                                resultParks.put(park);
                            }else{
                                resultParks.put(parks.get(j));
                            }
                        }
                        jsonObj.remove("parks");
                        jsonObj.put("parks", parks);

                        return saveJSONToFile(jsonObj, JSON_FILENAME_ATTRACTIONS_FAVORITES);
                    }


                }
            }
        }catch(JSONException e){
            e.printStackTrace();
        }
        return false;
    }

    /** Gives back a List with all Attraction Favorites of the User.
     *
     * @param parkId .
     * @param attractionList current attractionList
     * @return parkList of Favorites
     */
    public List<Attraction> getFavoriteAttractions(String parkId, List<Attraction> attractionList){
        List<Attraction> resultList = new ArrayList<Attraction>();
        if(attractionList == null || attractionList.isEmpty()){
            return resultList;
        }
        JSONObject jsonObj;
        JSONArray parks;
        try{
            jsonObj = readParkJSONFromFile(JSON_FILENAME_ATTRACTIONS_FAVORITES);
            if(jsonObj == null) return resultList;
            parks = jsonObj.getJSONArray("parks");
            for(int i = 0; i < parks.length(); i++){
                if(parks.getJSONObject(i).getString("parkId").equals(parkId)){
                    JSONArray favorites = parks.getJSONObject(i).getJSONArray("favorites");
                    for(int j = 0; j < favorites.length(); j++){
                        for(Attraction att : attractionList){
                            if(att.getId().equals(favorites.getString(j))){
                                resultList.add(att);
                            }
                        }
                    }
                }
            }

        }catch(Exception e){
            e.printStackTrace();
        }
        return resultList;
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
            if (jsonObj == null) return parkList;
            parks = jsonObj.getJSONArray("parks");

            for (int i = 0; i < parks.length(); i++) {
                JSONObject tmpPark = parks.getJSONObject(i);
                String id = tmpPark.getString("id");
                String name = tmpPark.getString("name");
                String location = tmpPark.getString("location");
                String description = tmpPark.getString("description");
                double lat = tmpPark.getDouble("lat");
                double lon = tmpPark.getDouble("lon");
                String image = tmpPark.has("image")? tmpPark.getString("image") : "";
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
            if (jsonObj == null) return attractionList;
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
                Date lastUpdated = new Date();
                try{
                    lastUpdated = new Date(tmpAttraction.getString("lastUpdated"));
                }catch(Exception eaaa){
                    eaaa.printStackTrace();
                    lastUpdated = new Date();
                }
                //String parkId = tmpAttraction.getString("parkId");

                Attraction newAttraction = new Attraction(id, name, type, description, lat, lon,
                        image, numberOfReviews, averageReview, numberOfWaitingTimes,
                        averageWaitingTime, numberOfTodayWaitingTimes, averageTodayWaitingTime,
                        currentWaitingTime, parkId, lastUpdated);
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

    /** Deletes the file with the given URI out of the internal storage.
     * Only for testing.
     *
     * @param filename .
     * @return true if successful
     */
    public boolean deleteFile(String filename){
        File dir = context.getFilesDir();
        File toDelete = new File(dir, filename);
        return toDelete.delete();
    }

    public Park deletePark(String parkId) {
        List<Park> parkList = getParkList();
        if(parkList == null || parkList.isEmpty()){
            return null;
        }
        for(Park p : parkList){
            if(p.getId().equals(parkId)){
                parkList.remove(p);
                if (writeParkList(parkList)) {
                    return p;
                }
                return null;
            }
        }

        return null;
    }

    public Attraction deleteAttraction(String parkId, String attrId) {
        List<Park> parkList = getParkList();
        if(parkList == null || parkList.isEmpty()){
            return null;
        }

        List<Attraction> attrList = getAttractionList(parkId);

        for (Attraction a : attrList) {
            if (a.getId().equals(attrId)) {
                attrList.remove(a);
                if (writeAttractionList(attrList, parkId)) {
                    return a;
                }
                return null;
            }
        }

        return null;
    }
}
