package legendary.octo.sniffle.core;

import legendary.octo.sniffle.error.CipherException;
import lombok.NonNull;

public interface ICipher {
    /**
     * Encrypt all data at once using given cipher, mode and password
     * @throws CipherException if cipher configuration or initialization was incorrect
     */
    byte[] encrypt(@NonNull byte[] data, @NonNull String password, @NonNull ECipher cipher, @NonNull EMode mode);

    /**
     * Decrypt all data at once using given cipher, mode and password
     * @throws CipherException if cipher configuration or initialization was incorrect or data padding is missing
     */
    byte[] decrypt(@NonNull byte[] data, @NonNull String password, @NonNull ECipher cipher, @NonNull EMode mode);
}
