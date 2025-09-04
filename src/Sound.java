import java.io.File;
import java.util.concurrent.*;
import javax.sound.sampled.*;

public class Sound {
    private static final int MAX_SOUNDS = 20;
    private static final ExecutorService soundPool = Executors.newFixedThreadPool(MAX_SOUNDS);
    private static final byte[] explosionData;
    private static final byte[] dripData;
    private static final byte[] bgData;
    private static final AudioFormat explosionFormat;
    private static final AudioFormat dripFormat;
    private static final AudioFormat bgFormat;
    public static float explosionVolume = 0.0f;
    public static float dripVolume = -20.0f;
    public static float bgVolume = -15.0f;
    private static Clip bgClip;
    private static long lastPlayTime = 0;
    private static final long MIN_INTERVAL = 20;
    
    static {
        byte[] expData = null;
        byte[] drpData = null;
        byte[] bgDataTemp = null;
        AudioFormat expFmt = null;
        AudioFormat drpFmt = null;
        AudioFormat bgFmt = null;
        try {
            AudioInputStream expStream = AudioSystem.getAudioInputStream(new File("./res/sound/explosion.wav"));
            expFmt = expStream.getFormat();
            expData = expStream.readAllBytes();
            expStream.close();
            
            AudioInputStream dripStream = AudioSystem.getAudioInputStream(new File("./res/sound/drip.wav"));
            drpFmt = dripStream.getFormat();
            drpData = dripStream.readAllBytes();
            dripStream.close();
            
            AudioInputStream bgStream = AudioSystem.getAudioInputStream(new File("./res/sound/bg.wav"));
            bgFmt = bgStream.getFormat();
            bgDataTemp = bgStream.readAllBytes();
            bgStream.close();
            
            bgClip = AudioSystem.getClip();
            bgClip.open(bgFmt, bgDataTemp, 0, bgDataTemp.length);
            bgClip.loop(Clip.LOOP_CONTINUOUSLY);
            FloatControl bgGainControl = (FloatControl) bgClip.getControl(FloatControl.Type.MASTER_GAIN);
            bgGainControl.setValue(bgVolume);
        } catch (Exception e) {
            Debug.log("Error loading sounds: " + e.getMessage());
        }
        explosionData = expData;
        dripData = drpData;
        bgData = bgDataTemp;
        explosionFormat = expFmt;
        dripFormat = drpFmt;
        bgFormat = bgFmt;
    }
    
    public static void playExplosion() {
        if (explosionData == null || explosionFormat == null) return;
        
        long now = System.currentTimeMillis();
        if (now - lastPlayTime < MIN_INTERVAL) return;
        lastPlayTime = now;
        
        soundPool.submit(() -> {
            try (SourceDataLine line = AudioSystem.getSourceDataLine(explosionFormat)) {
                line.open(explosionFormat, explosionData.length);
                FloatControl gainControl = (FloatControl) line.getControl(FloatControl.Type.MASTER_GAIN);
                gainControl.setValue(explosionVolume);
                line.start();
                line.write(explosionData, 0, explosionData.length);
                line.drain();
            } catch (Exception e) {
                Debug.log("Error playing sound: " + e.getMessage());
            }
        });
    }
    
    public static void playDrip() {
        if (dripData == null || dripFormat == null) return;
        
        soundPool.submit(() -> {
            try (SourceDataLine line = AudioSystem.getSourceDataLine(dripFormat)) {
                line.open(dripFormat, dripData.length);
                FloatControl gainControl = (FloatControl) line.getControl(FloatControl.Type.MASTER_GAIN);
                gainControl.setValue(dripVolume);
                line.start();
                line.write(dripData, 0, dripData.length);
                line.drain();
            } catch (Exception e) {
                Debug.log("Error playing sound: " + e.getMessage());
            }
        });
    }
    
    public static void playBG() {
        if (bgClip != null && !bgClip.isRunning()) {
            bgClip.start();
        }
    }
    
    public static void stopBG() {
        if (bgClip != null && bgClip.isRunning()) {
            bgClip.stop();
        }
    }
    
    public static void updateBGVolume() {
        if (bgClip != null) {
            FloatControl gainControl = (FloatControl) bgClip.getControl(FloatControl.Type.MASTER_GAIN);
            gainControl.setValue(bgVolume);
        }
    }
}