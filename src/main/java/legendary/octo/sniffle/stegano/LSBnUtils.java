package legendary.octo.sniffle.stegano;

import java.nio.ByteBuffer;

import legendary.octo.sniffle.error.SteganoException;
import lombok.NonNull;
import lombok.experimental.UtilityClass;

@UtilityClass
class LSBnUtils {

    static int modifyBit(int number, int position, int bitValue) {
		int mask = 1 << position;
		return (number & ~mask) | ((bitValue << position) & mask);
    }

    static int getBitByPosition(byte b, int bitPosition) {
		return (b >> bitPosition & 1);
	}

    @NonNull static String extractBits(int lsbFactor, byte data) {
		var result = "";
		for (var i = lsbFactor - 1 ; i >= 0; i--) {
			var bitValue = getBitByPosition(data, i);
			result += String.valueOf(bitValue);
		}
		return result;
	}

    static byte getByte(int lsbFactor, @NonNull ByteBuffer imageData) {
		var result = "";
		for (var i = 0; i < 8 / lsbFactor; i++) {
			result += extractBits(lsbFactor, imageData.get());
		}
		return (byte) Integer.parseInt(result, 2);
    }

    @NonNull static  byte[] getHiddenBytes(int lsbFactor, @NonNull ByteBuffer imageData, int hopBytes) {
        var limit = imageData.capacity() / 8 * lsbFactor;
        var bytes = new byte[limit];
        for (var i = 0; i < limit; i+= hopBytes) {
            bytes[i] = getByte(lsbFactor, imageData);
        }
        return bytes;
    }

    static void putBytesToHide(int lsbFactor, @NonNull byte[] data, @NonNull ByteBuffer bitmap, int hopBytes) {
        var toHide = ByteBuffer.wrap(data);
        var whichByte = 0;
        var c = 7;

        for (var y = 0; y < toHide.capacity() * 8 / lsbFactor; y+=hopBytes) {
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

    static byte safeGetByte(int index, @NonNull ByteBuffer bitmap) {
        try {
            return bitmap.get(index);
        } catch (IndexOutOfBoundsException e) {
            throw new SteganoException(e, "Bitmap (%d bytes) too small to hide data", bitmap.capacity());
        }
    }

    static int getHop(byte b) {
        for (int i = 7; i >=0; i--) {
            if(getBitByPosition(b, i) == 1) {
                return (int)Math.pow(2, i);
            }
        }
        return 256;
    }

    static  byte[] intToByteArray(int value) {
        return new byte[] { (byte) (value >>> 24), (byte) (value >>> 16), (byte) (value >>> 8), (byte) value };
    }

    static int byteArrayToInt(byte[] bytes) {
        return bytes[0] << 24 | (bytes[1] & 0xFF) << 16 | (bytes[2] & 0xFF) << 8 | (bytes[3] & 0xFF);
    }
}
