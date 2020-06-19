package legendary.octo.sniffle.crypto.internal;

import java.security.MessageDigest;

import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import com.google.common.primitives.Bytes;

import lombok.NonNull;

public class OpenSSLPBKDF implements IOpenSSLPBKDF {

    public @NonNull IOpenSSLPBKDFStateMachine of(
            @NonNull String algorithm,
            @NonNull MessageDigest md, 
            @NonNull String password, 
            byte[] salt,
            int keyLen, 
            int ivLen) {
        return new OpenSSLPBKDFStateMachine(algorithm, md, password, salt, keyLen, ivLen);
    }

    static private class OpenSSLPBKDFStateMachine implements IOpenSSLPBKDFStateMachine {

        private final @NonNull byte[] DK;
        private final int keyLen;
        private final int ivLen;
        private final @NonNull String algorithm;

        private OpenSSLPBKDFStateMachine(
                @NonNull String algorithm,
                @NonNull MessageDigest md, 
                @NonNull String password, 
                byte[] salt,
                int keyLen, 
                int ivLen) {
            var totalLen = keyLen + ivLen;
            var iterations = totalLen/md.getDigestLength() + 1;

            if (salt == null) {
                salt = new byte[0];
            }

            var D = new byte[iterations+1][];
            D[0] = new byte[0];
            for (var i = 1; i <= iterations; i++) {
                var message = Bytes.concat(D[i-1], password.getBytes(), salt);
                D[i] = md.digest(message);
            }

            this.DK = Bytes.concat(D);
            this.keyLen = keyLen;
            this.ivLen = ivLen;
            this.algorithm = algorithm;
        }

        public @NonNull SecretKeySpec deriveSecretKey() {
            return new SecretKeySpec(DK, 0, keyLen, algorithm);
        }

        public IvParameterSpec deriveIV() {
            if (ivLen == 0) {
                return null;
            }

            return new IvParameterSpec(DK, keyLen, ivLen);
        }
    }
}
