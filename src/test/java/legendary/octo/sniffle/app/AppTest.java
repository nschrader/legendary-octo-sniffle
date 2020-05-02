package legendary.octo.sniffle.app;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import java.nio.file.Files;
import java.nio.file.Path;

import com.google.common.io.Resources;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import lombok.SneakyThrows;

public class AppTest {

    @Test
    @SneakyThrows
    public void testEmbed(@TempDir Path dir) { // TODO: Change to real test vectors
        var bitmap = Resources.getResource("ok.bmp").getPath();
        var in = Resources.getResource("32b.bmp").getPath();
        var outFile = dir.resolve("outFile");

        var cmd = String.format("-embed -in %s -p %s -out %s -steg LSB1 -pass foo", in, bitmap, outFile.toString());
        App.main(cmd.split("\\s+"));

        assertArrayEquals(Resources.toByteArray(Resources.getResource("ok.bmp")), Files.readAllBytes(outFile));
    }

    @Test
    @SneakyThrows
    public void testExtract(@TempDir Path dir) { // TODO: Change to real test vectors
        var bitmap = Resources.getResource("ok.bmp").getPath();
        var outFile = dir.resolve("outFile");

        var cmd = String.format("-extract -p %s -out %s -steg LSB1 -pass foo", bitmap, outFile.toString());
        App.main(cmd.split("\\s+"));

        assertArrayEquals(new byte[0], Files.readAllBytes(outFile));
    }
}
