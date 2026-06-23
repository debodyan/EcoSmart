package com.example.ecosmart;

public class Dustbin {

    private String id;
    private String location;
    private int fillLevel;
    private String gasStatus;
    private double latitude;
    private double longitude;

    public Dustbin() {}

    public Dustbin(String id, String location, int fillLevel, String gasStatus, double latitude, double longitude) {
        this.id        = id;
        this.location  = location;
        this.fillLevel = fillLevel;
        this.gasStatus = gasStatus;
        this.latitude  = latitude;
        this.longitude = longitude;
    }

    public String getId()        { return id; }
    public String getLocation()  { return location; }
    public int getFillLevel()    { return fillLevel; }
    public String getGasStatus() { return gasStatus; }
    public double getLatitude()  { return latitude; }
    public double getLongitude() { return longitude; }


    public void setId(String id)               { this.id = id; }
    public void setLocation(String location)   { this.location = location; }
    public void setFillLevel(int fillLevel)    { this.fillLevel = fillLevel; }
    public void setGasStatus(String gasStatus) { this.gasStatus = gasStatus; }
    public void setLatitude(double latitude)   { this.latitude = latitude; }
    public void setLongitude(double longitude) { this.longitude = longitude; }
}