package com.example.joljak;

public class MedicineData {
    private String p_id;
    private String m_name;
    private String m_time;
    private int hour;
    public String getID(){
        return p_id;
    }
    public String getm_name(){
        return m_name;
    }
    public String getm_time(){
        return m_time;
    }
    public int gethour(){return hour;}
    public void setID(String p_id) {
        this.p_id = p_id;
    }
    public void settime(int time){this.hour=time;}
    public void setm_name(String m_name) {
        this.m_name=m_name;
    }

    public void setm_time(String m_time) {
        this.m_time=m_time;
    }
}
