package de.uulm.dbis.coaster2go.data;

import java.util.Date;

public class WaitingTime {
    private String id;
    private String attractionId; //Id of the  Attraction
    private String displayName;
    private String userId;
    private int minutes;
    private Date createdAt; //Not sure if this equals the Azure SQL Date format

    /** Constructor with id.
     *
     * @param id
     * @param attractionId
     * @param displayName
     * @param userId
     * @param minutes
     * @param createdAt
     */
    public WaitingTime(String id, String attractionId, String displayName, String userId,
                       int minutes, Date createdAt) {
        this.id = id;
        this.attractionId = attractionId;
        this.displayName = displayName;
        this.userId = userId;
        this.minutes = minutes;
        this.createdAt = createdAt;
    }

    /** Constructor without id.
     *
     * @param attractionId
     * @param displayName
     * @param userId
     * @param minutes
     * @param createdAt
     */
    public WaitingTime(String attractionId, String displayName, String userId,
                       int minutes, Date createdAt) {
        this.attractionId = attractionId;
        this.displayName = displayName;
        this.userId = userId;
        this.minutes = minutes;
        this.createdAt = createdAt;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAttractionId() {
        return attractionId;
    }

    public void setAttractionId(String attractionId) {
        this.attractionId = attractionId;
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

    public int getMinutes() {
        return minutes;
    }

    public void setMinutes(int minutes) {
        this.minutes = minutes;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}
