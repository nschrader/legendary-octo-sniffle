package legendary.octo.sniffle.stegano;

import static legendary.octo.sniffle.stegano.AssertBytesUtil.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.nio.ByteBuffer;
import java.util.Arrays;

import org.junit.jupiter.api.Test;

import legendary.octo.sniffle.core.IBmpFile;
import legendary.octo.sniffle.error.SteganoException;

public class LSB4ImplTest {

    @Test
    public void testConceal() {
        var in = new byte[12];
        Arrays.fill(in, 00, 4, (byte) 0xFF);
        Arrays.fill(in, 04, 8, (byte) 0x55);
        Arrays.fill(in, 8, 12, (byte) 0x00);

        var bm = new byte[30];
        Arrays.fill(bm, 0, 15, (byte) 0x00);
        Arrays.fill(bm, 15, 30, (byte) 0xFF);

        var bitmap = mock(IBmpFile.class);
        when(bitmap.getImageDataView()).thenReturn(ByteBuffer.wrap(bm));
        
        new LSB4Impl().conceal(in, bitmap);
        assertArrayFilledWith(bm, 0, 8, 0x0F);
        assertArrayFilledWith(bm, 8, 15, 0x05);
        assertArrayFilledWith(bm, 15, 16, 0xF5);
        assertArrayFilledWith(bm, 16, 24, 0xF0);
        assertArrayFilledWith(bm, 24, 30, 0xFF);
    }

    @Test
    public void testConcealTooBig() {
        var in = new byte[13];
        var bm = new byte[25];

        var bitmap = mock(IBmpFile.class);
        when(bitmap.getImageDataView()).thenReturn(ByteBuffer.wrap(bm));
        
        var lsb4 = new LSB4Impl();
        assertTrue(
            assertThrows(SteganoException.class, () -> lsb4.conceal(in, bitmap))
                .getMessage()
                .toLowerCase()
                .contains("too small"));
    }

    @Test
    public void testReveal() {
        var bm = new byte[20];
        Arrays.fill(bm, 00, 05, (byte) 0xFF);
        Arrays.fill(bm, 05, 10, (byte) 0x55);
        Arrays.fill(bm, 10, 15, (byte) 0xAA);
        Arrays.fill(bm, 15, 20, (byte) 0x00);

        var bitmap = mock(IBmpFile.class);
        when(bitmap.getImageDataView()).thenReturn(ByteBuffer.wrap(bm));

        var out = new LSB4Impl().reveal(bitmap);
        assertArrayFilledWith(out, 0, 2, 0xFF);
        assertArrayFilledWith(out, 2, 3, 0xF5);
        assertArrayFilledWith(out, 3, 5, 0x55);
        assertArrayFilledWith(out, 5, 7, 0xAA);
        assertArrayFilledWith(out, 7, 8, 0xA0);
        assertArrayFilledWith(out, 8, 10, 0x00);
    }    
}
