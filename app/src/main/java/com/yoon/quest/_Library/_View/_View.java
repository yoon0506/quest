package com.yoon.quest._Library._View;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

/**
 * Created by singleton on 2017. 3. 13..
 */

public class _View {
    public static final int VISIBLE = 10001;
    public static final int INVISIBLE = 10002;
    public static final int GONE = 10003;

    public LayoutInflater mLayoutInf;
    public ViewGroup mContainer;
    public View mContent;

    public String mContainerName = "";
    public String mContentName = "";

    public _View() {

    }

    public _View(Activity activity, String containerName, String contentName) {
        mContainerName = containerName;
        mContentName = contentName;
        int mmContainerId = activity.getResources().getIdentifier(containerName, "id", activity.getPackageName());
        int mmContentId = activity.getResources().getIdentifier(contentName, "layout", activity.getPackageName());
        mLayoutInf = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mContainer = (ViewGroup)activity.findViewById(mmContainerId);
        mContent = mLayoutInf.inflate(mmContentId, null);
    }

    public _View(Activity activity, View view, String containerName, String contentName) {
        int mmContainerId = view.getResources().getIdentifier(containerName, "id", activity.getPackageName());
        int mmContentId = view.getResources().getIdentifier(contentName, "layout", activity.getPackageName());
        mLayoutInf = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mContainer = (ViewGroup)view.findViewById(mmContainerId);
        mContent = mLayoutInf.inflate(mmContentId, null);
    }

    public void initInterface() {

    }

    public void addContentToContainer() {
        mContainer.addView(mContent);
        mContent.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
    }

    public void removeContentFromContainer() {
        ((Activity)mContent.getContext()).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mContainer.removeView(mContent);
            }
        });

    }

    public void setVisible(int visible) {
        switch (visible) {
            case _View.VISIBLE:
                mContent.setVisibility(View.VISIBLE);
                break;
            case _View.INVISIBLE:
                mContent.setVisibility(View.INVISIBLE);
                break;
            case _View.GONE:
                mContent.setVisibility(View.GONE);
                break;
        }
    }

    public int getVisible() {
        if(mContent.getVisibility() == View.VISIBLE) return _View.VISIBLE;
        else if(mContent.getVisibility() == View.INVISIBLE) return _View.INVISIBLE;
        else if(mContent.getVisibility() == View.GONE) return _View.GONE;
        return INVISIBLE;
    }
}
