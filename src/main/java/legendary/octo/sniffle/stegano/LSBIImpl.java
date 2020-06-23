package legendary.octo.sniffle.stegano;

import static legendary.octo.sniffle.stegano.LSBUtils.*;

import java.nio.ByteBuffer;
import java.util.function.Consumer;
import java.util.function.Supplier;

import com.google.inject.Inject;

import legendary.octo.sniffle.core.IBmpFile;
import legendary.octo.sniffle.core.IRC4;
import legendary.octo.sniffle.core.IStegano;
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
        var imageData = bitmap.getImageDataView();
        var key = getKey(imageData);
        var hop = getHop(key);

        var x = imageData.slice(KEY_BYTES_LEN, imageData.capacity() - KEY_BYTES_LEN);
        byte[] encrypt = rc4.encrypt(in, key);
        putBytesToHide(1, encrypt, x.capacity(), getSteadyImageDataGetter(x), getHopImageDataPutter(x, hop));
    }

    @Override
    public @NonNull byte[] reveal(@NonNull IBmpFile bitmap) {
        var imageDataView = bitmap.getImageDataView();
        var key = getKey(imageDataView);
        var hop = getHop(key);

        var x = imageDataView.slice(6, imageDataView.capacity() - 6);
        byte[] encryptedData = getHiddenBytes(1, x.capacity(), getHopImageDataGetter(x, hop));
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

    private int getNextPosition(@NonNull ByteBuffer internal, int hop) {
        var p = internal.position();
        var c = internal.capacity();
        var n = (p+hop);

        if (n >= c) {
            n = n%hop + 1;

            // Make next get() underflow buffer
            if (n == hop) {
                n = c;
            }
        }

        return n;
    }

    // Package-private por testing
    @NonNull Supplier<Byte> getHopImageDataGetter(@NonNull ByteBuffer internal, int hop) {
        return () -> {
            var n = getNextPosition(internal, hop);
            var res = internal.get();
            internal.position(n);
            return res;
        };
    }

    // Package-private por testing
    @NonNull Consumer<Byte> getHopImageDataPutter(@NonNull ByteBuffer internal, int hop) {
        return val -> {
            var n = getNextPosition(internal, hop);
            internal.put(val);
            internal.position(n);
        };
    }
}
