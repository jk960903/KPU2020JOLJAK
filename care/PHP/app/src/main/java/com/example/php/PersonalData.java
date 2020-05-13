package com.example.php;

public class PersonalData {
    private String p_id;
    private String p_pwd;

    public String getMember_id() {
        return p_id;
    }

    public String getMember_name() {
        return p_pwd;
    }


    public void setMember_id(String p_id) {
        this.p_id = p_id;
    }

    public void setMember_name(String p_pwd) {
        this.p_pwd=p_pwd;
    }

}
