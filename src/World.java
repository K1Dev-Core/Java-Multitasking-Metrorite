import java.util.*;

public class World {
    public final java.util.List<Rock> rocks = new ArrayList<>();
    public final java.util.List<Explosion> explosions = new ArrayList<>();
    private final Random rand = new Random();
    private long frameCount;
    private final long startTime = System.nanoTime();

    public void init() {
        for (Rock rock : rocks) {
            rock.stopThread();
        }
        rocks.clear();
        explosions.clear();
        for (int i = 0; i < Config.rockAmount; i++) {
            Rock rock = makeRock(i);
            rocks.add(rock);
            rock.startThread();
        }
    }

    private Rock makeRock(int i) {
        int rockSize = rand.nextInt(Config.rockSizeMax - Config.rockSizeMin + 1) + Config.rockSizeMin;
        double posX = rand.nextInt(Math.max(1, Config.screenWidth - 2 * rockSize)) + rockSize;
        double posY = rand.nextInt(Math.max(1, Config.screenHeight - 2 * rockSize)) + rockSize;
        double angle = rand.nextDouble() * Math.PI * 2;
        double speed = Config.rockSpeedMin + rand.nextDouble() * (Config.rockSpeedMax - Config.rockSpeedMin) + i * 0.03;
        double speedX = Math.cos(angle) * speed;
        double speedY = Math.sin(angle) * speed;
        return new Rock(posX, posY, speedX, speedY, rockSize);
    }
    
    public void addNewRock(double x, double y) {
        int rockSize = rand.nextInt(Config.rockSizeMax - Config.rockSizeMin + 1) + Config.rockSizeMin;
        double angle = rand.nextDouble() * Math.PI * 2;
        double speed = Config.rockSpeedMin + rand.nextDouble() * (Config.rockSpeedMax - Config.rockSpeedMin);
        double speedX = Math.cos(angle) * speed;
        double speedY = Math.sin(angle) * speed;
        Rock rock = new Rock(x, y, speedX, speedY, rockSize);
        rocks.add(rock);
        rock.startThread();
    }

    public void update() {
        Background.update();
        
        rocks.removeIf(rock -> {
            if (rock.isExplodeFinished()) {
                rock.stopThread();
                return true;
            }
            return false;
        });
        
        resolveHits();
        

        for (Iterator<Explosion> it = explosions.iterator(); it.hasNext();) {
            Explosion exp = it.next();
            exp.update();
            
            if (exp.isFinished()) {
                it.remove();
                continue;
            }
            
            for (Rock rock : rocks) {
                if (!rock.isExploding() && exp.hits(rock)) {
                    rock.explode();
                }
            }
        }
        
        frameCount++;
    }

    private void resolveHits() {
        if (rocks.size() <= 1) return;
        int totalRocks = rocks.size();
        for (int i = 0; i < totalRocks; i++) {
            Rock rock1 = rocks.get(i);
            if (rock1.isExploding()) continue;
            for (int j = i + 1; j < totalRocks; j++) {
                Rock rock2 = rocks.get(j);
                if (rock2.isExploding()) continue;
                if (rock1.overlaps(rock2)) {
                    Debug.log("Hit: " + rock1.rockID + " vs " + rock2.rockID);
                    double speed1 = rock1.speed();
                    double speed2 = rock2.speed();
                    
                    if (Math.abs(speed1 - speed2) < 0.000000001) {
                        if (rock1.rockID > rock2.rockID) {
                            rock1.explode();
                        } else {
                            rock2.explode();
                        }
                    } else {
                        if (speed1 < speed2) {
                            rock1.explode();
                        } else {
                            rock2.explode();
                        }
                    }
                }
            }
        }
    }

    public int alive() {
        int count = 0;
        for (Rock rock : rocks) {
            if (!rock.isExploding()) {
                count++;
            }
        }
        return count;
    }

    public double fps() {
        if (!Config.showFPS) return 0;
        long now = System.nanoTime();
        double sec = (now - startTime) / 1_000_000_000.0;
        return sec > 0 ? frameCount / sec : 0;
    }
}