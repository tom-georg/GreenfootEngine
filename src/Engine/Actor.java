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
    private double exactX;
    private double exactY;

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
        if (world != null) {
            world.getSpatialGrid().removeActor(this);
        }
        this.x = x;
        this.y = y;
        this.exactX = x;
        this.exactY = y;
        if (world != null) {
            world.getSpatialGrid().addActor(this);
        }
    }

    public int getRotation() {
        return rotation;
    }

    public void setRotation(int rotation) {
        this.rotation = rotation;
    }

    public void move(int distance) {
        double radians = Math.toRadians(rotation);
        double dx = distance * Math.cos(radians);
        double dy = distance * Math.sin(radians);
        
        // Aktualisiere die exakte Position
        exactX = (exactX == 0 ? x : exactX) + dx;
        exactY = (exactY == 0 ? y : exactY) + dy;
        
        // Setze die gerundete Position
        setLocation((int) Math.round(exactX), (int) Math.round(exactY));
    }

    public void turn(int amount) {
        //amount = amount *-1;
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
            // Hole nur potenzielle Kollisionen aus dem SpatialGrid
            List<Actor> potentialCollisions = world.getSpatialGrid().getPotentialCollisions(this);
            
            for (Actor other : potentialCollisions) {
                if (cls.isInstance(other) && intersects(other)) {
                    intersecting.add(cls.cast(other));
                }
            }
        }
        return intersecting;
    }

    protected boolean intersects(Actor other) {
        if (this.image == null || other.image == null) return false;
        
        // Phase 1: Grobe AABB-Prüfung (schnell, aber ungenau)
        int thisHalfWidth = image.getWidth() / 2;
        int thisHalfHeight = image.getHeight() / 2;
        int otherHalfWidth = other.getImage().getWidth() / 2;
        int otherHalfHeight = other.getImage().getHeight() / 2;
        
        // Schnelle Überprüfung der Grenzen
        if (Math.abs(this.x - other.x) > (thisHalfWidth + otherHalfWidth)) return false;
        if (Math.abs(this.y - other.y) > (thisHalfHeight + otherHalfHeight)) return false;
        
        // Phase 2: Genauere OBB-Kollisionsprüfung (Oriented Bounding Box)
        Point[] thisCorners = getRotatedCorners(this);
        Point[] otherCorners = getRotatedCorners(other);
        
        // Separating Axis Theorem (SAT)
        return checkSATCollision(thisCorners, otherCorners);
    }

    private Point[] getRotatedCorners(Actor actor) {
        int halfWidth = actor.getImage().getWidth() / 2;
        int halfHeight = actor.getImage().getHeight() / 2;
        double rad = Math.toRadians(actor.getRotation());
        double cos = Math.cos(rad);
        double sin = Math.sin(rad);
        
        Point[] corners = new Point[4];
        // Ursprüngliche Eckpunkte relativ zum Zentrum
        int[][] points = {
            {-halfWidth, -halfHeight},
            {halfWidth, -halfHeight},
            {halfWidth, halfHeight},
            {-halfWidth, halfHeight}
        };
        
        // Rotiere jeden Punkt und verschiebe ihn zur Position des Actors
        for (int i = 0; i < 4; i++) {
            int rotX = (int) (points[i][0] * cos - points[i][1] * sin);
            int rotY = (int) (points[i][0] * sin + points[i][1] * cos);
            corners[i] = new Point(rotX + actor.getX() + halfWidth, 
                                 rotY + actor.getY() + halfHeight);
        }
        
        return corners;
    }

    private boolean checkSATCollision(Point[] corners1, Point[] corners2) {
        // Prüfe Projektion auf alle Achsen des ersten Rechtecks
        for (int i = 0; i < 4; i++) {
            Point p1 = corners1[i];
            Point p2 = corners1[(i + 1) % 4];
            
            // Normale zur Kante
            double normalX = -(p2.y - p1.y);
            double normalY = p2.x - p1.x;
            
            // Normalisiere
            double len = Math.sqrt(normalX * normalX + normalY * normalY);
            normalX /= len;
            normalY /= len;
            
            // Projiziere beide Körper auf die Achse
            if (!overlapsOnAxis(corners1, corners2, normalX, normalY)) {
                return false;
            }
        }
        
        // Prüfe Projektion auf alle Achsen des zweiten Rechtecks
        for (int i = 0; i < 4; i++) {
            Point p1 = corners2[i];
            Point p2 = corners2[(i + 1) % 4];
            
            double normalX = -(p2.y - p1.y);
            double normalY = p2.x - p1.x;
            
            double len = Math.sqrt(normalX * normalX + normalY * normalY);
            normalX /= len;
            normalY /= len;
            
            if (!overlapsOnAxis(corners1, corners2, normalX, normalY)) {
                return false;
            }
        }
        
        return true;
    }

    private boolean overlapsOnAxis(Point[] corners1, Point[] corners2, 
                                 double axisX, double axisY) {
        double min1 = Double.POSITIVE_INFINITY;
        double max1 = Double.NEGATIVE_INFINITY;
        double min2 = Double.POSITIVE_INFINITY;
        double max2 = Double.NEGATIVE_INFINITY;
        
        // Projiziere alle Punkte des ersten Rechtecks
        for (Point p : corners1) {
            double proj = p.x * axisX + p.y * axisY;
            min1 = Math.min(min1, proj);
            max1 = Math.max(max1, proj);
        }
        
        // Projiziere alle Punkte des zweiten Rechtecks
        for (Point p : corners2) {
            double proj = p.x * axisX + p.y * axisY;
            min2 = Math.min(min2, proj);
            max2 = Math.max(max2, proj);
        }
        
        // Prüfe auf Überlappung
        return !(max1 < min2 || max2 < min1);
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