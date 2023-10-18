package com.example.aircraftbattle.factory;

import android.content.res.Resources;

import com.example.aircraftbattle.bullet.BigPlaneBullet;
import com.example.aircraftbattle.bullet.BossDefaultBullet;
import com.example.aircraftbattle.bullet.BossFlameBullet;
import com.example.aircraftbattle.bullet.BossGThunderBullet;
import com.example.aircraftbattle.bullet.BossRHellfireBullet;
import com.example.aircraftbattle.bullet.BossSunBullet;
import com.example.aircraftbattle.bullet.BossTriangleBullet;
import com.example.aircraftbattle.bullet.BossYHellfireBullet;
import com.example.aircraftbattle.bullet.MyBlueBullet;
import com.example.aircraftbattle.bullet.MyPurpleBullet;
import com.example.aircraftbattle.bullet.MyRedBullet;
import com.example.aircraftbattle.object.GameObject;
import com.example.aircraftbattle.object.LifeGoods;
import com.example.aircraftbattle.object.MissileGoods;
import com.example.aircraftbattle.object.PurpleBulletGoods;
import com.example.aircraftbattle.object.RedBulletGoods;
import com.example.aircraftbattle.plane.BigPlane;
import com.example.aircraftbattle.plane.BossPlane;
import com.example.aircraftbattle.plane.MiddlePlane;
import com.example.aircraftbattle.plane.MyPlane;
import com.example.aircraftbattle.plane.SmallPlane;

/**
 * 物品构建的工厂
 */
public class GameObjectFactory {
    /**
     * 小型机
     *
     * @param resources
     * @return
     */
    public GameObject createSmallPlane(Resources resources) {
        return new SmallPlane(resources);
    }

    /**
     * 生产中型机
     *
     * @param resources
     * @return
     */
    public GameObject createMiddlePlane(Resources resources) {
        return new MiddlePlane(resources);
    }

    /**
     * 生产大型机
     *
     * @param resources
     * @return
     */
    public GameObject createBigPlane(Resources resources) {
        return new BigPlane(resources);
    }

    /**
     * 生产boss机体
     *
     * @param resources
     * @return
     */
    public GameObject createBossPlane(Resources resources) {
        return new BossPlane(resources);
    }

    /**
     * 生产我方机体
     *
     * @param resources
     * @return
     */
    public GameObject createMyPlane(Resources resources) {
        return new MyPlane(resources);
    }

    /**
     * 生产我方的子弹
     *
     * @param resources
     * @return
     */
    public GameObject createMyBlueBullet(Resources resources) {
        return new MyBlueBullet(resources);
    }

    public GameObject createMyPurpleBullet(Resources resources) {
        return new MyPurpleBullet(resources);
    }

    public GameObject createMyRedBullet(Resources resources) {
        return new MyRedBullet(resources);
    }

    /**
     * 生产BOSS的子弹
     *
     * @param resources
     * @return
     */
    public GameObject createBossFlameBullet(Resources resources) {
        return new BossFlameBullet(resources);
    }

    public GameObject createBossSunBullet(Resources resources) {
        return new BossSunBullet(resources);
    }

    public GameObject createBossTriangleBullet(Resources resources) {
        return new BossTriangleBullet(resources);
    }

    public GameObject createBossGThunderBullet(Resources resources) {
        return new BossGThunderBullet(resources);
    }

    public GameObject createBossYHellfireBullet(Resources resources) {
        return new BossYHellfireBullet(resources);
    }

    public GameObject createBossRHellfireBullet(Resources resources) {
        return new BossRHellfireBullet(resources);
    }

    public GameObject createBossBulletDefault(Resources resources) {
        return new BossDefaultBullet(resources);
    }

    /**
     * 生产BigPlane的子弹
     *
     * @param resources
     * @return
     */
    public GameObject createBigPlaneBullet(Resources resources) {
        return new BigPlaneBullet(resources);
    }

    /**
     * 生产导弹物品
     *
     * @param resources
     * @return
     */
    public GameObject createMissileGoods(Resources resources) {
        return new MissileGoods(resources);
    }

    /**
     * 生产生命物品
     *
     * @param resources
     * @return
     */
    public GameObject createLifeGoods(Resources resources) {
        return new LifeGoods(resources);
    }


    /**
     * 生产子弹物品
     *
     * @param resources
     * @return
     */
    public GameObject createPurpleBulletGoods(Resources resources) {
        return new PurpleBulletGoods(resources);
    }

    public GameObject createRedBulletGoods(Resources resources) {
        return new RedBulletGoods(resources);
    }
}
