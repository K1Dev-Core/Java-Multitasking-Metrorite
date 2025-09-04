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
            addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseClicked(java.awt.event.MouseEvent e) {
                    if (!isPaused) {
                        int mouseX = e.getX();
                        int mouseY = e.getY();
                        if (e.getButton() == java.awt.event.MouseEvent.BUTTON1) {
                            world.explosions.add(new Explosion(mouseX, mouseY));
                        } else if (e.getButton() == java.awt.event.MouseEvent.BUTTON3) {
                            world.addNewRock(mouseX, mouseY);
                        }
                    }        }    });
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Background.draw(g, getWidth(), getHeight());
        
        if (isPaused) {
            g.setColor(new Color(0, 0, 0, 128));
            g.fillRect(0, 0, getWidth(), getHeight());
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 48));
            String text = "PAUSED";
            FontMetrics fm = g.getFontMetrics();
            g.drawString(text, (getWidth() - fm.stringWidth(text)) / 2, getHeight() / 2);
        }

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
        Font originalFont = g.getFont();
        g.setColor(Color.white);
        g.setFont(new Font("Arial", Font.PLAIN, 16));
        g.drawString("Alive: " + world.alive(), 10, 20);
        if (Config.showFPS) g.drawString(String.format("FPS: %.1f", world.fps()), 10, 38);
        g.setFont(originalFont);
    }
}
