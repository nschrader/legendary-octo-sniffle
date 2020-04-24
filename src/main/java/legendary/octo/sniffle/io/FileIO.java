package legendary.octo.sniffle.io;

import java.io.File;
import java.io.IOException;

import com.google.common.io.Files;

import org.jetbrains.annotations.NotNull;

public final class FileIO {
    public static final @NotNull Integer MiB = 2 << 20;
    public static final @NotNull Integer MAX_BYTES = 10 * MiB;

    public static @NotNull byte[] read(@NotNull String path) {
        try {
            var source = Files.asByteSource(new File(path));
            if (source.size() > MAX_BYTES) {
                throw new FileException("File too big: %d MiB (max: %d MiB)", source.size()/MiB, MAX_BYTES/MiB);
            }

            return source.read();
        } catch (IOException e) {
            throw new FileException(e);
        }
    }

    public static void write(@NotNull String path, @NotNull byte[] file) {
        try {
            var sink = Files.asByteSink(new File(path));
            sink.write(file);
        } catch (IOException e) {
            throw new FileException(e);
        }
    }
}
