package com.yoon.quest;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.room.Room;

import com.yoon.quest.databinding.ActivityMainBinding;

public class ActivityMain extends AppCompatActivity {

    private ActivityMain This = this;
    private Adapter mAdapter;
    private ActivityMainBinding mBinding;
    private FragmentAdd mFragmentAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(This, R.layout.activity_main);
        mAdapter = new Adapter();
        mAdapter.setListener(new Adapter.Listener() {
            @Override
            public void eventRemoveItem(DataModel dataModel) {
                if (dataModel != null) {
                    new DaoAsyncTask(
                            AppData.GetInstance().mDB.dataModelDAO(),
                            Key.DELETE,
                            Integer.parseInt(String.valueOf(dataModel.getId())),
                            ""
                    ).execute();
                }
            }
        });
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
            mAdapter.submitList(dataList);
            mAdapter.notifyDataSetChanged();
        });

        mBinding.recycler.setLayoutManager(new LinearLayoutManager(This));
        mBinding.recycler.setHasFixedSize(true);
        mBinding.recycler.setItemAnimator(new DefaultItemAnimator());
        mBinding.recycler.setAdapter(mAdapter);

        // 스와이프 이벤트
        ItemTouchHelper mItemTouchHelper = new ItemTouchHelper(new ItemTouchHelperCallback(mAdapter));
        mItemTouchHelper.attachToRecyclerView(mBinding.recycler);

        // 당기기 이벤트
        mBinding.recycler.setListener(new SpringRecyclerView.Listener() {
            @Override
            public void eventAddWrite(String event) {
                if (event.equals("add")) {
                    Handler delayHandler = new Handler();
                    delayHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            showFragmentAdd();
                        }
                    }, 500);
                }
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
                public void eventBack(String event) {
                    if (event.equals("back")) {
                        if (This.getCurrentFocus() != null) {
                            hideSoftKeyboard(This);
                        }
                        removeFragment(mFragmentAdd);
                    }
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
            if (This.getCurrentFocus() != null) {
                hideSoftKeyboard(This);
            }
            removeFragment(mFragmentAdd);
        }
    }

    // 키보드 숨기기.
    private void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getSystemService(
                        Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(
                activity.getCurrentFocus().getWindowToken(), 0);
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