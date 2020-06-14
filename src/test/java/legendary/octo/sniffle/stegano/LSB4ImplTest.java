package legendary.octo.sniffle.stegano;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.nio.ByteBuffer;
import java.util.Arrays;

import org.junit.jupiter.api.Test;

import legendary.octo.sniffle.core.DCommonFile;
import legendary.octo.sniffle.core.IBmpFile;

public class LSB4ImplTest {
    //TODO: Add error tests (too big, nothing hidden, gibberish extension, no extension given)

    final Integer LSB4_FACTOR = 2;
    final Integer LSB4_OFFSET = 8;

    @Test
    public void testConceal() {
        final var IN_LEN = 13;
        final var BM_LEN = 128;

        var in = new byte[IN_LEN];
        Arrays.fill(in, (byte) 0xFF);

        var bm = new byte[BM_LEN];
        Arrays.fill(bm, (byte) 0x00);

        var bitmap = mock(IBmpFile.class);
        doAnswer(i -> bm[i.getArgument(0, Integer.class)]).when(bitmap).getImageData(any());
        doAnswer(i -> bm[i.getArgument(0, Integer.class)] = i.getArgument(1, Byte.class)).when(bitmap).putImageData(any(), any());

        new LSB4Impl().conceal(new DCommonFile("x", in), bitmap);

        //TODO: Test size field
        for (var i = 0; i < IN_LEN * LSB4_FACTOR; i++) {
            assertEquals(0x0F, bm[LSB4_OFFSET + i]);
        }
        // TODO: Test extension
    }

    @Test
    public void testReveal() {
        final var OUT_LEN = 7;
        final var EXTENSION_LEN = 8;

        var bm = new byte[LSB4_OFFSET + OUT_LEN*LSB4_FACTOR + EXTENSION_LEN];
        Arrays.fill(bm, 0, 7, (byte) 0x00); // 7 integer most significant crumb zero = 0x00
        Arrays.fill(bm, 7, 8, (byte) 0x07); // 1 integer least significant crumb 0x07
        Arrays.fill(bm, 8, 22, (byte) 0x0F); // all concealed data = 0xFF

        // Dot = 0x2E
        bm[22] = 0x02;
        bm[23] = 0x0E;

        // x = 0x78
        bm[24] = 0x07;
        bm[25] = 0x08;

        // NULL = 0x00
        Arrays.fill(bm, 26, 28, (byte) 0x00);

        var bitmap = mock(IBmpFile.class);
        when(bitmap.getImageDataView()).thenReturn(ByteBuffer.wrap(bm));
        
        var expectedOut = new byte[OUT_LEN];
        Arrays.fill(expectedOut, (byte) 0xFF);

        var out = new LSB4Impl().reveal(bitmap);
        assertEquals(new DCommonFile("x", expectedOut), out);
    }
    
}
