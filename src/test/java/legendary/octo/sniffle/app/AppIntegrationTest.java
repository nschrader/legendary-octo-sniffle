package legendary.octo.sniffle.app;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import java.nio.file.Files;
import java.nio.file.Path;

import com.ginsberg.junit.exit.ExpectSystemExitWithStatus;
import com.google.common.io.Resources;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import lombok.SneakyThrows;

public class AppIntegrationTest {

    @Test
    @SneakyThrows
    @ExpectSystemExitWithStatus(0)
    public void testEmbed(@TempDir Path dir) {
        var bitmap = Resources.getResource("lado.bmp").getPath();
        var in = Resources.getResource("itba.png").getPath();
        var outFile = dir.resolve("outFile");

        App.main("-embed", "-in", in, "-p", bitmap, "-out", outFile.toString(), "-steg", "LSB1", "-pass", "foo");
        assertArrayEquals(Resources.toByteArray(Resources.getResource("ladoLSB1.bmp")), Files.readAllBytes(outFile));
    }

    @Test
    @SneakyThrows
    @ExpectSystemExitWithStatus(0)
    public void testExtract(@TempDir Path dir) {
        var bitmap = Resources.getResource("ladoLSB1.bmp").getPath();
        var outFile = dir.resolve("outFile");

        App.main("-extract", "-p", bitmap, "-out", outFile.toString(), "-steg", "LSB1", "-pass", "foo");
        assertArrayEquals(Resources.toByteArray(Resources.getResource("itba.png")), Files.readAllBytes(outFile));
    }

    @Test
    @ExpectSystemExitWithStatus(2)
    public void testError() {
        App.main("-xyz");
    }
}
