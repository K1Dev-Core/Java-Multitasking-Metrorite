import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class Galaxy {
    public static final List<Asteroid> list = new CopyOnWriteArrayList<>();
    public static final Object hitLock = new Object();

    public static void add(Asteroid a) { list.add(a); }
    public static void remove(Asteroid a) { list.remove(a); }
}
