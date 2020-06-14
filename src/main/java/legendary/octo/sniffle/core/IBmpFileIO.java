package legendary.octo.sniffle.core;

import java.io.File;

import lombok.NonNull;

public interface IBmpFileIO {
    /**
     * Read BMP file from disk
     * @throws BmpFileException
     * @throws FileException
     */
    @NonNull IBmpFile read(@NonNull File file);

    /**
     * Write BMP file to disk
     * @throws BmpFileException
     * @throws FileException
     */
    void write(@NonNull File file, @NonNull IBmpFile bmpFile);
}
