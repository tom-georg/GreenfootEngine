package Engine;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class World {
    private GreenfootImage background;
    private int width;
    private int height;
    private int cellSize;
    private List<Actor> actors;
    private List<Class<? extends Actor>> actOrder;  // Für setActOrder
    private List<Class<? extends Actor>> paintOrder; // Für setPaintOrder


    public World(int width, int height, int cellSize) {
        this.width = width;
        this.height = height;
        this.cellSize = cellSize;
        this.actors = new ArrayList<>();
        this.actOrder = new ArrayList<>();
        this.paintOrder = new ArrayList<>();
        this.background = new GreenfootImage(width * cellSize, height * cellSize);
        Graphics2D g = background.getImage().createGraphics();
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, width * cellSize, height * cellSize);
        g.dispose();
    }
	
	  public void act() {
	      // Standard-Act-Methode (tut nichts, wenn nicht überschrieben)
	  }
	
	  public void started() {
	      // Standard-Methode (tut nichts, wenn nicht überschrieben)
	  }
	
	  public void stopped() {
	      // Standard-Methode (tut nichts, wenn nicht überschrieben)
	  }


    public void addObject(Actor object, int x, int y) {
        object.setLocation(x, y);
        actors.add(object);
        object.addedToWorld(this);
    }

    public GreenfootImage getBackground() {
        return background;
    }

    public int getCellSize() {
        return cellSize;
    }
    
    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }


    public Color getColorAt(int x, int y) {
    	int cellsize = this.getCellSize();
        int pixelX = x * cellsize + cellsize / 2;
        int pixelY = y * cellsize + cellsize / 2;

        if (pixelX >= 0 && pixelX < background.getImage().getWidth() && pixelY >= 0 && pixelY < background.getImage().getHeight()) {
            int rgb = background.getImage().getRGB(pixelX, pixelY);
            return new Color(rgb);
        } else {
            return null; // Oder eine Standardfarbe, wenn außerhalb der Grenzen
        }
    }


    public  List<Actor> getObjects(Class<Actor> cls) {
        List<Actor> result = new ArrayList<>();
        for (Actor actor : actors) {
            if (cls.isInstance(actor)) {
                result.add(cls.cast(actor));
            }
        }
        return result;
    }

    public <A> List<A> getObjectsAt(int x, int y, Class<A> cls) {
    	int cellsize = this.getCellSize();
        List<A> result = new ArrayList<>();
        for (Actor actor : actors) {
            if (cls.isInstance(actor)) {
                if (actor.getX() >= x && actor.getX() < x+cellsize && actor.getY() >= y && actor.getY() < y+cellsize) {
                    result.add(cls.cast(actor));
                }
            }
        }
        return result;
    }

    public int numberOfObjects() {
        return actors.size();
    }


    public void removeObject(Actor object) {
        if (object != null) {
            actors.remove(object);
            object.addedToWorld(null);  // Setze die World-Referenz zurück
        }
    }

    public void removeObjects(Collection<? extends Actor> objects) {
        actors.removeAll(objects);
    }


    public void repaint() {
        // Wird vom Haupt-Game-Loop aufgerufen (siehe unten)
    }

    public void setActOrder(Class... classes) {
        actOrder.clear();
        for (Class cls : classes) {
            if (Actor.class.isAssignableFrom(cls)) { // Überprüfe, ob es eine Unterklasse von Actor ist
                actOrder.add(cls);
            }
        }
    }

    public void setPaintOrder(Class... classes) {
        paintOrder.clear();
         for (Class cls : classes) {
            if (Actor.class.isAssignableFrom(cls)) { // Überprüfe, ob es eine Unterklasse von Actor ist
                paintOrder.add(cls);
            }
        }
    }

    public void setBackground(GreenfootImage image) {
    	int cellsize = this.getCellSize();
        if (image.getWidth() != width * cellsize || image.getHeight() != height * cellsize) {
           image.scale(width*cellsize, height*cellsize);
        }
        background = image;
    }

    public void setBackground(String filename) {
        setBackground(new GreenfootImage(filename));
    }

    public void showText(String text, int x, int y) {
        Graphics2D g = background.getImage().createGraphics();
        g.setColor(Color.BLACK); // Textfarbe (könnte anpassbar gemacht werden)
        FontMetrics fm = g.getFontMetrics();
        int textWidth = fm.stringWidth(text);
        int textHeight = fm.getAscent();
        
        int cellsize = this.getCellSize();

        // Zentriere den Text in der Zelle
        int drawX = x * cellsize + (cellsize - textWidth) / 2;
        int drawY = y * cellsize + (cellsize + textHeight) / 2;


        g.drawString(text, drawX, drawY);
        g.dispose();
    }



    // --- Interne Methoden für die Game-Engine (nicht direkt Greenfoot-Methoden) ---

    public void run() {
        // Einfacher Game-Loop (normalerweise in einem separaten Thread)
        while (true) {
            act();  //World act

            // Act-Order beachten
            if (!actOrder.isEmpty()) {
                for (Class<? extends Actor> cls : actOrder) {
                    for (Actor actor : new ArrayList<>(actors)) { // Kopie, um ConcurrentModificationException zu vermeiden
                        if (cls.isInstance(actor)) {
                            actor.act();
                        }
                    }
                }
            } else {
                // Wenn keine Act-Order definiert ist, alle Actors in beliebiger Reihenfolge
                for (Actor actor : new ArrayList<>(actors)) { // Kopie!
                    actor.act();
                }
            }
            
            checkEdge();

            repaint(); // Zeichne die Welt neu
            try {
                Thread.sleep(50); // Kleine Pause (für die Framerate)
            } catch (InterruptedException e) {
                // Handle exception
            }
        }
    }
    
    public void checkEdge() {
        if (actors != null) {
            for (Actor act : actors) {
                if(act.isAtEdge()) {
                  act.act(); //rufe act methode erneut auf
                }
            }
        }
    }

    public void draw(Graphics2D g) {
       // Hintergrund zeichnen
		if (background != null) {
			g.drawImage(background.getImage(), 0, 0, null);
		}
		
		// Actors zeichnen (Paint-Order beachten)
		if (!paintOrder.isEmpty()) {
		    for (Class<? extends Actor> cls : paintOrder) {
		        for (Actor actor : actors) {
		            if (cls.isInstance(actor)) {
		                drawActor(g, actor);
		            }
		        }
		    }
		} else {
		    // Wenn keine Paint-Order definiert ist, alle Actors in der Reihenfolge zeichnen, in der sie hinzugefügt wurden
		    for (Actor actor : actors) {
		        drawActor(g, actor);
		    }
		}
    }

    private void drawActor(Graphics2D g, Actor actor) {
        if (actor.getImage() != null) {
	        AffineTransform originalTransform = g.getTransform(); // Aktuelle Transformation sichern
	
	        // Rotation anwenden
	        int centerX = actor.getX() + actor.getImage().getWidth() / 2;
	        int centerY = actor.getY() + actor.getImage().getHeight() / 2;
	        g.rotate(Math.toRadians(actor.getRotation()), centerX, centerY);
	
	        // Actor zeichnen
	        g.drawImage(actor.getImage().getImage(), actor.getX(), actor.getY(), null);
	
	        g.setTransform(originalTransform); // Ursprüngliche Transformation wiederherstellen
        }
    }
}
