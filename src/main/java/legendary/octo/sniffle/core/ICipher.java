package legendary.octo.sniffle.core;

import lombok.NonNull;

public interface ICipher {
    /**
     * Encrypt all data at once and in-place using given cipher, mode and password
     * @throws CipherException if cipher configuration or initialization was incorrect
     */
    void encrypt(@NonNull byte[] data, @NonNull String password, @NonNull ECipher cipher, @NonNull EMode mode);

    /**
     * Decrypt all data at once in-place using given cipher, mode and password
     * @throws CipherException if cipher configuration or initialization was incorrect or data padding is missing
     */
    void decrypt(@NonNull byte[] data, @NonNull String password, @NonNull ECipher cipher, @NonNull EMode mode);
}
