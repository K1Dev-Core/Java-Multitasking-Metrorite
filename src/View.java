import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.*;

public class View extends JPanel {
    private final World world;
    public static volatile boolean paused = false;
    private Rectangle expSlider = new Rectangle();
    private Rectangle dripSlider = new Rectangle();
    private Rectangle speedSlider = new Rectangle();
    private Rectangle bgSlider = new Rectangle();
    private Rectangle expHandle = new Rectangle();
    private Rectangle dripHandle = new Rectangle();
    private Rectangle speedHandle = new Rectangle();
    private Rectangle bgHandle = new Rectangle();
    private boolean dragExp = false;
    private boolean dragDrip = false;
    private boolean dragSpeed = false;
    private boolean dragBG = false;
    public static boolean autoSpawn = false;
    public static int toSpawn = 0;

    public View(World world) {
        this.world = world;
        setPreferredSize(new Dimension(Config.w, Config.h));
        new Timer(Config.updateTime, _ -> { 
            if (!paused) {
                world.update();
                if (autoSpawn && toSpawn > 0 && !world.rocks.isEmpty()) {
                    world.addNewRock(
                        (int)(Math.random() * (Config.w - 100)) + 50,
                        (int)(Math.random() * (Config.h - 100)) + 50
                    );
                    toSpawn--;
                }
            }
        }).start();
        Sound.playBG();
        new Timer(Config.frameTime, _ -> repaint()).start();
        
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    if (!world.rocks.isEmpty()) {
                        paused = !paused;
                        repaint();
                    }
                }
                if (e.getKeyCode() >= KeyEvent.VK_0 && e.getKeyCode() <= KeyEvent.VK_9) {
                    if (world.rocks.isEmpty()) {
                        int num = e.getKeyCode() - KeyEvent.VK_0;
                        Config.rockCount = Config.rockCount * 10 + num;
                        if (Config.rockCount > Config.rockMaxCount) Config.rockCount = Config.rockMaxCount;
                        repaint();
                    }
                }
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    if (world.rocks.isEmpty() && Config.rockCount > 0) {
                        world.init();
                        repaint();
                    }
                }
                if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
                    if (world.rocks.isEmpty()) {
                        Config.rockCount = Config.rockCount / 10;
                        repaint();
                    }
                }
                if (e.getKeyCode() == KeyEvent.VK_R) {
                                            if (!Config.cameraMode) {
                            if (!world.rocks.isEmpty()) {
                                Rock target = world.rocks.get((int)(Math.random() * world.rocks.size()));
                                Config.cameraX = target.posX;
                                Config.cameraY = target.posY;
                                Config.cameraMode = true;
                            }
                        } else {
                            Config.cameraMode = false;
                        }
                    repaint();
                }
                if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                    if (world.rocks.isEmpty()) {
                        autoSpawn = !autoSpawn;
                        if (autoSpawn) {
                            toSpawn = 1;
                        }
                        repaint();
                    } else {
                        autoSpawn = !autoSpawn;
                        if (autoSpawn) {
                            toSpawn = 1;
                        }
                        repaint();
                    }
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
        }
        
        if (Config.debug){
            addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    if (world.rocks.isEmpty()) {
                        return;
                    }
                    if (!paused) {
                        if (e.getButton() == MouseEvent.BUTTON1) {
                            world.explosions.add(new Explosion(e.getX(), e.getY()));
                        } else if (e.getButton() == MouseEvent.BUTTON3) {
                            world.addNewRock(e.getX(), e.getY());
                        }
                    } else {
                        if (expHandle.contains(e.getPoint())) {
                            dragExp = true;
                        } else if (dripHandle.contains(e.getPoint())) {
                            dragDrip = true;
                        } else if (speedHandle.contains(e.getPoint())) {
                            dragSpeed = true;
                        } else if (bgHandle.contains(e.getPoint())) {
                            dragBG = true;
                        }
                    }
                }
                
                @Override
                public void mouseReleased(MouseEvent e) {
                    dragExp = false;
                    dragDrip = false;
                    dragSpeed = false;
                    dragBG = false;
                }
            });
            
            addMouseMotionListener(new MouseMotionAdapter() {
                @Override
                public void mouseDragged(MouseEvent e) {
                    if (paused) {
                        if (dragExp) {
                            float vol = ((e.getX() - (getWidth() / 2 - 100)) / 200.0f * 36.0f) - 30;
                            Sound.expVol = Math.max(-30, Math.min(6, vol));
                            repaint();
                        } else if (dragDrip) {
                            float vol = ((e.getX() - (getWidth() / 2 - 100)) / 200.0f * 36.0f) - 30;
                            Sound.dripVol = Math.max(-30, Math.min(6, vol));
                            repaint();
                        } else if (dragSpeed) {
                            float speed = ((e.getX() - (getWidth() / 2 - 100)) / 200.0f * 3.0f) + 0.5f;
                            Config.speedMin = Math.max(0.5f, Math.min(3.5f, speed));
                            Config.speedMax = Config.speedMin + 2.0f;
                            
                            for (Rock rock : world.rocks) {
                                if (!rock.isExploding()) {
                                    double currentSpeed = rock.speed();
                                    if (currentSpeed > 0) {
                                        double ratio = (Config.speedMin + (Config.speedMax - Config.speedMin) / 2) / currentSpeed;
                                        rock.speedX *= ratio;
                                        rock.speedY *= ratio;
                                    }
                                }
                            }
                            repaint();
                        } else if (dragBG) {
                            float vol = ((e.getX() - (getWidth() / 2 - 100)) / 200.0f * 36.0f) - 30;
                            Sound.bgVol = Math.max(-30, Math.min(6, vol));
                            Sound.updateBGVolume();
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
 
        g.setColor(new Color(7, 7, 7, 242));
        g.drawRect(0, 0, getWidth()-1, getHeight()-1);
        g.drawRect(1, 1, getWidth()-3, getHeight()-3);
        
        for (Rock rock : world.rocks) {
            int drawX = (int) rock.posX;
            int drawY = (int) rock.posY;
            
            int drawSize = rock.size;
            
            if (Config.cameraMode) {
                drawX = (int)((rock.posX - Config.cameraX) * Config.cameraZoom + getWidth() / 2);
                drawY = (int)((rock.posY - Config.cameraY) * Config.cameraZoom + getHeight() / 2);
                drawSize = (int)(rock.size * Config.cameraZoom);
            }
            
            if (rock.isExploding()) {
                ExplosionSprite.drawFrame(g, rock.getExplosionFrame(),
                        drawX, drawY, drawSize * 4);
            } else {
                SpriteSheet.drawFrame(g, rock.getSpriteType(), rock.getCurrentFrame(),
                        drawX, drawY, drawSize);
                        
                if (Config.debugMode) {
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
            int drawX = exp.x;
            int drawY = exp.y;
            int drawSize = exp.size;
            
            if (Config.cameraMode) {
                drawX = (int)((exp.x - Config.cameraX) * Config.cameraZoom + getWidth() / 2);
                drawY = (int)((exp.y - Config.cameraY) * Config.cameraZoom + getHeight() / 2);
                drawSize = (int)(exp.size * Config.cameraZoom);
            }
            
            ExplosionSprite.drawFrame(g, exp.frame, drawX, drawY, drawSize * 4);
        }
        
        if (world.rocks.isEmpty()) {
            g.setColor(new Color(0, 0, 0, 220));
            g.fillRect(0, 0, getWidth(), getHeight());
            
            int centerX = getWidth() / 2;
            int centerY = getHeight() / 2;

            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 42));
            String title = "Enter Meteor Count";
            FontMetrics fm = g.getFontMetrics();
            g.drawString(title, centerX - fm.stringWidth(title) / 2, centerY - 120);
            
            g.setFont(new Font("Arial", Font.BOLD, 28));
            g.setColor(new Color(255, 215, 0));
            String countText = "Count: " + Config.rockCount;
            fm = g.getFontMetrics();
            g.drawString(countText, centerX - fm.stringWidth(countText) / 2, centerY - 60);
            
            g.setFont(new Font("Arial", Font.PLAIN, 18));
            g.setColor(new Color(200, 200, 200));
            String instruction1 = "Press 0-9 to input number, ENTER to start";
            fm = g.getFontMetrics();
            g.drawString(instruction1, centerX - fm.stringWidth(instruction1) / 2, centerY - 20);
            
            String instruction2 = "SPACE for Auto Spawn";
            fm = g.getFontMetrics();
            g.drawString(instruction2, centerX - fm.stringWidth(instruction2) / 2, centerY + 10);
            
            g.setColor(new Color(40, 40, 40));
            g.fillRoundRect(centerX - 120, centerY + 30, 240, 35, 8, 8);
            g.setColor(autoSpawn ? new Color(0, 150, 0) : new Color(150, 0, 0));
            g.fillRoundRect(centerX - 115, centerY + 35, 230, 25, 5, 5);
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 16));
            String autoText = autoSpawn ? "AUTO SPAWN: ON" : "AUTO SPAWN: OFF";
            fm = g.getFontMetrics();
            g.drawString(autoText, centerX - fm.stringWidth(autoText) / 2, centerY + 52);

        }
        
        if (paused) {
            g.setColor(new Color(0, 0, 0, 220));
            g.fillRect(0, 0, getWidth(), getHeight());
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 48));
            String text = "PAUSED [DebugMode]";
            FontMetrics fm = g.getFontMetrics();
            g.drawString(text, (getWidth() - fm.stringWidth(text)) / 2, 100);
            
            g.setFont(new Font("Arial", Font.PLAIN, 20));
            g.drawString("Explosion Volume:", getWidth() / 2 - 200, 150);
            
            expSlider.setRect(getWidth() / 2 - 100, 155, 200, 8);
            expHandle.setRect(
                getWidth() / 2 - 100 + ((Sound.expVol + 30) / 36.0f * 200) - 5,
                151,
                10, 16
            );
            
            g.setColor(new Color(100, 100, 100));
            g.fillRect(expSlider.x, expSlider.y, expSlider.width, expSlider.height);
            g.setColor(Color.WHITE);
            g.fill3DRect(expHandle.x, expHandle.y, expHandle.width, expHandle.height, true);
            
            g.drawString("Bounce Volume:", getWidth() / 2 - 200, 190);
            
            dripSlider.setRect(getWidth() / 2 - 100, 195, 200, 8);
            dripHandle.setRect(
                getWidth() / 2 - 100 + ((Sound.dripVol + 30) / 36.0f * 200) - 5,
                191,
                10, 16
            );
            
            g.setColor(new Color(100, 100, 100));
            g.fillRect(dripSlider.x, dripSlider.y, dripSlider.width, dripSlider.height);
            g.setColor(Color.WHITE);
            g.fill3DRect(dripHandle.x, dripHandle.y, dripHandle.width, dripHandle.height, true);
            
            g.drawString("Rock Speed:", getWidth() / 2 - 200, 230);
            
            speedSlider.setRect(getWidth() / 2 - 100, 235, 200, 8);
            speedHandle.setRect(
                getWidth() / 2 - 100 + ((Config.speedMin - 0.5f) / 3.0f * 200) - 5,
                231,
                10, 16
            );
            
            g.setColor(new Color(100, 100, 100));
            g.fillRect(speedSlider.x, speedSlider.y, speedSlider.width, speedSlider.height);
            g.setColor(Color.WHITE);
            g.fill3DRect(speedHandle.x, speedHandle.y, speedHandle.width, speedHandle.height, true);
            
            g.drawString("Background Music:", getWidth() / 2 - 200, 270);
            
            bgSlider.setRect(getWidth() / 2 - 100, 275, 200, 8);
            bgHandle.setRect(
                getWidth() / 2 - 100 + ((Sound.bgVol + 30) / 36.0f * 200) - 5,
                271,
                10, 16
            );
            
            g.setColor(new Color(100, 100, 100));
            g.fillRect(bgSlider.x, bgSlider.y, bgSlider.width, bgSlider.height);
            g.setColor(Color.WHITE);
            g.fill3DRect(bgHandle.x, bgHandle.y, bgHandle.width, bgHandle.height, true);
            
            g.drawString("Auto Spawn (SPACE):", getWidth() / 2 - 200, 320);
            g.setColor(autoSpawn ? Color.GREEN : Color.RED);
            g.fillRect(getWidth() / 2 - 100, 325, 200, 20);
            g.setColor(Color.WHITE);
            g.drawString(autoSpawn ? "ON" : "OFF", getWidth() / 2 - 10, 340);
            
            g.setFont(new Font("Tahoma", Font.BOLD, 28));
            g.setColor(new Color(255, 215, 0));
            g.drawString("ทีมพัฒนา", getWidth() / 2 - 60, 380);
            
            int centerX = getWidth() / 2;
            int startY = 420;
            int spacing = 80;
            
            int[] positions = {centerX - spacing, centerX, centerX + spacing};
            String[] names = {"วชิรวิทย์ วงค์แสง", "ชินดนัย ภูหัดสวน", "นางสาวเศรณี ภูนาโพธิ์"};
            String[] ids = {"(67011212055)", "(67011212026)", "(67011212143)"};
            String[] roles = {"Project Manager", "Lead Developer", "UI/UX Designer"};
            
            for (int i = 0; i < 3; i++) {
                g.setFont(new Font("Tahoma", Font.BOLD, 16));
                g.setColor(Color.WHITE);
                int nameWidth = g.getFontMetrics().stringWidth(names[i]);
                g.drawString(names[i], positions[i] - nameWidth/2, startY);
                
                g.setFont(new Font("Tahoma", Font.PLAIN, 14));
                g.setColor(new Color(200, 200, 200));
                int idWidth = g.getFontMetrics().stringWidth(ids[i]);
                g.drawString(ids[i], positions[i] - idWidth/2, startY + 25);
                
                g.setColor(new Color(150, 150, 150));
                int roleWidth = g.getFontMetrics().stringWidth(roles[i]);
                g.drawString(roles[i], positions[i] - roleWidth/2, startY + 45);
                
                startY += 100;
            }
            
            g.setFont(new Font("Tahoma", Font.ITALIC, 16));
            g.setColor(new Color(150, 150, 150));
            String gameTitle = "Java Multitasking Metrorite Game";
            int titleWidth = g.getFontMetrics().stringWidth(gameTitle);
            g.drawString(gameTitle, centerX - titleWidth/2, startY + 20);
            
        }
        Font originalFont = g.getFont();
        g.setColor(Color.white);
        g.setFont(new Font("Arial", Font.PLAIN, 16));
        g.drawString("Alive: " + world.alive(), 10, 20);
        if (Config.showFPS) g.drawString(String.format("FPS: %.0f", world.fps()), 10, 38);
        if (Config.cameraMode) {
            g.drawString("CAMERA MODE (R to exit)", 10, 56);
            g.drawString("Target: " + (int)Config.cameraX + "," + (int)Config.cameraY, 10, 74);
        }
        g.setFont(originalFont);
    }
}
