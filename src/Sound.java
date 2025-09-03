import java.io.File;
import java.util.concurrent.*;
import javax.sound.sampled.*;

public class Sound {
    private static final int POOL_SIZE = 8;
    private static final ExecutorService soundPool = Executors.newFixedThreadPool(POOL_SIZE);
    private static final byte[] soundData;
    private static final AudioFormat format;
    
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
        
        soundPool.submit(() -> {
            try {
                SourceDataLine line = AudioSystem.getSourceDataLine(format);
                line.open(format);
                line.start();
                line.write(soundData, 0, soundData.length);
                line.drain();
                line.close();
            } catch (Exception e) {
                Debug.log("Error playing sound: " + e.getMessage());
            }
        });
    }
}