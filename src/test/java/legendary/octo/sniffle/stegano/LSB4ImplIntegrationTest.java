package legendary.octo.sniffle.stegano;

import com.google.inject.Inject;

import org.junit.jupiter.api.Test;

public class LSB4ImplIntegrationTest extends ALSBIntegrationTest {
    //TODO: Add error tests (nothing hidden, to big to hide)

    @Inject
    private LSB4Impl lsb4Impl;

    @Test
    public void conceal() {
        concealAndCompareAgainstTestVector(lsb4Impl, "lado.bmp", "itba.png", "ladoLSB4.bmp");
    }

    @Test
    public void reveal() {
        revealAndCompareAgainstTestVector(lsb4Impl, "itba.png", "ladoLSB4.bmp");
    }

    @Test
    public void invertible() {
        concealAndReveal(lsb4Impl, "lado.bmp", "itba.png");
    }    
}
