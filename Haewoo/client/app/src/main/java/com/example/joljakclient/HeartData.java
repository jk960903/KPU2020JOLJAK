package com.example.joljakclient;

public class HeartData {
    String p_name;
    int p_heart;
    public String getName(){
        return p_name;
    }
    public int getHeart(){
        return p_heart;
    }
    public void setName(String p_id) {
        this.p_name = p_id;
    }

    public void setHeart(int Walk) {
        this.p_heart=Walk;
    }
}
