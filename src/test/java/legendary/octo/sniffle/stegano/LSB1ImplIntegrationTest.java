package legendary.octo.sniffle.stegano;

import com.google.inject.Guice;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class LSB1ImplIntegrationTest extends ALSBIntegrationSharedTest {

    @BeforeEach
    public void setUp() {
        Guice.createInjector(new LSBIntegrationTestModule(LSB1Impl.class)).injectMembers(this);
    }

    @Test
    public void conceal() {
        concealTestVector("lado.bmp", "itba.png", toPlaintext(), "ladoLSB1.bmp");
    }

    @Test
    public void reveal() {
        revealTestVector("itba.png", fromPlaintext(), "ladoLSB1.bmp");
    }
}
