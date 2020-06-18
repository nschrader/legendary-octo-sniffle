package legendary.octo.sniffle.app;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.itsallcode.junit.sysextensions.AssertExit.assertExitWithStatus;

import java.nio.file.Files;
import java.nio.file.Path;

import com.google.common.io.Resources;

import org.itsallcode.junit.sysextensions.ExitGuard;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;

import lombok.SneakyThrows;

@ExtendWith(ExitGuard.class)
public class AppIntegrationTest {

    @Test
    @SneakyThrows
    public void testEmbed(@TempDir Path dir) {
        var bitmap = Resources.getResource("lado.bmp").getPath();
        var in = Resources.getResource("itba.png").getPath();
        var outFile = dir.resolve("outFile.bmp");

        assertExitWithStatus(0, () -> App.main("-embed", "-in", in, "-p", bitmap, "-out", outFile.toString(), "-steg", "LSB1"));
        assertArrayEquals(Resources.toByteArray(Resources.getResource("ladoLSB1.bmp")), Files.readAllBytes(outFile));
    }

    @Test
    @SneakyThrows
    public void testEmbedEncrypted(@TempDir Path dir) {
        var bitmap = Resources.getResource("lado.bmp").getPath();
        var in = Resources.getResource("itba.png").getPath();
        var outFile = dir.resolve("outFile.bmp");

        assertExitWithStatus(0, () -> App.main("-embed", "-in", in, "-p", bitmap, "-out", outFile.toString(), "-steg", "LSB4", "-a", "aes256", "-m", "ofb", "-pass", "secreto"));
        assertArrayEquals(Resources.toByteArray(Resources.getResource("ladoLSB4aes256ofb.bmp")), Files.readAllBytes(outFile));
    }

    @Test
    @SneakyThrows
    public void testExtract(@TempDir Path dir) {
        var bitmap = Resources.getResource("ladoLSB1.bmp").getPath();
        var outFile = dir.resolve("outFile.png");

        assertExitWithStatus(0, () -> App.main("-extract", "-p", bitmap, "-out", outFile.toString(), "-steg", "LSB1"));
        assertArrayEquals(Resources.toByteArray(Resources.getResource("itba.png")), Files.readAllBytes(outFile));
    }

    @Test
    @SneakyThrows
    public void testExtractEncrypted(@TempDir Path dir) {
        var bitmap = Resources.getResource("ladoLSB4aes256ofb.bmp").getPath();
        var outFile = dir.resolve("outFile.png");

        assertExitWithStatus(0, () -> App.main("-extract", "-p", bitmap, "-out", outFile.toString(), "-steg", "LSB4", "-a", "aes256", "-m", "ofb", "-pass", "secreto"));
        assertArrayEquals(Resources.toByteArray(Resources.getResource("itba.png")), Files.readAllBytes(outFile));
    }

    @Test
    public void testError() {
        assertExitWithStatus(2, () -> App.main("-xyz"));
    }
}
