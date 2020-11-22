package com.example.joljakclient;

public class WalkData {//걸음수 정보 클래스
    private String p_id;
    private String walk;
    private String datetime;
    public String getID(){
        return p_id;
    }
    public String getWalk(){
        return walk;
    }
    public String getDatetime(){
        return datetime;
    }
    public void setID(String p_id) {
        this.p_id = p_id;
    }

    public void setWalk(String Walk) {
        this.walk=Walk;
    }

    public void setDatetime(String datetime) {
        this.datetime=datetime;
    }
}
