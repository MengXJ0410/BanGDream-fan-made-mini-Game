import javax.swing.*;
import java.awt.*;

public class Bullet {

    public static final double DEFAULT_BULLET_X_SPEED = 10.0;
    public static final double DEFAULT_BULLET_Y_SPEED = 5.77;
    public static final int DEFAULT_BULLET_WIDTH = 80;
    public static final int DEFAULT_BULLET_HEIGHT = 80;

    int x, y;
    int width = DEFAULT_BULLET_WIDTH, height = DEFAULT_BULLET_HEIGHT;
    Double x_speed = DEFAULT_BULLET_X_SPEED;
    Double y_speed = DEFAULT_BULLET_Y_SPEED;

    Image Uika_BulletOut = new ImageIcon(getClass()
            .getResource("/image/Uika_Bullet.png"))
            .getImage();
//子弹构造器


    public Bullet(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Bullet(int x, int y, Double y_speed) {
        this.x = x;
        this.y = y;
        this.y_speed = y_speed;
    }

    public Bullet(int x, int y, int width, int height, Double x_speed, Double y_speed) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.x_speed = x_speed;
        this.y_speed = y_speed;
    }
}
