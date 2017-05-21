package de.uulm.dbis.coaster2go.data;

import org.json.JSONException;
import org.json.JSONObject;

public class Park {
    private String id;
    private String name;
    private String location;
    private String description;
    private double lat;
    private double lon;
    private String image;
    private int numberOfReviews;
    private double averageReview;
    private String admin; //UserId of Park Creator


    /** Konstruktor mit id
     *
     * @param id
     * @param name
     * @param location
     * @param description
     * @param lat
     * @param lon
     * @param image
     * @param numberOfReviews
     * @param averageReview
     * @param admin
     */
    public Park(String id, String name, String location, String description, double lat, double lon, String image, int numberOfReviews, double averageReview, String admin) {
        this(name, location, description, lat, lon, image, numberOfReviews, averageReview, admin);
        this.id = id;
    }

    /** Konstruktor ohne id
     *
     * @param name
     * @param location
     * @param description
     * @param lat
     * @param lon
     * @param image
     * @param numberOfReviews
     * @param averageReview
     * @param admin
     */
    public Park(String name, String location, String description, double lat, double lon, String image, int numberOfReviews, double averageReview, String admin) {
        this.name = name;
        this.location = location;
        this.description = description;
        this.lat = lat;
        this.lon = lon;
        this.image = image;
        this.numberOfReviews = numberOfReviews;
        this.averageReview = averageReview;
        this.admin = admin;
    }

    @Override
    public String toString() {
        return "Park{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", location='" + location + '\'' +
                ", description='" + description + '\'' +
                ", lat=" + lat +
                ", lon=" + lon +
                ", image='" + image + '\'' +
                ", numberOfReviews=" + numberOfReviews +
                ", averageReview=" + averageReview +
                ", admin='" + admin + '\'' +
                '}';
    }

    /** Makes a JSONObject out of the current Park Object
     *
     * @return JSONObjecxt
     */
    public JSONObject toJSON() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("id", id);
            jsonObject.put("name", name);
            jsonObject.put("location", location);
            jsonObject.put("description", description);
            jsonObject.put("lat", lat);
            jsonObject.put("lon", lon);
            jsonObject.put("image", image);
            jsonObject.put("numberOfReviews", numberOfReviews);
            jsonObject.put("averageReview", averageReview);
            jsonObject.put("admin", admin);

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

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public String getAdmin() {
        return admin;
    }

    public void setAdmin(String admin) {
        this.admin = admin;
    }
}
