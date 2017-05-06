package de.uulm.dbis.coaster2go.data;

public class Attraction {
    private String id;
    private String name;
    private String type; //TODO make a list of all possible types
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
    private int currentWaitingTime;
    private String parkId;

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
     */
    public Attraction(String id, String name, String type, String description, double lat, double lon, String image,
                      int numberOfReviews, double averageReview, int numberOfWaitingTimes,
                      int averageWaitingTime, int numberOfTodayWaitingTimes,
                      int averageTodayWaitingTime, int currentWaitingTime, String parkId) {
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

    public double getAverageWaitingTime() {
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

    public double getAverageTodayWaitingTime() {
        return averageTodayWaitingTime;
    }

    public void setAverageTodayWaitingTime(int averageTodayWaitingTime) {
        this.averageTodayWaitingTime = averageTodayWaitingTime;
    }

    public double getCurrentWaitingTime() {
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
                '}';
    }
}
