package legendary.octo.sniffle.io;

import org.jetbrains.annotations.NotNull;

public final class BmpFileIO {
    public static @NotNull BmpFile read(@NotNull String path) {
        return new BmpFile(FileIO.read(path));
    }

    public static void write(@NotNull String path, @NotNull BmpFile bmpFile) {
        FileIO.write(path, bmpFile.byteBuffer.array());
    }
}
