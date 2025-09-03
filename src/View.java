import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.*;

public class View extends JPanel {
    private final World world;

    public View(World world) {
        this.world = world;
        setPreferredSize(new Dimension(Config.screenWidth, Config.screenHeight));
        new Timer(Config.updateDelay, _ -> world.update()).start();
        new Timer(Config.frameDelay, _ -> repaint()).start();

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
        }


        if (Config.debug){
            addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseClicked(java.awt.event.MouseEvent e) {

                    int mouseX = e.getX();
                    int mouseY = e.getY();

                    if (e.getButton() == java.awt.event.MouseEvent.BUTTON1) {

                        world.explosions.add(new Explosion(mouseX, mouseY));
                    } else if (e.getButton() == java.awt.event.MouseEvent.BUTTON3) {

                        world.addNewRock(mouseX, mouseY);
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
                        
                if (Config.debug) {
                    g.setColor(Color.WHITE);
                    String info = String.format("ID:%d [%d,%d]", 
                        rock.rockID, (int)rock.posX, (int)rock.posY);
                    Debug.log(info);

                    g.setColor(new Color(255, 0, 0, 50));
                    g.drawOval((int)(rock.posX - rock.size), (int)(rock.posY - rock.size), 
                             rock.size * 2, rock.size * 2);
                             

                    g.setColor(new Color(0, 255, 0, 100));
                    int lineLength = 50;
                    g.drawLine((int)rock.posX, (int)rock.posY,
                             (int)(rock.posX + rock.speedX * lineLength),
                             (int)(rock.posY + rock.speedY * lineLength));
                }
            }
        }
        

        for (Explosion exp : world.explosions) {
            exp.draw(g);
        }
        
        g.setColor(Color.white);
        g.drawString("Alive: " + world.alive(), 10, 20);
        if (Config.showFPS) g.drawString(String.format("FPS: %.1f", world.fps()), 10, 38);
    }
}
