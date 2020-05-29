package legendary.octo.sniffle.stegano;

import com.google.inject.Inject;

import legendary.octo.sniffle.core.IBmpFile;
import legendary.octo.sniffle.core.IRC4;
import legendary.octo.sniffle.core.IStegano;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(onConstructor_ = @Inject)
public class LSBIImpl implements IStegano {
    private final IRC4 rc4;

    @Override
    public void conceal(@NonNull byte[] in, @NonNull IBmpFile bitmap) {
        rc4.getClass();
    }

    @Override
    public @NonNull byte[] reveal(@NonNull IBmpFile bitmap) {
        return new byte[0];
    }
}
