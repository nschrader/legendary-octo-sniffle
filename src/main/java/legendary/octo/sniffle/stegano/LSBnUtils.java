package legendary.octo.sniffle.stegano;

import java.nio.ByteBuffer;

import legendary.octo.sniffle.error.SteganoException;
import lombok.NonNull;
import lombok.experimental.UtilityClass;

@UtilityClass
class LSBnUtils {

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

    byte getByte(int lsbFactor, @NonNull ByteBuffer imageData) {
		var result = "";
		for (var i = 0; i < 8 / lsbFactor; i++) {
			result += extractBits(lsbFactor, imageData.get());
		}
		return (byte) Integer.parseInt(result, 2);
    }

    @NonNull byte[] getHiddenBytes(int lsbFactor, @NonNull ByteBuffer imageData) {
        var limit = imageData.capacity() / 8 * lsbFactor;
        var bytes = new byte[limit];
        for (var i = 0; i < limit; i++) {
            bytes[i] = getByte(lsbFactor, imageData);
        }
        return bytes;
    }

    void putBytesToHide(int lsbFactor, @NonNull byte[] data, @NonNull ByteBuffer bitmap) {
        var toHide = ByteBuffer.wrap(data);
        var whichByte = 0;
        var c = 7;

        for (var y = 0; y < toHide.capacity() * 8 / lsbFactor; y++) {
            for (var z = lsbFactor-1; z >= 0; z--) {
                var component = safeGetByte(y, bitmap);
                var modifiedComponent = (byte) modifyBit(component, z, getBitByPosition(toHide.get(whichByte), c));
                bitmap.put(y, modifiedComponent);
                c--;
            }

            if (c < 0) {
                whichByte++;
                c = 7;
            }
        }
    }

    byte safeGetByte(int index, @NonNull ByteBuffer bitmap) {
        try {
            return bitmap.get(index);
        } catch (IndexOutOfBoundsException e) {
            throw new SteganoException(e, "Bitmap (%d bytes) too small to hide data", bitmap.capacity());
        }
    }
}
