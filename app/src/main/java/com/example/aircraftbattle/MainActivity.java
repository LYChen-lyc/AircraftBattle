package com.example.aircraftbattle;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Instrumentation;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.blankj.utilcode.util.GsonUtils;
import com.example.aircraftbattle.constant.ConstantUtil;
import com.example.aircraftbattle.constant.DebugConstant;
import com.example.aircraftbattle.ranking.RankingActivity;
import com.example.aircraftbattle.ranking.RankingBean;
import com.example.aircraftbattle.ranking.SPUtils;
import com.example.aircraftbattle.sounds.GameSoundPool;
import com.example.aircraftbattle.view.EndView;
import com.example.aircraftbattle.view.MainView;
import com.example.aircraftbattle.view.ReadyView;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private EndView endView;
    private MainView mainView;
    private ReadyView readyView;
    private GameSoundPool sounds;
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == ConstantUtil.TO_MAIN_VIEW) {
                toMainView();
            } else if (msg.what == ConstantUtil.TO_END_VIEW) {
                Log.d("11223344", "游戏结束");
                toEndView(msg.arg1);
            } else if (msg.what == ConstantUtil.END_GAME) {
                endGame();
            } else if (msg.what == ConstantUtil.TO_RANKING_VIEW) {
                toRankingView();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        sounds = new GameSoundPool(this);
        sounds.initGameSound();
        readyView = new ReadyView(this, sounds);
        setContentView(readyView);
    }

    /**
     * 进入游戏界面
     */
    public void toMainView() {
        if (mainView == null) {
            mainView = new MainView(this, sounds);
        }
        setContentView(mainView);
        readyView = null;
        endView = null;
    }

    /**
     * 进入结束分数统计界面
     *
     * @param score
     */
    public void toEndView(int score) {
        if (endView == null) {
            Log.d("11223344", "游戏结束，获得的分数: " + score);
            endView = new EndView(this, sounds);
            endView.setScore(score);
            RankingBean bean = new RankingBean(score);
            Log.d("11223344", "上传的bean: " + GsonUtils.toJson(bean));
            List<RankingBean> rankingList = SPUtils.getInstance().getRankingList();
            Log.d("11223344", "保存前积分list: " + GsonUtils.toJson(SPUtils.getInstance().getRankingList()));
            rankingList.add(bean);
            SPUtils.getInstance().saveRankingList(rankingList);
            Log.d("11223344", "保存后积分list: " + GsonUtils.toJson(SPUtils.getInstance().getRankingList()));
        }
        setContentView(endView);
        mainView = null;
    }

    public void toRankingView() {
        Intent intent = new Intent(this, RankingActivity.class);
        startActivity(intent);
    }

    /**
     * 结束游戏
     */
    public void endGame() {
        if (readyView != null) {
            readyView.setThreadFlag(false);
        } else if (mainView != null) {
            mainView.setThreadFlag(false);
        } else if (endView != null) {
            endView.setThreadFlag(false);
        }
        this.finish();
    }

    public Handler getHandler() {
        return handler;
    }

    public void setHandler(Handler handler) {
        this.handler = handler;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /**
     * 双击退出函数
     */
    private long firstTime = 0;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (DebugConstant.DOUBLECLICK_EXIT) {
            if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
                if (System.currentTimeMillis() - firstTime > 2000) {
                    Toast.makeText(MainActivity.this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
                    firstTime = System.currentTimeMillis();
                } else {
                    finish();
                    System.exit(0);
                }
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }
}