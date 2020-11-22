package com.example.joljakclient;

public class Patientdata {//환자 정보 데이터 클래스
        private String p_name;
        private String p_address;
        private String c_name;
        private String c_phone;
        public String getPName(){
            return p_name;
        }
        public String getAddress(){
            return p_address;
        }
        public String getCName(){
            return c_name;
        }
        public String getCPhone(){return c_phone;}
        public void setp_name(String p_name) {
            this.p_name = p_name;
        }

        public void setc_name(String c_name){this.c_name=c_name;}
        public void setp_address(String address) {
            this.p_address=address;
        }

        public void setC_phone(String c_phone) {
            this.c_phone=c_phone;
        }


}
