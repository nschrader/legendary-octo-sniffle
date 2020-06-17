package legendary.octo.sniffle.stegano;

import static legendary.octo.sniffle.stegano.AssertBytesUtil.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

import legendary.octo.sniffle.core.DCommonFile;
import legendary.octo.sniffle.error.SteganoException;

public class SteganoFormatterTest {

    @Test
    public void testFormat() {
        var in = new byte[13];
        Arrays.fill(in, (byte) 0xFF);
        var out = new SteganoFormatter().format(new DCommonFile("x", in));

        assertArrayFilledWith(out, 0, 3, 0x00); // First 3 crumbs are null
        assertArrayFilledWith(out, 3, 4, 0x0D); // Last crumb of size field contains 0x0D = 13
        assertArrayFilledWith(out, 4, 17, 0xFF); // Payload
        assertArrayFilledWith(out, 17, 18, 0x2E, 0x78, 0x00); // ".x\0"
        assertEquals(20, out.length);
    }

    @Test
    public void testFormatNoExtension() {
        var file = new DCommonFile("", new byte[13]);
        var formatter = new SteganoFormatter();

        assertTrue(
            assertThrows(SteganoException.class, () -> formatter.format(file))
                .getMessage()
                .contains("extension"));
    }

    @Test
    public void testFormatEncrypted() {
        var in = new byte[13];
        Arrays.fill(in, (byte) 0xFF);
        var out = new SteganoFormatter().formatEncrypted(in);

        assertArrayFilledWith(out, 0, 3, 0x00); // First 3 crumbs of size field are null
        assertArrayFilledWith(out, 3, 4, 0x0D); // Last crumb contains 0x0D = 13
        assertArrayFilledWith(out, 4, 17, 0xFF); // Payload
        assertEquals(17, out.length);
    }

    @Test
    public void testScan() {
        var in = new byte[14];
        Arrays.fill(in, 0, 3, (byte) 0x00); // First 3 crumbs of size field are null
        Arrays.fill(in, 3, 4, (byte) 0x07); // Last crumb contains 7
        Arrays.fill(in, 4, 11, (byte) 0xFF); // Payload
        Arrays.fill(in, 11, 12, (byte) 0x2E); // "."
        Arrays.fill(in, 12, 13, (byte) 0x78); // "x"
        Arrays.fill(in, 13, 14, (byte) 0x00); // NULL

        var out = new SteganoFormatter().scan(in);
        assertEquals("x", out.getExtension());
        assertArrayFilledWith(out.getBytes(), 0, 7, 0xFF);
        assertEquals(7, out.getBytes().length);
    }

    @Test
    public void testScanGibberishFileExtension() {
        var in = new byte[14];
        Arrays.fill(in, 0, 3, (byte) 0x00); // First 3 crumbs of size field are null
        Arrays.fill(in, 3, 4, (byte) 0x07); // Last crumb contains 7
        Arrays.fill(in, 4, 11, (byte) 0xFF); // Payload
        Arrays.fill(in, 11, 12, (byte) 0x78); // "x"
        Arrays.fill(in, 12, 13, (byte) 0x79); // "y"
        Arrays.fill(in, 13, 14, (byte) 0x7A); // "z"

        var formatter = new SteganoFormatter();
        assertTrue(
            assertThrows(SteganoException.class, () -> formatter.scan(in))
                .getMessage()
                .contains("extension")
        );
    }

    @Test
    public void testScanUnterminatedFileExtension() {
        var in = new byte[13];
        Arrays.fill(in, 0, 3, (byte) 0x00); // First 3 crumbs of size field are null
        Arrays.fill(in, 3, 4, (byte) 0x07); // Last crumb contains 7
        Arrays.fill(in, 4, 11, (byte) 0xFF); // Payload
        Arrays.fill(in, 11, 12, (byte) 0x2E); // "."
        Arrays.fill(in, 12, 13, (byte) 0x78); // "x"

        var formatter = new SteganoFormatter();
        assertTrue(
            assertThrows(SteganoException.class, () -> formatter.scan(in))
                .getMessage()
                .toLowerCase()
                .contains("malformed"));
    }

    @Test
    public void testScanMalformed() {
        var in = new byte[14];
        Arrays.fill(in, 0, 3, (byte) 0x00); // First 3 crumbs of size field are null
        Arrays.fill(in, 3, 4, (byte) 0xBE); // Last crumb contains 190
        Arrays.fill(in, 4, 11, (byte) 0xFF); // Payload
        Arrays.fill(in, 11, 12, (byte) 0x2E); // "."
        Arrays.fill(in, 12, 13, (byte) 0x78); // "x"
        Arrays.fill(in, 13, 14, (byte) 0x00); // NULL

        var formatter = new SteganoFormatter();
        assertTrue(
            assertThrows(SteganoException.class, () -> formatter.scan(in))
                .getMessage()
                .toLowerCase()
                .contains("malformed"));
    }

    @Test
    public void testScanEncrypted() {
        var in = new byte[11];
        Arrays.fill(in, 0, 3, (byte) 0x00); // First 3 crumbs of size field are null
        Arrays.fill(in, 3, 4, (byte) 0x07); // Last crumb contains 7
        Arrays.fill(in, 4, 11, (byte) 0xFF); // Payload

        var out = new SteganoFormatter().scanEncrypted(in);
        assertArrayFilledWith(out, 0, 7, 0xFF);
        assertEquals(7, out.length);
    }

    @Test
    public void testScanEncryptedMalformed() {
        var in = new byte[11];
        Arrays.fill(in, 0, 3, (byte) 0x00); // First 3 crumbs of size field are null
        Arrays.fill(in, 3, 4, (byte) 0xBE); // Last crumb contains 190
        Arrays.fill(in, 4, 11, (byte) 0xFF); // Payload

        var formatter = new SteganoFormatter();
        assertTrue(
            assertThrows(SteganoException.class, () -> formatter.scanEncrypted(in))
                .getMessage()
                .toLowerCase()
                .contains("malformed"));
    }
}
