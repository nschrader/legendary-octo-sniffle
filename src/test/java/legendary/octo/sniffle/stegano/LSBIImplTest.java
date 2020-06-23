package legendary.octo.sniffle.stegano;

import static legendary.octo.sniffle.stegano.AssertBytesUtil.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.stream.IntStream;

import com.google.common.primitives.Bytes;
import com.google.common.primitives.Ints;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.testing.fieldbinder.Bind;
import com.google.inject.testing.fieldbinder.BoundFieldModule;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import legendary.octo.sniffle.core.IBmpFile;
import legendary.octo.sniffle.core.IRC4;
import legendary.octo.sniffle.error.SteganoException;

@ExtendWith(MockitoExtension.class)
public class LSBIImplTest {

    @Mock
    IBmpFile bitmap;

    @Mock
    @Bind
    IRC4 rc4;

    @Inject
    LSBIImpl lsbI;

    @BeforeEach
    public void setUp() {
        Guice.createInjector(BoundFieldModule.of(this)).injectMembers(this);
    }

    @Test
    // Internal but test helped development
    public void testHopGetter() {
        var data = Bytes.toArray(Ints.asList(0xCA, 0x01, 0x04, 0x30, 0xFE, 0x02, 0x10, 0x40, 0xBE, 0x03, 0x20, 0x88, 0xEF));
        var bb = ByteBuffer.wrap(data);
        var id = lsbI.getHopImageDataGetter(bb, 4);

        var out = "";
        for (var i = 0; i < data.length; i++) {
            out += String.format("%02X", id.get());
        }
        assertEquals("CAFEBEEF010203041020304088", out);
    }

    @Test
    // Internal but test helped development
    public void testHopGetterBufferUnderflow() {
        var data = Bytes.toArray(Ints.asList(0x01, 0x02, 0x03, 0x40));
        var bb = ByteBuffer.wrap(data);
        var id = lsbI.getHopImageDataGetter(bb, 2);

        assertThrows(BufferUnderflowException.class, () -> IntStream.rangeClosed(1, 5).forEach(d -> id.get()));
    }

    @Test
    public void testConceal() {
        var in = new byte[] {(byte) 0x55};
        
        var bm = new byte[14];
        Arrays.fill(bm, 0, 6, (byte) 0x02); // Initialize key and hop to 2
        Arrays.fill(bm, 6, 10, (byte) 0x00);
        Arrays.fill(bm, 10, 14, (byte) 0xFF);

        when(bitmap.getImageDataView()).thenReturn(ByteBuffer.wrap(bm));
        when(rc4.encrypt(any(), any())).thenAnswer(i -> i.getArgument(0));
        
        lsbI.conceal(in, bitmap);
        assertArrayFilledWith(bm, 0, 06, 0x02);
        assertArrayFilledWith(bm, 6, 14, 0x00, 0x00, 0x01, 0x01, 0xFE, 0xFE, 0xFF, 0xFF);
    }

    @Test
    public void testConcealTooBig() {
        var in = new byte[13];
        var bm = new byte[109];

        when(bitmap.getImageDataView()).thenReturn(ByteBuffer.wrap(bm));
        when(rc4.encrypt(any(), any())).thenAnswer(i -> i.getArgument(0));
        
        assertTrue(
            assertThrows(SteganoException.class, () -> lsbI.conceal(in, bitmap))
                .getMessage()
                .toLowerCase()
                .contains("too small"));
    }

    @Test
    public void testReveal() {
        var header = new byte[6];
        Arrays.fill(header, 0, 6, (byte) 0x04); // Initialize key and hop to 4

        var body = Bytes.toArray(Ints.asList(0x11, 0x22, 0x33, 0x44, 0x55, 0x66, 0x77, 0x88));
        when(bitmap.getImageDataView()).thenReturn(ByteBuffer.wrap(Bytes.concat(header, body)));
        when(rc4.decrypt(any(), any())).thenAnswer(i -> i.getArgument(0));

        var out = lsbI.reveal(bitmap);
        assertArrayFilledWith(out, 0, 1, 0xCC);
    }
}
