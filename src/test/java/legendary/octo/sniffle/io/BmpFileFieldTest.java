package legendary.octo.sniffle.io;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import static legendary.octo.sniffle.io.BmpFileField.*;

public class BmpFileFieldTest {

    @Test
    public void testOffsetFirst() {
        assertEquals(0x00, MAGIC_NUMBER_B.offset());
    }

    @Test
    public void testOffsetSomewhere() {
        assertEquals(0x1C, COLOR_DEPTH.offset());
    }

    @Test
    public void testOffsetLast() {
        assertEquals(0x32, RSVD4.offset());
    }

    @Test
    public void testValue() {
        assertEquals(24, COLOR_DEPTH.value);
    }

    @Test
    public void testSize() {
        assertEquals(4, FILE_SIZE.size.val());
    }
}
