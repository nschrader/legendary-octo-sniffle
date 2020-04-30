package legendary.octo.sniffle.core;

import lombok.NonNull;

public interface ICipher {
    public void encrypt(@NonNull byte[] data, @NonNull String password, @NonNull ECipher cipher, @NonNull EMode mode);
    public void decrypt(@NonNull byte[] data, @NonNull String password, @NonNull ECipher cipher, @NonNull EMode mode);
}
