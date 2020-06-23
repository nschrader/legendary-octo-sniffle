package legendary.octo.sniffle.stegano;

import com.google.inject.Guice;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class LSBIImplIntegrationTest extends ALSBIntegrationSharedTest {

    @BeforeEach
    public void setUp() {
        Guice.createInjector(new LSBIntegrationTestModule(LSBIImpl.class)).injectMembers(this);
    }

    @Test
    public void conceal() {
        /*
         * lado.bmp was reconstructed from ladoLSB1.bmp and ladoLSB4.bmp.
         * That means that on the first 89844 bytes, the LSB is irrecoverably broken.
         * LSBI spreads the information in a way that is not able to cover this zone.
         * But we can try to rewrite the same data onto the same file as it should be idempotent.
         */
        concealTestVector("ladoLSBI.bmp", "itba.png", toPlaintext(), "ladoLSBI.bmp");
    }

    @Test
    public void reveal() {
        revealTestVector("itba.png", fromPlaintext(), "ladoLSBI.bmp");
    }
}
