import Engine.*;

public class MyGame {
    public static void main(String[] args) {
        MyWorld world = new MyWorld(10, 10, 50);  // 10x10 Zellen, jede Zelle 50x50 Pixel
        MyActor actor1 = new MyActor();
        MyActor actor2 = new MyActor();

        world.addObject(actor1, 2, 3);
        world.addObject(actor2, 7, 5);
        
        Greenfoot.setWorld(world);
        Greenfoot.start();
    }
}


