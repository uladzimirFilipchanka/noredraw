package creator;

public class Configuration {
    private static CliArgs args;

    static void setConfig(CliArgs newArgs) {
        args = newArgs;
    }

    public static CliArgs config() {
        if (args == null) {
            throw new IllegalArgumentException("Application has not been initialized");
        }
        return args;
    }
}
