package legendary.octo.sniffle.stegano;

import com.google.inject.Inject;

import org.junit.jupiter.api.Test;

public class LSB1ImplIntegrationTest extends ALSBIntegrationTest {
    //TODO: Add error tests (nothing hidden, to big to hide)

    @Inject
    private LSB1Impl lsb1Impl;

    @Test
    public void conceal() {
        concealAndCompareAgainstTestVector(lsb1Impl, "lado.bmp", "itba.png", "ladoLSB1.bmp");
    }

    @Test
    public void reveal() {
        revealAndCompareAgainstTestVector(lsb1Impl, "itba.png", "ladoLSB1.bmp");
    }

    @Test
    public void invertible() {
        concealAndReveal(lsb1Impl, "lado.bmp", "itba.png");
    }
}
