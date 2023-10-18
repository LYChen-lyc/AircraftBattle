package com.example.aircraftbattle.ranking;

import android.app.Application;
import android.content.Context;

/**
 * Created by RAYn on 2023.09.05 14:12.
 */
public class MyApplication extends Application {
    private static MyApplication instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }

    public static Context getInstance() {
        return instance;
    }
}
