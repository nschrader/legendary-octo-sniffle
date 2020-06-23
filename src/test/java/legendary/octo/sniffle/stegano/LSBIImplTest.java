package legendary.octo.sniffle.stegano;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.util.stream.IntStream;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.testing.fieldbinder.Bind;
import com.google.inject.testing.fieldbinder.BoundFieldModule;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import legendary.octo.sniffle.core.IRC4;
import lombok.NonNull;

@ExtendWith(MockitoExtension.class)
public class LSBIImplTest {

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
    public void testHop() {
        var data = new int[] {0xCA, 0x01, 0x04, 0x30, 0xFE, 0x02, 0x10, 0x40, 0xBE, 0x03, 0x20, 0x88, 0xEF};
        var b = intArrayToUnsignedByteArray(data);
        var bb = ByteBuffer.wrap(b);

        var out = "";
        for (var i = 0; i < data.length; i++) {
            out += String.format("%02X", lsbI.get_h(bb, 4));
        }
        assertEquals("CAFEBEEF010203041020304088", out);
    }

    @Test
    public void testHopBufferUnderflow() {
        var data = new int[] {0x01, 0x02, 0x03, 0x40};
        var b = intArrayToUnsignedByteArray(data);
        var bb = ByteBuffer.wrap(b);

        assertThrows(BufferUnderflowException.class, () -> IntStream.rangeClosed(1, 5).forEach(d -> lsbI.get_h(bb, 2)));
    }

    @NonNull byte[] intArrayToUnsignedByteArray(@NonNull int[] a) {
        var b = new byte[a.length];
        for (var i = 0; i < a.length; i++) {
            var tmp = a[i] & 0xFF;
            b[i] = (byte) tmp;
        }
        return b;
    }
}
