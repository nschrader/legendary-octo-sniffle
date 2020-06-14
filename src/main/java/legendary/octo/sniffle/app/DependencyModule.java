package legendary.octo.sniffle.app;

import static legendary.octo.sniffle.core.EStegano.LSB1;
import static legendary.octo.sniffle.core.EStegano.LSB4;
import static legendary.octo.sniffle.core.EStegano.LSBI;

import com.google.inject.AbstractModule;

import legendary.octo.sniffle.cipher.CipherImpl;
import legendary.octo.sniffle.cipher.RC4Impl;
import legendary.octo.sniffle.core.IBmpFileIO;
import legendary.octo.sniffle.core.ICipher;
import legendary.octo.sniffle.core.IFileIO;
import legendary.octo.sniffle.core.IRC4;
import legendary.octo.sniffle.core.IStegano;
import legendary.octo.sniffle.io.BmpFileIO;
import legendary.octo.sniffle.io.FileIO;
import legendary.octo.sniffle.stegano.LSB1Impl;
import legendary.octo.sniffle.stegano.LSB4Impl;
import legendary.octo.sniffle.stegano.LSBIImpl;

/**
 * Configure Guice dependency injection to use concrete implementations 
 * form sub-packages for given abstractions.
 */
public class DependencyModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(IFileIO.class).to(FileIO.class);
        bind(IBmpFileIO.class).to(BmpFileIO.class);
        bind(ICipher.class).to(CipherImpl.class);
        bind(IRC4.class).to(RC4Impl.class);
        bind(IStegano.class).annotatedWith(LSB1.named()).to(LSB1Impl.class);
        bind(IStegano.class).annotatedWith(LSB4.named()).to(LSB4Impl.class);
        bind(IStegano.class).annotatedWith(LSBI.named()).to(LSBIImpl.class);
    }
}
