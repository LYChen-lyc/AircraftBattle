package com.example.aircraftbattle.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.SurfaceHolder;

import com.example.aircraftbattle.MainActivity;
import com.example.aircraftbattle.R;
import com.example.aircraftbattle.constant.ConstantUtil;
import com.example.aircraftbattle.sounds.GameSoundPool;

/**
 * 游戏结束
 */
@SuppressLint("ViewConstructor")
public class EndView extends BaseView {
    private int score;
    private float button_x;
    private float button_y;
    private float button_y2;
    private float button_y3;
    private float strwid;
    private float strhei;
    private boolean isBtChange;                // ��ťͼƬ�ı�ı��
    private boolean isBtChange2;
    private boolean isBtChange3;
    private String startGame = "重新开始";    // ��ť������
    private String exitGame = "退出游戏";
    private String ranking = "排行榜";
    private Bitmap button;                    // ��ťͼƬ
    private Bitmap button2;                    // ��ťͼƬ
    private Bitmap button3;
    private Bitmap background;                // ����ͼƬ
    private Rect rect;                        // �������ֵ�����
    private MainActivity mainActivity;

    public EndView(Context context, GameSoundPool sounds) {
        super(context, sounds);
        this.mainActivity = (MainActivity) context;
        rect = new Rect();
        thread = new Thread(this);
    }

    @Override
    public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
        super.surfaceChanged(arg0, arg1, arg2, arg3);
    }

    @Override
    public void surfaceCreated(SurfaceHolder arg0) {
        super.surfaceCreated(arg0);
        initBitmap();
        if (thread.isAlive()) {
            thread.start();
        } else {
            thread = new Thread(this);
            thread.start();
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder arg0) {
        super.surfaceDestroyed(arg0);
        release();
    }

    /**
     * 触摸事件
     *
     * @param event
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN && event.getPointerCount() == 1) {
            float x = event.getX();
            float y = event.getY();

            if (x > button_x && x < button_x + button.getWidth()
                    && y > button_y && y < button_y + button.getHeight()) {
                sounds.playSound(7, 0);
                isBtChange = true;
                drawSelf();
                mainActivity.getHandler().sendEmptyMessage(ConstantUtil.TO_MAIN_VIEW);
            } else if (x > button_x && x < button_x + button.getWidth()
                    && y > button_y2 && y < button_y2 + button.getHeight()) {
                sounds.playSound(7, 0);
                isBtChange2 = true;
                drawSelf();
                threadFlag = false;
                mainActivity.getHandler().sendEmptyMessage(ConstantUtil.END_GAME);
            } else if (x > button_x && x < button_x + button.getWidth()
                    && y > button_y3 && y < button_y3 + button.getHeight()) {
                sounds.playSound(7, 0);
                isBtChange3 = true;
                drawSelf();
                threadFlag = false;
                mainActivity.getHandler().sendEmptyMessage(ConstantUtil.TO_RANKING_VIEW);
            }
            return true;
        } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
            float x = event.getX();
            float y = event.getY();
            isBtChange = x > button_x && x < button_x + button.getWidth() && y > button_y && y < button_y + button.getHeight();
            isBtChange2 = x > button_x && x < button_x + button.getWidth() && y > button_y2 && y < button_y2 + button.getHeight();
            isBtChange3 = x > button_x && x < button_x + button.getWidth() && y > button_y3 && y < button_y3 + button.getHeight();
            return true;
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            isBtChange = false;
            isBtChange2 = false;
            isBtChange3 = false;
            return true;
        }
        return false;
    }

    @Override
    public void initBitmap() {
        background = BitmapFactory.decodeResource(getResources(), R.drawable.bg_01);
        button = BitmapFactory.decodeResource(getResources(), R.drawable.button);
        button2 = BitmapFactory.decodeResource(getResources(), R.drawable.button2);
        button3 = BitmapFactory.decodeResource(getResources(), R.drawable.button);
        scalex = screen_width / background.getWidth();
        scaley = screen_height / background.getHeight();
        button_x = screen_width / 2 - button.getWidth() / 2;
        button_y = screen_height / 2 + button.getHeight();
        button_y2 = button_y + button.getHeight() + 40;
        button_y3 = button_y + button.getHeight() + 200;
        paint.setTextSize(40);
        paint.getTextBounds(startGame, 0, startGame.length(), rect);
        strwid = rect.width();
        strhei = rect.height();
    }

    @Override
    public void release() {
        if (!button.isRecycled()) {
            button.recycle();
        }
        if (!button2.isRecycled()) {
            button2.recycle();
        }
        if (!button3.isRecycled()) {
            button3.recycle();
        }
        if (!background.isRecycled()) {
            background.recycle();
        }
    }

    @Override
    public void drawSelf() {
        try {
            canvas = sfh.lockCanvas();
            //颜色
            canvas.drawColor(Color.BLACK);
            canvas.save();
            //缩放
            canvas.scale(scalex, scaley, 0, 0);
            //图片
            canvas.drawBitmap(background, 0, 0, paint);
            canvas.restore();
            if (isBtChange) {
                canvas.drawBitmap(button2, button_x, button_y, paint);
            } else {
                canvas.drawBitmap(button, button_x, button_y, paint);
            }
            if (isBtChange2) {
                canvas.drawBitmap(button2, button_x, button_y2, paint);
            } else {
                canvas.drawBitmap(button, button_x, button_y2, paint);
            }
            if (isBtChange3) {
                canvas.drawBitmap(button2, button_x, button_y3, paint);
            } else {
                canvas.drawBitmap(button, button_x, button_y3, paint);
            }
            paint.setTextSize(40);
            paint.getTextBounds(startGame, 0, startGame.length(), rect);
            canvas.drawText(startGame, screen_width / 2 - strwid / 2, button_y + button.getHeight() / 2 + strhei / 2, paint);
            canvas.drawText(exitGame, screen_width / 2 - strwid / 2, button_y2 + button.getHeight() / 2 + strhei / 2, paint);
            canvas.drawText(ranking, screen_width / 2 - strwid / 2, button_y3 + button.getHeight() / 2 + strhei / 2, paint);
            paint.setTextSize(60);
            float textlong = paint.measureText("总分:" + score);
            canvas.drawText("总分:" + score, screen_width / 2 - textlong / 2, screen_height / 2 - 100, paint);
        } catch (Exception err) {
            err.printStackTrace();
        } finally {
            if (canvas != null) {
                sfh.unlockCanvasAndPost(canvas);
            }
        }
    }

    @Override
    public void run() {
        while (threadFlag) {
            long startTime = System.currentTimeMillis();
            drawSelf();
            long endTime = System.currentTimeMillis();
            try {
                if (endTime - startTime < 400) {
                    Thread.sleep(400 - (endTime - startTime));
                }
            } catch (InterruptedException err) {
                err.printStackTrace();
            }
        }
    }

    public void setScore(int score) {
        this.score = score;
    }
}
