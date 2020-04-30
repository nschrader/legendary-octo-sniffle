package legendary.octo.sniffle.stegano;

import legendary.octo.sniffle.core.IStegano;
import legendary.octo.sniffle.io.BmpFile;
import lombok.NonNull;

public class LSB2Impl implements IStegano {

    @Override
    public void conceal(@NonNull byte[] in, @NonNull BmpFile bitmap) {
    }

    @Override
    public @NonNull byte[] reveal(@NonNull BmpFile bitmap) {
        return new byte[0];
    }
}
