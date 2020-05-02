package legendary.octo.sniffle.cli;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.io.File;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.google.inject.testing.fieldbinder.Bind;
import com.google.inject.testing.fieldbinder.BoundFieldModule;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import legendary.octo.sniffle.app.SkeletonModule;
import legendary.octo.sniffle.core.ECipher;
import legendary.octo.sniffle.core.EMode;
import legendary.octo.sniffle.core.IBmpFileIO;
import legendary.octo.sniffle.core.ICipher;
import legendary.octo.sniffle.core.IFileIO;
import legendary.octo.sniffle.core.IStegano;
import picocli.CommandLine;

@ExtendWith(MockitoExtension.class)
public class CommandDispatcherTest {

    @Bind
    @Mock
    private IFileIO fileIOmock;

    @Bind
    @Mock
    private IBmpFileIO bmpFileIOmock;

    @Bind
    @Mock
    private ICipher cipherMock;

    @Bind
    @Named("LSB1")
    @Mock
    private IStegano lsb1Mock;

    @Bind
    @Named("LSB2")
    @Mock
    private IStegano lsb2Mock;

    @Bind
    @Named("LSBI")
    @Mock
    private IStegano lsbIMock;

    @Inject
    public CommandDispatcher commandDispatcher;

    @Captor
    ArgumentCaptor<File> inFileCaptor;

    @Captor
    ArgumentCaptor<File> outFileCaptor;

    @Captor
    ArgumentCaptor<File> bitmapFileCaptor;

    @BeforeEach
    public void setUp() {
        Guice.createInjector(new SkeletonModule(), BoundFieldModule.of(this)).injectMembers(this);
    }

    @Test
    public void testEmbedDefault() {
        new CommandLine(commandDispatcher).execute("-embed", "-in", "inFile", "-p", "in.bmp", "-out", "out.bmp", "-steg", "LSB1", "-pass", "foo");

        verify(fileIOmock).read(inFileCaptor.capture());
        assertEquals("inFile", inFileCaptor.getValue().getName());

        verify(bmpFileIOmock).read(bitmapFileCaptor.capture());
        assertEquals("in.bmp", bitmapFileCaptor.getValue().getName());

        verify(bmpFileIOmock).write(outFileCaptor.capture(), any());
        assertEquals("out.bmp", outFileCaptor.getValue().getName());

        verify(lsb1Mock).conceal(any(), any());
        verify(cipherMock).encrypt(any(), eq("foo"), eq(ECipher.aes128), eq(EMode.cbc));
    }

    @Test
    public void testEmbedPlain() {
        new CommandLine(commandDispatcher).execute("-embed", "-in", "inFile", "-p", "in.bmp", "-out", "out.bmp", "-steg", "LSB1");
        verify(cipherMock, never()).encrypt(any(), any(), any(), any());
    }

    @Test
    public void testEmbedPlainForced() {
        new CommandLine(commandDispatcher).execute("-embed", "-in", "inFile", "-p", "in.bmp", "-out", "out.bmp", "-steg", "LSB1", "-a", "des", "-m", "ecb");
        verify(cipherMock, never()).encrypt(any(), any(), any(), any());
    }

    @Test
    public void testEmbedLSB2() {
        new CommandLine(commandDispatcher).execute("-embed", "-in", "inFile", "-p", "in.bmp", "-out", "out.bmp", "-steg", "LSB2");
        verify(lsb2Mock).conceal(any(), any());
    }

    @Test
    public void testEmbedLSBI() {
        new CommandLine(commandDispatcher).execute("-embed", "-in", "inFile", "-p", "in.bmp", "-out", "out.bmp", "-steg", "LSBI");
        verify(lsbIMock).conceal(any(), any());
    }

    @Test
    public void testEmbedAES128() {
        new CommandLine(commandDispatcher).execute("-embed", "-in", "inFile", "-p", "in.bmp", "-out", "out.bmp", "-steg", "LSB1", "-pass", "foo", "-a", "aes128");
        verify(cipherMock).encrypt(any(), eq("foo"), eq(ECipher.aes128), eq(EMode.cbc));
    }

    @Test
    public void testEmbedAES192() {
        new CommandLine(commandDispatcher).execute("-embed", "-in", "inFile", "-p", "in.bmp", "-out", "out.bmp", "-steg", "LSB1", "-pass", "foo", "-a", "aes192");
        verify(cipherMock).encrypt(any(), eq("foo"), eq(ECipher.aes192), eq(EMode.cbc));
    }

    @Test
    public void testEmbedAES256() {
        new CommandLine(commandDispatcher).execute("-embed", "-in", "inFile", "-p", "in.bmp", "-out", "out.bmp", "-steg", "LSB1", "-pass", "foo", "-a", "aes256");
        verify(cipherMock).encrypt(any(), eq("foo"), eq(ECipher.aes256), eq(EMode.cbc));
    }

    @Test
    public void testEmbedDES() {
        new CommandLine(commandDispatcher).execute("-embed", "-in", "inFile", "-p", "in.bmp", "-out", "out.bmp", "-steg", "LSB1", "-pass", "foo", "-a", "des");
        verify(cipherMock).encrypt(any(), eq("foo"), eq(ECipher.des), eq(EMode.cbc));
    }

    @Test
    public void testEmbedCBC() {
        new CommandLine(commandDispatcher).execute("-embed", "-in", "inFile", "-p", "in.bmp", "-out", "out.bmp", "-steg", "LSB1", "-pass", "foo", "-m", "cbc");
        verify(cipherMock).encrypt(any(), eq("foo"), eq(ECipher.aes128), eq(EMode.cbc));
    }

    @Test
    public void testEmbedOFB() {
        new CommandLine(commandDispatcher).execute("-embed", "-in", "inFile", "-p", "in.bmp", "-out", "out.bmp", "-steg", "LSB1", "-pass", "foo", "-m", "ofb");
        verify(cipherMock).encrypt(any(), eq("foo"), eq(ECipher.aes128), eq(EMode.ofb));
    }

    @Test
    public void testEmbedCFB() {
        new CommandLine(commandDispatcher).execute("-embed", "-in", "inFile", "-p", "in.bmp", "-out", "out.bmp", "-steg", "LSB1", "-pass", "foo", "-m", "cfb");
        verify(cipherMock).encrypt(any(), eq("foo"), eq(ECipher.aes128), eq(EMode.cfb));
    }

    @Test
    public void testEmbedECB() {
        new CommandLine(commandDispatcher).execute("-embed", "-in", "inFile", "-p", "in.bmp", "-out", "out.bmp", "-steg", "LSB1", "-pass", "foo", "-m", "ecb");
        verify(cipherMock).encrypt(any(), eq("foo"), eq(ECipher.aes128), eq(EMode.ecb));
    }

    @Test
    public void testExtractDefault() {
        new CommandLine(commandDispatcher).execute("-extract", "-p", "in.bmp", "-out", "outFile", "-steg", "LSB1", "-pass", "foo");

        verify(bmpFileIOmock).read(bitmapFileCaptor.capture());
        assertEquals("in.bmp", bitmapFileCaptor.getValue().getName());

        verify(fileIOmock).write(outFileCaptor.capture(), any());
        assertEquals("outFile", outFileCaptor.getValue().getName());

        verify(lsb1Mock).reveal(any());
        verify(cipherMock).decrypt(any(), eq("foo"), eq(ECipher.aes128), eq(EMode.cbc));
    }

    @Test
    public void testExtractPlain() {
        new CommandLine(commandDispatcher).execute("-extract", "-p", "in.bmp", "-out", "outFile", "-steg", "LSB1");
        verify(cipherMock, never()).decrypt(any(), any(), any(), any());
    }

    @Test
    public void testExtractPlainForced() {
        new CommandLine(commandDispatcher).execute("-extract", "-p", "in.bmp", "-out", "outFile", "-steg", "LSB1", "-a", "des", "-m", "ecb");
        verify(cipherMock, never()).decrypt(any(), any(), any(), any());
    }

    @Test
    public void testExtractLSB2() {
        new CommandLine(commandDispatcher).execute("-extract", "-p", "in.bmp", "-out", "outFile", "-steg", "LSB2");
        verify(lsb2Mock).reveal(any());
    }

    @Test
    public void testExtractLSBI() {
        new CommandLine(commandDispatcher).execute("-extract", "-p", "in.bmp", "-out", "outFile", "-steg", "LSBI");
        verify(lsbIMock).reveal(any());
    }

    @Test
    public void testExtractAES128() {
        new CommandLine(commandDispatcher).execute("-extract", "-p", "in.bmp", "-out", "outFile", "-steg", "LSB1", "-pass", "foo", "-a", "aes128");
        verify(cipherMock).decrypt(any(), eq("foo"), eq(ECipher.aes128), eq(EMode.cbc));
    }

    @Test
    public void testExtractAES192() {
        new CommandLine(commandDispatcher).execute("-extract", "-p", "in.bmp", "-out", "outFile", "-steg", "LSB1", "-pass", "foo", "-a", "aes192");
        verify(cipherMock).decrypt(any(), eq("foo"), eq(ECipher.aes192), eq(EMode.cbc));
    }

    @Test
    public void testExtractAES256() {
        new CommandLine(commandDispatcher).execute("-extract", "-p", "in.bmp", "-out", "outFile", "-steg", "LSB1", "-pass", "foo", "-a", "aes256");
        verify(cipherMock).decrypt(any(), eq("foo"), eq(ECipher.aes256), eq(EMode.cbc));
    }

    @Test
    public void testExtractDES() {
        new CommandLine(commandDispatcher).execute("-extract", "-p", "in.bmp", "-out", "outFile", "-steg", "LSB1", "-pass", "foo", "-a", "des");
        verify(cipherMock).decrypt(any(), eq("foo"), eq(ECipher.des), eq(EMode.cbc));
    }

    @Test
    public void testExtractCBC() {
        new CommandLine(commandDispatcher).execute("-extract", "-p", "in.bmp", "-out", "outFile", "-steg", "LSB1", "-pass", "foo", "-m", "cbc");
        verify(cipherMock).decrypt(any(), eq("foo"), eq(ECipher.aes128), eq(EMode.cbc));
    }

    @Test
    public void testExtractOFB() {
        new CommandLine(commandDispatcher).execute("-extract", "-p", "in.bmp", "-out", "outFile", "-steg", "LSB1", "-pass", "foo", "-m", "ofb");
        verify(cipherMock).decrypt(any(), eq("foo"), eq(ECipher.aes128), eq(EMode.ofb));
    }

    @Test
    public void testExtractCFB() {
        new CommandLine(commandDispatcher).execute("-extract", "-p", "in.bmp", "-out", "outFile", "-steg", "LSB1", "-pass", "foo", "-m", "cfb");
        verify(cipherMock).decrypt(any(), eq("foo"), eq(ECipher.aes128), eq(EMode.cfb));
    }

    @Test
    public void testExtractECB() {
        new CommandLine(commandDispatcher).execute("-extract", "-p", "in.bmp", "-out", "outFile", "-steg", "LSB1", "-pass", "foo", "-m", "ecb");
        verify(cipherMock).decrypt(any(), eq("foo"), eq(ECipher.aes128), eq(EMode.ecb));
    }
}
