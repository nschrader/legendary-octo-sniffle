package legendary.octo.sniffle.io;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Path;

import com.google.common.io.Files;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.testing.fieldbinder.BoundFieldModule;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import legendary.octo.sniffle.core.DCommonFile;
import legendary.octo.sniffle.error.FileException;

public class FileIOTest {
    private final File root = new File("/");
    private final DCommonFile simpleData = new DCommonFile("x", new byte[] {0x13});

    @Inject
    private FileIO fileIO;

    @BeforeEach
    public void setUp() {
        Guice.createInjector(BoundFieldModule.of(this)).injectMembers(this);
    }

    @Test
    public void read(@TempDir Path dir) throws IOException {
        var f = dir.resolve("file.x").toFile();
        Files.asByteSink(f).write(simpleData.getBytes());
        assertEquals(simpleData, fileIO.read(f));
    }

    @Test
    public void readNotExisting() {
        assertThrows(FileException.class, () -> fileIO.read(root));
    }

    @Test
    public void readTooBig(@TempDir Path dir) throws IOException {
        var f = dir.resolve("big").toFile();
        try (var raf = new RandomAccessFile(f, "rw")) {
            raf.setLength(11 * FileIO.MiB);
            assertTrue(
                assertThrows(FileException.class, () -> fileIO.read(f))
                    .getMessage()
                    .contains("too big"));
        }   
    }

    @Test
    public void write(@TempDir Path dir) throws IOException {
        var f = dir.resolve("file").toFile();
        fileIO.write(f, simpleData);
        assertArrayEquals(simpleData.getBytes(), Files.asByteSource(f).read());
    }

    @Test
    public void writeInvalidPath() {
        var data = new DCommonFile(".x", new byte[1]);
        assertThrows(FileException.class, () ->  fileIO.write(root, data));
    }
}
