package com.example.joljak;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class SetRequest extends StringRequest {
    final static private String url="http:/192.168.0.60/setperson.php";
    private Map<String,String> parameter;
    public SetRequest(String p_name,String p_address,String c_name,String c_phone,Response.Listener<String> listener){
        super(Request.Method.POST, url,listener,null);
        parameter=new HashMap<>();
        parameter.put("p_name",p_name);
        parameter.put("p_address",p_address);
        parameter.put("c_name",c_name);
        parameter.put("c_phone",c_phone);
    }
    @Override
    protected Map<String,String> getParams() throws AuthFailureError {
        return parameter;
    }
}
