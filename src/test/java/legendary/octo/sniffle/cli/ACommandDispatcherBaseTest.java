package legendary.octo.sniffle.cli;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.google.inject.testing.fieldbinder.Bind;
import com.google.inject.testing.fieldbinder.BoundFieldModule;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import legendary.octo.sniffle.app.SkeletonModule;
import legendary.octo.sniffle.core.IBmpFileIO;
import legendary.octo.sniffle.core.ICipher;
import legendary.octo.sniffle.core.IFileIO;
import legendary.octo.sniffle.core.IStegano;
import lombok.NonNull;
import picocli.CommandLine;

@ExtendWith(MockitoExtension.class)
public abstract class ACommandDispatcherBaseTest {

    @Bind
    @Mock
    protected IFileIO fileIOmock;

    @Bind
    @Mock
    protected IBmpFileIO bmpFileIOmock;

    @Bind
    @Mock
    protected ICipher cipherMock;

    @Bind
    @Named("LSB1")
    @Mock
    protected IStegano lsb1Mock;

    @Bind
    @Named("LSB4")
    @Mock
    protected IStegano lsb4Mock;

    @Bind
    @Named("LSBI")
    @Mock
    protected IStegano lsbIMock;

    @Inject
    protected CommandDispatcher commandDispatcher;

    @Captor
    protected ArgumentCaptor<File> inFileCaptor;

    @Captor
    protected ArgumentCaptor<File> outFileCaptor;

    @Captor
    protected ArgumentCaptor<File> bitmapFileCaptor;

    @BeforeEach
    public void setUp() {
        Guice.createInjector(new SkeletonModule(), BoundFieldModule.of(this)).injectMembers(this);
    }

    protected final @NonNull Integer SUCCESS = 0;
    protected final @NonNull Integer BUSINESS_FAILURE = 1;
    protected final @NonNull Integer USAGE_FAILURE = 2;

    protected void executeCommandLineAndAssert(@NonNull Integer expectedExitCode, String... args) {
        assertEquals(expectedExitCode, new CommandLine(commandDispatcher).execute(args));
    }
}
