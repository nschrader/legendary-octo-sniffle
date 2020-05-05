package legendary.octo.sniffle.io;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;
import java.nio.file.Path;

import com.google.common.io.Files;
import com.google.common.io.Resources;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.testing.fieldbinder.Bind;
import com.google.inject.testing.fieldbinder.BoundFieldModule;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import legendary.octo.sniffle.core.IBmpFile;
import legendary.octo.sniffle.core.IFileIO;
import legendary.octo.sniffle.error.BmpFileException;
import lombok.SneakyThrows;

@ExtendWith(MockitoExtension.class)
public class BmpFileIOTest {
    private final File ok = new File(Resources.getResource("ok.bmp").getPath());
    private final File _32b = new File(Resources.getResource("32b.bmp").getPath());

    @Bind
    @Mock
    private IFileIO fileIO;

    @Inject
    private BmpFileIO bmpFileIO;

    @BeforeEach
    public void setUp() {
        Guice.createInjector(BoundFieldModule.of(this)).injectMembers(this);
    }

    @Test
    @SneakyThrows
    public void readOk() {
        when(fileIO.read(ok)).thenReturn(Files.toByteArray(ok));
        assertNotNull(bmpFileIO.read(ok));
        verify(fileIO).read(ok);
    }

    @Test
    @SneakyThrows
    public void readInvalid() {
        when(fileIO.read(_32b)).thenReturn(Files.toByteArray(_32b));
        assertThrows(BmpFileException.class, () -> bmpFileIO.read(_32b));
    }

    @Test
    @SneakyThrows
    public void writeOk(@TempDir Path dir) {
        var bmpFile = mock(IBmpFile.class);
        when(bmpFile.getBytes()).thenReturn(Files.toByteArray(ok));
        bmpFileIO.write(dir.resolve("test.bmp").toFile(), bmpFile);
        verify(fileIO).write(any(), any());
    }
}
