package com.yoon.quest;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.yoon.quest.databinding.FragmentAddBinding;

import java.util.HashMap;

public class FragmentAdd extends Fragment {

    private FragmentAdd This = this;
    FragmentAddBinding mBinding;

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

        /**
         * Database 를 관찰하고 변경이 감지될 때 UI 갱신
         * 데이터베이스 mDB -> 데이터베이스 mDataModelDAO()
         * -> getAll() 가져오는 List<DataModel> 객체는 관찰 가능한 객체 이므로
         * -> observe 메소드로 관찰하고 변경이 되면 dataList 에 추가한다.
         * 변경된 내용이 담긴 dataList 를 출력한다.
         **/
        /*
         * 람다식 사용
         * file -> project structure -> modules -> source compatibility, target compatibility -> 1.8
         **/
        AppData.GetInstance().mDB.dataModelDAO().getAll().observe(This, dataList -> {
            mBinding.resultText.setText(dataList.toString());
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
            new DaoAsyncTask(AppData.GetInstance().mDB.dataModelDAO(), Key.INSERT, 0, "")
                    .execute(new DataModel(mBinding.addEdit.getText().toString()));
        });

        /**
         * Update
         * 데이터베이스 -> getData(id) -> id 입력하여 DataModel 받아오기
         * -> update(DataModel) 해당 데이터 업데이트
         **/
        mBinding.updateButton.setOnClickListener(v ->
                new DaoAsyncTask(
                        AppData.GetInstance().mDB.dataModelDAO(),
                        Key.UPDATE,
                        Integer.parseInt(mBinding.updateIdEdit.getText().toString()),
                        mBinding.updateTitleEdit.getText().toString()
                ).execute()
        );

        /**
         * Delete
         * 데이터베이스 -> getData(id) -> id 입력하여 DataModel 받아오기
         * -> delete(DataModel) 해당 데이터 삭제
         * */
        mBinding.deleteButton.setOnClickListener(v ->
                new DaoAsyncTask(
                        AppData.GetInstance().mDB.dataModelDAO(),
                        Key.DELETE,
                        Integer.parseInt(mBinding.deleteEdit.getText().toString()),
                        ""
                ).execute()
        );

        /**
         * Clear
         * 데이터베이스 -> allClear -> 리스트 전부 지움
         * */
        mBinding.clearButton.setOnClickListener(v ->
                new DaoAsyncTask(AppData.GetInstance().mDB.dataModelDAO(), Key.CLEAR, 0, "").execute()
        );
    }

    /**
     * 비동기 처리 해주는 것은 dataModelDAO() 이다.
     * InsertAsyncTask 생성자를 만들어 dataModelDAO() 객체를 받는다.
     **/


    public interface Listener {
        public void didRespond(Fragment fragment, String event, HashMap<String, String> data);
    }
}