package legendary.octo.sniffle.core;

import java.io.File;

import legendary.octo.sniffle.io.BmpFile;
import lombok.NonNull;

public interface IBmpFileIO {
    /**
     * Read BMP file from disk
     * @throws BmpFileException
     * @throws FileException
     */
    public @NonNull BmpFile read(@NonNull File file);

    /**
     * Write BMP file to disk
     * @throws BmpFileException
     * @throws FileException
     */
    public void write(@NonNull File file, @NonNull BmpFile bmpFile);
}
