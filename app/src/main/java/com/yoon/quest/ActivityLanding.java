package com.yoon.quest;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import timber.log.Timber;

public class ActivityLanding extends AppCompatActivity {
    private ActivityLanding This = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing);
        Timber.plant(new Timber.DebugTree());
        goToActivityMain();
    }

    private void goToActivityMain() {
        Intent mmIntent = new Intent(This, ActivityMain.class);
        startActivity(mmIntent);
    }
}