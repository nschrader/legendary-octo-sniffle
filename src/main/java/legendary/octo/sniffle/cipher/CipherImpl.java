package legendary.octo.sniffle.cipher;

import legendary.octo.sniffle.core.ECipher;
import legendary.octo.sniffle.core.EMode;
import legendary.octo.sniffle.core.ICipher;
import lombok.NonNull;

public class CipherImpl implements ICipher {

    @Override
    public void encrypt(@NonNull byte[] data, @NonNull String password, @NonNull ECipher cipher, @NonNull EMode mode) { }

    @Override
    public void decrypt(@NonNull byte[] data, @NonNull String password, @NonNull ECipher cipher, @NonNull EMode mode) { }
}
