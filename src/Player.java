
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

//定义玩家，使用键盘适配器

public class Player extends KeyAdapter {

    private static final int DEFAULT_PLAYER_HEALTH = 3;
    private static final int DEFAULT_PLAYER_SHIELD = 3;
    GameFrame MainFrame;
    public static int HEALTH = DEFAULT_PLAYER_HEALTH;
    public static int SHIELD = DEFAULT_PLAYER_SHIELD;
    static boolean isLive = true;
    public static int money = 50;
    public static int kill = 0;


    public Player(GameFrame GameFrame,int HEALTH,int SHIELD,int money){
        this.MainFrame = GameFrame;
        this.HEALTH = HEALTH;
        this.SHIELD = SHIELD;

        this.money = money;
    }



    @Override
    public void keyPressed(KeyEvent e) {

        int keyCode = e.getKeyCode();
        switch (keyCode) {
            case KeyEvent.VK_A:
                MainFrame.uika.left = true;break;
            case KeyEvent.VK_D:
                MainFrame.uika.right = true;break;
            case KeyEvent.VK_W:
                MainFrame.uika.up = true;break;
            case KeyEvent.VK_S:
                MainFrame.uika.down = true;break;
            case KeyEvent.VK_SHIFT:
                MainFrame.uika.speedUp = true;break;
            case KeyEvent.VK_SPACE:
                MainFrame.uika.shooting = true;break;
            case KeyEvent.VK_E:
                MainFrame.uika.MAD();break;
            case KeyEvent.VK_L://外挂
                money+=100;
                HEALTH+=10;
                ;break;
        }
    }



    @Override
    public void keyReleased(KeyEvent e) {
        int keyCode = e.getKeyCode();
        switch (keyCode) {
            case KeyEvent.VK_A:
                MainFrame.uika.left = false;break;
            case KeyEvent.VK_D:
                MainFrame.uika.right = false;break;
            case KeyEvent.VK_W:
                MainFrame.uika.up = false;break;
            case KeyEvent.VK_S:
                MainFrame.uika.down = false;break;
            case KeyEvent.VK_SHIFT:
                MainFrame.uika.speedUp = false;break;
            case KeyEvent.VK_SPACE:
                MainFrame.uika.shooting = false;break;

        }
    }



}
