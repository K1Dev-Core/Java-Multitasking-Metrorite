import java.util.*;

public class World {
    public final java.util.List<Rock> rocks = new ArrayList<>();
    public final java.util.List<Explosion> explosions = new ArrayList<>();
    private final Random rand = new Random();
    private long frameCount;
    private final long startTime = System.nanoTime();

    public void init() {
        rocks.clear();
        for (int i = 0; i < Config.rockAmount; i++) rocks.add(makeRock(i));
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
        rocks.add(new Rock(x, y, speedX, speedY, rockSize));
    }

    public void update() {

        rocks.removeIf(Rock::isExplodeFinished);
        

        for (Rock a : rocks) a.move();
        for (Rock a : rocks) a.bounceIfEdge();
        resolveHits();
        

        Iterator<Explosion> it = explosions.iterator();
        while (it.hasNext()) {
            Explosion exp = it.next();
            exp.update();
            

            for (Rock rock : rocks) {
                if (!rock.isExploding() && exp.hits(rock)) {
                    rock.explode();
                }
            }
            
            if (exp.isFinished()) {
                it.remove();
            }
        }
        
        frameCount++;
    }

    private void resolveHits() {
        if (rocks.size() <= 1) return;
        Set<Rock> rocksToRemove = new HashSet<>();
        

        rocks.removeIf(Rock::isExplodeFinished);
        
        int totalRocks = rocks.size();
        for (int i = 0; i < totalRocks; i++) {
            Rock rock1 = rocks.get(i);
            if (rock1.isExploding()) continue;
            for (int j = i + 1; j < totalRocks; j++) {
                Rock rock2 = rocks.get(j);
                if (rock2.isExploding()) continue;
                if (rock1.overlaps(rock2)) {
                    Debug.log("Collision detected between rocks " + rock1.rockID + " and " + rock2.rockID);
                    double speed1 = rock1.speed();
                    double speed2 = rock2.speed();
                    
                    if (Math.abs(speed1 - speed2) < 1e-9) {
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
        if (!rocksToRemove.isEmpty()) rocks.removeAll(rocksToRemove);
    }

    public int alive() {
        return rocks.size();
    }

    public double fps() {
        if (!Config.showFPS) return 0;
        long now = System.nanoTime();
        double sec = (now - startTime) / 1_000_000_000.0;
        return sec > 0 ? frameCount / sec : 0;
    }
}