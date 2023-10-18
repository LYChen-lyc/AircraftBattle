package com.example.aircraftbattle.ranking;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.blankj.utilcode.util.GsonUtils;
import com.blankj.utilcode.util.LogUtils;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by RAYn on 2023.02.03 16:55.
 */
public class SPUtils {
    private final SharedPreferences mSharedPreferences;
    private static final SPUtils INSTANCE = new SPUtils(MyApplication.getInstance());

    public SPUtils(Context context) {
        mSharedPreferences = context.getSharedPreferences("config", Context.MODE_PRIVATE);
    }

    public static SPUtils getInstance() {
        return INSTANCE;
    }

    public void saveRankingList(List<RankingBean> list) {
        String settings = GsonUtils.toJson(list);
        mSharedPreferences.edit().putString("settings", settings).apply();
    }

    public List<RankingBean> getRankingList() {
        List<RankingBean> list = new ArrayList<>();
        try {
            String settings = mSharedPreferences.getString("settings", "");
            if (TextUtils.isEmpty(settings)) {
                return list;
            }
            list = new Gson().fromJson(settings, new TypeToken<List<RankingBean>>() {
            }.getType());
        } catch (JsonSyntaxException e) {
            LogUtils.d(e);
        }
        return list;
    }
}
