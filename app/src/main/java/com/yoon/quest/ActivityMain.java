package com.yoon.quest;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.room.Room;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.yoon.quest.databinding.ActivityMainBinding;
import com.yoon.quest.databinding.ActivityMainBindingImpl;

import java.util.HashMap;

public class ActivityMain extends AppCompatActivity {

    private ActivityMain This = this;
    Adapter adapter;
    ActivityMainBinding mBinding;
    private FragmentAdd mFragmentAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(This, R.layout.activity_main);
        adapter = new Adapter();
        AppData.GetInstance().mDB = Room.databaseBuilder(This, LocalDatabase.class, "test-mDB")
                .build();
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
            adapter.submitList(dataList);
            adapter.notifyDataSetChanged();
        });

        mBinding.recycler.setLayoutManager(new LinearLayoutManager(This));
        mBinding.recycler.setHasFixedSize(true);
        mBinding.recycler.setItemAnimator(new DefaultItemAnimator());
        mBinding.recycler.setAdapter(adapter);

        mBinding.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFragmentAdd();
            }
        });
    }

    public void showFragmentAdd() {
        try {
            FragmentManager mFragmentManager = getSupportFragmentManager();
            FragmentTransaction mFragmentTransaction = mFragmentManager.beginTransaction();
            mFragmentAdd = new FragmentAdd();
            mFragmentAdd.setListener(new FragmentAdd.Listener() {
                @Override
                public void didRespond(Fragment fragment, String event, HashMap<String, String> data) {

                }
            });
            mBinding.mainFullFrame.setVisibility(View.VISIBLE);
            mFragmentTransaction.replace(R.id.mainFullFrame, mFragmentAdd);
            mFragmentTransaction.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        if (mFragmentAdd != null) {
            removeFragment(mFragmentAdd);
        }
    }

    private void removeFragment(Fragment fragment) {
        FragmentManager mFragmentManager = getSupportFragmentManager();
        FragmentTransaction mFragmentTransaction = mFragmentManager.beginTransaction();
        mFragmentTransaction.remove(fragment);
        mFragmentTransaction.commit();
        fragment.onDestroy();
        fragment.onDetach();
        fragment = null;
    }
}