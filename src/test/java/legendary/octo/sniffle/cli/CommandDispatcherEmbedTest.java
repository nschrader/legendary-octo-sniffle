package legendary.octo.sniffle.cli;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;

import legendary.octo.sniffle.core.ECipher;
import legendary.octo.sniffle.core.EMode;
import legendary.octo.sniffle.error.SteganoException;

public class CommandDispatcherEmbedTest extends ACommandDispatcherBaseTest {

    @Test
    public void testEmbedHelp() {
        executeCommandLineAndAssert(SUCCESS, "-embed", "-help");
    }

    @Test
    public void testEmbedDefault() {
        executeCommandLineAndAssert(SUCCESS, "-embed", "-in", "inFile", "-p", "in.bmp", "-out", "out.bmp", "-steg", "LSB1", "-pass", "foo");

        verify(fileIOmock).read(inFileCaptor.capture());
        assertEquals("inFile", inFileCaptor.getValue().getName());

        verify(bmpFileIOmock).read(bitmapFileCaptor.capture());
        assertEquals("in.bmp", bitmapFileCaptor.getValue().getName());

        verify(bmpFileIOmock).write(outFileCaptor.capture(), any());
        assertEquals("out.bmp", outFileCaptor.getValue().getName());

        verify(lsb1Mock).conceal(any(), any());
        verify(steganoFormatterMock).format(any());
        verify(cipherMock).encrypt(any(), eq("foo"), eq(ECipher.aes128), eq(EMode.cbc));
        verify(steganoFormatterMock).formatEncrypted(any());
    }

    @Test
    public void testEmbedPlain() {
        executeCommandLineAndAssert(SUCCESS, "-embed", "-in", "inFile", "-p", "in.bmp", "-out", "out.bmp", "-steg", "LSB1");
        verify(steganoFormatterMock).format(any());
        verify(cipherMock, never()).encrypt(any(), any(), any(), any());
        verify(steganoFormatterMock, never()).formatEncrypted(any());
    }

    @Test
    public void testEmbedPlainForced() {
        executeCommandLineAndAssert(SUCCESS, "-embed", "-in", "inFile", "-p", "in.bmp", "-out", "out.bmp", "-steg", "LSB1", "-a", "des", "-m", "ecb");
        verify(steganoFormatterMock).format(any());
        verify(cipherMock, never()).encrypt(any(), any(), any(), any());
        verify(steganoFormatterMock, never()).formatEncrypted(any());
    }

    @Test
    public void testEmbedLSB4() {
        executeCommandLineAndAssert(SUCCESS, "-embed", "-in", "inFile", "-p", "in.bmp", "-out", "out.bmp", "-steg", "LSB4");
        verify(lsb4Mock).conceal(any(), any());
    }

    @Test
    public void testEmbedLSBI() {
        executeCommandLineAndAssert(SUCCESS, "-embed", "-in", "inFile", "-p", "in.bmp", "-out", "out.bmp", "-steg", "LSBI");
        verify(lsbIMock).conceal(any(), any());
    }

    @Test
    public void testEmbedAES128() {
        executeCommandLineAndAssert(SUCCESS, "-embed", "-in", "inFile", "-p", "in.bmp", "-out", "out.bmp", "-steg", "LSB1", "-pass", "foo", "-a", "aes128");
        verify(cipherMock).encrypt(any(), eq("foo"), eq(ECipher.aes128), eq(EMode.cbc));
    }

    @Test
    public void testEmbedAES192() {
        executeCommandLineAndAssert(SUCCESS, "-embed", "-in", "inFile", "-p", "in.bmp", "-out", "out.bmp", "-steg", "LSB1", "-pass", "foo", "-a", "aes192");
        verify(cipherMock).encrypt(any(), eq("foo"), eq(ECipher.aes192), eq(EMode.cbc));
    }

    @Test
    public void testEmbedAES256() {
        executeCommandLineAndAssert(SUCCESS, "-embed", "-in", "inFile", "-p", "in.bmp", "-out", "out.bmp", "-steg", "LSB1", "-pass", "foo", "-a", "aes256");
        verify(cipherMock).encrypt(any(), eq("foo"), eq(ECipher.aes256), eq(EMode.cbc));
    }

    @Test
    public void testEmbedDES() {
        executeCommandLineAndAssert(SUCCESS, "-embed", "-in", "inFile", "-p", "in.bmp", "-out", "out.bmp", "-steg", "LSB1", "-pass", "foo", "-a", "des");
        verify(cipherMock).encrypt(any(), eq("foo"), eq(ECipher.des), eq(EMode.cbc));
    }

    @Test
    public void testEmbedCBC() {
        executeCommandLineAndAssert(SUCCESS, "-embed", "-in", "inFile", "-p", "in.bmp", "-out", "out.bmp", "-steg", "LSB1", "-pass", "foo", "-m", "cbc");
        verify(cipherMock).encrypt(any(), eq("foo"), eq(ECipher.aes128), eq(EMode.cbc));
    }

    @Test
    public void testEmbedOFB() {
        executeCommandLineAndAssert(SUCCESS, "-embed", "-in", "inFile", "-p", "in.bmp", "-out", "out.bmp", "-steg", "LSB1", "-pass", "foo", "-m", "ofb");
        verify(cipherMock).encrypt(any(), eq("foo"), eq(ECipher.aes128), eq(EMode.ofb));
    }

    @Test
    public void testEmbedCFB() {
        executeCommandLineAndAssert(SUCCESS, "-embed", "-in", "inFile", "-p", "in.bmp", "-out", "out.bmp", "-steg", "LSB1", "-pass", "foo", "-m", "cfb");
        verify(cipherMock).encrypt(any(), eq("foo"), eq(ECipher.aes128), eq(EMode.cfb));
    }

    @Test
    public void testEmbedECB() {
        executeCommandLineAndAssert(SUCCESS, "-embed", "-in", "inFile", "-p", "in.bmp", "-out", "out.bmp", "-steg", "LSB1", "-pass", "foo", "-m", "ecb");
        verify(cipherMock).encrypt(any(), eq("foo"), eq(ECipher.aes128), eq(EMode.ecb));
    }

    @Test
    public void testEmbedBusinessException() {
        doThrow(mock(SteganoException.class)).when(lsb1Mock).conceal(any(), any());
        executeCommandLineAndAssert(BUSINESS_FAILURE, "-embed", "-in", "inFile", "-p", "in.bmp", "-out", "out.bmp", "-steg", "LSB1");
        verify(fileIOmock, never()).write(any(), any());
    }

    @Test
    public void testEmbedMissing() {
        executeCommandLineAndAssert(USAGE_FAILURE, "-in", "inFile", "-p", "in.bmp", "-out", "out.bmp", "-steg", "LSB1");
        verify(fileIOmock, never()).write(any(), any());
    }

    @Test
    public void testEmbedInMissing() {
        executeCommandLineAndAssert(USAGE_FAILURE, "-embed", "-p", "in.bmp", "-out", "out.bmp", "-steg", "LSB1");
        verify(fileIOmock, never()).write(any(), any());
    }

    @Test
    public void testEmbedOutMissing() {
        executeCommandLineAndAssert(USAGE_FAILURE, "-embed", "-in", "inFile", "-p", "in.bmp", "-steg", "LSB1");
        verify(fileIOmock, never()).write(any(), any());
    }

    @Test
    public void testEmbedBitmapMissing() {
        executeCommandLineAndAssert(USAGE_FAILURE, "-embed", "-in", "inFile", "-out", "out.bmp", "-steg", "LSB1");
        verify(fileIOmock, never()).write(any(), any());
    }

    @Test
    public void testEmbedStegMissing() {
        executeCommandLineAndAssert(USAGE_FAILURE, "-embed", "-in", "inFile", "-p", "in.bmp", "-out", "out.bmp");
        verify(fileIOmock, never()).write(any(), any());
    }

    @Test
    public void testEmbedUnknownSteg() {
        executeCommandLineAndAssert(USAGE_FAILURE, "-embed", "-in", "inFile", "-p", "in.bmp", "-out", "out.bmp", "-steg", "x");
        verify(fileIOmock, never()).write(any(), any());
    }

    @Test
    public void testEmbedUnknownCipher() {
        executeCommandLineAndAssert(USAGE_FAILURE, "-embed", "-in", "inFile", "-p", "in.bmp", "-out", "out.bmp", "-steg", "LSB1", "-a", "x");
        verify(fileIOmock, never()).write(any(), any());
    }

    @Test
    public void testEmbedUnknownMode() {
        executeCommandLineAndAssert(USAGE_FAILURE, "-embed", "-in", "inFile", "-p", "in.bmp", "-out", "out.bmp", "-steg", "LSB1", "-m", "x");
        verify(fileIOmock, never()).write(any(), any());
    }

    @Test
    public void testEmbedUnknownParam() {
        executeCommandLineAndAssert(USAGE_FAILURE, "-embed", "-in", "inFile", "-p", "in.bmp", "-out", "out.bmp", "-steg", "LSB1", "-x");
        verify(fileIOmock, never()).write(any(), any());
    }
}
