package legendary.octo.sniffle.stegano;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.nio.ByteBuffer;
import java.util.Arrays;

import org.junit.jupiter.api.Test;

import legendary.octo.sniffle.core.IBmpFile;

public class LSB1ImplTest {
    //TODO: Add error tests (too big, nothing hidden, gibberish extension, no extension given)

    @Test
    public void testConceal() {
        final var IN_LEN = 13;
        final var BM_LEN = 256;
        final var LSB1_FACTOR = 8;
        final var LSB1_OFFSET = 32;

        var in = new byte[IN_LEN];
        Arrays.fill(in, (byte) 0xFF);

        var bm = new byte[BM_LEN];
        Arrays.fill(bm, (byte) 0x00);

        var bitmap = mock(IBmpFile.class);
        doAnswer(i -> bm[i.getArgument(0, Integer.class)]).when(bitmap).getImageData(any());
        doAnswer(i -> bm[i.getArgument(0, Integer.class)] = i.getArgument(1, Byte.class)).when(bitmap).putImageData(any(), any());

        new LSB1Impl().conceal(in, bitmap);

        for (var i = 0; i < IN_LEN*LSB1_FACTOR; i++) {
            assertEquals(0x01, bm[LSB1_OFFSET+i]);
        }
    }

    @Test
    public void testReveal() {
        final var OUT_LEN = 7;
        final var LSB1_FACTOR = 8;
        final var LSB1_OFFSET = 32;
        final var EXTENSION_LEN = 32;

        var bm = new byte[LSB1_OFFSET + OUT_LEN*LSB1_FACTOR + EXTENSION_LEN];
        Arrays.fill(bm, 00, 29, (byte) 0x00); // 29 integer MSB zero = 0x00
        Arrays.fill(bm, 29, 32, (byte) 0x01); // 3 integer LSB one = 0x07
        Arrays.fill(bm, 32, 88, (byte) 0x01); // all concealed data = 0xFF

        // Dot = 0x2E
        bm[88] = 0x00;
        bm[89] = 0x00;
        bm[90] = 0x01;
        bm[91] = 0x00;
        bm[92] = 0x01;
        bm[93] = 0x01;
        bm[94] = 0x01;
        bm[95] = 0x00;

        // x = 0x78
        Arrays.fill(bm, 96, 97, (byte) 0x00);
        Arrays.fill(bm, 97, 101, (byte) 0x01);
        Arrays.fill(bm, 101, 104, (byte) 0x00);

        // NULL = 0x00
        Arrays.fill(bm, 104, 112, (byte) 0x00);

        var bitmap = mock(IBmpFile.class);
        when(bitmap.getImageDataView()).thenReturn(ByteBuffer.wrap(bm));
        
        var expectedOut = new byte[OUT_LEN];
        Arrays.fill(expectedOut, (byte) 0xFF);

        var out = new LSB1Impl().reveal(bitmap);
        assertArrayEquals(expectedOut, out);
    }
    
}
