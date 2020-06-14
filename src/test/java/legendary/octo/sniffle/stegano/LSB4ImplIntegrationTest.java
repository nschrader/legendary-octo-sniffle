package legendary.octo.sniffle.stegano;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import java.io.File;

import com.google.common.io.Resources;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.testing.fieldbinder.BoundFieldModule;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import legendary.octo.sniffle.core.IBmpFileIO;
import legendary.octo.sniffle.core.IFileIO;
import legendary.octo.sniffle.io.BmpFileIO;
import legendary.octo.sniffle.io.FileIO;

@ExtendWith(MockitoExtension.class)
public class LSB4ImplIntegrationTest {
    //TODO: Add error tests (nothing hidden, to big to hide)

    @Inject
    private IFileIO fileIO;

    @Inject
    private IBmpFileIO bmpFileIO;

    @Inject
    private LSB4Impl lsb4Impl;

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

    @Test
    public void conceal() {
        var carrierBmp = bmpFileIO.read(getResource("lado.bmp"));
        var toHide = fileIO.read(getResource("itba.png"));
        lsb4Impl.conceal(toHide, carrierBmp);

        var expected = bmpFileIO.read(getResource("ladoLSB4.bmp"));
        assertArrayEquals(expected.getBytes(), carrierBmp.getBytes());
    }

    @Test
    public void reveal() {
        var carrierBmp = bmpFileIO.read(getResource("ladoLSB4.bmp"));
        var result = lsb4Impl.reveal(carrierBmp);

        var expected = fileIO.read(getResource("itba.png"));
        assertArrayEquals(expected, result);
    }

    @Test
    public void invertible() {
        var carrierBmp = bmpFileIO.read(getResource("lado.bmp"));
        var toHide = fileIO.read(getResource("itba.png"));

        lsb4Impl.conceal(toHide, carrierBmp);
        var hidden = lsb4Impl.reveal(carrierBmp);

        assertArrayEquals(toHide, hidden);
    }

    private File getResource(String relativePath) {
        var absolutePath = Resources.getResource(relativePath).getPath();
        return new File(absolutePath);
    }
    
}
