package legendary.octo.sniffle.stegano;

import static legendary.octo.sniffle.stegano.LSBnUtils.*;

import java.nio.ByteBuffer;

import com.google.inject.Inject;

import legendary.octo.sniffle.core.IBmpFile;
import legendary.octo.sniffle.core.IRC4;
import legendary.octo.sniffle.core.IStegano;
import legendary.octo.sniffle.error.SteganoException;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(onConstructor_ = @Inject)
public class LSBIImpl implements IStegano {
    private static final int HOP_BYTES_OFF = 0;
    private static final int KEY_BYTES_OFF = 0;
    private static final int KEY_BYTES_LEN = 6;

    private final IRC4 rc4;

    @Override
    public void conceal(@NonNull byte[] in, @NonNull IBmpFile bitmap) {
        var imageDataView = bitmap.getImageDataView();
        var key = getKey(imageDataView);

        var hop = getHop(key);

        var x = imageDataView.slice(KEY_BYTES_LEN, imageDataView.capacity() - KEY_BYTES_LEN);
        byte[] encrypt = rc4.encrypt(in, key);
        putBytesToHide_h(1, encrypt, x, hop);
    }

    void putBytesToHide_h(int lsbFactor, @NonNull byte[] data, @NonNull ByteBuffer bitmap, int hopBytes) {
        var toHide = ByteBuffer.wrap(data);
        var whichByte = 0;
        var c = 7;

        for (var y = 0; y < toHide.capacity() * 8 / lsbFactor; y++) {
            for (var z = lsbFactor-1; z >= 0; z--) {
                var component = safeGetByte_h(bitmap);
                var modifiedComponent = (byte) modifyBit(component, z, getBitByPosition(toHide.get(whichByte), c));
                put_h(bitmap, modifiedComponent, hopBytes);
                c--;
            }

            if (c < 0) {
                whichByte++;
                c = 7;
            }
        }
    }

    byte safeGetByte_h(@NonNull ByteBuffer bitmap) {
        try {
            return get_h_current(bitmap);
        } catch (IndexOutOfBoundsException e) {
            throw new SteganoException(e, "Bitmap (%d bytes) too small to hide data", bitmap.capacity());
        }
    }

    byte get_h_current(@NonNull ByteBuffer internal) {
        var p = internal.position();
        var res = internal.get();
        internal.position(p);
        return res;
    }

    void put_h(@NonNull ByteBuffer internal, byte val, int hop) {
        var p = internal.position();
        var c = internal.capacity();
        var n = (p+hop);

        if (n >= c) {
            n = n%hop + 1;

            // Make next get() underflow buffer
            if (n == hop) {
                n = internal.capacity();
            }
        }


        internal.put(val);
        internal.position(n);
    }

    @Override
    public @NonNull byte[] reveal(@NonNull IBmpFile bitmap) {
        var imageDataView = bitmap.getImageDataView();
        var key = getKey(imageDataView);

        var hop = getHop(key);

        var x = imageDataView.slice(6, imageDataView.capacity() - 6);
        byte[] encryptedData = getHiddenBytes_h(1, x, hop);
        return rc4.decrypt(encryptedData, key);
    }

    private @NonNull byte[] getKey(@NonNull ByteBuffer imageDataView) {
        var key = new byte[KEY_BYTES_LEN];
        imageDataView.get(key, KEY_BYTES_OFF, KEY_BYTES_LEN);
        return key;
    }

    private int getHop(@NonNull byte[] key) {
        var b = key[HOP_BYTES_OFF];

        for (int i = 7; i >= 0; i--) {
            if (getBitByPosition(b, i) == 1) {
                return (int) Math.pow(2, i);
            }
        }
        return 256;
    }

    byte[] getHiddenBytes_h(int lsbFactor, @NonNull ByteBuffer imageData, int hopBytes) {
        var limit = imageData.capacity() / 8 * lsbFactor;
        var bytes = new byte[limit];
        for (var i = 0; i < limit; i++) {
            bytes[i] = getByte_h(lsbFactor, hopBytes, imageData);
        }
        return bytes;
    }

    byte getByte_h(int lsbFactor, int hop, @NonNull ByteBuffer imageData) {
		var result = "";
		for (var i = 0; i < 8 / lsbFactor; i++) {
			result += extractBits(lsbFactor, get_h(imageData, hop));
		}
		return (byte) Integer.parseInt(result, 2);
    }

    byte get_h(@NonNull ByteBuffer internal, int hop) {
        var p = internal.position();
        var c = internal.capacity();
        var n = (p+hop);

        if (n >= c) {
            n = n%hop + 1;

            // Make next get() underflow buffer
            if (n == hop) {
                n = internal.capacity();
            }
        }


        var res = internal.get();
        internal.position(n);
        return res;
    }

    byte[] intToByteArray(int value) {
        return new byte[] { (byte) (value >>> 24), (byte) (value >>> 16), (byte) (value >>> 8), (byte) value };
    }

    int byteArrayToInt(byte[] bytes) {
        return bytes[0] << 24 | (bytes[1] & 0xFF) << 16 | (bytes[2] & 0xFF) << 8 | (bytes[3] & 0xFF);
    }
}
