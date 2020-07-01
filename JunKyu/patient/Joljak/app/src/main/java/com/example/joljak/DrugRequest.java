package com.example.joljak;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class DrugRequest extends StringRequest {

    final static private String url="http://192.168.62.36/setdrug.php";
    private Map<String,String> parameter;
    public DrugRequest(String p_name, String m_name, String m_time,Response.Listener<String> listener){
        super(Method.POST, url,listener,null);
        parameter=new HashMap<>();
        parameter.put("p_name",p_name);
        parameter.put("m_name",m_name);
        parameter.put("m_time",m_time);
    }
    @Override
    protected Map<String,String> getParams() throws AuthFailureError {
        return parameter;
    }
}
