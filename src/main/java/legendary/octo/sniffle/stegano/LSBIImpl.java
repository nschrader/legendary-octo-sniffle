package legendary.octo.sniffle.stegano;

import legendary.octo.sniffle.core.IBmpFile;
import legendary.octo.sniffle.core.IStegano;
import lombok.NonNull;

public class LSBIImpl implements IStegano {

    @Override
    public void conceal(@NonNull byte[] in, @NonNull IBmpFile bitmap) {
    }

    @Override
    public @NonNull byte[] reveal(@NonNull IBmpFile bitmap) {
        return new byte[0];
    }
}
