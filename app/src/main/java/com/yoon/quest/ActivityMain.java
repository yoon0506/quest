package com.yoon.quest;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.LiveData;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.room.Room;

import com.yoon.quest.databinding.ActivityMainBinding;

import me.everything.android.ui.overscroll.IOverScrollDecor;
import me.everything.android.ui.overscroll.IOverScrollUpdateListener;
import me.everything.android.ui.overscroll.VerticalOverScrollBounceEffectDecorator;
import me.everything.android.ui.overscroll.adapters.RecyclerViewOverScrollDecorAdapter;
import timber.log.Timber;

public class ActivityMain extends AppCompatActivity {

    private ActivityMain This = this;
    private Adapter mAdapter;
    private ActivityMainBinding mBinding;

    // fragment
    private FragmentAdd mFragmentAdd;
    private FragmentEdit mFragmentEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(This, R.layout.activity_main);
        mAdapter = new Adapter(This);
        mAdapter.setListener(new Adapter.Listener() {
            @Override
            public void eventRemoveItem(DataModel dataModel) {
                if (dataModel != null) {
                    new DaoAsyncTask(
                            AppData.GetInstance().mDB.dataModelDAO(),
                            Key.DELETE,
                            Integer.parseInt(String.valueOf(dataModel.getId())),
                            "", "",""
                    ).execute();
                }
            }

            @Override
            public void eventItemClick(DataModel dataModel) {
                if (dataModel != null) {
                    showFragmentEdit(dataModel);
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
            AppData.GetInstance().mDataCnt = dataList.size();
        });

        mBinding.recycler.setHasFixedSize(true);
        mBinding.recycler.setItemAnimator(new DefaultItemAnimator());
        mBinding.recycler.setAdapter(mAdapter);
        mBinding.recycler.setLayoutManager(new LinearLayoutManager(This));
        VerticalOverScrollBounceEffectDecorator decor = new VerticalOverScrollBounceEffectDecorator(new RecyclerViewOverScrollDecorAdapter(mBinding.recycler, new ItemTouchHelperCallback(mAdapter)));
        decor.setOverScrollUpdateListener((decor1, state, offset) -> {
            final View view = decor1.getView();
//                if (offset > 0) {
            if (offset > 150) {
                Timber.tag("checkCheck").d("offset : %s", offset);
                // 'view' is currently being over-scrolled from the top.
                Handler delayHandler = new Handler();
                delayHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (mFragmentAdd != null) {
                            Timber.tag("checkCheck").d("return");
                            return;
                        }
                        showFragmentAdd();
                    }
                }, 500);
            } else if (offset < 0) {
                // 'view' is currently being over-scrolled from the bottom.
            } else {
                // No over-scroll is in-effect.
                // This is synonymous with having (state == STATE_IDLE).
            }
        });
    }

    public void showFragmentAdd() {
        try {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(getResources().getColor(R.color.white));

            FragmentManager mFragmentManager = getSupportFragmentManager();
            FragmentTransaction mFragmentTransaction = mFragmentManager.beginTransaction();
            mFragmentAdd = new FragmentAdd();
            mFragmentAdd.setListener(() -> {
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                window.setStatusBarColor(getResources().getColor(R.color.lightGreen));

                if (This.getCurrentFocus() != null) {
                    hideSoftKeyboard(This);
                }
                hideFragmentAdd();
            });
            mBinding.mainFullFrame.setVisibility(View.VISIBLE);
            mFragmentTransaction.replace(R.id.mainFullFrame, mFragmentAdd);
            mFragmentTransaction.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showFragmentEdit(DataModel dataModel) {
        try {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(getResources().getColor(R.color.white));

            FragmentManager mFragmentManager = getSupportFragmentManager();
            FragmentTransaction mFragmentTransaction = mFragmentManager.beginTransaction();
            mFragmentEdit = new FragmentEdit();
            mFragmentEdit.setData(dataModel);
            mFragmentEdit.setListener(() -> {
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                window.setStatusBarColor(getResources().getColor(R.color.lightGreen));

                if (This.getCurrentFocus() != null) {
                    hideSoftKeyboard(This);
                }
                hideFragmentEdit();
            });
            mBinding.mainFullFrame.setVisibility(View.VISIBLE);
            mFragmentTransaction.replace(R.id.mainFullFrame, mFragmentEdit);
            mFragmentTransaction.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void hideFragmentAdd() {
        removeFragment(mFragmentAdd);
        mFragmentAdd = null;
    }

    private void hideFragmentEdit() {
        removeFragment(mFragmentEdit);
        mFragmentEdit = null;
    }

    private long mBackKeyPressedTime = 0;
    @Override
    public void onBackPressed() {
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(getResources().getColor(R.color.lightGreen));

        if (mFragmentAdd != null) {
            hideFragmentAdd();
            return;
        }
        if (mFragmentEdit != null) {
            hideFragmentEdit();
            return;
        }
        // 두 번 눌러 종료.
        if (System.currentTimeMillis() <= mBackKeyPressedTime + 3000) {
            exitApp();
        }
        if (System.currentTimeMillis() > mBackKeyPressedTime + 3000) {
            mBackKeyPressedTime = System.currentTimeMillis();
            Toast.makeText(This, "'뒤로가기' 버튼을 한 번 더 누르면 앱이 종료됩니다.", Toast.LENGTH_SHORT).show();
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

    private void exitApp() {
        Intent mmIntent = new Intent(This, ActivityLanding.class);
        mmIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        mmIntent.putExtra(Key.EVENT_APP_EXIT, true);
        startActivity(mmIntent);
    }


}