package legendary.octo.sniffle.stegano;

import static legendary.octo.sniffle.stegano.LSBnUtils.*;

import legendary.octo.sniffle.core.IBmpFile;
import legendary.octo.sniffle.core.IStegano;
import lombok.NonNull;

public class LSB1Impl implements IStegano {

    @Override
    public void conceal(@NonNull byte[] in, @NonNull IBmpFile bitmap) {
        putBytesToHide(1, in, bitmap.getImageDataView());
    }

    @Override
    public @NonNull byte[] reveal(@NonNull IBmpFile bitmap) {
        return getHiddenBytes(1, bitmap.getImageDataView());
    }
}
