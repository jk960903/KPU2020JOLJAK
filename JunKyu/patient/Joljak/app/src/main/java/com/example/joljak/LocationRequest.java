package com.example.joljak;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class LocationRequest extends StringRequest {
    final static private String URL = "http://192.168.62.36/location.php";
    private Map<String, String> parameters;
    //생성자
    public LocationRequest(String p_name, double longitued,double latitude, Response.Listener<String> listener) {
        super(Method.POST, URL, listener, null);
        parameters = new HashMap<>();
        parameters.put("p_name", p_name);
        parameters.put("p_longitude",Double.toString(longitued));
        parameters.put("p_latitude",Double.toString(latitude));
    }


    //추후 사용을 위한 부분
    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return parameters;

    }
}
