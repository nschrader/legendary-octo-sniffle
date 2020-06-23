package legendary.octo.sniffle.stegano;

import java.nio.ByteBuffer;
import java.util.function.Consumer;
import java.util.function.Supplier;

import legendary.octo.sniffle.error.SteganoException;
import lombok.NonNull;
import lombok.experimental.UtilityClass;

@UtilityClass
class LSBUtils {
    int modifyBit(int number, int position, int bitValue) {
		int mask = 1 << position;
		return (number & ~mask) | ((bitValue << position) & mask);
    }

    int getBitByPosition(byte b, int bitPosition) {
		return (b >> bitPosition & 1);
	}

    @NonNull String extractBits(int lsbFactor, byte data) {
		var result = "";
		for (var i = lsbFactor - 1 ; i >= 0; i--) {
			var bitValue = getBitByPosition(data, i);
			result += String.valueOf(bitValue);
		}
		return result;
	}

    byte getByte(int lsbFactor, @NonNull Supplier<Byte> imageDataGetter) {
		var result = "";
		for (var i = 0; i < 8 / lsbFactor; i++) {
			result += extractBits(lsbFactor, imageDataGetter.get());
		}
		return (byte) Integer.parseInt(result, 2);
    }

    byte[] getHiddenBytes(
            int lsbFactor, 
            int imageDataLength, 
            @NonNull Supplier<Byte> imageDataGetter) {
        var limit = imageDataLength / 8 * lsbFactor;
        var bytes = new byte[limit];
        for (var i = 0; i < limit; i++) {
            bytes[i] = getByte(lsbFactor, imageDataGetter);
        }
        return bytes;
    }

    void putBytesToHide(
            int lsbFactor, 
            @NonNull byte[] data, 
            int imageDataLength,
            @NonNull Supplier<Byte> steadyImageDataGetter,
            @NonNull Consumer<Byte> imageDataPutter) {
        var outerIterations = data.length * 8 / lsbFactor;
        if (outerIterations > imageDataLength) {
            throw new SteganoException("Bitmap (%d bytes) too small to hide data", imageDataLength);
        }

        var whichByte = 0;
        var c = 7;

        for (var y = 0; y < outerIterations; y++) {
            var component = steadyImageDataGetter.get();
            for (var z = lsbFactor-1; z >= 0; z--) {
                var bit = getBitByPosition(data[whichByte], c);
                component = (byte) modifyBit(component, z, bit);
                c--;
            }
            imageDataPutter.accept(component);

            if (c < 0) {
                whichByte++;
                c = 7;
            }
        }
    }

    @NonNull Supplier<Byte> getSteadyImageDataGetter(@NonNull ByteBuffer internal) {
        return () -> {
            var n = internal.position();
            var res = internal.get();
            internal.position(n);
            return res;
        };
    }
}
