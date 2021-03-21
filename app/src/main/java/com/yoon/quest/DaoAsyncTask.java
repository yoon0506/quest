package com.yoon.quest;

import android.os.AsyncTask;

public class DaoAsyncTask extends AsyncTask<DataModel, Void, Void> {
    private DataModelDAO mDataModelDAO;
    private String mType;
    private int mId;
    private String mTitle;

    public DaoAsyncTask(DataModelDAO dataModelDAO, String type, int id, String title) {
        this.mDataModelDAO = dataModelDAO;
        this.mType = type;
        this.mId = id;
        this.mTitle = title;
    }

    @Override
    protected Void doInBackground(DataModel... dataModels) {
        if (mType.equals("INSERT")) {
            mDataModelDAO.insert(dataModels[0]);
        } else if (mType.equals("UPDATE")) {
            if (mDataModelDAO.getData(mId) != null) {
                mDataModelDAO.dataUpdate(mId, mTitle);
            }
        } else if (mType.equals("DELETE")) {
            if (mDataModelDAO.getData(mId) != null) {
                mDataModelDAO.delete(mDataModelDAO.getData(mId));
            }
        } else if (mType.equals("CLEAR")) {
            mDataModelDAO.clearAll();
        }
        return null;
    }
}