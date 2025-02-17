package Engine;

import javax.swing.JPanel;
import java.awt.Graphics;
import java.awt.Graphics2D;

class WorldView extends JPanel {
    private World world;
    
    public WorldView(World w) {
        world = w;
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        world.draw(g2d);
    }
} 