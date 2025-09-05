import java.util.*;

public class World {
    public final java.util.List<Rock> rocks = new ArrayList<>();
    public final java.util.List<Explosion> explosions = new ArrayList<>();
    private final Random rand = new Random();
    private long frames;
    private long lastTime = System.currentTimeMillis();
    private double fps = 0;

    public void init() {
        for (Rock rock : rocks) {
            rock.stopThread();
        }
        rocks.clear();
        explosions.clear();
        for (int i = 0; i < Config.rockCount; i++) {
            Rock rock = makeRock(i);
            rocks.add(rock);
            rock.startThread();
        }
    }

    private Rock makeRock(int i) {
        int size = rand.nextInt(Config.rockMax - Config.rockMin + 1) + Config.rockMin;
        double x = rand.nextInt(Math.max(1, Config.w - 2 * size)) + size;
        double y = rand.nextInt(Math.max(1, Config.h - 2 * size)) + size;
        double angle = rand.nextDouble() * Math.PI * 2;
        double speed = Config.speedMin + rand.nextDouble() * (Config.speedMax - Config.speedMin) + i * 0.03;
        double dx = Math.cos(angle) * speed;
        double dy = Math.sin(angle) * speed;
        return new Rock(x, y, dx, dy, size);
    }
    
    public void addNewRock(double x, double y) {
        int size = rand.nextInt(Config.rockMax - Config.rockMin + 1) + Config.rockMin;
        double angle = rand.nextDouble() * Math.PI * 2;
        double speed = Config.speedMin + rand.nextDouble() * (Config.speedMax - Config.speedMin);
        double dx = Math.cos(angle) * speed;
        double dy = Math.sin(angle) * speed;
        Rock rock = new Rock(x, y, dx, dy, size);
        rocks.add(rock);
        rock.startThread();
    }

    public void update() {
        Background.update();
        
        rocks.removeIf(rock -> {
            if (rock.isExplodeFinished()) {
                rock.stopThread();
                if (View.autoSpawn) {
                    View.toSpawn++;
                }
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
        
        frames++;
        
        long now = System.currentTimeMillis();
        if (now - lastTime >= 1000) {
            fps = frames * 1000.0 / (now - lastTime);
            frames = 0;
            lastTime = now;
        }
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
        return Config.showFPS ? fps : 0;
    }
}