package legendary.octo.sniffle.io;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.nio.file.Path;

import com.google.common.io.Resources;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class BmpFileIOTest {
    private final @NotNull String ok = Resources.getResource("ok.bmp").getPath();
    private final @NotNull String _32b = Resources.getResource("32b.bmp").getPath();

    @Test
    public void readOk() {
        assertNotNull(BmpFileIO.read(ok));
    }

    @Test
    public void readInvalid() {
        assertThrows(BmpFileException.class, () -> BmpFileIO.read(_32b));
    }

    @Test
    public void writeOk(@TempDir Path dir) {
        var f = BmpFileIO.read(ok);
        BmpFileIO.write(dir.resolve("test.bmp").toString(), f);
    }
}
