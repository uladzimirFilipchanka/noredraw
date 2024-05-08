package noredraw;

import com.beust.jcommander.JCommander;
import noredraw.args.CliArgs;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AppRunner {

    public static void main(String[] args) {
        CliArgs cliArgs = new CliArgs();
        JCommander.newBuilder()
                .addObject(cliArgs)
                .build()
                .parse(args);

        Configuration.setConfig(cliArgs);

        StarterService service = new StarterService();
        service.start();
    }
}
