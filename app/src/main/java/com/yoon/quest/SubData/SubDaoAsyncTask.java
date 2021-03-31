package com.yoon.quest.SubData;

import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import com.yoon.quest.Key;

import java.util.List;

/**
 * 비동기 처리 해주는 것은 dataModelDAO() 이다.
 * InsertAsyncTask 생성자를 만들어 dataModelDAO() 객체를 받는다.
 **/
public class SubDaoAsyncTask extends AsyncTask<SubDataModel, Void, Void> {
    private SubDataModelDAO mDataModelDAO;
    private String mType;
    private int mId;
    private String mTitle;
    private String mContent;
    private String mColor;

    public SubDaoAsyncTask(SubDataModelDAO dataModelDAO, String type, int id, String title, String content, String color) {
        this.mDataModelDAO = dataModelDAO;
        this.mType = type;
        this.mId = id;
        this.mTitle = title;
        this.mContent = content;
        this.mColor = color;
    }

    @Override
    protected Void doInBackground(SubDataModel... dataModels) {
        if (mType.equals(Key.INSERT)) {
            mDataModelDAO.insert(dataModels[0]);
        } else if (mType.equals(Key.UPDATE)) {
            if (mDataModelDAO.getData(mId) != null) {
                if (!mTitle.isEmpty() && !mColor.isEmpty()) {
                    mDataModelDAO.dataAllUpdate(mId, mTitle, mContent, mColor);
                }
//                else if (!mTitle.isEmpty() && mContent.isEmpty()) {
//                    mDataModelDAO.dataUpdate(mId, mTitle,"", mColor);
//                } else if (mTitle.isEmpty() && !mContent.isEmpty()) {
//                    mDataModelDAO.dataContentUpdate(mId, mContent, mColor);
//                }
            }
        } else if (mType.equals(Key.DELETE)) {
            if (mDataModelDAO.getData(mId) != null) {
                mDataModelDAO.delete(mDataModelDAO.getData(mId));
            } else if (mId == -1) {
                List<SubDataModel> data = mDataModelDAO.getDataIncludeColor(mColor);
                for (SubDataModel dataModel : data) {
                    mDataModelDAO.delete(dataModel);
                }
            }
        } else if (mType.equals(Key.CLEAR)) {
            mDataModelDAO.clearAll();
        } else if (mType.equals(Key.SELECT)) {
            List<SubDataModel> data = mDataModelDAO.getDataPickedColor(mColor);
            for (SubDataModel dataModel : data) {
                mDataModelDAO.insert(dataModel);
            }
        }
        return null;
    }
}