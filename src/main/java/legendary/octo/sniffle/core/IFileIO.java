package legendary.octo.sniffle.core;

import java.io.File;

import lombok.NonNull;

public interface IFileIO {
    /**
     * Read a file from disk to memory. It mustn't exceed {@link #MAX_BYTES}.
     * @throws FileException
     */
    @NonNull DCommonFile read(@NonNull File file);

    /**
     * Write from memory to file.
     * @throws FileException
     */
    void write(@NonNull File file, @NonNull DCommonFile content);
}
