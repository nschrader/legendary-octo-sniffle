package legendary.octo.sniffle.io;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.google.common.io.Resources;

import org.junit.jupiter.api.Test;

import legendary.octo.sniffle.error.BmpFileException;
import lombok.SneakyThrows;

public class BmpFileTest {

    @Test
    @SneakyThrows
    public void testOk() {
        var file = new BmpFile(Resources.toByteArray(Resources.getResource("ok.bmp")));
        assertEquals(4, file.getWidth());
        assertEquals(5, file.getHeight());
        assertEquals(2835, file.getHorizontalResolution());
        assertEquals(2835, file.getVerticalResolution());
    }

    @Test
    @SneakyThrows
    public void testJpeg() {
        assertTrue(
            assertThrows(BmpFileException.class, () -> new BmpFile(Resources.toByteArray(Resources.getResource("jpeg.bmp"))))
            .getMessage()
            .contains("COMPRESSION"));
    }

    @Test
    @SneakyThrows
    public void test32b() {
        assertTrue(
            assertThrows(BmpFileException.class, () -> new BmpFile(Resources.toByteArray(Resources.getResource("32b.bmp"))))
            .getMessage()
            .contains("DEPTH"));
    }

    @Test
    public void testEmpty() {
        assertThrows(BmpFileException.class, () -> new BmpFile(new byte[0]));
    }

    @Test
    @SneakyThrows
    public void testImageData() {
        var file = new BmpFile(Resources.toByteArray(Resources.getResource("ok.bmp")));
        assertEquals(0x13, file.getImageData(0));
        assertEquals(0x07, file.getImageData(4*5*3-1));
    }
}
