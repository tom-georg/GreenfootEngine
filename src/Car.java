import Engine.*;

public class Car extends Actor {
    double maxSpeed = 8;
    double speed= 0;
    int turnradius = 3;
    double slowDownRate = 0.97;

    public Car() {
        setImage("redcar.png");  // Beispiel: Lade "player.png"
        move();
    }

    @Override
    public void act() {
        move();

        if(isTouching(Food.class)) {
            removeTouching(Food.class);
        }
    }

    public void move(){
        
        if(Greenfoot.isKeyDown("w")){
            if(speed<maxSpeed){
                speed+=0.1;
            }
            
        } 
        if(speed>0&&!Greenfoot.isKeyDown("w")||speed>maxSpeed){
            speed*=slowDownRate;
        } 
        
        if(Greenfoot.isKeyDown("a")&&speed>2){
            turn(turnradius*-1);
        }
        if(Greenfoot.isKeyDown("d")&&speed>2){
            turn(turnradius);
        }
        if(Greenfoot.isKeyDown("a")&&speed>0){
            turn(1*-1);
        }
        if(Greenfoot.isKeyDown("d")&&speed>0){
            turn(1);
        }
        
        if(Greenfoot.isKeyDown("s")){
            speed=speed-0.5;
        } else if(speed<0){
            speed+=0.5;
        }
        move((int)speed);
    }
} 