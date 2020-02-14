package com.example.joljakclient.contents;

import android.content.Context;

import java.util.ArrayList;

public class ContentManager {
    private static final String TAG = "ContentManager";

    public static final int RESPONSE_INVALID_PARAMETER = -1;
    public static final int RESPONSE_NO_MATCHING_CONTENT = -2;

    public static final int RESPONSE_OBJECT_ADDED = 11;
    public static final int RESPONSE_OBJECT_UPDATED = 12;
    public static final int RESPONSE_OBJECT_DELETED = 13;

    private static ContentManager mContentManager = null;		// Singleton pattern

    private Context mContext;
    private IContentManagerListener mContentManagerListener;
    private ArrayList<ContentObject> mContentList;		// Cache content objects
    /**
     * Constructor
     */
    private ContentManager(Context c, IContentManagerListener l) {
        mContext = c;
        mContentManagerListener = l;

        mContentList = new ArrayList<ContentObject>();
    }
    /**
     * Singleton pattern
     */
    public synchronized static ContentManager getInstance(Context c, IContentManagerListener l) {
        if(mContentManager == null)
            mContentManager = new ContentManager(c, l);

        return mContentManager;
    }

    public void setListener(IContentManagerListener l) {
        mContentManagerListener = l;
    }
}
