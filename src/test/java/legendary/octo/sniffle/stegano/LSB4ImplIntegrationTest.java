package legendary.octo.sniffle.stegano;

import com.google.inject.Guice;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import legendary.octo.sniffle.core.ECipher;
import legendary.octo.sniffle.core.EMode;

public class LSB4ImplIntegrationTest extends ALSBIntegrationSharedTest {

    @BeforeEach
    public void setUp() {
        Guice.createInjector(new LSBIntegrationTestModule(LSB4Impl.class)).injectMembers(this);
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
