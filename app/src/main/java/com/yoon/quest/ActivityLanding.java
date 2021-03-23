package com.yoon.quest;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.yoon.quest.databinding.ActivityLandingBinding;

import timber.log.Timber;

public class ActivityLanding extends AppCompatActivity {
    private ActivityLanding This = this;
    private ActivityLandingBinding mBinding;
    private AnimationDrawable mAnimDrawable;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(This, R.layout.activity_landing);
        Timber.plant(new Timber.DebugTree());
        Handler delayHandler = new Handler();
        delayHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                goToActivityMain();
            }
        }, 2000);
//        mAnimDrawable = new AnimationDrawable();
//        mAnimDrawable.addFrame(getResources().getDrawable(R.mipmap.frog), 500);
//        mAnimDrawable.addFrame(getResources().getDrawable(R.mipmap.frog_hands_up), 500);
//        mAnimDrawable.addFrame(getResources().getDrawable(R.mipmap.frog), 500);
//        mAnimDrawable.addFrame(getResources().getDrawable(R.mipmap.frog_hands_up), 500);
//        mAnimDrawable.setOneShot(true);
//        mBinding.frogImage.setImageDrawable(mAnimDrawable);
//        mAnimDrawable.start();

        final AnimationDrawable mmAnimation = (AnimationDrawable) mBinding.frogImage.getBackground();
        mBinding.frogImage.post(new Runnable() {
            @Override
            public void run() {
                mmAnimation.start();
            }
        });

        // 종료
        if (getIntent().getBooleanExtra(Key.EVENT_APP_EXIT, false)) {
            this.finishAndRemoveTask();
            System.exit(0);
        }
    }

    private void goToActivityMain() {

        Intent mmIntent = new Intent(This, ActivityMain.class);
        startActivity(mmIntent);
    }
}