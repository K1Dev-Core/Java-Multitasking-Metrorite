public class Debug {
    public static void log(String message) {
        if (Config.debug) {
            System.out.println("[DEBUG] " + message);
        }
    }
}
