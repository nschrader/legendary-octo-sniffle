package legendary.octo.sniffle.io;

import java.io.File;

import com.google.inject.Inject;

import legendary.octo.sniffle.core.IBmpFile;
import legendary.octo.sniffle.core.IBmpFileIO;
import legendary.octo.sniffle.core.IFileIO;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(onConstructor_ = @Inject)
public class BmpFileIO implements IBmpFileIO {
    private final IFileIO fileIOImpl;

    @Override
    public @NonNull IBmpFile read(@NonNull File file) {
        return new BmpFile(fileIOImpl.read(file));
    }

    @Override
    public void write(@NonNull File file, @NonNull IBmpFile bmpFile) {
        fileIOImpl.write(file, bmpFile.getBytes());
    }
}
