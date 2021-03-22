package com.yoon.quest;

import android.app.Activity;

public class AppData {

    private static AppData mInstance = new AppData();

    public static AppData GetInstance() {
        return mInstance;
    }

    // Room Database
    public LocalDatabase mDB;

    public int mDataCnt;
    public Activity mActivity;
}
