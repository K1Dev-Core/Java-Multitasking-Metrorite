import java.io.File;
import java.util.concurrent.*;
import javax.sound.sampled.*;

public class Sound {
    private static final int MAX = 20;
    private static final ExecutorService pool = Executors.newFixedThreadPool(MAX);
    private static final byte[] expData;
    private static final byte[] dripData;
    private static final byte[] bgData;
    private static final AudioFormat expFmt;
    private static final AudioFormat dripFmt;
    private static final AudioFormat bgFmt;
    public static float expVol = Config.expVol;
    public static float dripVol = Config.dripVol;
    public static float bgVol = Config.bgVol;
    private static Clip bgClip;
    private static long lastTime = 0;
    private static final long MIN_INTERVAL = 20;
    
    static {
        byte[] exp = null;
        byte[] drip = null;
        byte[] bg = null;
        AudioFormat expF = null;
        AudioFormat dripF = null;
        AudioFormat bgF = null;
        try {
            AudioInputStream expStream = AudioSystem.getAudioInputStream(new File("./res/sound/explosion.wav"));
            expF = expStream.getFormat();
            exp = expStream.readAllBytes();
            expStream.close();
            
            AudioInputStream dripStream = AudioSystem.getAudioInputStream(new File("./res/sound/drip.wav"));
            dripF = dripStream.getFormat();
            drip = dripStream.readAllBytes();
            dripStream.close();
            
            AudioInputStream bgStream = AudioSystem.getAudioInputStream(new File("./res/sound/bg.wav"));
            bgF = bgStream.getFormat();
            bg = bgStream.readAllBytes();
            bgStream.close();
            
            bgClip = AudioSystem.getClip();
            bgClip.open(bgF, bg, 0, bg.length);
            bgClip.loop(Clip.LOOP_CONTINUOUSLY);
            FloatControl bgGainControl = (FloatControl) bgClip.getControl(FloatControl.Type.MASTER_GAIN);
            bgGainControl.setValue(bgVol);
        } catch (Exception e) {
            Debug.log("Error loading sounds: " + e.getMessage());
        }
        expData = exp;
        dripData = drip;
        bgData = bg;
        expFmt = expF;
        dripFmt = dripF;
        bgFmt = bgF;
    }
    
    public static void playExplosion() {
        if (expData == null || expFmt == null) return;
        
        long now = System.currentTimeMillis();
        if (now - lastTime < MIN_INTERVAL) return;
        lastTime = now;
        
        pool.submit(() -> {
            try (SourceDataLine line = AudioSystem.getSourceDataLine(expFmt)) {
                line.open(expFmt, expData.length);
                FloatControl gainControl = (FloatControl) line.getControl(FloatControl.Type.MASTER_GAIN);
                gainControl.setValue(expVol);
                line.start();
                line.write(expData, 0, expData.length);
                line.drain();
            } catch (Exception e) {
                Debug.log("Error playing sound: " + e.getMessage());
            }
        });
    }
    
    public static void playDrip() {
        if (dripData == null || dripFmt == null) return;
        
        pool.submit(() -> {
            try (SourceDataLine line = AudioSystem.getSourceDataLine(dripFmt)) {
                line.open(dripFmt, dripData.length);
                FloatControl gainControl = (FloatControl) line.getControl(FloatControl.Type.MASTER_GAIN);
                gainControl.setValue(dripVol);
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
            gainControl.setValue(bgVol);
        }
    }
}