package com.yoon.quest;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.CompoundButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.yoon.quest.MainData.DaoAsyncTask;
import com.yoon.quest.MainData.DataModel;
import com.yoon.quest.databinding.FragmentAddBinding;

import timber.log.Timber;

public class FragmentAdd extends Fragment {

    private FragmentAdd This = this;
    private FragmentAddBinding mBinding;
    private String mSelectedColor;

    private Listener mListener = null;

    public void setListener(Listener listener) {
        mListener = listener;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_add, container, false);
        View view = mBinding.getRoot();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

//        /**
//         * Database 를 관찰하고 변경이 감지될 때 UI 갱신
//         * 데이터베이스 mDB -> 데이터베이스 mDataModelDAO()
//         * -> getAll() 가져오는 List<DataModel> 객체는 관찰 가능한 객체 이므로
//         * -> observe 메소드로 관찰하고 변경이 되면 dataList 에 추가한다.
//         * 변경된 내용이 담긴 dataList 를 출력한다.
//         **/
//        /*
//         * 람다식 사용
//         * file -> project structure -> modules -> source compatibility, target compatibility -> 1.8
//         **/
//        AppData.GetInstance().mDB.dataModelDAO().getAll().observe(This, dataList -> {
//            mBinding.resultText.setText(dataList.toString());
//        });

        mBinding.backButton.setOnClickListener(v -> {
            if (mListener != null) {
                mListener.eventBack();
            }
        });

        /**
         * Insert
         * 데이터배이스 객체 . 데이터베이스 DAO . insert -> new DataModel (텍스트 추가)
         **/
        mBinding.addButton.setOnClickListener(v -> {
            /**
             *  AsyncTask 생성자에 execute 로 DataModelDAO 객체를 던저준다.
             *  비동기 처리
             **/
            if (mBinding.addEdit.getText().toString().equals("")) {
                Toast.makeText(getContext(), "제목을 입력해주세요.", Toast.LENGTH_SHORT).show();
                return;
            }
//            if (mBinding.addContentEdit.getText().toString().equals("")) {
//                Toast.makeText(getContext(), "내용을 입력해주세요.", Toast.LENGTH_SHORT).show();
//                return;
//            }
            if (mSelectedColor == null)
            {
                Toast.makeText(getContext(), "색깔을 선택해주세요.", Toast.LENGTH_SHORT).show();
                return;
            }

            new DaoAsyncTask(AppData.GetInstance().mDB.dataModelDAO(), Key.INSERT, 0, "", "", "")
                    .execute(new DataModel(mBinding.addEdit.getText().toString(), mBinding.addContentEdit.getText().toString(), mSelectedColor));
            if (mListener != null) {
                mListener.eventInsertDone();
            }
        });
//
//        /**
//         * Update
//         * 데이터베이스 -> getData(id) -> id 입력하여 DataModel 받아오기
//         * -> update(DataModel) 해당 데이터 업데이트
//         **/
//        mBinding.updateButton.setOnClickListener(v ->
//                new DaoAsyncTask(
//                        AppData.GetInstance().mDB.dataModelDAO(),
//                        Key.UPDATE,
//                        Integer.parseInt(mBinding.updateIdEdit.getText().toString()),
//                        mBinding.updateTitleEdit.getText().toString(),
//                        mBinding.updateContentEdit.getText().toString()
//                ).execute()
//        );
//
//        /**
//         * Delete
//         * 데이터베이스 -> getData(id) -> id 입력하여 DataModel 받아오기
//         * -> delete(DataModel) 해당 데이터 삭제
//         * */
//        mBinding.deleteButton.setOnClickListener(v ->
//                new DaoAsyncTask(
//                        AppData.GetInstance().mDB.dataModelDAO(),
//                        Key.DELETE,
//                        Integer.parseInt(mBinding.deleteEdit.getText().toString()),
//                        "", ""
//                ).execute()
//        );
//
//        /**
//         * Clear
//         * 데이터베이스 -> allClear -> 리스트 전부 지움
//         * */
//        mBinding.clearButton.setOnClickListener(v ->
//                _Popup.GetInstance().ShowBinaryPopup(getContext(), "전체 삭제하시겠습니까?", "확인", "취소", new _Popup.BinaryPopupListener() {
//                    @Override
//                    public void didSelectBinaryPopup(String mainMessage, String selectMessage) {
//                        if (selectMessage.equals("확인")) {
//                            new DaoAsyncTask(AppData.GetInstance().mDB.dataModelDAO(), Key.CLEAR, 0, "", "").execute();
//                        }
//                    }
//                })
//        );

        // 토글 컬러 color
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
    private void showKeyboard(){
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
    }
    public interface Listener {
        public void eventBack();
        public void eventInsertDone();
        public void eventLayoutDone();
    }
}