package legendary.octo.sniffle.stegano;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import java.util.Arrays;

import lombok.experimental.UtilityClass;

@UtilityClass
class AssertBytesUtil {

    void assertArrayFilledWith(byte[] actual, int inclusiveStart, int exclusiveEnd, int... expected) {
        var expectedArray = new byte[exclusiveEnd-inclusiveStart];
        for (var i = 0; i < expectedArray.length; i++) {
            expectedArray[i] = (byte) expected[i % expected.length];
        }

        var actualArray = Arrays.copyOfRange(actual, inclusiveStart, exclusiveEnd);
        assertArrayEquals(expectedArray, actualArray);
    }
    
}
