package Engine;

import javax.swing.JFrame;
import javax.swing.Timer;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashMap;
import java.util.Map;

public class Greenfoot {
    private static World currentWorld;
    private static JFrame frame;
    private static WorldView view;
    private static Thread gameThread;
    private static Map<String, Boolean> keyStates = new HashMap<>();
    
    public static void setWorld(World world) {
        currentWorld = world;
    }
    
    public static void start() {
        if (currentWorld == null) {
            throw new IllegalStateException("Keine World gesetzt. Bitte erst setWorld() aufrufen.");
        }
        
        // Erstelle das JFrame
        frame = new JFrame("Greenfoot Game");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // Füge die WorldView hinzu
        view = new WorldView(currentWorld);
        frame.add(view);
        
        // Setze die Fenstergröße
        frame.setSize(currentWorld.getWidth() * currentWorld.getCellSize(), 
                     currentWorld.getHeight() * currentWorld.getCellSize());
        frame.setResizable(false);
        
        // Füge KeyListener hinzu
        frame.addKeyListener(new KeyListener() {
            @Override
            public void keyPressed(KeyEvent e) {
                updateKeyState(e.getKeyCode(), true);
            }
            
            @Override
            public void keyReleased(KeyEvent e) {
                updateKeyState(e.getKeyCode(), false);
            }
            
            @Override
            public void keyTyped(KeyEvent e) {}
        });
        frame.setFocusable(true);
        
        // Starte den Game-Loop
        gameThread = new Thread(() -> currentWorld.run());
        gameThread.start();
        
        // Timer für Repaints
        Timer timer = new Timer(50, e -> view.repaint());
        timer.start();
        
        // Zeige das Fenster
        frame.setVisible(true);
    }
    
    private static void updateKeyState(int keyCode, boolean pressed) {
        String key = getKeyName(keyCode);
        keyStates.put(key, pressed);
    }
    
    public static boolean isKeyDown(String key) {
        return keyStates.getOrDefault(key.toLowerCase(), false);
    }
    
    private static String getKeyName(int keyCode) {
        switch(keyCode) {
            case KeyEvent.VK_LEFT: return "left";
            case KeyEvent.VK_RIGHT: return "right";
            case KeyEvent.VK_UP: return "up";
            case KeyEvent.VK_DOWN: return "down";
            case KeyEvent.VK_SPACE: return "space";
            default: return KeyEvent.getKeyText(keyCode).toLowerCase();
        }
    }
} 