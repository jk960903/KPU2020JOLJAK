package com.example.joljak;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class RegisterRequest extends StringRequest {
    final static private String URL = "http://192.168.62.120/register.php";
    private Map<String, String> parameters;

    public RegisterRequest(String p_id, String p_pw, String p_name, String p_phone, String p_birth, Response.Listener<String> listener){
        super(Request.Method.POST, URL, listener, null);//해당 URL에 POST방식으로 파마미터들을 전송함
        parameters = new HashMap<>();
        parameters.put("p_id", p_id);
        parameters.put("p_pw", p_pw);
        parameters.put("p_name", p_name);
        parameters.put("p_phone", p_phone);
        parameters.put("p_birth", p_birth);
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return parameters;
    }
}
