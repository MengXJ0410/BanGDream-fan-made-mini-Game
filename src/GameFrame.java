import javax.imageio.ImageIO;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Random;
import java.util.Vector;

public class GameFrame extends JFrame {

    public static final int DEFAULT_WIDTH = 1280;
    public static final int DEFAULT_HEIGHT = 720;
    private static final int DEFAULT_BGM_ICON_X = DEFAULT_WIDTH-100;
    private static final int DEFAULT_BGM_ICON_WIDTH = 50;
    private static final int DEFAULT_BGM_ICON_Y = 40;
    private static final int DEFAULT_BGM_ICON_HEIGHT = 50;
    public static double DEFAULT_ENEMY_SPAWN_COLDTIME = 500;

    static Plane uika;//定义飞机
    static Vector<Bullet> bullets = new Vector<>();//定义子弹
    static Vector<Enemy> enemies = new Vector<>();//定义敌人

    private static Image CACHED_DEATH = null;
    private static Image CACHED_SAKI_NORMAL = null; // 正常saki状态图片
    private static Image CACHED_SAKI_CRY = null; // 哭泣saki状态图片
    private static Image CACHED_SAKI = null; //

    private static Image CACHED_BGMBUTTON = null;
    private static Image CACHED_BGMBUTTON_NORMAL = null;
    private static Image CACHED_BGMBUTTON_STOP = null;

    private static boolean isSakiCry = false;
    private boolean ShowGAMEOVER= false;
    public static boolean UIKAISMAD = false;
    private static BufferedImage CACHED_BG = null;

    static GameFrame MainFrame;
    private Enemy Anon;

    //BGM
    public static Clip bgmClip = null;
    public static boolean isBGMPlaying = false;
    public static String[] BGM_PATH = {//BGM
            "/Sounds/BGM/BGDbgm",
    };





    public GameFrame() {

        MainFrame = this;
        //生成UIKA
        uika = new Plane();
        uika.start();

        //初始化BGM按钮和BGM
//        initBGMButton();
        initBGMAudio();
        this.addMouseListener(new MouseAdapter() {//检测有没有点到小黄瓜
            @Override
            public void mouseClicked(MouseEvent e) {
                // 获取鼠标点击坐标
                int clickX = e.getX();
                int clickY = e.getY();

                // 判断点击坐标是否在BGM图标区域内（矩形判断）
                boolean isClickOnBgmIcon = (clickX >= DEFAULT_BGM_ICON_X && clickX <= DEFAULT_BGM_ICON_X + DEFAULT_BGM_ICON_WIDTH)
                        && (clickY >= DEFAULT_BGM_ICON_Y && clickY <= DEFAULT_BGM_ICON_Y + DEFAULT_BGM_ICON_HEIGHT);
                if (isClickOnBgmIcon) {
                    // 点击了BGM图标，切换播放/暂停

                    System.out.println("点击了BGM图标");
                    if (bgmClip == null) {
                        isBGMPlaying = true;
                        GameFrame.playSound("/Sounds/BGM/BGDbgm.WAV",-5.0);
                        CACHED_BGMBUTTON = CACHED_BGMBUTTON_STOP;
                    } else {
                        if (bgmClip.isRunning()){
                            isBGMPlaying = false;
                            bgmClip.stop();
                            CACHED_BGMBUTTON = CACHED_BGMBUTTON_NORMAL;
                            System.out.println("bgmstop");
                        } else {
                            bgmClip.start();
                            CACHED_BGMBUTTON = CACHED_BGMBUTTON_STOP;
                        }
                    }
                }
            }
        });

        //设置窗口图标
        try {
            setIconImage(ImageIO.read(getClass().getResource("/image/MXJAPP.ico")));
        }
        catch (IOException | NullPointerException e) {
            System.out.println("不行!!!!!!!!!!!!!!!!!!!!!!!!!!!" + e.getMessage());
        }
        //设置窗口大小
        this.setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
        //关闭按键
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //可视
        this.setVisible(true);
        //可重新改变大小
        this.setResizable(false);
        //设置窗口标题
        this.setTitle("Mujica打飞机");

        //新建一个线程
        new Thread(new Runnable() {
            @Override
            public void run(){
                while (Player.isLive){
                    repaint();
                    try {
                        //休眠，防止电脑爆炸
                        Thread.sleep(16);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                if(!Player.isLive){
                    System.out.println("游戏结束");
                    ShowGAMEOVER= true;
                    repaint();
                }
            }
        }).start();

        //为Anon敌人新建一个线程
        new Thread(new Runnable() {
            @Override
            public void run(){

                while (Player.isLive){
                    Random random = new Random();
                    Enemy Anon = new Enemy(
                            "Anon",
                            DEFAULT_WIDTH+100,
                            Enemy.DEFAULT_ENEMY_HEIGHT+random.nextInt(DEFAULT_HEIGHT - 150),
                            Enemy.DEFAULT_ENEMY_WIDTH,
                            Enemy.DEFAULT_ENEMY_HEIGHT,
                            MainFrame,
                            true,
                            Enemy.DEFAULT_ENEMY_HEALTH
                    );
                    Anon.start();
                    enemies.add(Anon);
                    try {
                        Thread.sleep((long) DEFAULT_ENEMY_SPAWN_COLDTIME);//每500ms产生一个敌人
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
        //Mortis
        new Thread(new Runnable() {
            @Override
            public void run(){

                while (Player.isLive){
                    Random random = new Random();
                    Enemy Mortis = new Enemy(
                            "Mortis",
                            DEFAULT_WIDTH+100,
                            Enemy.DEFAULT_ENEMY_HEIGHT+random.nextInt(DEFAULT_HEIGHT - 150),
                            Enemy.DEFAULT_ENEMY_WIDTH,
                            Enemy.DEFAULT_ENEMY_HEIGHT,
                            MainFrame,
                            true,
                            Enemy.DEFAULT_ENEMY_HEALTH+1
                    );
                    Mortis.start();
                    enemies.add(Mortis);
                    try {
                        Thread.sleep((long) ((long) 10*DEFAULT_ENEMY_SPAWN_COLDTIME));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
        //Rana
        new Thread(new Runnable() {
            @Override
            public void run(){

                while (Player.isLive){
                    Random random = new Random();
                    Enemy Rana = new Enemy(
                            "Rana",
                            DEFAULT_WIDTH+100,
                            Enemy.DEFAULT_ENEMY_HEIGHT+random.nextInt(DEFAULT_HEIGHT - 150),
                            Enemy.DEFAULT_ENEMY_WIDTH,
                            Enemy.DEFAULT_ENEMY_HEIGHT,
                            MainFrame,
                            true,
                            Enemy.DEFAULT_ENEMY_HEALTH+4
                    );
                    Rana.start();
                    enemies.add(Rana);
                    try {
                        Thread.sleep((long) ((long) 50*DEFAULT_ENEMY_SPAWN_COLDTIME));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    public static void SoyoMAD() {
        System.out.println("为什么演奏春日影！！！！！！！");
        playSound("/Sounds/Soyo/WHY.WAV",0.0);
        Enemy Soyo = new Enemy(
                "Soyo",
                DEFAULT_WIDTH+100,
                DEFAULT_HEIGHT/2-(7*Enemy.DEFAULT_ENEMY_HEIGHT)/2,
                7*Enemy.DEFAULT_ENEMY_WIDTH,
                7*Enemy.DEFAULT_ENEMY_HEIGHT,
                MainFrame,
                true,
                Enemy.DEFAULT_ENEMY_HEALTH+4
        );
        Soyo.start();
        enemies.add(Soyo);

    }

    //    private void initBGMButton() {
//        BGMButton = new JButton("Play BGM");
//        BGMButton.setBounds(DEFAULT_WIDTH-140,20,120,40);//设置按钮位置大小
//        BGMButton.setIcon(new ImageIcon(GameFrame.class.getResource("/image/BGMIcon.png")));
//        BGMButton.setBorderPainted(true);
//        BGMButton.setFocusPainted(false);
//        BGMButton.setFont(new Font("Arial",Font.BOLD,14));
//        BGMButton.setBackground(new Color(50,255,100));
//        BGMButton.setForeground(Color.WHITE);
//
//        BGMButton.setFocusable(false);
//
//        BGMButton.addActionListener(e -> {
//        });
//    }
//
    private void initBGMAudio() {
        try {
            // 读取BGM文件（使用BufferedInputStream确保循环播放稳定性）
            InputStream inputStream = GameFrame.class.getResourceAsStream(BGM_PATH[0]);
            if (inputStream == null) {
                System.out.println("BGM文件不存在: " + BGM_PATH[0]);
                return;
            }
            BufferedInputStream bufferedIn = new BufferedInputStream(inputStream);
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(bufferedIn);

            // 初始化音频剪辑
            bgmClip = AudioSystem.getClip();
            bgmClip.open(audioIn);
            // 设置BGM循环播放（-1表示无限循环）
            bgmClip.loop(Clip.LOOP_CONTINUOUSLY);
            // 初始状态：暂停（等待点击图标播放）
            bgmClip.stop();
        } catch (Exception e) {
            System.out.println("BGM初始化失败: " + e.getMessage());
            bgmClip = null;
        }
    }


    static {
        //设置背景
        try {
            CACHED_BG = ImageIO.read(GameFrame.class.getResource("/image/bg2.png"));
        } catch (IOException e) {
            System.out.println("背景图加载失败: " + e.getMessage());
            CACHED_BG = new BufferedImage(DEFAULT_WIDTH, DEFAULT_HEIGHT, BufferedImage.TYPE_INT_RGB);
            // 加载失败时画个红色背景方便确认问题
            Graphics Pan = CACHED_BG.getGraphics();
            Pan.setColor(Color.RED);
            Pan.fillRect(0, 0, DEFAULT_WIDTH, DEFAULT_HEIGHT);
        }
        //死亡
        Image GAMEOVER = null;
        try {
            GAMEOVER = new ImageIcon(GameFrame.class.getResource("/image/GAMEOVER.jpg")).getImage();
        } catch (NullPointerException e) {
            System.out.println("死亡图加载失败: " + e.getMessage());
        }
        CACHED_DEATH = GAMEOVER;
        //saki
        try {
            CACHED_SAKI_NORMAL = ImageIO.read(GameFrame.class.getResource("/image/SakiBody.png"));
            CACHED_SAKI_CRY = ImageIO.read(GameFrame.class.getResource("/image/SakiBodyCry.png"));
            CACHED_SAKI = CACHED_SAKI_NORMAL;//初始化
        } catch (NullPointerException e) {
            System.out.println("saki图加载失败: " + e.getMessage());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        //BGM
        try {

            CACHED_BGMBUTTON_NORMAL = ImageIO.read(GameFrame.class.getResource("/image/BGMIcon.png"));
            CACHED_BGMBUTTON_STOP = ImageIO.read(GameFrame.class.getResource("/image/BGMIcon_SPOT.png"));
            CACHED_BGMBUTTON = CACHED_BGMBUTTON_NORMAL;//初始化
        } catch (NullPointerException | IOException e) {
            System.out.println("BGM图加载失败: " + e.getMessage());
        }
    }

    public static void GAMEOVER() {
        Player.isLive = false;
        GameFrame.playSound("/Sounds/Anon/Anno_Laugh_ALL.WAV", 0.0);
        // 可选：添加玩家死亡逻辑（如停止移动、播放死亡音效）
    }


    public void paint(Graphics g){
      //  System.out.println("TEST");
        //背景
        BufferedImage image = (BufferedImage) this.createImage(
                DEFAULT_WIDTH,
                DEFAULT_HEIGHT
        );
        Graphics Pan = image.getGraphics();
        Pan.drawImage(CACHED_BG, 0, 0, null);
        Pan.drawImage(CACHED_SAKI, -50, (int) (0.05*DEFAULT_HEIGHT), (int) (0.3*DEFAULT_HEIGHT),DEFAULT_HEIGHT ,null);
        if (ShowGAMEOVER) {
            for(int i = 0; i < 100; i++) {
                int deathImgX = (DEFAULT_WIDTH -CACHED_DEATH.getWidth(null)) / 2;
                int deathImgY = (DEFAULT_HEIGHT - CACHED_DEATH.getHeight(null)) / 2;
                Pan.drawImage(CACHED_DEATH, deathImgX, deathImgY,i*8 ,i*9, null); // 简化写法，用图片原始大小
                g.drawImage(image, 0, 0, null);
            }
            return;
        }

        try{//绘制UI
            Pan.setColor(Color.WHITE);
            Pan.setFont(new Font("Arial", Font.BOLD, 26));
            Pan.drawString("HEALTH: " + Player.HEALTH, DEFAULT_WIDTH/10, DEFAULT_HEIGHT/8);
            Pan.drawString("MONEY: " + Player.money, DEFAULT_WIDTH/10, DEFAULT_HEIGHT/6);
            Pan.drawString("KILL: " + Player.kill, DEFAULT_WIDTH-DEFAULT_WIDTH/8, DEFAULT_HEIGHT/6);

            Pan.drawString("ESRM: " + String.format("%.2f",500/GameFrame.DEFAULT_ENEMY_SPAWN_COLDTIME), DEFAULT_WIDTH/10, DEFAULT_HEIGHT/5);

            Pan.setColor(Color.GRAY);
            Pan.setFont(new Font("Arial",Font.BOLD, 10));
            Pan.drawString("MOVE:WASD   SHOOT:SPACE   RUN:Shift  ", DEFAULT_WIDTH/10, DEFAULT_HEIGHT/11);
            Pan.setFont(new Font("Arial",Font.BOLD, 18));
            Pan.drawString(String.valueOf(Player.SHIELD), DEFAULT_WIDTH/10+150, DEFAULT_HEIGHT/8);

            if (CACHED_BGMBUTTON != null) {
                Pan.drawImage(CACHED_BGMBUTTON,DEFAULT_BGM_ICON_X,DEFAULT_BGM_ICON_Y ,
                        DEFAULT_BGM_ICON_WIDTH,DEFAULT_BGM_ICON_HEIGHT ,
                        null);
            }
        } catch (Exception e) {
            System.out.println("生命值加载失败: " + e.getMessage());
        }

        Pan.drawImage(
                uika.UikaOut,
                uika.x,
                uika.y,
                uika.width,
                uika.height,
                null);


        //绘制子弹
        synchronized (bullets){
            for (int i = 0; i < bullets.size(); i++) {
                Bullet b = bullets.get(i);
                try {
                    if(b.x<=DEFAULT_WIDTH+b.width){
                        Pan.drawImage(
                                b.Uika_BulletOut,
                                b.x+=b.x_speed,
                                b.y+=b.y_speed,
                                b.width,
                                b.height,
                                null);


                    }else bullets.remove(b);
                } catch (Exception e) {
                    System.out.println("子弹图加载失败: " + e.getMessage());
                }
            }
        }


        //绘制敌人
        //任何被加入enemies的敌人都会被绘制
        for (int i = 0; i < enemies.size(); i++) {
            Enemy enemy = enemies.get(i);
            try {
                if(enemy.x>=100){
                    if(enemy.name.equals("Anon")){
                        Pan.drawImage(
                                enemy.enemy_anon_out,
                                enemy.x-=enemy.speed,
                                enemy.y,
                                enemy.width,
                                enemy.height,
                                null);
                    }
//                    else if(enemy.name.equals("Saki")){
//                        Pan.drawImage(
//                                enemy.enemy_saki_out,
//                                enemy.x-=enemy.speed,
//                                enemy.y, enemy.width,
//                                enemy.height,
//                                null);
//                    }
                    else if(enemy.name.equals("Mortis")){
                        Pan.drawImage(
                                enemy.enemy_mortis_out,
                                enemy.x-=enemy.speed,
                                enemy.y,
                                (int) (1.4*enemy.width),
                                (int) (1.4*enemy.height),
                                null);
                    }
                    else if(enemy.name.equals("Rana")){
                        Pan.drawImage(
                                enemy.enemy_rana_out,
                                enemy.x-=enemy.speed,
                                enemy.y,
                                (int) (1*enemy.width),
                                (int) (1*enemy.height),
                                null);
                    }
                    else if(enemy.name.equals("Soyo")){

                        //非常简略的逐帧播放，一共8帧
                        if(enemy.x>=1200){
                            Enemy.enemy_soyo_out=Enemy.ENEMY_SOYO1;
                        } else if(enemy.x>=1100&&enemy.x<1200){
                            Enemy.enemy_soyo_out=Enemy.ENEMY_SOYO2;
                        } else if(enemy.x>=1000&&enemy.x<1100){
                            Enemy.enemy_soyo_out=Enemy.ENEMY_SOYO3;
                        } else if(enemy.x>=900&&enemy.x<1000){
                            Enemy.enemy_soyo_out=Enemy.ENEMY_SOYO4;
                        } else if(enemy.x>=800&&enemy.x<900){
                            Enemy.enemy_soyo_out=Enemy.ENEMY_SOYO5;
                        } else if(enemy.x>=700&&enemy.x<800){
                            Enemy.enemy_soyo_out=Enemy.ENEMY_SOYO6;
                        } else if(enemy.x>=600&&enemy.x<700){
                            Enemy.enemy_soyo_out=Enemy.ENEMY_SOYO7;
                        } else if(enemy.x<600){
                            Enemy.enemy_soyo_out=Enemy.ENEMY_SOYO8;
                        }
                        Pan.drawImage(
                                enemy.enemy_soyo_out,
                                enemy.x-=4*enemy.speed,
                                enemy.y,
                                (int) (1*enemy.width),
                                (int) (1*enemy.height),
                                null);
                    }
                }else {
                    if(enemy.isLive&&!"Rana".equals(enemy.name)){
                        if(!isSakiCry) {
                            SakiBeat();

                        }
                    } else if(enemy.isLive&&"Rana".equals(enemy.name)){
                        Player.HEALTH++;
                        Player.money+=10;
                    }
                    enemy.health=0;
                    enemy.isLive=false;
                    synchronized (enemies) {
                        enemies.remove(this);
                    }
                }
            } catch (Exception e) {
                System.out.println("敌人加载失败: " + e.getMessage());
            }
        }
        //生效

        g.drawImage(image,0,0,null);
    }

    public static void SakiBeat() {

        if (isSakiCry) return; // 已经在哭泣状态，直接返回
        isSakiCry = true;
        CACHED_SAKI = CACHED_SAKI_CRY;
        playRandomSound(Plane.SakiCry,0.0);
        Player.HEALTH--;
        if(Player.HEALTH<=0){
            GAMEOVER();
        }
        //启动单独线程处理Saki哭泣的冷却
        new Thread(() -> {
            try {
                Thread.sleep(2000); // 等待2秒
                CACHED_SAKI = CACHED_SAKI_NORMAL;

                Thread.sleep(1000); // 等待1秒
                isSakiCry = false;
            } catch (Exception e) {
                System.out.println("Saki哭泣冷却失败: " + e.getMessage());
            }
        }).start();
        Plane.UikaAngry();
    }

    public static void SakiCry() {

        if (isSakiCry) return; // 已经在哭泣状态，直接返回
        isSakiCry = true;
        CACHED_SAKI = CACHED_SAKI_CRY;
        playSound("/Sounds/SAKI/Saki_CRY.WAV",5.0);
        if(Player.HEALTH<=0){
            GAMEOVER();
        }
        //启动单独线程处理Saki哭泣的冷却
        new Thread(() -> {
            try {
                Thread.sleep(7000); // 等待2秒
                CACHED_SAKI = CACHED_SAKI_NORMAL;
                isSakiCry = false;
            } catch (Exception e) {
                System.out.println("Saki哭泣冷却失败: " + e.getMessage());
            }
        }).start();
    }

    public static void playRandomSound(String[] soundPaths,double dp) {
        new Thread(()->{
            Clip clip = null;
            if (soundPaths.length == 0) {
                System.out.println("无音频文件: ");
                return;
            }
            try {
                Random random = new Random();
                int randomIndex = random.nextInt(soundPaths.length);
                InputStream inputStream = Plane.class.getResourceAsStream(soundPaths[randomIndex]);
                if (inputStream == null) {
                    System.out.println("音频文件不存在: " + soundPaths[randomIndex]);
                    return;
                }

                // 2. 用 BufferedInputStream 包装（关键：支持 mark/reset）
                BufferedInputStream bufferedIn = new BufferedInputStream(inputStream);
                // 3. 用 AudioInputStream 包装，指定音频格式
                AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(bufferedIn);
                clip = AudioSystem.getClip();
                clip.open(audioInputStream);

                // 设置音量（分贝调节）
                setVolume(clip, dp);

                clip.start();//播放音频



            } catch (Exception e) {
                e.printStackTrace();
                if(clip != null) {
                    clip.close();
                }
            }
        }).start();
    }

    public static void playSound(String s,double dp) {
        Clip clip = null;
        if (s == null) {
            System.out.println("无音频文件: ");
            return;
        }
        try {
            InputStream inputStream = Plane.class.getResourceAsStream(s);
            if (inputStream == null) {
                System.out.println("音频文件不存在: " + s);
                return;
            }
            // 关键：用 BufferedInputStream 包装
            BufferedInputStream bufferedIn = new BufferedInputStream(inputStream);
            // 2. 用 AudioSystem 获取音频输入流
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(bufferedIn);
            clip = AudioSystem.getClip();
            clip.open(audioInputStream);
            // 只加这1行：把当前Clip存到全局变量（如果是BGM文件的话）
            if (s.equals("/Sounds/BGM/BGDbgm.WAV")) { // 这里填你的BGM路径
                bgmClip = clip;
                bgmClip.loop(Clip.LOOP_CONTINUOUSLY); // 给BGM加循环（可选，想要循环就加）
            }

            // 设置音量（分贝调节）
            setVolume(clip, dp);
            
            clip.start();//播放音频
        } catch (Exception e) {
            e.printStackTrace();
            if(clip != null) {
                clip.close();
            }
        }
    }

    // 在GameFrame类中添加一个新方法，用于播放音频并返回Clip实例
    public static Clip playSoundAndReturn(String s, double dp) {
        Clip clip = null;
        if (s == null) {
            System.out.println("无音频文件: ");
            return null;
        }
        try {
            InputStream inputStream = Plane.class.getResourceAsStream(s);
            if (inputStream == null) {
                System.out.println("音频文件不存在: " + s);
                return null;
            }
            BufferedInputStream bufferedIn = new BufferedInputStream(inputStream);
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(bufferedIn);
            clip = AudioSystem.getClip();
            clip.open(audioInputStream);

            // 设置音量
            setVolume(clip, dp);

            clip.start();//播放音频
            return clip; // 返回Clip实例
        } catch (Exception e) {
            e.printStackTrace();
            if(clip != null) {
                clip.close();
            }
            return null;
        }
    }

    private static void setVolume(Clip clip, double dp) {
        try {
            // 获取音量控制对象（支持分贝调节）
            FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);

            // dp值转换为分贝（通常范围在-80.0到6.02之间）
            // 限制范围避免超出音频设备支持的范围
            float minGain = gainControl.getMinimum();
            float maxGain = gainControl.getMaximum();
            float desiredGain = (float) dp;

            // 确保增益在有效范围内
            float actualGain = Math.max(minGain, Math.min(maxGain, desiredGain));

            // 设置实际增益（分贝）
            gainControl.setValue(actualGain);

        } catch (IllegalArgumentException e) {
            // 某些音频可能不支持音量调节，忽略即可
            System.out.println("该音频不支持音量调节: " + e.getMessage());
        }
    }


}
