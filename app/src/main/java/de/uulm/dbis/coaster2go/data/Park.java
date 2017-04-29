package de.uulm.dbis.coaster2go.data;

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
    private String admin;


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
        this.id = id;
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
