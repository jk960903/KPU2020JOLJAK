package com.example.joljakclient.contents;

public interface IContentManagerListener {
    public static final int CALLBACK_CONTENT_UPDATED = 1;

    public void OnContentCallback(int msgType, int arg0, int arg1, String arg2, String arg3, Object arg4);
}
