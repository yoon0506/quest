package com.yoon.quest;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.CompoundButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.room.Room;

import com.yoon.quest.MainData.DaoAsyncTask;
import com.yoon.quest.MainData.DataModel;
import com.yoon.quest.SubData.SubDaoAsyncTask;
import com.yoon.quest.SubData.SubDataModel;
import com.yoon.quest.databinding.ActivityMainBinding;

import java.util.ArrayList;
import java.util.List;

import me.everything.android.ui.overscroll.VerticalOverScrollBounceEffectDecorator;
import me.everything.android.ui.overscroll.adapters.RecyclerViewOverScrollDecorAdapter;
import timber.log.Timber;

public class ActivityMain extends AppCompatActivity {

    private ActivityMain This = this;
    private Adapter mAdapter;
    private SubAdapter mSubAdapter;
    private ActivityMainBinding mBinding;

    // fragment
    private FragmentAdd mFragmentAdd;
    private FragmentEdit mFragmentEdit;

    private String mSelectedColor;
    // update, add, delete, select
    private String mDataEvent = "";
    private List<DataModel> mDataModelList = new ArrayList<>();
    private ArrayList<String> mSelectedColorList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppData.GetInstance().mActivity = This;
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
                            "", "", ""
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
        mSubAdapter = new SubAdapter(This);
//        mSubAdapter.setListener(new SubAdapter.Listener() {
//            @Override
//            public void eventRemoveItem(SubDataModel dataModel) {
//                if (dataModel != null) {
//                    new DaoAsyncTask(
//                            AppData.GetInstance().mDB.dataModelDAO(),
//                            Key.DELETE,
//                            Integer.parseInt(String.valueOf(dataModel.getId())),
//                            "", "", ""
//                    ).execute();
//                }
//            }
//
//            @Override
//            public void eventItemClick(SubDataModel dataModel) {
//                if (dataModel != null) {
//                    showFragmentEdit(dataModel);
//                }
//            }
//        });
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

        AppData.GetInstance().mDB.subdataModelDAO().getAll().observe(This, dataList -> {
            mSubAdapter.submitList(dataList);
            mSubAdapter.notifyDataSetChanged();
            AppData.GetInstance().mSubDataCnt = dataList.size();
            if(dataList.size() == 0 ){
                mBinding.recycler.setVisibility(View.VISIBLE);
                mBinding.subRecycler.setVisibility(View.GONE);
            }else{
                mBinding.recycler.setVisibility(View.GONE);
                mBinding.subRecycler.setVisibility(View.VISIBLE);
            }
        });

        mBinding.recycler.setHasFixedSize(true);
        mBinding.recycler.setItemAnimator(new DefaultItemAnimator());
        mBinding.recycler.setAdapter(mAdapter);
        mBinding.recycler.setLayoutManager(new LinearLayoutManager(This));
        mBinding.recycler.addItemDecoration(new DividerItemDecoration(This, DividerItemDecoration.VERTICAL));

        // 아래로 끌어당겨 데이터 추가
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

        // for sub
        mBinding.subRecycler.setHasFixedSize(true);
        mBinding.subRecycler.setItemAnimator(new DefaultItemAnimator());
        mBinding.subRecycler.setAdapter(mSubAdapter);
        mBinding.subRecycler.setLayoutManager(new LinearLayoutManager(This));
        mBinding.subRecycler.addItemDecoration(new DividerItemDecoration(This, DividerItemDecoration.VERTICAL));


        // fold button
        mBinding.foldButton.setOnClickListener(v -> {
            clickFoldBtn();
        });

        // unfold button
        mBinding.unFoldButton.setOnClickListener(v -> {
            clickUnFoldBtn();
        });

        // 토글 컬러 color
        mBinding.colorBtn1.setOnCheckedChangeListener(mColorClickListener);
        mBinding.colorBtn2.setOnCheckedChangeListener(mColorClickListener);
        mBinding.colorBtn3.setOnCheckedChangeListener(mColorClickListener);
        mBinding.colorBtn4.setOnCheckedChangeListener(mColorClickListener);
        mBinding.colorBtn5.setOnCheckedChangeListener(mColorClickListener);
        mBinding.colorBtn6.setOnCheckedChangeListener(mColorClickListener);
        mBinding.colorBtn7.setOnCheckedChangeListener(mColorClickListener);
        mBinding.colorBtn8.setOnCheckedChangeListener(mColorClickListener);
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
            mFragmentAdd.setListener(new FragmentAdd.Listener() {
                @Override
                public void eventBack() {
                    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                    window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                    window.setStatusBarColor(getResources().getColor(R.color.lightGreen));

                    if (This.getCurrentFocus() != null) {
                        hideSoftKeyboard(This);
                    }
                    hideFragmentAdd();
                }

                @Override
                public void eventInsertDone() {
                    mDataEvent = Key.INSERT;
                    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                    window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                    window.setStatusBarColor(getResources().getColor(R.color.lightGreen));

                    if (This.getCurrentFocus() != null) {
                        hideSoftKeyboard(This);
                    }
                    hideFragmentAdd();
                }

                @Override
                public void eventLayoutDone() {
                    if (mBinding.foldView.getVisibility() == View.VISIBLE) {
                        clickUnFoldBtn();
                    }
                }
            });
            mBinding.mainFullFrame.setVisibility(View.VISIBLE);
            mFragmentTransaction.replace(R.id.main_full_frame, mFragmentAdd);
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
            mFragmentEdit.setListener(new FragmentEdit.Listener() {
                @Override
                public void eventBack() {
                    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                    window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                    window.setStatusBarColor(getResources().getColor(R.color.lightGreen));

                    if (This.getCurrentFocus() != null) {
                        hideSoftKeyboard(This);
                    }
                    hideFragmentEdit();
                }

                @Override
                public void eventLayoutDone() {
                    if (mBinding.foldView.getVisibility() == View.VISIBLE) {
                        clickUnFoldBtn();
                    }
                }
            });
            mBinding.mainFullFrame.setVisibility(View.VISIBLE);
            mFragmentTransaction.replace(R.id.main_full_frame, mFragmentEdit);
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

    private void clickFoldBtn() {
        mBinding.foldButton.setVisibility(View.GONE);
        mBinding.foldView.setVisibility(View.VISIBLE);
        Animation mmSlideUp = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.slide_up);
        mBinding.foldView.startAnimation(mmSlideUp);
    }

    private void clickUnFoldBtn() {
        Animation mmSlideDown = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.slide_down);
        mBinding.foldView.startAnimation(mmSlideDown);
        mBinding.foldView.setVisibility(View.GONE);
        Handler delayHandler = new Handler();
        delayHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mBinding.foldButton.setVisibility(View.VISIBLE);
            }
        }, 500);
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

        // fold view 닫기.
        if (mBinding.foldView.getVisibility() == View.VISIBLE) {
            clickUnFoldBtn();
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

    private CompoundButton.OnCheckedChangeListener mColorClickListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (buttonView != null) {
                mSelectedColor = setBlockColor(buttonView);
                if (!mSelectedColorList.contains(mSelectedColor)) {
                    mSelectedColorList.add(mSelectedColor);
                    buttonView.setChecked(true);
                    selectColor(mSelectedColor);
                } else {
                    mSelectedColorList.remove(mSelectedColor);
                    buttonView.setChecked(false);
                    cancelColor(mSelectedColor);
                }
            }

            Timber.tag("checkCheck").d("선택한 색갈 : %s", mSelectedColor);
            for (String color : mSelectedColorList) {
                Timber.tag("checkCheck").d("선택한 색갈 리스트 : %s", color);
            }
            if(mSelectedColorList.size()== 0){
                mBinding.recycler.setVisibility(View.VISIBLE);
                mBinding.subRecycler.setVisibility(View.GONE);
            }
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

    private void selectColor(String color) {
        new SubDaoAsyncTask(
                AppData.GetInstance().mDB.subdataModelDAO(),
                Key.SELECT,
                -1,
                "",
                "",
                color
        ).execute();
        mBinding.recycler.setVisibility(View.GONE);
        mBinding.subRecycler.setVisibility(View.VISIBLE);
    }

    private void cancelColor(String color) {
        mDataEvent = Key.DELETE;
        new SubDaoAsyncTask(
                AppData.GetInstance().mDB.subdataModelDAO(),
                Key.DELETE,
                -1,
                "",
                "",
                color
        ).execute();
//
//        AppData.GetInstance().mDB.dataModelDAO().getDataPickedColor(color).observe(This, dataModels -> {
//            if (mSelectedColorList.size() == 0) {
//                mDataModelList.clear();
//                mDataModelList = null;
//                mDataModelList = new ArrayList<>();
//                mDataModelList.addAll(AppData.GetInstance().mAllDataModelList);
//            } else {
//                List<DataModel> mmRemoveList = new ArrayList<>();
//                List<DataModel> mmTempList = new ArrayList<>();
//                mmTempList.addAll(mDataModelList);
//                for (DataModel dataModel : mmTempList) {
//                    for (DataModel compareModel : dataModels) {
//                        if (dataModel.equals(compareModel)) {
//                            mmRemoveList.add(dataModel);
//                        }
//                    }
//                }
//
//                if (mmRemoveList.size() > 0) {
//                    for (DataModel dataModel : mmRemoveList) {
//                        mmTempList.remove(dataModel);
//                    }
//                }
//                mDataModelList.clear();
//                mDataModelList = null;
//                mDataModelList = new ArrayList<>();
//                mDataModelList.addAll(mmTempList);
//            }
//            AppData.GetInstance().mDataCnt = mDataModelList.size();
//            mAdapter.submitList(mDataModelList);
//            mAdapter.notifyDataSetChanged();
//        });
    }

    private void exitApp() {
        Intent mmIntent = new Intent(This, ActivityLanding.class);
        mmIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        mmIntent.putExtra(Key.EVENT_APP_EXIT, true);
        startActivity(mmIntent);
    }
}