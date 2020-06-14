package legendary.octo.sniffle.stegano;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;

import com.google.common.io.Resources;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.testing.fieldbinder.BoundFieldModule;

import org.junit.jupiter.api.BeforeEach;

import legendary.octo.sniffle.core.IBmpFileIO;
import legendary.octo.sniffle.core.IFileIO;
import legendary.octo.sniffle.core.IStegano;
import legendary.octo.sniffle.io.BmpFileIO;
import legendary.octo.sniffle.io.FileIO;
import lombok.NonNull;

abstract class ALSBIntegrationTest {
    @Inject
    protected IFileIO fileIO;

    @Inject
    protected IBmpFileIO bmpFileIO;

    private static class TestModule extends AbstractModule {
        @Override
        public void configure() {
            bind(IFileIO.class).to(FileIO.class);
            bind(IBmpFileIO.class).to(BmpFileIO.class);
        }
    }

    @BeforeEach
    public void setUp() {
        Guice.createInjector(new TestModule(), BoundFieldModule.of(this)).injectMembers(this);
    }

    protected void concealAndCompareAgainstTestVector(
        @NonNull IStegano impl, 
        @NonNull String carrier, 
        @NonNull String secret, 
        @NonNull String testVector) {
        var carrierBmp = bmpFileIO.read(getResource(carrier));
        var toHide = fileIO.read(getResource(secret));
        impl.conceal(toHide, carrierBmp);

        var expected = bmpFileIO.read(getResource(testVector));
        assertEquals(expected.getCommonFile(), carrierBmp.getCommonFile());
    }

    protected void revealAndCompareAgainstTestVector(
        @NonNull IStegano impl, 
        @NonNull String secret, 
        @NonNull String testVector) {
        var carrierBmp = bmpFileIO.read(getResource(testVector));
        var result = impl.reveal(carrierBmp);

        var expected = fileIO.read(getResource(secret));
        assertEquals(expected, result);
    }

    protected void concealAndReveal(
        @NonNull IStegano impl, 
        @NonNull String carrier, 
        @NonNull String secret) {
        var carrierBmp = bmpFileIO.read(getResource(carrier));
        var toHide = fileIO.read(getResource(secret));

        impl.conceal(toHide, carrierBmp);
        var hidden = impl.reveal(carrierBmp);

        assertEquals(toHide, hidden);
    }

    protected File getResource(String relativePath) {
        var absolutePath = Resources.getResource(relativePath).getPath();
        return new File(absolutePath);
    }
    
}