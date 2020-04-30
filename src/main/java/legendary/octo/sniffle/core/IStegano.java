package legendary.octo.sniffle.core;

import legendary.octo.sniffle.io.BmpFile; // TODO: Extract into interface
import lombok.NonNull;

public interface IStegano {
    public void conceal(@NonNull byte[] in, @NonNull BmpFile bitmap);
    public @NonNull byte[] reveal(@NonNull BmpFile bitmap);
}
