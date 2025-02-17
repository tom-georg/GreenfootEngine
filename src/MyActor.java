import Engine.*;

public class MyActor extends Actor {
    private int speed = 2;

    public MyActor() {
        setImage("player.png");  // Beispiel: Lade "player.png"
    }

    @Override
    public void act() {
        System.out.println("Hello");
   
        if (Greenfoot.isKeyDown("left")) {
            move(-speed);
        }
        if (Greenfoot.isKeyDown("right")) {
            move(speed);
        }
        if (Greenfoot.isKeyDown("up")) {
            turn(-5);
        }
        if (Greenfoot.isKeyDown("down")) {
            turn(5);
        }
        
        if (isAtEdge()) {
            turn(180);
        }

        if(isTouching(MyActor.class)) {
            removeTouching(MyActor.class);
        }
    }
} 