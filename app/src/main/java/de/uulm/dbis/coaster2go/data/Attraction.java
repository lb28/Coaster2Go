package de.uulm.dbis.coaster2go.data;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

public class Attraction {
    private String id;
    private String name;
    private String type;
    private String description;
    private double lat;
    private double lon;
    private String image;
    private int numberOfReviews;
    private double averageReview;
    private int numberOfWaitingTimes;
    private int averageWaitingTime;
    private int numberOfTodayWaitingTimes;
    private int averageTodayWaitingTime;
    //private int numberOfCurrentWaitingTimes //probably not necessary
    private int currentWaitingTime; //avg from the last 3 entries
    private String parkId;
    private Date lastUpdated; //Date of the last time when the WaitingTimes got updated


    /** Konstruktor ohne Id.
     *
     * @param name
     * @param type
     * @param description
     * @param lat
     * @param lon
     * @param image
     * @param numberOfReviews
     * @param averageReview
     * @param numberOfWaitingTimes
     * @param averageWaitingTime
     * @param numberOfTodayWaitingTimes
     * @param averageTodayWaitingTime
     * @param currentWaitingTime
     * @param parkId
     */
    public Attraction( String name, String type, String description, double lat, double lon, String image,
                      int numberOfReviews, double averageReview, int numberOfWaitingTimes,
                       int averageWaitingTime, int numberOfTodayWaitingTimes,
                       int averageTodayWaitingTime, int currentWaitingTime, String parkId) {
        this.name = name;
        this.type = type;
        this.description = description;
        this.lat = lat;
        this.lon = lon;
        this.image = image;
        this.numberOfReviews = numberOfReviews;
        this.averageReview = averageReview;
        this.numberOfWaitingTimes = numberOfWaitingTimes;
        this.averageWaitingTime = averageWaitingTime;
        this.numberOfTodayWaitingTimes = numberOfTodayWaitingTimes;
        this.averageTodayWaitingTime = averageTodayWaitingTime;
        this.currentWaitingTime = currentWaitingTime;
        this.parkId = parkId;
        this.lastUpdated = new Date();
    }

    /** CKonstruktor mit id.
     *
     * @param id
     * @param name
     * @param type
     * @param description
     * @param lat
     * @param lon
     * @param image
     * @param numberOfReviews
     * @param averageReview
     * @param numberOfWaitingTimes
     * @param averageWaitingTime
     * @param numberOfTodayWaitingTimes
     * @param averageTodayWaitingTime
     * @param currentWaitingTime
     * @param parkId
     * @param lastUpdated
     */
    public Attraction(String id, String name, String type, String description, double lat, double lon, String image,
                      int numberOfReviews, double averageReview, int numberOfWaitingTimes,
                      int averageWaitingTime, int numberOfTodayWaitingTimes,
                      int averageTodayWaitingTime, int currentWaitingTime, String parkId, Date lastUpdated) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.description = description;
        this.lat = lat;
        this.lon = lon;
        this.image = image;
        this.numberOfReviews = numberOfReviews;
        this.averageReview = averageReview;
        this.numberOfWaitingTimes = numberOfWaitingTimes;
        this.averageWaitingTime = averageWaitingTime;
        this.numberOfTodayWaitingTimes = numberOfTodayWaitingTimes;
        this.averageTodayWaitingTime = averageTodayWaitingTime;
        this.currentWaitingTime = currentWaitingTime;
        this.parkId = parkId;
        try{
            this.lastUpdated = lastUpdated;
        }catch(Exception e){
            e.printStackTrace();
            this.lastUpdated = new Date();
        }
    }

    /** Makes a JSONObject out of the current Attraction Object
     *
     * @return JSONObjecxt
     */
    public JSONObject toJSON() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("id", id);
            jsonObject.put("name", name);
            jsonObject.put("type", type);
            jsonObject.put("description", description);
            jsonObject.put("lat", lat);
            jsonObject.put("lon", lon);
            jsonObject.put("image", image);
            jsonObject.put("numberOfReviews", numberOfReviews);
            jsonObject.put("averageReview", averageReview);
            jsonObject.put("numberOfWaitingTimes", numberOfWaitingTimes);
            jsonObject.put("averageWaitingTime", averageWaitingTime);
            jsonObject.put("numberOfTodayWaitingTimes", numberOfTodayWaitingTimes);
            jsonObject.put("averageTodayWaitingTime", averageTodayWaitingTime);
            jsonObject.put("currentWaitingTime", currentWaitingTime);
            try{
                jsonObject.put("lastUpdated", lastUpdated.toString());
            }catch(Exception eaaa){
                eaaa.printStackTrace();
                Date now = new Date();
                jsonObject.put("lastUpdated", now.toString());
            }
            //jsonObject.put("parkId", parkId);

            return jsonObject;

        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getNumberOfReviews() {
        return numberOfReviews;
    }

    public void setNumberOfReviews(int numberOfReviews) {
        this.numberOfReviews = numberOfReviews;
    }

    public double getAverageReview() {
        return averageReview;
    }

    public void setAverageReview(double averageReview) {
        this.averageReview = averageReview;
    }

    public int getNumberOfWaitingTimes() {
        return numberOfWaitingTimes;
    }

    public void setNumberOfWaitingTimes(int numberOfWaitingTimes) {
        this.numberOfWaitingTimes = numberOfWaitingTimes;
    }

    public int getAverageWaitingTime() {
        return averageWaitingTime;
    }

    public void setAverageWaitingTime(int averageWaitingTime) {
        this.averageWaitingTime = averageWaitingTime;
    }

    public int getNumberOfTodayWaitingTimes() {
        return numberOfTodayWaitingTimes;
    }

    public void setNumberOfTodayWaitingTimes(int numberOfTodayWaitingTimes) {
        this.numberOfTodayWaitingTimes = numberOfTodayWaitingTimes;
    }

    public int getAverageTodayWaitingTime() {
        return averageTodayWaitingTime;
    }

    public void setAverageTodayWaitingTime(int averageTodayWaitingTime) {
        this.averageTodayWaitingTime = averageTodayWaitingTime;
    }

    public int getCurrentWaitingTime() {
        return currentWaitingTime;
    }

    public void setCurrentWaitingTime(int currentWaitingTime) {
        this.currentWaitingTime = currentWaitingTime;
    }

    public String getParkId() {
        return parkId;
    }

    public void setParkId(String parkId) {
        this.parkId = parkId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getLastUpdated (){
        if(lastUpdated == null){
            return new Date();
        }
        return lastUpdated;}

    public void setLastUpdated(Date lastUpdated){
        try{
        this.lastUpdated = lastUpdated;
    }catch(Exception e){
            e.printStackTrace();
        this.lastUpdated = new Date();
    }}

    @Override
    public String toString() {
        return "Attraction{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", lat=" + lat +
                ", lon=" + lon +
                ", image='" + image + '\'' +
                ", numberOfReviews=" + numberOfReviews +
                ", averageReview=" + averageReview +
                ", numberOfWaitingTimes=" + numberOfWaitingTimes +
                ", averageWaitingTime=" + averageWaitingTime +
                ", numberOfTodayWaitingTimes=" + numberOfTodayWaitingTimes +
                ", averageTodayWaitingTime=" + averageTodayWaitingTime +
                ", currentWaitingTime=" + currentWaitingTime +
                ", parkId='" + parkId + '\'' +
                ", lastUpdated='" + lastUpdated.toString() + '\'' +
                '}';
    }
}
