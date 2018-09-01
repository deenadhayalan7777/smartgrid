package com.example.balasakthi.smartgrid.Admin.Model;

public class Tapmodel {

    private String id;

    private String latitude,longitude;

    private String pincode;

    private String status;

    private String water_remaining;

    private String water_allocated;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getPincode() {
        return pincode;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getWater_remaining() {
        return water_remaining;
    }

    public void setWater_remaining(String water_remaining) {
        this.water_remaining = water_remaining;
    }

    public String getWater_allocated() {
        return water_allocated;
    }

    public void setWater_allocated(String water_allocated) {
        this.water_allocated = water_allocated;
    }
}

