package legendary.octo.sniffle.cli;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;

import legendary.octo.sniffle.core.ECipher;
import legendary.octo.sniffle.core.EMode;
import legendary.octo.sniffle.error.SteganoException;
import picocli.CommandLine;

public class CommandDispatcherExtractTest extends ACommandDispatcherTest {

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

    @Test
    public void testExtractBusinessException() {
        doThrow(new SteganoException()).when(lsb1Mock).reveal(any());
        executeCommandLineAndAssert(BUSINESS_FAILURE, "-extract", "-p", "in.bmp", "-out", "outFile", "-steg", "LSB1");
        verify(fileIOmock, never()).write(any(), any());
    }

    @Test
    public void testExtractMissing() {
        executeCommandLineAndAssert(USAGE_FAILURE, "-p", "in.bmp", "-out", "outFile", "-steg", "LSB1");
        verify(fileIOmock, never()).write(any(), any());
    }

    @Test
    public void testExtractOutMissing() {
        executeCommandLineAndAssert(USAGE_FAILURE, "-extract", "-p", "in.bmp", "-steg", "LSB1");
        verify(fileIOmock, never()).write(any(), any());
    }

    @Test
    public void testExtractBitmapMissing() {
        executeCommandLineAndAssert(USAGE_FAILURE, "-extract", "-out", "outFile", "-steg", "LSB1");
        verify(fileIOmock, never()).write(any(), any());
    }

    @Test
    public void testExtractStegMissing() {
        executeCommandLineAndAssert(USAGE_FAILURE, "-extract", "-p", "in.bmp", "-out", "outFile");
        verify(fileIOmock, never()).write(any(), any());
    }

    @Test
    public void testExtractUnknownSteg() {
        executeCommandLineAndAssert(USAGE_FAILURE, "-extract", "-p", "in.bmp", "-out", "outFile", "-steg", "x");
        verify(fileIOmock, never()).write(any(), any());
    }

    @Test
    public void testExtractUnknownCipher() {
        executeCommandLineAndAssert(USAGE_FAILURE, "-extract", "-p", "in.bmp", "-out", "outFile", "-steg", "LSB1", "-a", "x");
        verify(fileIOmock, never()).write(any(), any());
    }

    @Test
    public void testExtractUnknownMode() {
        executeCommandLineAndAssert(USAGE_FAILURE, "-extract", "-p", "in.bmp", "-out", "outFile", "-steg", "LSB1", "-m", "x");
        verify(fileIOmock, never()).write(any(), any());
    }

    @Test
    public void testExtractUnknownParam() {
        executeCommandLineAndAssert(USAGE_FAILURE, "-extract", "-p", "in.bmp", "-out", "outFile", "-steg", "LSB1", "-x");
        verify(fileIOmock, never()).write(any(), any());
    }
}
