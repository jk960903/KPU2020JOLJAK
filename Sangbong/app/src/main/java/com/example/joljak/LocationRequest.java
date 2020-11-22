package com.example.joljak;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class LocationRequest extends StringRequest {//위치정보 전송 리퀘스트 클래스
    final static private String URL = "http://192.168.62.120/location.php";
    private Map<String, String> parameters;
    //생성자
    public LocationRequest(String p_id, double longitued,double latitude, Response.Listener<String> listener) {
        super(Method.POST, URL, listener, null);
        parameters = new HashMap<>();
        parameters.put("p_id", p_id);
        parameters.put("p_longiitude",Double.toString(longitued));
        parameters.put("p_latitude",Double.toString(latitude));
    }


    //추후 사용을 위한 부분
    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return parameters;

    }
}
