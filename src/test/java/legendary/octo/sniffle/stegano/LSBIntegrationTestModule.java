package legendary.octo.sniffle.stegano;

import com.google.inject.AbstractModule;

import legendary.octo.sniffle.core.IBmpFileIO;
import legendary.octo.sniffle.core.ICipher;
import legendary.octo.sniffle.core.IFileIO;
import legendary.octo.sniffle.core.IRC4;
import legendary.octo.sniffle.core.IStegano;
import legendary.octo.sniffle.core.ISteganoFormatter;
import legendary.octo.sniffle.crypto.CipherImpl;
import legendary.octo.sniffle.crypto.CryptoModule;
import legendary.octo.sniffle.crypto.RC4Impl;
import legendary.octo.sniffle.io.BmpFileIO;
import legendary.octo.sniffle.io.FileIO;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class LSBIntegrationTestModule extends AbstractModule {

    private final Class<? extends IStegano> ISteganoImpl;

    @Override
    public void configure() {
        bind(IFileIO.class).to(FileIO.class);
        bind(IBmpFileIO.class).to(BmpFileIO.class);
        bind(IStegano.class).to(ISteganoImpl);
        bind(ISteganoFormatter.class).to(SteganoFormatter.class);
        bind(IRC4.class).to(RC4Impl.class);
        bind(ICipher.class).to(CipherImpl.class);
        install(new CryptoModule());
    }   
}
