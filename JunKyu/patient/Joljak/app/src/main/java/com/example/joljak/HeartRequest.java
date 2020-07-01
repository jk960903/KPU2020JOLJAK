package com.example.joljak;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class HeartRequest extends StringRequest {
    final static private String URL = "http://192.168.62.36/heart.php";
    private Map<String, String> parameters;
    //생성자
    public HeartRequest(String p_name, String heartrate, Response.Listener<String> listener) {
        super(Method.POST, URL, listener, null);
        parameters = new HashMap<>();
        parameters.put("p_name", p_name);
        parameters.put("p_heart",heartrate);
    }


    //추후 사용을 위한 부분
    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return parameters;

    }
}
