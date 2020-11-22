package com.example.joljakclient;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class LoginRequest extends StringRequest {//로그인 요청 클래스 데모버전에서는 사용 x
    final static private String URL = "http://192.168.0.10/login.php";
    private Map<String, String> parameters;
    public LoginRequest(String p_id, String p_pw, Response.Listener<String> listener) {
        super(Request.Method.POST, URL, listener, null);
        parameters = new HashMap<>();
        parameters.put("p_id", p_id);
        parameters.put("p_pw", p_pw);
    }


    //추후 사용을 위한 부분
    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return parameters;

    }
}
