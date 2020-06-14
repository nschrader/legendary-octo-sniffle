package legendary.octo.sniffle.stegano;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import legendary.octo.sniffle.core.IBmpFile;
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
    
    @NonNull String getExtension(int lsbFactor, @NonNull ByteBuffer imageData) {
        var notNullChar = false;
        var extension = "";

        while (!notNullChar) {
            var b = getByte(lsbFactor, imageData);
            if (b == 0x00) {
                notNullChar = true;
            } else if (b != 0x2e) {
                extension += (char) b;
            }
        }

        return extension;
    }

    int getLimit(int lsbFactor, @NonNull ByteBuffer imageData) {
        var offset = ByteBuffer.allocate(4).order(ByteOrder.BIG_ENDIAN);
        for (var i = 0; i < 4; i++) {
            offset.put(getByte(lsbFactor, imageData));
        }
        return offset.getInt(0);
    }

    @NonNull byte[] getHiddenBytes(int lsbFactor, int limit, @NonNull ByteBuffer imageData) {
        var bytes = new byte[limit];
        for (var i = 0; i < limit; i++) {
            bytes[i] = getByte(lsbFactor, imageData);
        }
        return bytes;
    }

    @NonNull byte[] prepareFileExtension(@NonNull String rawExtension) {
        if (rawExtension.isEmpty()) {
            throw new SteganoException("No file extension to embed, use a file that has one");
        }
        return ('.' + rawExtension + '\0').getBytes();
    }

    @NonNull ByteBuffer prepareBytesToHide(@NonNull byte[] data, @NonNull byte[] extension) {
        var lengthToHide = Integer.BYTES + data.length + extension.length;
        var toHide = ByteBuffer.allocate(lengthToHide).order(ByteOrder.BIG_ENDIAN);

        toHide.putInt(data.length);
        toHide.put(data);
        toHide.put(extension);

        return toHide;
    }

    void putBytesToHide(int lsbFactor, @NonNull ByteBuffer toHide, @NonNull IBmpFile bitmap) {
        var whichByte = 0;
        var c = 7;

        for (var y = 0; y < toHide.capacity() * 8 / lsbFactor; y++) {
            for (var z = lsbFactor-1; z >= 0; z--) {
                var component = bitmap.getImageData(y);
                var modifiedComponent = (byte) modifyBit(component, z, getBitByPosition(toHide.get(whichByte), c));
                bitmap.putImageData(y, modifiedComponent);
                c--;
            }

            if (c < 0) {
                whichByte++;
                c = 7;
            }
        }
    }
}
