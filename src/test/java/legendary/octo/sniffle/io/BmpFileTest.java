package legendary.octo.sniffle.io;

import static legendary.octo.sniffle.io.IOTestUtils.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import legendary.octo.sniffle.core.DCommonFile;
import legendary.octo.sniffle.error.BmpFileException;
import lombok.SneakyThrows;

public class BmpFileTest {

    @Test
    @SneakyThrows
    public void testOk() {
        var file = new BmpFile(getCommonFile("ok.bmp"));
        assertEquals(4, file.getWidth());
        assertEquals(5, file.getHeight());
        assertEquals(2835, file.getHorizontalResolution());
        assertEquals(2835, file.getVerticalResolution());
        assertTrue(file.getImageOffset() != 54); // Contains color table
    }

    @Test
    @SneakyThrows
    public void testUnaligned() {
        var file = new BmpFile(getCommonFile("unaligned.bmp"));
        assertEquals(3, file.getWidth());
        assertEquals(4, file.getHeight());
        assertEquals(54 + 4*4*3, file.getFileSize());
    }

    @Test
    @SneakyThrows
    public void testJpeg() {
        assertTrue(
            assertThrows(BmpFileException.class, () -> new BmpFile(getCommonFile("jpeg.bmp")))
            .getMessage()
            .contains("COMPRESSION"));
    }

    @Test
    @SneakyThrows
    public void test32b() {
        assertTrue(
            assertThrows(BmpFileException.class, () -> new BmpFile(getCommonFile("32b.bmp")))
            .getMessage()
            .contains("DEPTH"));
    }

    @Test
    public void testEmpty() {
        assertThrows(BmpFileException.class, () -> new BmpFile(new DCommonFile(".x", new byte[0])));
    }

    @Test
    @SneakyThrows
    public void testImageDataView() {
        var file = new BmpFile(getCommonFile("ok.bmp"));
        var view = file.getImageDataView();
        assertEquals(0x13, view.get(0));
        assertEquals(0x07, view.get(4*5*3-1));
    }

    @Test
    @SneakyThrows
    public void testCommonFile() {
        var commonFile = getCommonFile("ok.bmp");
        var bmp = new BmpFile(commonFile);
        assertEquals(commonFile, bmp.getCommonFile());
    }

    @Test
    @SneakyThrows
    public void testCommonFileNoExtension() {
        var commonFile = new DCommonFile("", getCommonFile("ok.bmp").getBytes());
        var bmp = new BmpFile(commonFile);
        assertEquals("bmp", bmp.getCommonFile().getExtension());
    }
}
