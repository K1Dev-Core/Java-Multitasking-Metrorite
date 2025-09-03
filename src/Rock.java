public class Rock {
    private static int rockCount = 0;  
    public final int rockID = rockCount++; 
    public double posX, posY;     
    public double speedX, speedY; 
    public int size;          
    private double animationTime;  
    private static final double ANIMATION_SPEED = 0.2; 
    private static final int TOTAL_FRAMES = 3;
    private final int spriteType;  
    private boolean isExploding;
    private int explosionFrame;
    


    public Rock(double x, double y, double dx, double dy, int r) {
        this.posX = x;
        this.posY = y;
        this.speedX = dx;
        this.speedY = dy;
        this.size = r;
        this.animationTime = Math.random() * ANIMATION_SPEED;
        this.spriteType = rockID % Config.meteorSpriteSheetMax;
        this.isExploding = false;
        this.explosionFrame = 0;
    }

    public void explode() {
        if (!isExploding) {
            isExploding = true;
            explosionFrame = 0;
            Sound.playExplosion();
        }
    }

    public boolean isExploding() {
        return isExploding;
    }

    public boolean isExplodeFinished() {
        return isExploding && explosionFrame >= 30;
    }

    public int getExplosionFrame() {
        return explosionFrame;
    }

    // ความเร็ว = √(speedX² + speedY²)
    public double speed() {
        return Math.sqrt(speedX * speedX + speedY * speedY); 
    }

        public void move() {
        if (!isExploding) {
            posX += speedX;  
            posY += speedY;  
            
            double speedMultiplier = speed() / Config.rockSpeedMin; 
            animationTime += Config.updateDelay / 1000.0 * speedMultiplier;  
        } else {
            explosionFrame++;
        }
    }

        public int getCurrentFrame() {
        double normalizedTime = (animationTime / ANIMATION_SPEED) % 1.0; 
        return (int)(normalizedTime * TOTAL_FRAMES);
    }

    public int getSpriteType() {
        return spriteType;
    }

    // เด้งกลับถ้าชนขอบจอ
    public void bounceIfEdge() {
        boolean didBounce = false;
        if (posX - size < 0 && speedX < 0) { speedX = -speedX; didBounce = true; }  // ชนขอบซ้าย
        if (posX + size > Config.screenWidth && speedX > 0) { speedX = -speedX; didBounce = true; }  // ชนขอบขวา
        if (posY - size < 0 && speedY < 0) { speedY = -speedY; didBounce = true; }  // ชนขอบบน
        if (posY + size > Config.screenHeight && speedY > 0) { speedY = -speedY; didBounce = true; }  // ชนขอบล่าง
        if (didBounce) speedUp();  // ถ้าชนขอบให้เพิ่มความเร็ว
    }

    // เพิ่มความเร็ว
    private void speedUp() {
        speedX *= Config.rockSpeedUpRate;  
        speedY *= Config.rockSpeedUpRate; 
        double currentSpeed = speed();
        if (currentSpeed > Config.rockMaxSpeed) {  

            speedX = speedX * Config.rockMaxSpeed / currentSpeed;
            speedY = speedY * Config.rockMaxSpeed / currentSpeed;
        }
    }

    // เช้คว่าอุกกาบาตนี้ชนกับอีกก้อนมั้ย
    // สูตร: distance = √((x1 - x2)² + (y1 - y2)²)
    // ถ้า distance ≤ r1 + r2 แปลว่าชนกัน
    public boolean overlaps(Rock otherRock) {
 
        double dx = posX - otherRock.posX;
        double dy = posY - otherRock.posY;
        double distance = Math.sqrt(dx * dx + dy * dy);
        return distance <= size + otherRock.size;  
    }
}
