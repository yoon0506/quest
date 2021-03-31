package com.yoon.quest;

import android.app.Activity;

import com.yoon.quest.MainData.DataModel;

import java.util.ArrayList;
import java.util.List;

public class AppData {

    private static AppData mInstance = new AppData();

    public static AppData GetInstance() {
        return mInstance;
    }

    // Room Database
    public LocalDatabase mDB;

    public int mDataCnt;
    public int mSubDataCnt;
    public Activity mActivity;
    public List<DataModel> mAllDataModelList = new ArrayList<>();

}
