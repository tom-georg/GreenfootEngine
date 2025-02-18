import Engine.*;

public class MyWorld extends World {
    public MyWorld(int width, int height, int cellSize) {
        super(width, height, cellSize);
        //setBackground("background.png");

        Car actor1 = new Car();
        Food actor2 = new Food();

        addObject(actor1, 100, 100);
        addObject(actor2, 200, 30);
    }

    @Override
    public void act() {
    }
}
