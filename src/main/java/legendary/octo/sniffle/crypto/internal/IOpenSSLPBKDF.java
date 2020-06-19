package legendary.octo.sniffle.crypto.internal;

import java.security.MessageDigest;

import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import lombok.NonNull;

/**
 * Adapter for OenSSL's EVP_BytesToKey KDF
 * https://www.openssl.org/docs/manmaster/man3/EVP_BytesToKey.html
 */
public interface IOpenSSLPBKDF {

    interface IOpenSSLPBKDFStateMachine {

        public @NonNull SecretKeySpec deriveSecretKey();

        public IvParameterSpec deriveIV();
    }

    public @NonNull IOpenSSLPBKDFStateMachine of(
            @NonNull String algorithm,
            @NonNull MessageDigest md, 
            @NonNull String password, 
            byte[] salt,
            int keyLen, 
            int ivLen);    
}
