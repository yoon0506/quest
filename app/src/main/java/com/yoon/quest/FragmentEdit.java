package com.yoon.quest;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.yoon.quest.MainData.DaoAsyncTask;
import com.yoon.quest.MainData.DataModel;
import com.yoon.quest.databinding.FragmentEditBinding;

import timber.log.Timber;

public class FragmentEdit extends Fragment {

    private FragmentEdit This = this;
    private FragmentEditBinding mBinding;

    private DataModel mDataModel;
    private String mSelectedColor;

    private Listener mListener = null;

    public void setListener(Listener listener) {
        mListener = listener;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_edit, container, false);
        View view = mBinding.getRoot();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mBinding.backButton.setOnClickListener(v -> {
            if (mListener != null) {
                mListener.eventBack();
            }
        });

        mBinding.updateTitleEdit.setText(mDataModel.getTitle());
        mBinding.updateContentEdit.setText(mDataModel.getContent());
        /**
         * Update
         * 데이터베이스 -> getData(id) -> id 입력하여 DataModel 받아오기
         * -> update(DataModel) 해당 데이터 업데이트
         **/
        mBinding.updateButton.setOnClickListener(v -> {
                    new DaoAsyncTask(
                            AppData.GetInstance().mDB.dataModelDAO(),
                            Key.UPDATE,
                            Integer.parseInt(String.valueOf(mDataModel.getId())),
                            mBinding.updateTitleEdit.getText().toString(),
                            mBinding.updateContentEdit.getText().toString(),
                            mSelectedColor
                    ).execute();

                    if (mListener != null) {
                        mListener.eventBack();
                    }
                }
        );

        // 토글 컬러 color
        getScheduleColor(mDataModel.getColor());
        mBinding.colorBtn1.setOnCheckedChangeListener(mColorClickListener);
        mBinding.colorBtn2.setOnCheckedChangeListener(mColorClickListener);
        mBinding.colorBtn3.setOnCheckedChangeListener(mColorClickListener);
        mBinding.colorBtn4.setOnCheckedChangeListener(mColorClickListener);
        mBinding.colorBtn5.setOnCheckedChangeListener(mColorClickListener);
        mBinding.colorBtn6.setOnCheckedChangeListener(mColorClickListener);
        mBinding.colorBtn7.setOnCheckedChangeListener(mColorClickListener);
        mBinding.colorBtn8.setOnCheckedChangeListener(mColorClickListener);

        if (mListener != null) {
            mListener.eventLayoutDone();
        }
    }

    public void setData(DataModel dataModel) {
        mDataModel = dataModel;
    }

    /**
     * toggle
     */
    private CompoundButton.OnCheckedChangeListener mColorClickListener = new CompoundButton.OnCheckedChangeListener() {
        boolean avoidRecursions = false;

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (avoidRecursions) return;
            avoidRecursions = true;

            // don't allow the un-checking
            if (!isChecked) {
                buttonView.setChecked(true);
                avoidRecursions = false;
                return;
            }

            // un-check the previous checked button
            if (buttonView != mBinding.colorBtn1 && mBinding.colorBtn1.isChecked())
                mBinding.colorBtn1.setChecked(false);
            else if (buttonView != mBinding.colorBtn2 && mBinding.colorBtn2.isChecked())
                mBinding.colorBtn2.setChecked(false);
            else if (buttonView != mBinding.colorBtn3 && mBinding.colorBtn3.isChecked())
                mBinding.colorBtn3.setChecked(false);
            else if (buttonView != mBinding.colorBtn4 && mBinding.colorBtn4.isChecked())
                mBinding.colorBtn4.setChecked(false);
            else if (buttonView != mBinding.colorBtn5 && mBinding.colorBtn5.isChecked())
                mBinding.colorBtn5.setChecked(false);
            else if (buttonView != mBinding.colorBtn6 && mBinding.colorBtn6.isChecked())
                mBinding.colorBtn6.setChecked(false);
            else if (buttonView != mBinding.colorBtn7 && mBinding.colorBtn7.isChecked())
                mBinding.colorBtn7.setChecked(false);
            else if (buttonView != mBinding.colorBtn8 && mBinding.colorBtn8.isChecked())
                mBinding.colorBtn8.setChecked(false);

            avoidRecursions = false;

            mSelectedColor = setBlockColor(buttonView);
            Timber.tag("checkCheck").d("선택한 색갈 : %s", mSelectedColor);
        }
    };

    private String setBlockColor(CompoundButton colorBtnName) {
        String mmSetColor = null;
        if (mBinding.colorBtn1.equals(colorBtnName)) {
            mmSetColor = "#f5b1c8";
        } else if (mBinding.colorBtn2.equals(colorBtnName)) {
            mmSetColor = "#f5d3ae";
        } else if (mBinding.colorBtn3.equals(colorBtnName)) {
            mmSetColor = "#fff5b7";
        } else if (mBinding.colorBtn4.equals(colorBtnName)) {
            mmSetColor = "#c8f6ae";
        } else if (mBinding.colorBtn5.equals(colorBtnName)) {
            mmSetColor = "#a6e3db";
        } else if (mBinding.colorBtn6.equals(colorBtnName)) {
            mmSetColor = "#afe4f4";
        } else if (mBinding.colorBtn7.equals(colorBtnName)) {
            mmSetColor = "#c4c5f3";
        } else if (mBinding.colorBtn8.equals(colorBtnName)) {
            mmSetColor = "#e1c3f4";
        }
        return mmSetColor;
    }

    private void getScheduleColor(String color) {
        mSelectedColor = color;
        switch (color) {
            case "#f5b1c8":
                mBinding.colorBtn1.setChecked(true);
                break;
            case "#f5d3ae":
                mBinding.colorBtn2.setChecked(true);
                break;
            case "#fff5b7":
                mBinding.colorBtn3.setChecked(true);
                break;
            case "#c8f6ae":
                mBinding.colorBtn4.setChecked(true);
                break;
            case "#a6e3db":
                mBinding.colorBtn5.setChecked(true);
                break;
            case "#afe4f4":
                mBinding.colorBtn6.setChecked(true);
                break;
            case "#c4c5f3":
                mBinding.colorBtn7.setChecked(true);
                break;
            case "#e1c3f4":
                mBinding.colorBtn8.setChecked(true);
                break;
        }
    }

    private void showKeyboard(){
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
    }
    public interface Listener {
        public void eventBack();
        public void eventLayoutDone();
    }
}