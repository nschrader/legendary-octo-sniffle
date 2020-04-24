package legendary.octo.sniffle.io;

import lombok.NonNull;
import lombok.experimental.UtilityClass;

@UtilityClass
public class BmpFileIO {
    /**
     * Read BMP file from disk
     * @throws BmpFileException
     * @throws FileException
     */
    public @NonNull BmpFile read(@NonNull String path) {
        return new BmpFile(FileIO.read(path));
    }

    /**
     * Write BMP file to disk
     * @throws BmpFileException
     * @throws FileException
     */
    public void write(@NonNull String path, @NonNull BmpFile bmpFile) {
        FileIO.write(path, bmpFile.byteBuffer.array());
    }
}
