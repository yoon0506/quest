package com.yoon.quest;

public class AppData {

    private static AppData mInstance = new AppData();

    public static AppData GetInstance() {
        return mInstance;
    }

    // Room Database
    public LocalDatabase mDB;
}
