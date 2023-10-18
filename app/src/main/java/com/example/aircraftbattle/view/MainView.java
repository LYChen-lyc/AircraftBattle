package com.example.aircraftbattle.view;

import static android.content.Context.SENSOR_SERVICE;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;

import com.example.aircraftbattle.DirectionEvent;
import com.example.aircraftbattle.R;
import com.example.aircraftbattle.constant.ConstantUtil;
import com.example.aircraftbattle.constant.DebugConstant;
import com.example.aircraftbattle.constant.GameConstant;
import com.example.aircraftbattle.factory.GameObjectFactory;
import com.example.aircraftbattle.object.GameObject;
import com.example.aircraftbattle.object.LifeGoods;
import com.example.aircraftbattle.object.MissileGoods;
import com.example.aircraftbattle.object.PurpleBulletGoods;
import com.example.aircraftbattle.object.RedBulletGoods;
import com.example.aircraftbattle.plane.BigPlane;
import com.example.aircraftbattle.plane.BossPlane;
import com.example.aircraftbattle.plane.EnemyPlane;
import com.example.aircraftbattle.plane.MiddlePlane;
import com.example.aircraftbattle.plane.MyPlane;
import com.example.aircraftbattle.plane.SmallPlane;
import com.example.aircraftbattle.sounds.GameSoundPool;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

/**
 * 游戏进行的主界面
 */
@SuppressLint("ViewConstructor")
public class MainView extends BaseView implements SensorEventListener {

    private Handler mHandler = new Handler();
    private int missileCount; // 导弹的数量
    private int middlePlaneScore; // 中型敌机的积分
    private int bigPlaneScore; // 大型敌机的积分
    private int bossPlaneScore; // boss型敌机的积分
    private int missileScore; // 导弹的积分
    private int lifeScore; // 生命的积分
    private int bulletScore; // 子弹的积分
    private int bulletScore2; // 子弹2的积分
    private int sumScore; // 游戏总得分
    private static int speedTime; // 游戏速度的倍数
    private float bg_y; // 图片的坐标
    private float bg_y2;
    private float play_bt_w;
    private float play_bt_h;
    private float missile_bt_y;
    private boolean isPlay; // 标记游戏运行状态
    private boolean isTouchPlane; // 判断玩家是否按下屏幕

    private Bitmap background; // 背景图片
    private Bitmap background2; // 背景图片
    private Bitmap playButton; // 开始/暂停游戏的按钮图片
    private Bitmap missile_bt; // 导弹按钮图标
    private Bitmap life_amount;// 生命总数图标
    private Bitmap boom;// 爆炸效果图
    private Bitmap plane_shield;// 防护盾效果图

    private MyPlane myPlane; // 玩家的飞机
    private BossPlane bossPlane; // boss飞机
    private List<EnemyPlane> enemyPlanes;
    private MissileGoods missileGoods;
    private LifeGoods lifeGoods; // 生命物品
    private PurpleBulletGoods purpleBulletGoods;
    private RedBulletGoods redBulletGoods; // 子弹2

    private int mLifeAmount;// 生命总数
    private GameObjectFactory factory;
    private MediaPlayer mMediaPlayer; // 用来实现背景音乐播放

    private List<BigPlane> bigPlanes;// 大型机集合,用于实现子弹的遍历

    private int bossAppearAgain_score;//boss重新出现需要的积分
    /**
     * 加速度传感器数据
     */
    private float[] accValues = new float[3];
    /**
     * 地磁传感器数据
     */
    private float[] magValues = new float[3];
    /**
     * 旋转矩阵，用来保存磁场和加速度的数据
     */
    private final float[] r = new float[9];
    /**
     * 模拟方向传感器的数据（原始数据为弧度）
     */
    private final float[] values = new float[3];
    private SensorManager mSensorManager;
    private Sensor mMSensor;
    private Sensor mNSensor;
    public static final int ACTION_DOWN = 0;
    public static final int ACTION_UP = 1;
    public static final int ACTION_LEFT = 3;
    public static final int ACTION_RIGHT = 4;
    private int DIRECTION;
    public int TURN;

    public MainView(Context context, GameSoundPool sounds) {
        super(context, sounds);
//        if (!EventBus.getDefault().isRegistered(this)) {
//            EventBus.getDefault().register(this);
//        }
        mSensorManager = (SensorManager) mainActivity.getSystemService(SENSOR_SERVICE);
        mMSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        mNSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        isPlay = true;

        speedTime = GameConstant.GAMESPEED;
        mLifeAmount = GameConstant.LIFEAMOUNT;// 初始生命值
        missileCount = GameConstant.MISSILECOUNT;// 初始导弹数

        // 背景音乐
        mMediaPlayer = MediaPlayer.create(mainActivity, R.raw.game);
        mMediaPlayer.setLooping(true);
        if (!mMediaPlayer.isPlaying()) {
            mMediaPlayer.start();
        }

        factory = new GameObjectFactory(); // 工厂类
        bigPlanes = new ArrayList<BigPlane>(); // 大型机集合
        enemyPlanes = new ArrayList<EnemyPlane>();// 敌机集合
        myPlane = (MyPlane) factory.createMyPlane(getResources());// 生产玩家的飞机
        myPlane.setMainView(this);

        for (int i = 0; i < SmallPlane.sumCount; i++) {
            // 生产小型敌机
            SmallPlane smallPlane = (SmallPlane) factory
                    .createSmallPlane(getResources());
            enemyPlanes.add(smallPlane);
        }
        for (int i = 0; i < MiddlePlane.sumCount; i++) {
            // 生产中型敌机
            MiddlePlane middlePlane = (MiddlePlane) factory
                    .createMiddlePlane(getResources());
            enemyPlanes.add(middlePlane);
        }
        for (int i = 0; i < BigPlane.sumCount; i++) {
            BigPlane bigPlane = (BigPlane) factory
                    .createBigPlane(getResources());
            enemyPlanes.add(bigPlane);
            bigPlane.setMyPlane(myPlane);

            bigPlanes.add(bigPlane);
        }
        // 生产BOSS敌机
        bossPlane = (BossPlane) factory.createBossPlane(getResources());
        bossPlane.setMyPlane(myPlane);
        enemyPlanes.add(bossPlane);
        // 生产导弹物品
        missileGoods = (MissileGoods) factory
                .createMissileGoods(getResources());
        // 生产生命物品
        lifeGoods = (LifeGoods) factory.createLifeGoods(getResources());
        // 生产子弹物品
        purpleBulletGoods = (PurpleBulletGoods) factory
                .createPurpleBulletGoods(getResources());
        redBulletGoods = (RedBulletGoods) factory
                .createRedBulletGoods(getResources());
        thread = new Thread(this);
    }

    // 视图改变的方法
    @Override
    public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
        super.surfaceChanged(arg0, arg1, arg2, arg3);
    }

    // 视图创建的方法
    @Override
    public void surfaceCreated(SurfaceHolder arg0) {
        super.surfaceCreated(arg0);
        initBitmap(); // 初始化图片资源
        for (GameObject obj : enemyPlanes) {
            obj.setScreenWH(screen_width, screen_height);
        }
        missileGoods.setScreenWH(screen_width, screen_height);
        lifeGoods.setScreenWH(screen_width, screen_height);

        purpleBulletGoods.setScreenWH(screen_width, screen_height);
        redBulletGoods.setScreenWH(screen_width, screen_height);

        myPlane.setScreenWH(screen_width, screen_height);
        myPlane.setAlive(true);
        if (thread.isAlive()) {
            thread.start();
        } else {
            thread = new Thread(this);
            thread.start();
        }
    }

    // 视图销毁的方法
    @Override
    public void surfaceDestroyed(SurfaceHolder arg0) {
        super.surfaceDestroyed(arg0);
        release();// 释放资源
        mMediaPlayer.stop();
    }

    // 初始化图片资源方法
    @Override
    public void initBitmap() {
        playButton = BitmapFactory.decodeResource(getResources(),
                R.drawable.play);
        background = BitmapFactory.decodeResource(getResources(),
                R.drawable.bg_01);
        background2 = BitmapFactory.decodeResource(getResources(),
                R.drawable.bg_02);
        missile_bt = BitmapFactory.decodeResource(getResources(),
                R.drawable.missile_bt);

        life_amount = BitmapFactory.decodeResource(getResources(),
                R.drawable.life_amount);

        boom = BitmapFactory.decodeResource(getResources(), R.drawable.boom);
        plane_shield = BitmapFactory.decodeResource(getResources(), R.drawable.plane_shield);

        scalex = screen_width / background.getWidth();
        scaley = screen_height / background.getHeight();
        play_bt_w = playButton.getWidth();
        play_bt_h = playButton.getHeight() / 2;
        bg_y = 0;
        bg_y2 = bg_y - screen_height;
        missile_bt_y = screen_height - 10 - missile_bt.getHeight();

    }

    // 初始化游戏对象
    public void initObject() {
        for (EnemyPlane obj : enemyPlanes) {
            // 初始化小型敌机
            if (obj instanceof SmallPlane) {
                if (!obj.isAlive()) {
                    obj.initial(speedTime, 0, 0);
                    break;
                }
            }
            // 初始化中型敌机
            else if (obj instanceof MiddlePlane) {
                if (middlePlaneScore >= GameConstant.MIDDLEPLANE_APPEARSCORE) {
                    if (!obj.isAlive()) {
                        obj.initial(speedTime, 0, 0);
                        break;
                    }
                }
            }
            // 初始化大型敌机
            else if (obj instanceof BigPlane) {
                if (bigPlaneScore >= GameConstant.BIGPLANE_APPEARSCORE) {
                    if (!obj.isAlive()) {
                        obj.initial(speedTime, 0, 0);
                        break;
                    }
                }
            }
            // 初始化BOSS敌机
            else {
                if (bossPlaneScore >= GameConstant.BOSSPLANE_APPEARSCORE) {
                    if (!obj.isAlive()) {
                        obj.initial(speedTime, 0, 0);
                        bossPlaneScore = 0;
                        break;
                    }
                }
            }
        }

        // 初始化导弹物品
        if (missileScore >= GameConstant.MISSILE_APPEARSCORE) {
            if (!missileGoods.isAlive()) {
                missileScore = 0;
                if (DebugConstant.MISSILEGOODS_APPEAR) {
                    missileGoods.initial(0, 0, 0);
                }
            }
        }

        // 初始化生命物品
        if (lifeScore >= GameConstant.LIFE_APPEARSCORE) {
            if (!lifeGoods.isAlive()) {
                lifeScore = 0;
                if (DebugConstant.LIFEGOODS_APPEAR) {
                    lifeGoods.initial(0, 0, 0);
                }
            }
        }
        // 初始化子弹1物品
        if (bulletScore >= GameConstant.BULLET1_APPEARSCORE) {
            if (!purpleBulletGoods.isAlive()) {
                bulletScore = 0;
                if (DebugConstant.BULLETGOODS1_APPEAR) {
                    purpleBulletGoods.initial(0, 0, 0);
                }
            }
        }
        // 初始化子弹2物品
        if (bulletScore2 >= GameConstant.BULLET2_APPEARSCORE) {
            if (!redBulletGoods.isAlive()) {
                bulletScore2 = 0;
                if (DebugConstant.BULLETGOODS2_APPEAR) {
                    redBulletGoods.initial(0, 0, 0);
                }
            }
        }

        // 初始化BOSS飞机的子弹
        if (bossPlane.isAlive()) {
            if (!myPlane.getMissileState()) {
                bossPlane.initBullet();
            }
        }

        // 初始化bigPlane的子弹，遍历所有大型机
        for (BigPlane big_plane : bigPlanes) {
            if (big_plane.isAlive()) {
                if (!myPlane.getMissileState()) {
                    big_plane.initBullet();
                }
            }
        }

        myPlane.isBulletOverTime();
        myPlane.initBullet(); // 初始化玩家飞机的子弹
        // 提升等级
        if (sumScore >= speedTime * GameConstant.LEVELUP_SCORE && speedTime < GameConstant.MAXGRADE) {
            speedTime++;
        }
    }

    // 释放图片资源的方法
    @Override
    public void release() {
        for (GameObject obj : enemyPlanes) {
            obj.release();
        }

        myPlane.release();
        missileGoods.release();
        lifeGoods.release();
        purpleBulletGoods.release();
        redBulletGoods.release();

        if (!playButton.isRecycled()) {
            playButton.recycle();
        }
        if (!background.isRecycled()) {
            background.recycle();
        }
        if (!background2.isRecycled()) {
            background2.recycle();
        }
        if (!missile_bt.isRecycled()) {
            missile_bt.recycle();
        }
        if (!life_amount.isRecycled()) {
            life_amount.recycle();
        }
        if (!boom.isRecycled()) {
            boom.recycle();
        }
        if (!plane_shield.isRecycled()) {
            plane_shield.recycle();
        }
    }

    // 绘图方法
    @Override
    public void drawSelf() {
        try {
            canvas = sfh.lockCanvas();
            canvas.drawColor(Color.BLACK); // 绘制背景色
            canvas.save();
            // 计算背景图片与屏幕的比例
            canvas.scale(scalex, scaley, 0, 0);
            canvas.drawBitmap(background, 0, bg_y, paint); // 绘制背景图
            canvas.drawBitmap(background2, 0, bg_y2, paint); // 绘制背景图
            canvas.restore();
            // 绘制按钮
            canvas.save();
            canvas.clipRect(10, 10, 10 + play_bt_w, 10 + play_bt_h);
            if (isPlay) {
                canvas.drawBitmap(playButton, 10, 10, paint);
            } else {
                canvas.drawBitmap(playButton, 10, 10 - play_bt_h, paint);
            }
            canvas.restore();

            // 绘制积分文字
            paint.setTextSize(40);
            paint.setColor(Color.rgb(235, 161, 1));
            canvas.drawText("积分:" + String.valueOf(sumScore), 30 + play_bt_w,
                    50, paint);
            // 绘制等级
            canvas.drawText("等级 X " + String.valueOf(speedTime),
                    screen_width - 160, 50, paint);
            // 绘制生命数值
            if (mLifeAmount > 0) {
                paint.setColor(Color.BLACK);
                canvas.drawBitmap(life_amount, screen_width - 150,
                        screen_height - life_amount.getHeight() - 10, paint);
                canvas.drawText("X " + String.valueOf(mLifeAmount),
                        screen_width - life_amount.getWidth(),
                        screen_height - 25, paint);
            }

            // 绘制爆炸效果图
            if (myPlane.getMissileState()) {
                float boom_x = myPlane.getMiddle_x() - boom.getWidth() / 2;
                float boom_y = myPlane.getMiddle_y() - boom.getHeight() / 2;

                canvas.drawBitmap(boom, boom_x, boom_y, paint);

            }

            // 绘制无敌防护效果图
            if (myPlane.isInvincible() && !myPlane.getDamaged()) {
                float plane_shield_x = myPlane.getMiddle_x() - plane_shield.getWidth() / 2;
                float plane_shield_y = myPlane.getMiddle_y() - plane_shield.getHeight() / 2;

                canvas.drawBitmap(plane_shield, plane_shield_x, plane_shield_y, paint);

            }

            // 绘制导弹按钮
            if (missileCount > 0) {
                paint.setTextSize(40);
                paint.setColor(Color.BLACK);
                canvas.drawBitmap(missile_bt, 10, missile_bt_y, paint);
                canvas.drawText("X " + missileCount,
                        10 + missile_bt.getWidth(), screen_height - 25, paint);// 绘制文字
            }

            // 绘制导弹物品
            if (missileGoods.isAlive()) {
                if (missileGoods.isCollide(myPlane)) {
                    if (missileCount < GameConstant.MISSILE_MAXCOUNT) {
                        missileCount++;
                    }
                    missileGoods.setAlive(false);
                    sounds.playSound(6, 0);
                } else {
                    missileGoods.drawSelf(canvas);
                }
            }
            // 绘制生命物品
            if (lifeGoods.isAlive()) {
                if (lifeGoods.isCollide(myPlane)) {
                    if (mLifeAmount < GameConstant.LIFE_MAXCOUNT) {
                        mLifeAmount++;
                    }
                    lifeGoods.setAlive(false);
                    sounds.playSound(6, 0);
                } else {
                    lifeGoods.drawSelf(canvas);
                }
            }
            // 绘制子弹物品
            if (purpleBulletGoods.isAlive()) {
                if (purpleBulletGoods.isCollide(myPlane)) {
                    purpleBulletGoods.setAlive(false);
                    sounds.playSound(6, 0);

                    myPlane.setChangeBullet(true);
                    myPlane.changeBullet(ConstantUtil.MYBULLET1);
                    myPlane.setStartTime(System.currentTimeMillis());

                } else {
                    purpleBulletGoods.drawSelf(canvas);
                }
            }
            // 绘制子弹2物品
            if (redBulletGoods.isAlive()) {
                if (redBulletGoods.isCollide(myPlane)) {
                    redBulletGoods.setAlive(false);
                    sounds.playSound(6, 0);

                    myPlane.setChangeBullet(true);
                    myPlane.changeBullet(ConstantUtil.MYBULLET2);
                    myPlane.setStartTime(System.currentTimeMillis());

                } else {
                    redBulletGoods.drawSelf(canvas);
                }
            }

            // 绘制敌机
            for (EnemyPlane obj : enemyPlanes) {
                if (obj.isAlive()) {
                    obj.drawSelf(canvas);
                    // 检测敌机是否与玩家的飞机碰撞
                    if (obj.isCanCollide() && myPlane.isAlive()) {
                        // 检测我方是否处于无敌状态或者导弹爆炸状态
                        if (obj.isCollide(myPlane) && !myPlane.isInvincible()
                                && !myPlane.getMissileState()) {
                            myPlane.setAlive(false);
                        }
                    }
                }
            }
            if (!myPlane.isAlive()) {
                sounds.playSound(4, 0); // 飞机炸毁的音效

                // 判断生命总数，数值大于零则-1，直到生命总数小于零，Gameover
                if (mLifeAmount > 0) {
                    mLifeAmount--;
                    myPlane.setAlive(true);
                    new Thread(new Runnable() {

                        @Override
                        public void run() {
                            myPlane.setDamaged(true);
                            myPlane.setInvincibleTime(GameConstant.BOOM_TIME);
                            myPlane.setDamaged(false);
                            myPlane.setInvincibleTime(GameConstant.INVINCIBLE_TIME);
                        }
                    }).start();

                } else {
                    if (DebugConstant.ETERNAL) {
                        // 设置不死亡，供游戏测试使用
                        threadFlag = true;
                        myPlane.setAlive(true);

                        // 继续实现机体受损及闪光效果
                        new Thread(new Runnable() {

                            @Override
                            public void run() {
                                myPlane.setDamaged(true);
                                myPlane.setInvincibleTime(GameConstant.BOOM_TIME);
                                myPlane.setDamaged(false);
                                myPlane.setInvincibleTime(GameConstant.INVINCIBLE_TIME);
                            }
                        }).start();

                    } else {
                        // 正常情况，游戏结束,并停止音乐
                        mSensorManager.unregisterListener(this);
                        threadFlag = false;
//                        if (EventBus.getDefault().isRegistered(this)) {
//                            EventBus.getDefault().unregister(this);
//                        }
                        if (mMediaPlayer.isPlaying()) {
                            mMediaPlayer.stop();
                        }
                    }
                }
            }
            // 绘制玩家的飞机
            myPlane.drawSelf(canvas);
            myPlane.shoot(canvas, enemyPlanes);
            // 子弹音效
            sounds.playSound(1, 0);

        } catch (Exception err) {
            err.printStackTrace();
        } finally {
            if (canvas != null) {
                sfh.unlockCanvasAndPost(canvas);
            }
        }
    }

    // 背景移动的逻辑函数
    public void viewLogic() {
        if (bg_y > bg_y2) {
            bg_y += 10;
            bg_y2 = bg_y - background.getHeight();
        } else {
            bg_y2 += 10;
            bg_y = bg_y2 - background.getHeight();
        }
        if (bg_y >= background.getHeight()) {
            bg_y = bg_y2 - background.getHeight();
        } else if (bg_y2 >= background.getHeight()) {
            bg_y2 = bg_y - background.getHeight();
        }
    }

    // 增加游戏分数的方法
    public void addGameScore(int score) {
        middlePlaneScore += score; // 中型敌机的积分
        bigPlaneScore += score; // 大型敌机的积分
        bossPlaneScore += score; // boss型敌机的积分
        missileScore += score; // 导弹的积分
        lifeScore += score;// 生命的积分
        bulletScore += score; // 子弹的积分
        bulletScore2 += score; // 子弹的积分
        sumScore += score; // 游戏总得分

    }

    // 播放音效
    public void playSound(int key) {
        sounds.playSound(key, 0);
    }

    // 线程运行的方法
    @Override
    public void run() {
        while (threadFlag) {
            long startTime = System.currentTimeMillis();
            initObject();
            drawSelf();
            viewLogic(); // 背景移动的逻辑
            long endTime = System.currentTimeMillis();

            if (!isPlay) {
                mMediaPlayer.pause();// 音乐暂停
                mSensorManager.unregisterListener(this);
                synchronized (thread) {
                    try {
                        thread.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            } else {
                mSensorManager.registerListener(this, mMSensor, SensorManager.SENSOR_DELAY_UI);
                mSensorManager.registerListener(this, mNSensor, SensorManager.SENSOR_DELAY_UI);
                if (!mMediaPlayer.isPlaying()) {
                    mMediaPlayer.start();
                }
            }

            try {
                if (endTime - startTime < 100) {
                    Thread.sleep(100 - (endTime - startTime));
                }
            } catch (InterruptedException err) {
                err.printStackTrace();
            }
        }
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Message message = new Message();
        message.what = ConstantUtil.TO_END_VIEW;
        mSensorManager.unregisterListener(this);
        message.arg1 = sumScore;
        mainActivity.getHandler().sendMessage(message);
    }

    // 响应触屏事件的方法
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            isTouchPlane = false;
            return true;
        } else if (event.getAction() == MotionEvent.ACTION_DOWN) {
            float x = event.getX();
            float y = event.getY();
            if (x > 10 && x < 10 + play_bt_w && y > 10 && y < 10 + play_bt_h) {
                if (isPlay) {
                    isPlay = false;
                } else {
                    isPlay = true;
                    synchronized (thread) {
                        thread.notify();
                    }
                }
                return true;
            }
            // 判断导弹按钮是否被按下
            else if (x > 10 && x < 10 + missile_bt.getWidth()
                    && y > missile_bt_y
                    && y < missile_bt_y + missile_bt.getHeight()) {
                if (missileCount > 0) {
                    missileCount--;
                    myPlane.setMissileState(true);
                    sounds.playSound(5, 0);

                    for (EnemyPlane pobj : enemyPlanes) {
                        if (pobj.isCanCollide()) {
                            pobj.attacked(GameConstant.MISSILE_HARM); // 敌机增加伤害
                            if (pobj.isExplosion()) {
                                addGameScore(pobj.getScore());// 获得分数
                            }
                        }
                    }

                    // 此线程不能放在绘图函数中，否则当处于无敌状态或者导弹连续按下时，爆炸效果无法显现
                    new Thread(new Runnable() {

                        @Override
                        public void run() {
                            try {
                                Thread.sleep(GameConstant.MISSILEBOOM_TIME);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            } finally {
                                myPlane.setMissileState(false);
                            }

                        }
                    }).start();

                }
                return true;
            }
        }
        return false;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            accValues = event.values.clone();
        } else if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            magValues = event.values.clone();
        }
        SensorManager.getRotationMatrix(r, null, accValues, magValues);
        SensorManager.getOrientation(r, values);
        float azimuth = (float) Math.toDegrees(values[0]);
        float pitch = (float) Math.toDegrees(values[1]);
        float roll = (float) Math.toDegrees(values[2]);
        Log.d("sensor_location", pitch + "," + roll);
        float middle_y = myPlane.getMiddle_y();
        if (middle_y < 0) {
            middle_y = 0;
        }
        if (middle_y > screen_height) {
            middle_y = screen_height;
        }
        if (pitch <= 0 && pitch >= -90) {
            //后退
            myPlane.setMiddle_y(middle_y + 5);
        } else {
            //前进
            myPlane.setMiddle_y(middle_y - 5);
        }
        float middle_x = myPlane.getMiddle_x();
        if (middle_x > screen_width) {
            middle_x = screen_width;
        }
        if (middle_x < 0) {
            middle_x = 0;
        }
        if (roll <= 0 && pitch >= -90) {
            //左移
            myPlane.setMiddle_x(middle_x - 5);
        } else {
            //右移
            myPlane.setMiddle_x(middle_x + 5);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
