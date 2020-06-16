package legendary.octo.sniffle.stegano;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

import legendary.octo.sniffle.core.DCommonFile;
import legendary.octo.sniffle.error.SteganoException;

public class SteganoFormatterTest {

    private void assertArrayFilledWith(int expected, byte[] actual, int inclusiveStart, int exclusiveEnd) {
        var expectedArray = new byte[exclusiveEnd-inclusiveStart];
        Arrays.fill(expectedArray, (byte) expected);

        var actualArray = Arrays.copyOfRange(actual, inclusiveStart, exclusiveEnd);
        assertArrayEquals(expectedArray, actualArray);
    }

    @Test
    public void testFormat() {
        var in = new byte[13];
        Arrays.fill(in, (byte) 0xFF);
        var out = new SteganoFormatter().format(new DCommonFile("x", in));

        assertArrayFilledWith(0x00, out, 0, 3); // First 3 crumbs are null
        assertArrayFilledWith(0x0D, out, 3, 4); // Last crumb of size field contains 0x0D = 13
        assertArrayFilledWith(0xFF, out, 4, 17); // Payload
        assertArrayFilledWith(0x2E, out, 17, 18); // "."
        assertArrayFilledWith(0x78, out, 18, 19); // "x"
        assertArrayFilledWith(0x00, out, 19, 20); // NULL
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

        assertArrayFilledWith(0x00, out, 0, 3); // First 3 crumbs of size field are null
        assertArrayFilledWith(0x0D, out, 3, 4); // Last crumb contains 0x0D = 13
        assertArrayFilledWith(0xFF, out, 4, 17); // Payload
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
        assertArrayFilledWith(0xFF, out.getBytes(), 0, 7);
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
    public void testScanunterminatedFileExtension() {
        var in = new byte[13];
        Arrays.fill(in, 0, 3, (byte) 0x00); // First 3 crumbs of size field are null
        Arrays.fill(in, 3, 4, (byte) 0x07); // Last crumb contains 7
        Arrays.fill(in, 4, 11, (byte) 0xFF); // Payload
        Arrays.fill(in, 11, 12, (byte) 0x2E); // "."
        Arrays.fill(in, 12, 13, (byte) 0x78); // "x"

        var formatter = new SteganoFormatter();
        assertThrows(SteganoException.class, () -> formatter.scan(in));
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
        assertThrows(SteganoException.class, () -> formatter.scan(in));
    }

    @Test
    public void testScanEncrypted() {
        var in = new byte[11];
        Arrays.fill(in, 0, 3, (byte) 0x00); // First 3 crumbs of size field are null
        Arrays.fill(in, 3, 4, (byte) 0x07); // Last crumb contains 7
        Arrays.fill(in, 4, 11, (byte) 0xFF); // Payload

        var out = new SteganoFormatter().scanEncrypted(in);
        assertArrayFilledWith(0xFF, out, 0, 7);
        assertEquals(7, out.length);
    }

    @Test
    public void testScanEncryptedMalformed() {
        var in = new byte[11];
        Arrays.fill(in, 0, 3, (byte) 0x00); // First 3 crumbs of size field are null
        Arrays.fill(in, 3, 4, (byte) 0xBE); // Last crumb contains 190
        Arrays.fill(in, 4, 11, (byte) 0xFF); // Payload

        var formatter = new SteganoFormatter();
        assertThrows(SteganoException.class, () -> formatter.scan(in));
    }
    
}