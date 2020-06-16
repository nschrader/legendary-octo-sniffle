package legendary.octo.sniffle.core;

import lombok.NonNull;

public interface ISteganoFormatter {
    /**
     * Format plain file content to conceal or encrypt later.
     * @throws SteganoException if {@code content} does not contain an extension
     */
    @NonNull byte[] format(@NonNull DCommonFile content);

    /**
     * Format encrypted file content.
     */
    @NonNull byte[] formatEncrypted(@NonNull byte[] encryptedContent);

    /**
     * Undo plain file content formatting after reveal or decryption.
     * @throws SteganoException if {@code data} is mal formatted
     */
    @NonNull DCommonFile scan(@NonNull byte[] data);

    /**
     * Undo encrypted file content formatting.
     * @throws SteganoException if {@code data} is mal formatted
     */
    @NonNull byte[] scanEncrypted(@NonNull byte[] encryptedData);
}
