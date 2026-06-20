package com.example.ecosmart;

public class Notification {

    private String notificationId;
    private String dustbinId;
    private String location;
    private String type;
    private String message;
    private long timestamp;

    public Notification() {}

    public Notification(String notificationId, String dustbinId, String location,
                        String type, String message, long timestamp) {
        this.notificationId = notificationId;
        this.dustbinId      = dustbinId;
        this.location       = location;
        this.type           = type;
        this.message        = message;
        this.timestamp      = timestamp;
    }


    public String getNotificationId() { return notificationId; }
    public String getDustbinId()      { return dustbinId; }
    public String getLocation()       { return location; }
    public String getType()           { return type; }
    public String getMessage()        { return message; }
    public long getTimestamp()        { return timestamp; }

    public void setNotificationId(String notificationId) { this.notificationId = notificationId; }
    public void setDustbinId(String dustbinId)           { this.dustbinId = dustbinId; }
    public void setLocation(String location)             { this.location = location; }
    public void setType(String type)                     { this.type = type; }
    public void setMessage(String message)               { this.message = message; }
    public void setTimestamp(long timestamp)             { this.timestamp = timestamp; }
}