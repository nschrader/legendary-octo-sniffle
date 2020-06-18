package legendary.octo.sniffle.crypto;

import legendary.octo.sniffle.core.IRC4;
import lombok.NonNull;

/**
 * Handle indexes internally as ints, but expose them as bytes
 */
public class RC4Impl implements IRC4 {

    /**
     * Helper for this implementation and tests
     */
    static @NonNull byte[] intArrayToUnsignedByteArray(@NonNull int[] a) {
        var b = new byte[a.length];
        for (var i = 0; i < a.length; i++) {
            var tmp = a[i] & 0xFF;
            b[i] = (byte) tmp;
        }
        return b;
    }

    /**
     * Helper for this implementation and tests
     */
    static @NonNull int[] unsignedByteArrayToIntArray(@NonNull byte[] a) {
        var b = new int[a.length];
        for (var i = 0; i < a.length; i++) {
            var tmp = a[i] & 0xFF;
            b[i] = (int) tmp;
        }
        return b;
    }

    static class RC4ImplStateMachine implements IRC4StateMachine {

        private int[] S = new int[256];
        private int i = 0;
        private int j = 0;

        public RC4ImplStateMachine(@NonNull byte[] key) {
            var k = unsignedByteArrayToIntArray(key);
            KSA(k);
        }

        public byte nextKeystreamByte() {
            var b = PRGA() & 0xFF;
            return (byte) b;
        }

        private void swap(int idx1, int idx2) {
            var tmp = S[idx1];
            S[idx1] = S[idx2];
            S[idx2] = tmp;
        }
    
        private void KSA(@NonNull int[] key) {
            for (var i = 0; i < 256; i++) {
                S[i] = i;
            }
            
            var j = 0;
            for (var i = 0; i < 256; i++) {
                j = (j + S[i] + key[i % key.length]) % 256;
                swap(i, j);
            }
        }

        private int PRGA() {
            i = (i+1) % 256;
            j = (j+S[i]) % 256;
            swap(i, j);
            return S[(S[i] + S[j]) % 256];
        }
    }

    public IRC4StateMachine forKey(@NonNull byte[] key) {
        return new RC4ImplStateMachine(key);
    }

    public @NonNull byte[] encrypt(@NonNull byte[] plaintext, @NonNull byte[] key) {
        var rc4 = forKey(key);
        var ciphertext = plaintext.clone();

        for (var i = 0; i < ciphertext.length; i++) {
            ciphertext[i] ^= rc4.nextKeystreamByte();
        }

        return ciphertext;
    }

    public @NonNull byte[] decrypt(@NonNull byte[] ciphertext, @NonNull byte[] key) {
        return encrypt(ciphertext, key);
    }
}
