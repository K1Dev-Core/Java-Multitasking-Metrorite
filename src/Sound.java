import java.io.File;
import javax.sound.sampled.*;
import java.util.concurrent.*;

public class Sound {
    private static final int MAX_SOUNDS = 16;
    private static final ExecutorService soundPool = Executors.newFixedThreadPool(MAX_SOUNDS);
    private static final byte[] soundData;
    private static final AudioFormat format;
    private static long lastPlayTime = 0;
    private static final long MIN_INTERVAL = 20;
    
    static {
        byte[] data = null;
        AudioFormat fmt = null;
        try {
            AudioInputStream stream = AudioSystem.getAudioInputStream(new File("./res/sound/explosion.wav"));
            fmt = stream.getFormat();
            data = stream.readAllBytes();
            stream.close();
        } catch (Exception e) {
            Debug.log("Error loading sound: " + e.getMessage());
        }
        soundData = data;
        format = fmt;
    }
    
    public static void playExplosion() {
        if (soundData == null || format == null) return;
        
        long now = System.currentTimeMillis();
        if (now - lastPlayTime < MIN_INTERVAL) return;
        lastPlayTime = now;
        
        soundPool.submit(() -> {
            try (SourceDataLine line = AudioSystem.getSourceDataLine(format)) {
                line.open(format, soundData.length);
                line.start();
                line.write(soundData, 0, soundData.length);
                line.drain();
            } catch (Exception e) {
                Debug.log("Error playing sound: " + e.getMessage());
            }
        });
    }
}