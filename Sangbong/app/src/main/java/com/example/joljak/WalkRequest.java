package com.example.joljak;

import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;
//걸음정보 전송리퀘스트 클래스 
public class WalkRequest extends StringRequest {
    final static private String URL = "http://192.168.62.120/walk.php";//주소가 들어가야함
    private Map<String,String> parameter;
    public WalkRequest(String p_id,int walkcount,String day, Response.Listener<String> listener){
        super(Method.POST,URL,listener,null);
        parameter=new HashMap<>();
        parameter.put("p_id",p_id);
        String walk=Integer.toString(walkcount);
        parameter.put("walk",walk);
        parameter.put("datetime",day);

    }
    @Override
    protected Map<String,String> getParams() throws AuthFailureError{
        return parameter;
    }

}
