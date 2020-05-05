package legendary.octo.sniffle.app;

import com.google.inject.Guice;
import com.google.inject.Inject;

import legendary.octo.sniffle.cli.CommandDispatcher;
import picocli.CommandLine;

/**
 * Entry point
 */
public class App {

    @Inject
    private CommandDispatcher commandDispatcher;

    public void run(String[] args) {
        Guice.createInjector(new SkeletonModule(), new DependencyModule()).injectMembers(this);
        new CommandLine(commandDispatcher).execute(args);
    }

    public static void main(String[] args) {
        new App().run(args);
    }
}
