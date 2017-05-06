package de.uulm.dbis.coaster2go.data;

import java.util.Date;

public class Review {
    private String id;
    //private boolean type; //Probably not necessary
    private String reviewedId; //Id of the reviewed Park or Attraction
    private String displayName;
    private String userId;
    private int numberOfStars;
    private String comment;
    private Date createdAt; //Not sure if this equals the Azure SQL Date format

    /** Constructor with id and with createdAt.
     *
     * @param id
     * @param reviewedId
     * @param displayName
     * @param userId
     * @param numberOfStars
     * @param comment
     * @param createdAt
     */
    public Review(String id, String reviewedId, String displayName, String userId, int numberOfStars,
                  String comment, Date createdAt) {
        this.id = id;
        this.reviewedId = reviewedId;
        this.displayName = displayName;
        this.userId = userId;
        this.numberOfStars = numberOfStars;
        this.comment = comment;
        this.createdAt = createdAt;
    }

    /** Constructor without id and without createdAt .
     *
     * @param reviewedId
     * @param displayName
     * @param userId
     * @param numberOfStars
     * @param comment
     */
    public Review(String reviewedId, String displayName, String userId, int numberOfStars,
                  String comment) {
        this.reviewedId = reviewedId;
        this.displayName = displayName;
        this.userId = userId;
        this.numberOfStars = numberOfStars;
        this.comment = comment;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    /** Returns the Id of the reviewed park or attraction.
     *
     * @return Id of the reviewed Park or Attraction.
     */
    public String getReviewedId() {
        return reviewedId;
    }

    /** Sets the id of the reviewed Park or Attraction.
     *
     * @param reviewedId .
     */
    public void setReviewedId(String reviewedId) {
        this.reviewedId = reviewedId;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getNumberOfStars() {
        return numberOfStars;
    }

    public void setNumberOfStars(int numberOfStars) {
        this.numberOfStars = numberOfStars;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "Review{" +
                "id='" + id + '\'' +
                ", reviewedId='" + reviewedId + '\'' +
                ", displayName='" + displayName + '\'' +
                ", userId='" + userId + '\'' +
                ", numberOfStars=" + numberOfStars +
                ", comment='" + comment + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}
