package legendary.octo.sniffle.io;

import org.jetbrains.annotations.NotNull;

public final class BmpFileIO {
    /**
     * Read BMP file from disk
     * @throws BmpFileException
     * @throws FileException
     */
    public static @NotNull BmpFile read(@NotNull String path) {
        return new BmpFile(FileIO.read(path));
    }

    /**
     * Write BMP file to disk
     * @throws BmpFileException
     * @throws FileException
     */
    public static void write(@NotNull String path, @NotNull BmpFile bmpFile) {
        FileIO.write(path, bmpFile.byteBuffer.array());
    }
}
