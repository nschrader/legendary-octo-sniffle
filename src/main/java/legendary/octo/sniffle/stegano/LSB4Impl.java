package legendary.octo.sniffle.stegano;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import com.google.common.io.Files;

import legendary.octo.sniffle.core.IBmpFile;
import legendary.octo.sniffle.core.IStegano;
import legendary.octo.sniffle.error.SteganoException;
import lombok.NonNull;

public class LSB4Impl implements IStegano {

    @Override
    public void conceal(@NonNull byte[] in, @NonNull IBmpFile bitmap) {
        //TODO: Copied code start
        final var OFFSET_LEN = 4;

        //TODO: Get filename from somewhere
        var path = "file.png";
        var rawExtension = Files.getFileExtension(path);
        if (rawExtension.isEmpty()) {
            throw new SteganoException(); //TODO: Add error message
        }
        var extension = ('.' + rawExtension + '\0').getBytes();

        var lengthToHide = OFFSET_LEN + in.length + extension.length;
        var toHide = ByteBuffer.allocate(lengthToHide).order(ByteOrder.BIG_ENDIAN);

        toHide.putInt(in.length);
        toHide.put(in);
        toHide.put(extension);
        //TODO: Copied code end

        var whichByte = 0;
        var c = 7;

        for (var y = 0; y < lengthToHide * 2; y++) {
            for (var z = 3; z >= 0; z--) {
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

    //TODO: Copied code start
    private int modifyBit(int number, int position, int bitValue) {
		int mask = 1 << position;
		return (number & ~mask) | ((bitValue << position) & mask);
    }
    
    private int getBitByPosition(byte b, int bitPosition) {
		return (b >> bitPosition & 1);
    }
    //TODO: Copied code end

    @Override
    public @NonNull byte[] reveal(@NonNull IBmpFile bitmap) {
        // TODO: Copied code start
        var imageData = bitmap.getImageDataView();

        //TODO: Use loop
        var lsb1Offset = ByteBuffer.allocate(4).order(ByteOrder.BIG_ENDIAN);
        lsb1Offset.put(getByte(4, imageData));
        lsb1Offset.put(getByte(4, imageData));
        lsb1Offset.put(getByte(4, imageData));
        lsb1Offset.put(getByte(4, imageData));
        var max = lsb1Offset.getInt(0);
        
        var fileBytes = new byte[max];
        for (var i = 0; i < max; i++) {
            fileBytes[i] = getByte(4, imageData);
        }
        
        var notNullChar = false;
        var extension = "";
        while (!notNullChar) {
            var b = getByte(4, imageData);
            if (b == 0) {
                notNullChar = true;
            } else {
                extension += (char) b;
            }
        }

        //TODO: Do something with extension
        return fileBytes;
        //TODO: Copied code end
    }

    //TODO: Copied code start
    private String extractBits(int lsbFactor, byte data) {
		var result = "";
		for (var i = lsbFactor - 1 ; i >= 0; i--) {
			var bitValue = getBitByPosition(data, i);
			result += String.valueOf(bitValue);
		}
		return result;
	}

    private byte getByte(int lsbFactor, ByteBuffer imageData) {
		var result = "";
		for (var i = 0; i < 8 / lsbFactor; i++) {
			result += extractBits(lsbFactor, imageData.get());
		}
		return (byte) Integer.parseInt(result,2);
    }
    //TODO: Copied Code end
}
