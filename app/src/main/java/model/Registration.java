package model;

import java.io.Serializable;

public class Registration implements Serializable {
    private String regId;
    private String renterId;
    private String roomId;
    private String ownerId;
    private String currentDate;
    private String currentTime;


    public Registration(String renterId, String roomId, String ownerId, String currentDate, String currentTime) {
        this.renterId = renterId;
        this.roomId = roomId;
        this.ownerId = ownerId;
        this.currentDate = currentDate;
        this.currentTime = currentTime;
    }

    public Registration() {
    }

    public String getRegId() {
        return regId;
    }

    public void setRegId(String regId) {
        this.regId = regId;
    }

    public String getCurrentDate() {
        return currentDate;
    }

    public void setCurrentDate(String currentDate) {
        this.currentDate = currentDate;
    }

    public String getCurrentTime() {
        return currentTime;
    }

    public void setCurrentTime(String currentTime) {
        this.currentTime = currentTime;
    }

    public String getRenterId() {
        return renterId;
    }

    public void setRenterId(String renterId) {
        this.renterId = renterId;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }
}
