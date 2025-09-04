import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.*;

public class View extends JPanel {
    private final World world;
    public static volatile boolean isPaused = false;
    private Rectangle explosionSlider = new Rectangle();
    private Rectangle dripSlider = new Rectangle();
    private Rectangle explosionHandle = new Rectangle();
    private Rectangle dripHandle = new Rectangle();
    private boolean isDraggingExplosion = false;
    private boolean isDraggingDrip = false;

    public View(World world) {
        this.world = world;
        setPreferredSize(new Dimension(Config.screenWidth, Config.screenHeight));
        new Timer(Config.updateDelay, _ -> { if (!isPaused) world.update(); }).start();
        new Timer(Config.frameDelay, _ -> repaint()).start();
        
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    isPaused = !isPaused;
                    repaint();
                }
            }
        });
        setFocusable(true);

        try {
            BufferedImage cursorImg = ImageIO.read(new File("./res/cursor/crosshair.png"));
            Cursor customCursor = Toolkit.getDefaultToolkit().createCustomCursor(
                    cursorImg,
                    new Point(cursorImg.getWidth() / 2, cursorImg.getHeight() / 2),
                    "Crosshair"
            );
            setCursor(customCursor); 
        } catch (IOException e) {
            Debug.log("Error loading crosshair: " + e.getMessage());
        }if (Config.debug){
            addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    if (!isPaused) {
                        if (e.getButton() == MouseEvent.BUTTON1) {
                            world.explosions.add(new Explosion(e.getX(), e.getY()));
                        } else if (e.getButton() == MouseEvent.BUTTON3) {
                            world.addNewRock(e.getX(), e.getY());
                        }
                    } else {
                        if (explosionHandle.contains(e.getPoint())) {
                            isDraggingExplosion = true;
                        } else if (dripHandle.contains(e.getPoint())) {
                            isDraggingDrip = true;
                        }
                    }
                }
                
                @Override
                public void mouseReleased(MouseEvent e) {
                    isDraggingExplosion = false;
                    isDraggingDrip = false;
                }
            });
            
            addMouseMotionListener(new MouseMotionAdapter() {
                @Override
                public void mouseDragged(MouseEvent e) {
                    if (isPaused) {
                        if (isDraggingExplosion) {
                            float newVolume = ((e.getX() - (getWidth() / 2 - 100)) / 200.0f * 36.0f) - 30;
                            Sound.explosionVolume = Math.max(-30, Math.min(6, newVolume));
                            repaint();
                        } else if (isDraggingDrip) {
                            float newVolume = ((e.getX() - (getWidth() / 2 - 100)) / 200.0f * 36.0f) - 30;
                            Sound.dripVolume = Math.max(-30, Math.min(6, newVolume));
                            repaint();
                        }
                    }
                }
            });
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Background.draw(g, getWidth(), getHeight());
        
        for (Rock rock : world.rocks) {
            if (rock.isExploding()) {
                ExplosionSprite.drawFrame(g, rock.getExplosionFrame(),
                        (int) rock.posX, (int) rock.posY, rock.size * 4);
            } else {
                SpriteSheet.drawFrame(g, rock.getSpriteType(), rock.getCurrentFrame(),
                        (int) rock.posX, (int) rock.posY, rock.size);
                        
                if (Config.debugViewMode) {
                    g.setColor(Color.WHITE);
                    String info = String.format("ID:%d [%d,%d] Speed:%.1f Size:%d Type:%d", 
                        rock.rockID, (int)rock.posX, (int)rock.posY,
                        rock.speed(), rock.size, rock.getSpriteType());
                    Debug.log(info);
                    g.drawString(info, (int)rock.posX + 1, (int)rock.posY + 1);

                    g.setColor(new Color(255, 0, 0, 50));
                    g.drawOval((int)(rock.posX - rock.size), (int)(rock.posY - rock.size), 
                             rock.size * 2, rock.size * 2);
                             

                    g.setColor(new Color(0, 255, 0, 100));
                    int lineLength = 50;
                    g.drawLine((int)rock.posX, (int)rock.posY,
                             (int)(rock.posX + rock.speedX * lineLength),
                             (int)(rock.posY + rock.speedY * lineLength));
                }    }
        }

        for (Explosion exp : world.explosions) {
            exp.draw(g);
        }
        
        if (isPaused) {
            g.setColor(new Color(0, 0, 0, 200));
            g.fillRect(0, 0, getWidth(), getHeight());
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 48));
            String text = "PAUSED";
            FontMetrics fm = g.getFontMetrics();
            g.drawString(text, (getWidth() - fm.stringWidth(text)) / 2, 100);
            
            g.setFont(new Font("Arial", Font.PLAIN, 20));
            g.drawString("Explosion Volume:", getWidth() / 2 - 200, 150);
            
            explosionSlider.setRect(getWidth() / 2 - 100, 155, 200, 8);
            explosionHandle.setRect(
                getWidth() / 2 - 100 + ((Sound.explosionVolume + 30) / 36.0f * 200) - 5,
                151,
                10, 16
            );
            
            g.setColor(new Color(100, 100, 100));
            g.fillRect(explosionSlider.x, explosionSlider.y, explosionSlider.width, explosionSlider.height);
            g.setColor(Color.WHITE);
            g.fill3DRect(explosionHandle.x, explosionHandle.y, explosionHandle.width, explosionHandle.height, true);
            
            g.drawString("Bounce Volume:", getWidth() / 2 - 200, 190);
            
            dripSlider.setRect(getWidth() / 2 - 100, 195, 200, 8);
            dripHandle.setRect(
                getWidth() / 2 - 100 + ((Sound.dripVolume + 30) / 36.0f * 200) - 5,
                191,
                10, 16
            );
            
            g.setColor(new Color(100, 100, 100));
            g.fillRect(dripSlider.x, dripSlider.y, dripSlider.width, dripSlider.height);
            g.setColor(Color.WHITE);
            g.fill3DRect(dripHandle.x, dripHandle.y, dripHandle.width, dripHandle.height, true);
        }
        Font originalFont = g.getFont();
        g.setColor(Color.white);
        g.setFont(new Font("Arial", Font.PLAIN, 16));
        g.drawString("Alive: " + world.alive(), 10, 20);
        if (Config.showFPS) g.drawString(String.format("FPS: %.1f", world.fps()), 10, 38);
        g.setFont(originalFont);
    }
}
