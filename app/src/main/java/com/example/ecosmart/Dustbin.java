package com.example.ecosmart;

public class Dustbin {
    private String id;
    private String location;
    private int fillLevel;
    private String gasStatus;

    public Dustbin()
    {

    }
    public Dustbin(String id, String location, int fillLevel, String gasStatus) {
        this.id = id;
        this.location = location;
        this.fillLevel = fillLevel;
        this.gasStatus = gasStatus;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getFillLevel() {
        return fillLevel;
    }

    public void setFillLevel(int fillLevel) {
        this.fillLevel = fillLevel;
    }

    public String getGasStatus() {
        return gasStatus;
    }

    public void setGasStatus(String gasStatus) {
        this.gasStatus = gasStatus;
    }
}
