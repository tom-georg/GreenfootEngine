import Engine.*;

public class MyGame {
    public static void main(String[] args) {
        MyWorld world = new MyWorld(10, 10, 50);  // 10x10 Zellen, jede Zelle 50x50 Pixel
        Greenfoot.setWorld(world);
        Greenfoot.start();
        
    }
}


