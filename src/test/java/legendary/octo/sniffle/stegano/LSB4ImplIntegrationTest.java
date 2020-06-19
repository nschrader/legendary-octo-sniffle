package legendary.octo.sniffle.stegano;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import legendary.octo.sniffle.app.CryptoModule;
import legendary.octo.sniffle.core.ECipher;
import legendary.octo.sniffle.core.EMode;
import legendary.octo.sniffle.core.IBmpFileIO;
import legendary.octo.sniffle.core.ICipher;
import legendary.octo.sniffle.core.IFileIO;
import legendary.octo.sniffle.core.IStegano;
import legendary.octo.sniffle.core.ISteganoFormatter;
import legendary.octo.sniffle.crypto.CipherImpl;
import legendary.octo.sniffle.io.BmpFileIO;
import legendary.octo.sniffle.io.FileIO;

public class LSB4ImplIntegrationTest extends ALSBIntegrationSharedTest {

    private static class TestModule extends AbstractModule {
        @Override
        public void configure() {
            bind(IFileIO.class).to(FileIO.class);
            bind(IBmpFileIO.class).to(BmpFileIO.class);
            bind(IStegano.class).to(LSB4Impl.class);
            bind(ISteganoFormatter.class).to(SteganoFormatter.class);
            bind(ICipher.class).to(CipherImpl.class);
        }
    }

    @BeforeEach
    public void setUp() {
        Guice.createInjector(new TestModule(), new CryptoModule()).injectMembers(this);
    }

    @Test
    public void conceal() {
        concealTestVector("lado.bmp", "itba.png", toPlaintext(), "ladoLSB4.bmp");
    }

    @Test
    public void concealEncrypted() {
        concealTestVector("lado.bmp", "itba.png", toCiphertext("secreto", ECipher.aes256, EMode.ofb), "ladoLSB4aes256ofb.bmp");
    }

    @Test
    public void reveal() {
        revealTestVector("itba.png", fromPlaintext(), "ladoLSB4.bmp");
    }

    @Test
    public void revealEncrypted() {
        revealTestVector("itba.png", fromCiphertext("secreto", ECipher.aes256, EMode.ofb), "ladoLSB4aes256ofb.bmp");
    }
}
