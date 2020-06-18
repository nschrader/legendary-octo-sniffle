package legendary.octo.sniffle.crypto;

import java.security.GeneralSecurityException;
import java.security.MessageDigest;

import javax.crypto.Cipher;

import com.google.inject.Inject;

import legendary.octo.sniffle.core.ECipher;
import legendary.octo.sniffle.core.EMode;
import legendary.octo.sniffle.core.ICipher;
import legendary.octo.sniffle.core.IJCAFactory;
import legendary.octo.sniffle.error.CipherException;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(onConstructor_ = @Inject)
public class CipherImpl implements ICipher {

    private final IJCAFactory<Cipher> cipherFactory;
    private final IJCAFactory<MessageDigest> messageDigestFactory;

    @Override
    public byte[] encrypt(@NonNull byte[] data, @NonNull String password, @NonNull ECipher cipher, @NonNull EMode mode) {
        return operate(Cipher.ENCRYPT_MODE, data, password, cipher, mode);
    }

    @Override
    public byte[] decrypt(@NonNull byte[] data, @NonNull String password, @NonNull ECipher cipher, @NonNull EMode mode) { 
        return operate(Cipher.DECRYPT_MODE, data, password, cipher, mode);
    }

    private @NonNull byte[] operate(int opMode, 
            @NonNull byte[] data,
            @NonNull String password, 
            @NonNull ECipher cipher, 
            @NonNull EMode mode) {
        try {
            var sha256 = messageDigestFactory.getInstance("SHA-256");
            var pbkdf = new OpenSSLPBKDF(getJCACipherName(cipher), sha256, password, null, getKeyLength(cipher), getIVLength(cipher, mode));

            var transformation = getJCATransformation(cipher, mode);
            var cipherInstance = cipherFactory.getInstance(transformation);
            cipherInstance.init(opMode, pbkdf.deriveSecretKey(), pbkdf.deriveIV());

            return cipherInstance.doFinal(data);
        } catch (GeneralSecurityException e) {
            throw new CipherException(e);
        }
    }

    private @NonNull String getJCATransformation(@NonNull ECipher cipher, @NonNull EMode mode) {
        var m = switch(mode) {
            case ecb -> "ECB";
            case cfb -> "CFB8";
            case ofb -> "OFB";
            case cbc -> "CBC";
        };

        return String.format("%s/%s/NoPadding", getJCACipherName(cipher), m);
    }

    private @NonNull String getJCACipherName(@NonNull ECipher cipher) {
        return switch(cipher) {
            case aes128, aes192, aes256 -> "AES";
            case des -> "DES";
        };
    }

    private int getKeyLength(@NonNull ECipher cipher) {
        return switch(cipher) {
            case aes128 -> 16;
            case aes192 -> 24;
            case aes256 -> 32;
            case des -> 7;
        };
    }

    private int getIVLength(@NonNull ECipher cipher, @NonNull EMode mode) {
        if (mode == EMode.ecb) {
            return 0;
        }

        return switch(cipher) {
            case aes128, aes192, aes256 -> 16;
            case des -> 8;
        };
    }
}
