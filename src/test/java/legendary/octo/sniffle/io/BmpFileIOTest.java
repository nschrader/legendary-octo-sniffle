package legendary.octo.sniffle.io;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.io.File;
import java.nio.file.Path;

import com.google.common.io.Files;
import com.google.common.io.Resources;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import legendary.octo.sniffle.core.IFileIO;

public class BmpFileIOTest {
    private final File ok = new File(Resources.getResource("ok.bmp").getPath());
    private final File _32b = new File(Resources.getResource("32b.bmp").getPath());

    private IFileIO fileIO;
    private BmpFileIO bmpFileIO;

    @BeforeEach
    public void setUp() {
        fileIO = spy(new FileIO());
        doNothing().when(fileIO).write(any(), any());
        bmpFileIO = new BmpFileIO(fileIO);
    }

    @Test
    public void readOk() {
        assertNotNull(bmpFileIO.read(ok));
        verify(fileIO).read(ok);
    }

    @Test
    public void readInvalid() {
        assertThrows(BmpFileException.class, () -> bmpFileIO.read(_32b));
    }

    @Test
    public void writeOk(@TempDir Path dir) {
        var f = bmpFileIO.read(ok);
        bmpFileIO.write(dir.resolve("test.bmp").toFile(), f);
        verify(fileIO).write(any(), any());
    }
}
