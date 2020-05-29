package legendary.octo.sniffle.core;

import lombok.NonNull;

public interface IRC4 {

    /**
     * Holds the internal cipher state
     */
    interface IRC4StateMachine {
        /**
         * @return Next keystream byte the next plaintext byte needs to be XOR'd with
         */
        byte nextKeystreamByte();
    }

    /**
     * Initialize a cipher
     */
    IRC4StateMachine forKey(@NonNull byte[] key);

    /**
     * Convenience method to encrypt all of plaintext with given key out-of-place
     */
    @NonNull byte[] encrypt(@NonNull byte[] plaintext, @NonNull byte[] key);   

    /**
     * Same as encrypt as it is its own inverse
     */
    @NonNull byte[] decrypt(@NonNull byte[] ciphertext, @NonNull byte[] key);   
}
