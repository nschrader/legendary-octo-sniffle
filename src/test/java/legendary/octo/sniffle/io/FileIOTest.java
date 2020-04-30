package legendary.octo.sniffle.io;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Path;

import com.google.common.io.Files;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class FileIOTest {
    private final File root = new File("/");

    private FileIO fileIO() {
        return new FileIO();
    }

    @Test
    public void read(@TempDir Path dir) throws IOException {
        var data = new byte[] {0x13};
        var f = dir.resolve("file").toFile();
        Files.asByteSink(f).write(data);
        assertArrayEquals(data, fileIO().read(f));
    }

    @Test
    public void readNotExisting() {
        assertThrows(FileException.class, () -> fileIO().read(root));
    }

    @Test
    public void readTooBig(@TempDir Path dir) throws IOException {
        var f = dir.resolve("big").toFile();
        try (var raf = new RandomAccessFile(f, "rw")) {
            raf.setLength(11 * FileIO.MiB);
            assertTrue(
                assertThrows(FileException.class, () -> fileIO().read(f))
                    .getMessage()
                    .contains("too big"));
        }   
    }

    @Test
    public void write(@TempDir Path dir) throws IOException {
        var data = new byte[] {0x13};
        var f = dir.resolve("file").toFile();
        fileIO().write(f, data);
        assertArrayEquals(data, Files.asByteSource(f).read());
    }

    @Test
    public void writeInvalidPath() {
        assertThrows(FileException.class, () ->  fileIO().write(root, new byte[1]));
    }
}
