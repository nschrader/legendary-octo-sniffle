package legendary.octo.sniffle.core;

import lombok.NonNull;

public interface IStegano {
    public void conceal(@NonNull byte[] in, @NonNull IBmpFile bitmap);
    public @NonNull byte[] reveal(@NonNull IBmpFile bitmap);
}
