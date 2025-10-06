import javax.sound.sampled.Clip;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Enemy extends Thread{

    public final int DEFAULT_ENEMY_SPEED = 2;
    public static final int DEFAULT_ENEMY_HEALTH = 1;
    public int health = DEFAULT_ENEMY_HEALTH;
    public static final int DEFAULT_ENEMY_WIDTH = 50;
    public static final int DEFAULT_ENEMY_HEIGHT = 50;

//保存声音
    private static List<Clip> DeathClips = new ArrayList<Clip>();
    private static String[] AnnoCrySoundPaths = {
            "/Sounds/Anon/Anon_Cry1.WAV",
            "/Sounds/Anon/Anon_Cry2.WAV",
            "/Sounds/Anon/Anon_Cry3.WAV",
            "/Sounds/Anon/Anon_Cry4.WAV",
            "/Sounds/Anon/Anon_Cry5.WAV",
            // 可以继续添加更多音频路径
    };
    private static String[] MortisCrySoundPaths = {
            "/Sounds/Mu/Mortis_Cry2.WAV",
            "/Sounds/Mu/Mortis_Cry3.WAV",
    };
    public static String[] AnnoLaughSoundPaths = {
            "/Sounds/Anon/Anno_Laugh1.WAV",
            "/Sounds/Anon/Anno_Laugh2.WAV",
            "/Sounds/Anon/Anno_Laugh3.WAV",
//            "/Sounds/Anno/Anno_Laugh_ALL.WAV",
            // 可以继续添加更多音频路径
    };

    public static String[] EnemyName = {//敌人种类
            "Anon",
            "Saki",
    };

    // 在Enemy类中添加Rana专属音频变量
    private Clip harukageClip = null; // 用于记录HARUKAGE音频的Clip实例



    public GameFrame gf;
    volatile public String name;
    public int x,y;

    volatile boolean isLive=true;
    //volatile关键字保证多线程的可见性
    public int width=DEFAULT_ENEMY_WIDTH,height=DEFAULT_ENEMY_HEIGHT;
    public int speed=DEFAULT_ENEMY_SPEED;

    //预加载，减少性能浪费
    private static final Image ENEMY_ANON_NORMAL = new ImageIcon(Enemy.class.getResource("/image/Anon_Head.png")).getImage();
    private static final Image ENEMY_ANON_HIT = new ImageIcon(Enemy.class.getResource("/image/Anon_Cry.png")).getImage();
    private static final Image ENEMY_SAKI_NORMAL = new ImageIcon(Enemy.class.getResource("/image/Saki.png")).getImage();
    private static final Image ENEMY_SAKI_HIT = new ImageIcon(Enemy.class.getResource("/image/Anon_Cry.png")).getImage();
    private static final Image ENEMY_MORTIS_NORMAL = new ImageIcon(Enemy.class.getResource("/image/Mortis_Smile.png")).getImage();
    private static final Image ENEMY_MORTIS_HIT = new ImageIcon(Enemy.class.getResource("/image/Mortis_Hit.png")).getImage();
    private static final Image ENEMY_MORTIS_ANGRY = new ImageIcon(Enemy.class.getResource("/image/Mortis_Angry.png")).getImage();
    private static final Image ENEMY_MORTIS_DIE = new ImageIcon(Enemy.class.getResource("/image/Mortis_Die.png")).getImage();
    private static final Image ENEMY_RANA_PAFI = new ImageIcon(Enemy.class.getResource("/image/Ranaegg.png")).getImage();
    private static final Image ENEMY_RANA_PLAY = new ImageIcon(Enemy.class.getResource("/image/Rana.png")).getImage();
    public static final Image ENEMY_SOYO1 = new ImageIcon(Enemy.class.getResource("/image/Soyo/1.png")).getImage();
    public static final Image ENEMY_SOYO2 = new ImageIcon(Enemy.class.getResource("/image/Soyo/2.png")).getImage();
    public static final Image ENEMY_SOYO3 = new ImageIcon(Enemy.class.getResource("/image/Soyo/3.png")).getImage();
    public static final Image ENEMY_SOYO4 = new ImageIcon(Enemy.class.getResource("/image/Soyo/4.png")).getImage();
    public static final Image ENEMY_SOYO5 = new ImageIcon(Enemy.class.getResource("/image/Soyo/5.png")).getImage();
    public static final Image ENEMY_SOYO6 = new ImageIcon(Enemy.class.getResource("/image/Soyo/6.png")).getImage();
    public static final Image ENEMY_SOYO7 = new ImageIcon(Enemy.class.getResource("/image/Soyo/7.png")).getImage();
    public static final Image ENEMY_SOYO8 = new ImageIcon(Enemy.class.getResource("/image/Soyo/8.png")).getImage();


    public Image enemy_anon_out = ENEMY_ANON_NORMAL;
    public Image enemy_saki_out = ENEMY_SAKI_NORMAL;
    public Image enemy_mortis_out = ENEMY_MORTIS_NORMAL;
    public Image enemy_rana_out = ENEMY_RANA_PAFI;
    public static Image enemy_soyo_out = ENEMY_SOYO1;

    public Enemy(String name, int x,int y,GameFrame gf,boolean isLive,int health){
        this.name=name;
        this.x=x;
        this.y=y;
        this.gf=gf;
        this.isLive=isLive;
        this.health=health;
    }
    public Enemy(String name, int x,int y,int width,int height,GameFrame gf,boolean isLive,int health){
        this.name=name;
        this.x=x;
        this.y=y;
        this.width=width;
        this.height=height;
        this.gf=gf;
        this.isLive=isLive;
        this.health=health;
    }

    public String getEnemyName() {
        return this.name;
    }


//碰撞检测
    @Override
    public void run(){

        while (isLive){//直接以islive为判断条件，如果被死了，则停止
            if(hit()){
                this.health--;
                if(this.health<=0){
                    isLive=false;
                    Player.kill++;
                    this.speed=0;
                    if("Anon".equals(this.name)){
                        Player.money++;
                        this.enemy_anon_out = ENEMY_ANON_HIT;
                        GameFrame.playRandomSound(AnnoCrySoundPaths,-10.0);
                    }
//                else if(this.name=="Saki"){
//                    this.enemy_saki_out = ENEMY_SAKI_HIT;
//                }
                    else if("Mortis".equals(this.name)){
                        Player.money+=2;
                        this.enemy_mortis_out = ENEMY_MORTIS_DIE;
                        GameFrame.playSound("/Sounds/Mu/Mortis_YA.WAV",0.0);
                    }
                    else if("Rana".equals(this.name)){
                        Player.money+=5;
                        this.enemy_rana_out = ENEMY_RANA_PAFI;
                        GameFrame.playSound("/Sounds/Rana/Zumane.WAV",0.0);
                        //如果BGM正在播放，则恢复
                        if(GameFrame.isBGMPlaying){
                            GameFrame.bgmClip.start();
                        }
                        // 停止HARUKAGE音频（如果正在播放）
                        if (harukageClip != null && harukageClip.isRunning()) {
                            harukageClip.stop();
                            harukageClip.close(); // 释放资源
                            harukageClip = null;
                        }
                    }
                    try {
                        this.sleep(700);
                    } catch (InterruptedException e){
                        e.printStackTrace();
                        break;
                    }
                    // 移除自身时加锁，避免并发问题
                    synchronized (gf.enemies) {
                        gf.enemies.remove(this);
                    }
                    // 敌人生成冷却调整（保留）
                    if(GameFrame.DEFAULT_ENEMY_SPAWN_COLDTIME >= 100){
                        GameFrame.DEFAULT_ENEMY_SPAWN_COLDTIME--;
                    }
                    break;
                }
                else {
//Anon
                    if("Anon".equals(this.name)){
                        GameFrame.playRandomSound(Enemy.AnnoCrySoundPaths,-10.0);
                        this.enemy_anon_out = ENEMY_ANON_HIT;
                    }
//Mortis
                    else if("Mortis".equals(this.name)){
                        GameFrame.playRandomSound(MortisCrySoundPaths,-10.0);
                        this.enemy_mortis_out = ENEMY_MORTIS_HIT;}
//Rana
                    else if("Rana".equals(this.name)){
                        if(this.health==4){
                            GameFrame.playSound("/Sounds/Rana/Omoshire.WAV",10.0);//有趣的女人
                        } else {
                            GameFrame.playSound("/Sounds/Rana/Yaru.WAV",0.0);//被击中
                        }
                        this.enemy_rana_out = ENEMY_RANA_PLAY;
                        }
                    // 受击硬直（避免连续快速减血）
                    try {
                        int temp = this.speed;
                        this.speed = 0;
                        // 恢复正常图片（可选）
                        if("Anon".equals(this.name)){
                            this.enemy_anon_out = ENEMY_ANON_NORMAL;
                        } else if("Mortis".equals(this.name)){
                            Thread.sleep(2500);
                            this.speed = 3*temp;
                            this.enemy_mortis_out = ENEMY_MORTIS_ANGRY;
                        } else if ("Rana".equals(this.name)) {
                            if(this.health==4){
                                Thread.sleep(1000);
                                moveittomiddle();
                                this.height=4*DEFAULT_ENEMY_HEIGHT;
                                this.width=4*DEFAULT_ENEMY_WIDTH;
                                if(GameFrame.isBGMPlaying){
                                    System.out.println("BGM is playing, stop it");
                                    GameFrame.bgmClip.stop();
                                } else {
                                    System.out.println("BGM is not playing, play it");
                                }
                                harukageClip =GameFrame.playSoundAndReturn("/Sounds/BGM/HARUKAGE.WAV",-10.0);
                                GameFrame.SakiCry();
                                // 2. 启动5秒计时线程（关键：独立线程，不阻塞Rana主逻辑）5秒后召唤Soyo
                                new Thread(() -> {
                                    try {
                                        // 等待5000ms（5秒）
                                        Thread.sleep(5000);

                                        // 3. 计时结束后，判断Rana是否仍存活
                                        // 用volatile修饰的isLive保证多线程可见性（你的代码已加volatile，无需修改）
                                        if (isLive && health > 0) {
                                            // Rana还活着，调用SoyoMAD()
                                            GameFrame.SoyoMAD();
                                            System.out.println("Rana存活超过5秒，触发SoyoMAD");
                                            Thread.sleep(2000);
                                            if(isLive && health > 0){
                                                harukageClip.stop();
                                                // 移除自身时加锁，避免并发问题
                                                this.isLive=false;
                                                this.health=0;
                                                synchronized (gf.enemies) {
                                                    gf.enemies.remove(this);
                                                }
                                            }

                                        } else {
                                            System.out.println("Rana已死亡，不触发SoyoMAD");
                                        }
                                    } catch (InterruptedException e) {
                                        // 线程被中断（如Rana死亡时），直接退出，不触发方法
                                        System.out.println("计时线程被中断，不触发SoyoMAD");
                                    }
                                }).start();

                                moveittomiddle();
                            } else {
                                moveittomiddle();
                                this.height*=0.8;
                                this.width*=0.8;
                                Thread.sleep(1000);
                            }
                        }
                    } catch (InterruptedException e){
                        e.printStackTrace();
                        break;
                    }
                }
            }
            try {
                this.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
                break;
            }
        }
        releaseResources();
    }

    private void moveittomiddle() {
        this.x=GameFrame.DEFAULT_WIDTH/2-this.width/2;
        this.y=GameFrame.DEFAULT_HEIGHT/2-this.height/2;
    }


    private boolean hit() {
        //swing的碰撞检测
        Rectangle enemyrect = new Rectangle(this.x,this.y,this.width,this.height);//矩形
        Rectangle bulletrect = null;

        synchronized (gf.bullets) {
            for (int i=0 ; i< gf.bullets.size();i++){
                if(isLive && health>0){
                    Bullet bullet = gf.bullets.get(i);
                    bulletrect = new Rectangle(bullet.x+1,bullet.y,bullet.width,bullet.height);

                    if (enemyrect.intersects(bulletrect)){//判断是否碰撞
                        // 移除碰撞的子弹（使用索引移除避免迭代器异常）
                        gf.bullets.remove(i);
                        return true;
                    }
                }
            }
        }
        return false;
    }
//释放内存，提高性能喵
    private void releaseResources() {
        // 清除图片引用，帮助GC回收
        enemy_anon_out = null;
        enemy_mortis_out = null;
        // 断开与游戏框架的引用
        gf = null;

    }
    // 提供外部停止线程的方法
    public void stopEnemy() {
        isLive = false;
        interrupt(); // 中断休眠，快速结束线程
    }
}
