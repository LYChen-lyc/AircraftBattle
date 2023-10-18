package com.example.aircraftbattle.ranking;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.blankj.utilcode.util.GsonUtils;
import com.example.aircraftbattle.MainActivity;
import com.example.aircraftbattle.R;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class RankingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ranking);
        RecyclerView rvRanking = findViewById(R.id.rv_ranking);
        LinearLayoutManager manager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        RankingAdapter adapter = new RankingAdapter();
        rvRanking.setLayoutManager(manager);
        rvRanking.setAdapter(adapter);
        List<RankingBean> rankingList = SPUtils.getInstance().getRankingList();
        Log.d("11223344", "onCreate: " + GsonUtils.toJson(rankingList));
        Collections.sort(rankingList, new Comparator<RankingBean>() {
            @Override
            public int compare(RankingBean o1, RankingBean o2) {
                return o2.getScore() - o1.getScore();
            }

            @Override
            public boolean equals(Object obj) {
                return false;
            }
        });
        adapter.setList(rankingList);
        Button btnRestart = findViewById(R.id.btn_restart);
        btnRestart.setOnClickListener(v -> {
            Intent intent = new Intent(RankingActivity.this, MainActivity.class);
            startActivity(intent);
        });
    }
}