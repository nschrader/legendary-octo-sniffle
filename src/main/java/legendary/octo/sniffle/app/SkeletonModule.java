package legendary.octo.sniffle.app;

import com.google.inject.AbstractModule;

import legendary.octo.sniffle.cli.CommandDispatcher;
import legendary.octo.sniffle.cli.SteganoResolver;

/**
 * Configure Guice dependency injection to use given 
 * concrete implementations as an applications skeleton.
 */
public class SkeletonModule extends AbstractModule {

    @Override
    protected void configure() {
        binder().requireExplicitBindings();
        bind(CommandDispatcher.class);
        bind(SteganoResolver.class);
    }
}