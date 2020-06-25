package legendary.octo.sniffle.io;

import java.io.File;
import java.io.IOException;

import com.google.common.io.Files;

import legendary.octo.sniffle.core.DCommonFile;
import legendary.octo.sniffle.core.IFileIO;
import legendary.octo.sniffle.error.FileException;
import lombok.NonNull;

public class FileIO implements IFileIO {

    @Override
    public @NonNull DCommonFile read(@NonNull File file) {
        try {
            var source = Files.asByteSource(file);
            if (source.size() > MAX_BYTES) {
                throw new FileException("File too big: %d MiB (max: %d MiB)", source.size()/MiB, MAX_BYTES/MiB);
            }

            var bytes = source.read();
            var extension = Files.getFileExtension(file.getPath());
            return new DCommonFile(extension, bytes);
        } catch (IOException e) {
            throw new FileException(e);
        }
    }

    @Override
    public void write(@NonNull File file, @NonNull DCommonFile content) {
        try {
            var extension = "." + content.getExtension();
            if (!file.getPath().endsWith(extension)) {
                var newPath = file.getPath() + extension;
                file = new File(newPath);
            }

            var sink = Files.asByteSink(file);
            sink.write(content.getBytes());
        } catch (IOException e) {
            throw new FileException(e);
        }
    }
}
