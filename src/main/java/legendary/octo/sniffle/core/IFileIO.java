package legendary.octo.sniffle.core;

import java.io.File;

import lombok.NonNull;

public interface IFileIO {
    /**
     * Read a file from disk to memory. It mustn't exceed {@link #MAX_BYTES}.
     * @throws FileException
     */
    public @NonNull byte[] read(@NonNull File file);

    /**
     * Write from memory to file.
     * @throws FileException
     */
    public void write(@NonNull File file, @NonNull byte[] content);
}
