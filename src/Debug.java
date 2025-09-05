public class Debug {
    public static void log(String msg) {
        if (Config.debug) {
            System.out.println("[DEBUG] " + msg);
        }
    }
}
