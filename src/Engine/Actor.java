package Engine;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;


public class Actor {
    private GreenfootImage image;
    private int x;
    private int y;
    private int rotation;
    private World world;

    public Actor() {
        // Standardkonstruktor (könnte ein Standardbild setzen)
        image = new GreenfootImage(50, 50);  // Beispiel: Graues Quadrat
        Graphics2D g = image.getImage().createGraphics();
        g.setColor(Color.GRAY);
        g.fillRect(0, 0, 50, 50);
        g.dispose();
    }

    public void act() {
        // Standard-Act-Methode (tut nichts)
    }

    protected void addedToWorld(World world) {
        this.world = world;
    }

    public GreenfootImage getImage() {
        return image;
    }
    
    public void setImage(GreenfootImage image) {
    	if(image != null) {
    		this.image = image;
    	}
    }

    public void setImage(String filename) {
        this.image = new GreenfootImage(filename);
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void setLocation(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getRotation() {
        return rotation;
    }

    public void setRotation(int rotation) {
        this.rotation = rotation;
    }

    public void move(int distance) {
        double radians = Math.toRadians(rotation);
        int dx = (int) Math.round(distance * Math.cos(radians));
        int dy = (int) Math.round(distance * Math.sin(radians));
        setLocation(x + dx, y + dy);
    }

    public void turn(int amount) {
        rotation = (rotation + amount) % 360;
        if (rotation < 0) {
            rotation += 360; // Stelle sicher, dass die Rotation zwischen 0 und 359 bleibt
        }
    }

    public void turnTowards(int targetX, int targetY) {
        int deltaX = targetX - x;
        int deltaY = targetY - y;
        double radians = Math.atan2(deltaY, deltaX);
        rotation = (int) Math.toDegrees(radians);
        if (rotation < 0) {
          rotation += 360;
        }
    }

    public World getWorld() {
        return world;
    }

    public <W> W getWorldOfType(Class<W> worldClass) {
        if (worldClass.isInstance(world)) {
            return worldClass.cast(world);
        }
        return null;
    }

    protected <A> List<A> getIntersectingObjects(Class<A> cls) {
        List<A> intersecting = new ArrayList<>();
        if (world != null) {
            for (Actor other : world.getObjects(Actor.class)) {
                if (other != this && cls.isInstance(other) && intersects(other)) {
                    intersecting.add(cls.cast(other));
                }
            }
        }
        return intersecting;
    }

    protected boolean intersects(Actor other) {
    	if (this.image == null || other.image == null) return false;

        // Einfache Rechteck-Kollisionserkennung (kann durch präzisere Methoden ersetzt werden)
        Rectangle myRect = new Rectangle(x, y, image.getWidth(), image.getHeight());
        Rectangle otherRect = new Rectangle(other.getX(), other.getY(), other.getImage().getWidth(), other.getImage().getHeight());
        return myRect.intersects(otherRect);
    }
    

    protected List<Actor> getNeighbours(int distance, boolean diagonal, Class<Actor> cls) {
        List<Actor> neighbours = new ArrayList<>();
        if (world != null) {
            for (Actor other : world.getObjects(cls)) {
                if (other != this) {
                    int dx = other.getX() - x;
                    int dy = other.getY() - y;
                    int cellsize = world.getCellSize();
                    dx = dx / cellsize;
                    dy = dy / cellsize;
                    double dist = Math.sqrt(dx * dx + dy * dy);
                    if (dist <= distance && (diagonal || dx == 0 || dy == 0)) {
                        neighbours.add(cls.cast(other));
                    }
                }
            }
        }
        return neighbours;
    }
    
    
    protected List<Actor> getObjectsAtOffset(int dx, int dy, Class<Actor> cls) {
        List<Actor> objects = new ArrayList<>();
        if (world != null) {
        	int cellsize = world.getCellSize();
            int targetX = x + dx*cellsize;
            int targetY = y + dy*cellsize;
            
            for(Actor act : world.getObjectsAt(targetX, targetY, cls)) {
            	objects.add(cls.cast(act));
            }
        }
        return objects;
    }
    

    protected List<Actor> getObjectsInRange(int radius, Class<Actor> cls) {
        List<Actor> objects = new ArrayList<>();
        if (world != null) {
            for (Actor other : world.getObjects(cls)) {
                if (other != this) {
                    int dx = other.getX() - x;
                    int dy = other.getY() - y;
                    double dist = Math.sqrt(dx * dx + dy * dy);
                    if (dist <= radius) {
                        objects.add(cls.cast(other));
                    }
                }
            }
        }
        return objects;
    }

    protected Actor getOneIntersectingObject(Class<?> cls) {
        List<?> intersecting = getIntersectingObjects(cls);
        if (!intersecting.isEmpty()) {
            return (Actor) intersecting.get(0);
        }
        return null;
    }

    protected Actor getOneObjectAtOffset(int dx, int dy, Class<Actor> cls) {
        List<Actor> objects = getObjectsAtOffset(dx, dy, cls);
        if (!objects.isEmpty()) {
            return (Actor) objects.get(0);
        }
        return null;
    }

    public boolean isAtEdge() {
        if (world == null) return false;
        int cellsize = world.getCellSize();
        return x <= 0 || x >= (world.getWidth() * cellsize) - image.getWidth() || y <= 0 || y >= (world.getHeight()* cellsize) - image.getHeight();
    }

    protected boolean isTouching(Class<?> cls) {
        return !getIntersectingObjects(cls).isEmpty();
    }
    
    protected void removeTouching(Class<?> cls) {
        List<?> objects = getIntersectingObjects(cls);
        if (!objects.isEmpty()) {
            for (Object obj : objects) {
                if (obj != this) {  // Nicht sich selbst entfernen
                    world.removeObject((Actor)obj);
                }
            }
        }
    }
    
    public void sleepFor(int sleepFor) {
    	try {
			Thread.sleep(sleepFor);
		} catch (InterruptedException e) {}
    }
}