package legendary.octo.sniffle.crypto;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.security.MessageDigest;
import java.util.Arrays;

import javax.crypto.Cipher;

import com.google.common.primitives.Bytes;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.testing.fieldbinder.Bind;
import com.google.inject.testing.fieldbinder.BoundFieldModule;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import legendary.octo.sniffle.core.ECipher;
import legendary.octo.sniffle.core.EMode;
import legendary.octo.sniffle.core.IJCAFactory;
import legendary.octo.sniffle.core.IOpenSSLPBKDF;
import lombok.NonNull;
import lombok.SneakyThrows;

@ExtendWith(MockitoExtension.class)
public class CipherImplTest {

    @Mock
    @Bind
    private IJCAFactory<Cipher> cipherSpy;

    @Mock
    @Bind
    private IJCAFactory<MessageDigest> hashSpy;

    @Spy
    @Bind(to = IOpenSSLPBKDF.class)
    private OpenSSLPBKDF pbkdfSpy;

    @Inject
    private CipherImpl cipher;

    private byte[] testBytes;

    @BeforeEach
    @SneakyThrows
    public void setUp() {
        Guice.createInjector(BoundFieldModule.of(this)).injectMembers(this);

        var md = MessageDigest.getInstance("SHA-1");
        testBytes = Bytes.concat(md.digest("Test".getBytes()), md.digest("Bytes".getBytes()));

        when(cipherSpy.getInstance(any())).thenAnswer(i -> Cipher.getInstance((String) i.getArgument(0)));
        when(hashSpy.getInstance(any())).thenAnswer(i -> MessageDigest.getInstance((String) i.getArgument(0)));
    }

    @Test
    @SneakyThrows
    public void testAes128Ecb() {
        test(ECipher.aes128, EMode.ecb, "AES/ECB/PKCS5Padding", 16);
    }

    @Test
    @SneakyThrows
    public void testAes192Ecb() {
        test(ECipher.aes192, EMode.ecb, "AES/ECB/PKCS5Padding", 24);
    }

    @Test
    @SneakyThrows
    public void testAes256Ecb() {
        test(ECipher.aes256, EMode.ecb, "AES/ECB/PKCS5Padding", 32);
    }

    @Test
    @SneakyThrows
    public void testDesEcb() {
        test(ECipher.des, EMode.ecb, "DES/ECB/PKCS5Padding", 8);
    }

    @Test
    @SneakyThrows
    public void testAes128Cbc() {
        test(ECipher.aes128, EMode.cbc, "AES/CBC/PKCS5Padding", 16);
    }

    @Test
    @SneakyThrows
    public void testAes192Cbc() {
        test(ECipher.aes192, EMode.cbc, "AES/CBC/PKCS5Padding", 24);
    }

    @Test
    @SneakyThrows
    public void testAes256Cbc() {
        test(ECipher.aes256, EMode.cbc, "AES/CBC/PKCS5Padding", 32);
    }

    @Test
    @SneakyThrows
    public void testDesCbc() {
        test(ECipher.des, EMode.cbc, "DES/CBC/PKCS5Padding", 8);
    }

    @Test
    @SneakyThrows
    public void testAes128Cfb() {
        test(ECipher.aes128, EMode.cfb, "AES/CFB8/NoPadding", 16);
    }

    @Test
    @SneakyThrows
    public void testAes192Cfb() {
        test(ECipher.aes192, EMode.cfb, "AES/CFB8/NoPadding", 24);
    }

    @Test
    @SneakyThrows
    public void testAes256Cfb() {
        test(ECipher.aes256, EMode.cfb, "AES/CFB8/NoPadding", 32);
    }

    @Test
    @SneakyThrows
    public void testDesCfb() {
        test(ECipher.des, EMode.cfb, "DES/CFB8/NoPadding", 8);
    }

    @Test
    @SneakyThrows
    public void testAes128Ofb() {
        test(ECipher.aes128, EMode.ofb, "AES/OFB/NoPadding", 16);
    }

    @Test
    @SneakyThrows
    public void testAes192Ofb() {
        test(ECipher.aes192, EMode.ofb, "AES/OFB/NoPadding", 24);
    }

    @Test
    @SneakyThrows
    public void testAes256Ofb() {
        test(ECipher.aes256, EMode.ofb, "AES/OFB/NoPadding", 32);
    }

    @Test
    @SneakyThrows
    public void testDesOfb() {
        test(ECipher.des, EMode.ofb, "DES/OFB/NoPadding", 8);
    }

    @SneakyThrows
    private void test(ECipher eCipher, EMode eMode, @NonNull String expectedAlgorithm, int expectedKeySize) {
        var c = cipher.encrypt(testBytes, "pass", eCipher, eMode);
        var m = cipher.decrypt(c, "pass", eCipher, eMode);

        assertArrayEquals(testBytes, m);
        assertArrayNotEquals(m, c);
        verify(cipherSpy, times(2)).getInstance(eq(expectedAlgorithm));
        verify(hashSpy, times(2)).getInstance(eq("SHA-256"));
        verify(pbkdfSpy, times(2)).of(any(), any(), any(), any(), eq(expectedKeySize), anyInt());
    }

    private void assertArrayNotEquals(@NonNull byte[] a, @NonNull byte[] b) {
        assertFalse(Arrays.equals(a, b));
    }
}
