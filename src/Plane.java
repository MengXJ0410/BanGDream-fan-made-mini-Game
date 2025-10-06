import javax.sound.sampled.Clip;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Plane extends Thread {

    private static final int DEFAULT_WIDTH = 1280;
    private static final int DEFAULT_HEIGHT = 720;
    private static final int DEFAULT_SPEED = 4;  // 稍微调大最大速度以便观察效果
    private static final double DEFAULT_ASPEED = 0.4;  // 加速度值
    private static final double DEFAULT_FRICTION = 0.85;  // 最大速度
    public static double DEFAULT_SHOOT_COLDTIME = 600;
    public static double SHOOT_COLDTIME = DEFAULT_SHOOT_COLDTIME;

    private static List<Clip> shootClips = new ArrayList<Clip>();
    private static String[] UikaCrySika = {
            "/Sounds/Uika_SAKI/1.WAV",
            "/Sounds/Uika_SAKI/2.WAV",
            "/Sounds/Uika_SAKI/3.WAV",
            "/Sounds/Uika_SAKI/4.WAV",
            "/Sounds/Uika_SAKI/5.WAV",
            "/Sounds/Uika_SAKI/6.WAV",
//                "/Sounds/Uika_SAKI/888.WAV",
            // 可以继续添加更多音频路径
    };
    public static String[] SakiCry = {
            "/Sounds/SAKI/Saki_Cry1.WAV",
            "/Sounds/SAKI/Saki_Cry2.WAV",
            "/Sounds/SAKI/Saki_Cry3.WAV",
            "/Sounds/SAKI/Saki_Cry4.WAV",
    };

    public static final Image UIKA_NORMAL = new ImageIcon(Enemy.class.getResource("/image/Uika.png")).getImage();
    private static final Image UIKA_HIT1 = new ImageIcon(Enemy.class.getResource("/image/Uika_Hurt1.png")).getImage();
    public static final Image UIKA_HIT2 = new ImageIcon(Enemy.class.getResource("/image/Uika_Hurt2.png")).getImage();
    //飞行外形（UIKA
    static Image UikaOut = UIKA_NORMAL;
    private Random random = new Random();



    int x = 100, y = 310;
    int width = 100, height = 100;
    double currentSpeedX = 0;  // 当前X方向速度
    double currentSpeedY = 0;  // 当前Y方向速度
    double aspeed = DEFAULT_ASPEED;  // 加速度


    //方向键
    boolean up, down, left, right;
    //加速
    public boolean speedUp;
    public boolean shooting;




    public Plane(int width, int y, int x, int height, Image uika) {
        this.width = width;
        this.y = y;
        this.x = x;
        this.height = height;
    }


    public Plane() {
        // 碰撞检测单线程：独立于run()，避免重复创建
        new Thread(() -> {
            while (Player.isLive) { // 仅在玩家存活时循环
                Enemy hitEnemy = hit();
                if (hitEnemy!= null) {//发生碰撞
                    if(true){
                        if(Player.SHIELD>0){
                            Player.SHIELD--;
                            GameFrame.playSound("/Sounds/Uika_SAKI/Uika_Cry3.WAV",0.0);
                            UikaOut = UIKA_HIT1;
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                                break; // 中断时退出，避免无效循环
                            }
                            UikaOut = UIKA_NORMAL;
                        } else {
                            Player.HEALTH--;
                            if (Player.HEALTH >= 2) {
                                this.UikaOut = UIKA_HIT1;
                                GameFrame.playSound("/Sounds/Uika_SAKI/Uika_Cry3.WAV",0.0);
                                try {
                                    Thread.sleep(1500);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                    break; // 中断时退出，避免无效循环
                                }
                                if (Player.HEALTH >= 2) {
                                    this.UikaOut = UIKA_NORMAL;
                                }
                            } else if (Player.HEALTH == 1) {
                                this.UikaOut = UIKA_HIT2;
                                GameFrame.playSound("/Sounds/Uika_SAKI/Uika_Cry4.WAV",0.0);
                                try {
                                    Thread.sleep(2500);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                    break; // 中断时退出，避免无效循环
                                }
                                if (Player.HEALTH == 1) {
                                    this.UikaOut = UIKA_NORMAL;
                                    SHOOT_COLDTIME = 0.4 * DEFAULT_SHOOT_COLDTIME;
                                }
                            }
                        }
                        try {
                            Thread.sleep(500); // 保持200ms检测间隔，平衡性能与精度
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                            break; // 中断时退出，避免无效循环
                        }
                        GameFrame.playRandomSound(Enemy.AnnoLaughSoundPaths,0.0);
                        if (Player.HEALTH <= 0) { // 避免生命值为负
                            GameFrame.GAMEOVER();
                        }
                    }else {
                        System.out.println("hitEnemy is NULL");
                    }
                }
                try {
                    Thread.sleep(200); // 保持200ms检测间隔，平衡性能与精度
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    break; // 中断时退出，避免无效循环
                }
            }
        }, "PlayerCollisionThread").start(); // 给线程命名，方便调试
    }

    public static void UikaAngry() {
        new Thread(() -> {
            UikaOut = Plane.UIKA_HIT2;
            GameFrame.playSound("/Sounds/Uika_SAKI/Uika_Cry4.WAV",0.0);
            SHOOT_COLDTIME = 0.2 * Plane.DEFAULT_SHOOT_COLDTIME;
            Player.SHIELD++;
            GameFrame.UIKAISMAD = true;
            try {
                Thread.sleep(4000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            UikaOut = Plane.UIKA_NORMAL;
            GameFrame.UIKAISMAD = false;
            if (Player.HEALTH > 1) {
                SHOOT_COLDTIME = DEFAULT_SHOOT_COLDTIME;
            } else {

                SHOOT_COLDTIME = 0.4 * Plane.DEFAULT_SHOOT_COLDTIME;
            }
        }).start();
    }


    @Override
    public void run() {
        while (Player.isLive) {

            // 重置加速度方向
            double accelX = 0;
            double accelY = 0;

            // 根据按键设置加速度方向
            if (left) accelX = -aspeed;
            if (right) accelX = aspeed;
            if (up) accelY = -aspeed;
            if (down) accelY = aspeed;
            if (shooting) {
                //射击
                shoot();
            }

            // 计算当前速度（应用加速度）
            currentSpeedX += accelX;
            currentSpeedY += accelY;

            // 计算最大速度（考虑加速状态）
            double maxSpeed = speedUp ? 2 * DEFAULT_SPEED : DEFAULT_SPEED;

            // 限制最大速度（防止无限加速）
            currentSpeedX = Math.max(-maxSpeed, Math.min(currentSpeedX, maxSpeed));
            currentSpeedY = Math.max(-maxSpeed, Math.min(currentSpeedY, maxSpeed));

            // 如果没有按键按下，逐渐减速到0
            if (!left && !right) {
                if (Math.abs(currentSpeedX) < aspeed) {
                    currentSpeedX = 0;
                } else {
                    currentSpeedX *= DEFAULT_FRICTION;  // 摩擦减速
                }
            }
            if (!up && !down) {
                if (Math.abs(currentSpeedY) < aspeed) {
                    currentSpeedY = 0;
                } else {
                    currentSpeedY *= DEFAULT_FRICTION;  // 摩擦减速
                }
            }

            // 更新位置（将double转为int）
            x += (int) currentSpeedX;
            y += (int) currentSpeedY;

            //边界检测
            if (x < 0) {
                x = 0;
                currentSpeedX = 0;  // 碰到边界停止X方向移动
            }
            if (x > DEFAULT_WIDTH - width) {
                x = DEFAULT_WIDTH - width;
                currentSpeedX = 0;
            }
            if (y < 0) {
                y = 0;
                currentSpeedY = 0;  // 碰到边界停止Y方向移动
            }
            if (y > DEFAULT_HEIGHT - height) {
                y = DEFAULT_HEIGHT - height;
                currentSpeedY = 0;
            }

            try {
                Thread.sleep(16);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    long LastShootTime = 0;



    private void shoot() {
        long currentTimeMillis = System.currentTimeMillis();

        if (currentTimeMillis - LastShootTime > SHOOT_COLDTIME) {
            GameFrame.playRandomSound(UikaCrySika,0.0);
            // 基础子弹Y坐标
            int baseY = GameFrame.uika.y + GameFrame.uika.height / 2 - 40;
            // 子弹X坐标（统一从飞机前端发射）
            int bulletX = GameFrame.uika.x + GameFrame.uika.width - 30;
            if(GameFrame.UIKAISMAD){
                    GameFrame.bullets.add(new Bullet(
                            bulletX,
                            baseY+30,
                            0.5*Bullet.DEFAULT_BULLET_Y_SPEED
                    ));
                GameFrame.bullets.add(new Bullet(
                        bulletX,
                        baseY-30,
                        -0.5*Bullet.DEFAULT_BULLET_Y_SPEED
                ));

            }
            GameFrame.bullets.add(new Bullet(
                    bulletX,
                    baseY,
                    0.0
            ));
            LastShootTime = currentTimeMillis;
        }
    }

    public Enemy hit() {
        //swing的碰撞检测
        Rectangle PlayerRect = new Rectangle(this.x,this.y,this.width,this.height);//矩形
        Rectangle EnemyRect = null;

        synchronized (GameFrame.enemies) {//同步
            for (int i=0 ; i< GameFrame.enemies.size();i++){
                Enemy enemy = GameFrame.enemies.get(i);
                if(Player.isLive&&enemy.isLive&&!"Rana".equals((enemy.name))){
                    EnemyRect = new Rectangle(enemy.x,enemy.y,enemy.width,enemy.height);
                    if (EnemyRect.intersects(PlayerRect)){//判断是否碰撞
                        Enemy hitEnemy = enemy;
                        // 移除碰撞的子弹（使用索引移除避免迭代器异常）
                        GameFrame.enemies.remove(i);
                        return hitEnemy;
                    }
                }
            }
        }
        return null;
    }

    public void MAD() {
        if (Player.money>= 50){
            Player.money -= 50;
            UikaAngry();

        }
    }
}
