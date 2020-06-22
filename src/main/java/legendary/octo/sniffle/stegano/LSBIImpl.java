package legendary.octo.sniffle.stegano;

import com.google.inject.Inject;

import legendary.octo.sniffle.core.IBmpFile;
import legendary.octo.sniffle.core.IRC4;
import legendary.octo.sniffle.core.IStegano;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.nio.ByteBuffer;

import static legendary.octo.sniffle.stegano.LSBnUtils.*;

@RequiredArgsConstructor(onConstructor_ = @Inject)
public class LSBIImpl implements IStegano {
    private final IRC4 rc4;

    @Override
    public void conceal(@NonNull byte[] in, @NonNull IBmpFile bitmap) {
        //rc4.getClass();
        @NonNull ByteBuffer imageDataView = bitmap.getImageDataView();
        byte[] key = getKey(imageDataView);

        int hop = key[0];
        hop = getHop((byte)hop);

        byte[] encrypt = rc4.encrypt(in, key);

        byte[] encryptSize = intToByteArray(encrypt.length);

        int fal = encryptSize.length; // determines length of firstArray
        int sal = encrypt.length; // determines length of secondArray
        byte[] toHide = new byte[fal + sal]; // resultant array of size first array and second array
        System.arraycopy(encryptSize, 0, toHide, 0, fal);
        System.arraycopy(encrypt, 0, toHide, fal, sal);


        //In this case LSBI its gonna hide on the LSB1
        putBytesToHide(1, toHide, imageDataView ,hop);
    }

    @Override
    public @NonNull byte[] reveal(@NonNull IBmpFile bitmap) {
        @NonNull ByteBuffer imageDataView = bitmap.getImageDataView();
        byte[] key = getKey(imageDataView);

        int hop = key[0];
        hop = getHop((byte)hop);

        //IF LSB1 --- IF NOT SUBSTITUTE lsbFactor
        byte[] encryptedData = getHiddenBytes(1, imageDataView,hop);
        byte[] sizeOfEncrypted = new byte[4];

        System.arraycopy(encryptedData, 0, sizeOfEncrypted, 0, sizeOfEncrypted.length);

        int sizeToRead = byteArrayToInt(sizeOfEncrypted);

        byte[] data = new byte[sizeToRead];
        System.arraycopy(encryptedData,4,data, 0, sizeToRead);

        return rc4.decrypt(data, key);

    }

    private byte[] getKey(ByteBuffer imageDataView){
        byte[] key = new byte[6];
        for (int i = 0; i < key.length; i++) {
            key[i] = imageDataView.get();
        }
        return key;
    }
}
