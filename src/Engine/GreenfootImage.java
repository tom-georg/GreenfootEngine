package Engine;
import java.awt.*;
import java.awt.image.BufferedImage;


class GreenfootImage {
    private BufferedImage image;

    public GreenfootImage(int width, int height) {
        image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
    }
    
    public GreenfootImage(BufferedImage img) {
          image = img;
    }


    public GreenfootImage(String filename) {
        // Lade das Bild aus der Datei (hier vereinfacht - du brauchst echte Bild-I/O)
        try {
            image = javax.imageio.ImageIO.read(new java.io.File(filename));
        } catch (java.io.IOException e) {
            System.err.println("Fehler beim Laden des Bildes: " + filename);
            image = new BufferedImage(100, 100, BufferedImage.TYPE_INT_ARGB); // Standard-Fehlerbild
        }
    }

    public BufferedImage getImage() {
        return image;
    }
    
    public int getWidth() {
        return image.getWidth();
    }

    public int getHeight() {
        return image.getHeight();
    }

    public void draw(Graphics2D g, int x, int y) {
    	if (image != null) {
            g.drawImage(image, x, y, null);
        }
    }
    
    public void scale(int w, int h) {
    	BufferedImage resized = new BufferedImage(w, h, image.getType());
        Graphics2D g2 = resized.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR); // Optional für bessere Qualität
        g2.drawImage(image, 0, 0, w, h, null);
        g2.dispose();

        image = resized;
    }
}