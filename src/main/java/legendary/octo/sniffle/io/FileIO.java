package legendary.octo.sniffle.io;

import java.io.File;
import java.io.IOException;

import com.google.common.io.Files;

import legendary.octo.sniffle.core.IFileIO;
import lombok.NonNull;

public class FileIO implements IFileIO {
    public static final Integer MiB = 2 << 20;
    public static final Integer MAX_BYTES = 10 * MiB;

    @Override
    public @NonNull byte[] read(@NonNull File file) {
        try {
            var source = Files.asByteSource(file);
            if (source.size() > MAX_BYTES) {
                throw new FileException("File too big: %d MiB (max: %d MiB)", source.size()/MiB, MAX_BYTES/MiB);
            }

            return source.read();
        } catch (IOException e) {
            throw new FileException(e);
        }
    }

    @Override
    public void write(@NonNull File file, @NonNull byte[] content) {
        try {
            var sink = Files.asByteSink(file);
            sink.write(content);
        } catch (IOException e) {
            throw new FileException(e);
        }
    }
}
