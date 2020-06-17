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

public class LSB1ImplTest {

    @Test
    public void testConceal() {
        var in = new byte[12];
        Arrays.fill(in, 00, 4, (byte) 0xFF);
        Arrays.fill(in, 04, 8, (byte) 0x55);
        Arrays.fill(in, 8, 12, (byte) 0x00);

        var bm = new byte[100];
        Arrays.fill(bm, 0, 50, (byte) 0x00);
        Arrays.fill(bm, 50, 100, (byte) 0xFF);

        var bitmap = mock(IBmpFile.class);
        when(bitmap.getImageDataView()).thenReturn(ByteBuffer.wrap(bm));
        
        new LSB1Impl().conceal(in, bitmap);
        assertArrayFilledWith(bm, 0, 32, 0x01);
        assertArrayFilledWith(bm, 32, 50, 0x00, 0x01);
        assertArrayFilledWith(bm, 50, 64, 0xFE, 0xFF);
        assertArrayFilledWith(bm, 64, 96, 0xFE);
        assertArrayFilledWith(bm, 96, 100, 0xFF);
    }

    @Test
    public void testConcealTooBig() {
        var in = new byte[13];
        var bm = new byte[100];

        var bitmap = mock(IBmpFile.class);
        when(bitmap.getImageDataView()).thenReturn(ByteBuffer.wrap(bm));
        
        var lsb1 = new LSB1Impl();
        assertTrue(
            assertThrows(SteganoException.class, () -> lsb1.conceal(in, bitmap))
                .getMessage()
                .toLowerCase()
                .contains("too small"));
    }

    @Test
    public void testReveal() {
        var bm = new byte[60];
        Arrays.fill(bm, 00, 15, (byte) 0xFF);
        Arrays.fill(bm, 15, 30, (byte) 0x55);
        Arrays.fill(bm, 30, 45, (byte) 0xAA);
        Arrays.fill(bm, 45, 60, (byte) 0x00);

        var bitmap = mock(IBmpFile.class);
        when(bitmap.getImageDataView()).thenReturn(ByteBuffer.wrap(bm));

        var out = new LSB1Impl().reveal(bitmap);
        assertArrayFilledWith(out, 0, 3, 0xFF);
        assertArrayFilledWith(out, 3, 4, 0xFC);
        assertArrayFilledWith(out, 4, 7, 0x00);
    }
    
}
