package legendary.octo.sniffle.stegano;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import com.google.common.io.Files;

import legendary.octo.sniffle.core.IBmpFile;
import legendary.octo.sniffle.core.IStegano;
import legendary.octo.sniffle.error.SteganoException;
import lombok.NonNull;

public class LSB1Impl implements IStegano {

    @Override
    public void conceal(@NonNull byte[] in, @NonNull IBmpFile bitmap) {
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

        var whichByte = 0;
        var c = 7;

        var numBits = lengthToHide * 8;
        for (var y = 0; y < numBits; y++) {
            var component = bitmap.getImageData(y);
            var modifiedComponent = (byte) modifyBit(component, 0, getBitByPosition(toHide.get(whichByte), c));
            bitmap.putImageData(y, modifiedComponent);
            c--;
            if (c < 0) {
                whichByte++;
                c = 7;
            }
        }
    }

    @Override
    public @NonNull byte[] reveal(@NonNull IBmpFile bitmap) {
        var imageData = bitmap.getImageDataView();

        var lsb1Offset = ByteBuffer.allocate(4).order(ByteOrder.BIG_ENDIAN);
        lsb1Offset.put(getByte(1, imageData));
        lsb1Offset.put(getByte(1, imageData));
        lsb1Offset.put(getByte(1, imageData));
        lsb1Offset.put(getByte(1, imageData));
        var max = lsb1Offset.getInt(0);
        
        byte[] fileBytes = new byte[max];
        for (var i = 0; i < fileBytes.length; i++) {
            fileBytes[i] = getByte(1, imageData);
        }
        
        boolean notNullChar = false;
        String extension = "";
        while (!notNullChar) {
            var b = getByte(1, imageData);
            if (b == 0) {
                notNullChar = true;
            } else {
                extension += (char) b;
            }
        }

        //TODO: Do something with extension
        return fileBytes;        
    }

    private int modifyBit(int number, int position, int bitValue) {
		int mask = 1 << position;
		return (number & ~mask) | ((bitValue << position) & mask);
    }
    
    private int getBitByPosition(byte b, int bitPosition) {
		return (b >> bitPosition & 1);
	}

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
}
