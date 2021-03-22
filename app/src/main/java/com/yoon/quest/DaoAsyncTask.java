package com.yoon.quest;

import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

/**
 * 비동기 처리 해주는 것은 dataModelDAO() 이다.
 * InsertAsyncTask 생성자를 만들어 dataModelDAO() 객체를 받는다.
 **/
public class DaoAsyncTask extends AsyncTask<DataModel, Void, Void> {
    private DataModelDAO mDataModelDAO;
    private String mType;
    private int mId;
    private String mTitle;
    private String mContent;
    private String mColor;

    protected DaoAsyncTask(DataModelDAO dataModelDAO, String type, int id, String title, String content, String color) {
        this.mDataModelDAO = dataModelDAO;
        this.mType = type;
        this.mId = id;
        this.mTitle = title;
        this.mContent = content;
        this.mColor = color;
    }

    @Override
    protected Void doInBackground(DataModel... dataModels) {
        if (mType.equals("INSERT")) {
            mDataModelDAO.insert(dataModels[0]);
        } else if (mType.equals("UPDATE")) {
            if (mDataModelDAO.getData(mId) != null) {
                if (!mTitle.isEmpty() && !mContent.isEmpty()) {
                    mDataModelDAO.dataAllUpdate(mId, mTitle, mContent, mColor);
                } else if (!mTitle.isEmpty() && mContent.isEmpty()) {
                    mDataModelDAO.dataTitleUpdate(mId, mTitle);
                } else if (mTitle.isEmpty() && !mContent.isEmpty()) {
                    mDataModelDAO.dataContentUpdate(mId, mContent);
                }
            }
        }  else if (mType.equals("DELETE")) {
            if (mDataModelDAO.getData(mId) != null) {
                mDataModelDAO.delete(mDataModelDAO.getData(mId));
            }
        } else if (mType.equals("CLEAR")) {
            mDataModelDAO.clearAll();
        }
        return null;
    }
}
