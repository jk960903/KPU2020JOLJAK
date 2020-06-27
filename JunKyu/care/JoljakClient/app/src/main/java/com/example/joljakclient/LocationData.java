package com.example.joljakclient;

public class LocationData {
    private String p_id;
    private String longitude;
    private String latitude;
    private String datetime;
    public String getID(){
        return p_id;
    }
    public String getLongitude(){
        return longitude;
    }
    public String getLatitude(){
        return latitude;
    }
    public String getDatetime(){
        return datetime;
    }
    public void setID(String p_id) {
        this.p_id = p_id;
    }

    public void setLongitude(String longitude) {
        this.longitude=longitude;
    }

    public void setLatitude(String latitude) {
        this.latitude=latitude;
    }
}
