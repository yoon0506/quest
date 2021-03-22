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

import java.util.HashMap;

import me.everything.android.ui.overscroll.IOverScrollDecor;
import me.everything.android.ui.overscroll.IOverScrollStateListener;
import me.everything.android.ui.overscroll.IOverScrollUpdateListener;
import me.everything.android.ui.overscroll.OverScrollDecoratorHelper;
import me.everything.android.ui.overscroll.VerticalOverScrollBounceEffectDecorator;
import me.everything.android.ui.overscroll.adapters.RecyclerViewOverScrollDecorAdapter;
import timber.log.Timber;

import static com.google.android.material.behavior.SwipeDismissBehavior.STATE_IDLE;
import static me.everything.android.ui.overscroll.IOverScrollState.STATE_BOUNCE_BACK;
import static me.everything.android.ui.overscroll.IOverScrollState.STATE_DRAG_END_SIDE;
import static me.everything.android.ui.overscroll.IOverScrollState.STATE_DRAG_START_SIDE;

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
                            "",""
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

        mBinding.recycler.setHasFixedSize(true);
        mBinding.recycler.setItemAnimator(new DefaultItemAnimator());
        mBinding.recycler.setAdapter(mAdapter);
        mBinding.recycler.setLayoutManager(new LinearLayoutManager(This));
        VerticalOverScrollBounceEffectDecorator decor = new VerticalOverScrollBounceEffectDecorator(new RecyclerViewOverScrollDecorAdapter(mBinding.recycler, new ItemTouchHelperCallback(mAdapter)));
        decor.setOverScrollUpdateListener(new IOverScrollUpdateListener() {
            @Override
            public void onOverScrollUpdate(IOverScrollDecor decor, int state, float offset) {
                final View view = decor.getView();
                if (offset > 0) {
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
                public void eventBack() {
                    if (This.getCurrentFocus() != null) {
                        hideSoftKeyboard(This);
                    }
                    hideFragmentAdd();
                }
            });
            mBinding.mainFullFrame.setVisibility(View.VISIBLE);
            mFragmentTransaction.replace(R.id.mainFullFrame, mFragmentAdd);
            mFragmentTransaction.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void hideFragmentAdd() {
        removeFragment(mFragmentAdd);
        mFragmentAdd = null;
    }

    @Override
    public void onBackPressed() {
        if (mFragmentAdd != null) {
            hideFragmentAdd();
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