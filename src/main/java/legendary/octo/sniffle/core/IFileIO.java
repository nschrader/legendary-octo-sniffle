package legendary.octo.sniffle.core;

import java.io.File;

import legendary.octo.sniffle.error.FileException;
import lombok.NonNull;

public interface IFileIO {
    static final Integer MiB = 2 << 20;
    static final Integer MAX_BYTES = 50 * MiB;

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
