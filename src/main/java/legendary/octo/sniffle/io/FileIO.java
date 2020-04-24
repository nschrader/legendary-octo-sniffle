package legendary.octo.sniffle.io;

import java.io.File;
import java.io.IOException;

import com.google.common.io.Files;

import lombok.NonNull;
import lombok.experimental.UtilityClass;

@UtilityClass
public class FileIO {
    public final @NonNull Integer MiB = 2 << 20;
    public final @NonNull Integer MAX_BYTES = 10 * MiB;

    /**
     * Read a file from disk to memory. It mustn't exceed {@link #MAX_BYTES}.
     * @throws FileException
     */
    public @NonNull byte[] read(@NonNull String path) {
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

    /**
     * Write from memory to file.
     * @throws FileException
     */
    public void write(@NonNull String path, @NonNull byte[] file) {
        try {
            var sink = Files.asByteSink(new File(path));
            sink.write(file);
        } catch (IOException e) {
            throw new FileException(e);
        }
    }
}
