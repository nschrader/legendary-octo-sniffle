package legendary.octo.sniffle.app;

import com.google.inject.AbstractModule;

import legendary.octo.sniffle.cli.CommandDispatcher;
import legendary.octo.sniffle.cli.SteganoResolver;

public class SkeletonModule extends AbstractModule {

    @Override
    protected void configure() {
        binder().requireExplicitBindings();
        bind(CommandDispatcher.class);
        bind(SteganoResolver.class);
    }
}