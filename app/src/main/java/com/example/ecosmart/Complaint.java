package com.example.ecosmart;

public class Complaint {

    private String complaintId;
    private String username;
    private String location;
    private String description;
    private String status;
    private long timestamp;

    public Complaint() {}

    public Complaint(String complaintId, String username, String location,
                     String description, String status, long timestamp) {
        this.complaintId = complaintId;
        this.username    = username;
        this.location    = location;
        this.description = description;
        this.status      = status;
        this.timestamp   = timestamp;
    }

    public String getComplaintId()  { return complaintId; }
    public String getUsername()     { return username; }
    public String getLocation()     { return location; }
    public String getDescription()  { return description; }
    public String getStatus()       { return status; }
    public long getTimestamp()      { return timestamp; }

    public void setComplaintId(String complaintId)  { this.complaintId = complaintId; }
    public void setUsername(String username)         { this.username = username; }
    public void setLocation(String location)         { this.location = location; }
    public void setDescription(String description)   { this.description = description; }
    public void setStatus(String status)             { this.status = status; }
    public void setTimestamp(long timestamp)         { this.timestamp = timestamp; }
}