package legendary.octo.sniffle.io;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;

import com.google.common.io.Resources;

import org.junit.jupiter.api.Test;

import lombok.NonNull;

public class BmpFileTest {
    private @NonNull byte[] load(@NonNull String path) {
        try {
            return Resources.toByteArray(Resources.getResource(path));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testOk() {
        var file = new BmpFile(load("ok.bmp"));
        assertEquals(4, file.getWidth());
        assertEquals(5, file.getHeight());
        assertEquals(2835, file.getHorizontalResolution());
        assertEquals(2835, file.getVerticalResolution());
    }

    @Test
    public void testJpeg() {
        assertTrue(
            assertThrows(BmpFileException.class, () -> new BmpFile(load("jpeg.bmp")))
            .getMessage()
            .contains("COMPRESSION"));
    }

    @Test
    public void test32b() {
        assertTrue(
            assertThrows(BmpFileException.class, () -> new BmpFile(load("32b.bmp")))
            .getMessage()
            .contains("DEPTH"));
    }

    @Test
    public void testEmpty() {
        assertThrows(BmpFileException.class, () -> new BmpFile(new byte[0]));
    }

    @Test
    public void testImageData() {
        var file = new BmpFile(load("ok.bmp"));
        assertEquals(0x13, file.getImageData(0));
        assertEquals(0x07, file.getImageData(4*5*3-1));
    }
}
