package legendary.octo.sniffle.stegano;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;
import java.util.function.Function;

import com.google.common.io.Resources;
import com.google.inject.Inject;

import legendary.octo.sniffle.core.DCommonFile;
import legendary.octo.sniffle.core.ECipher;
import legendary.octo.sniffle.core.EMode;
import legendary.octo.sniffle.core.IBmpFileIO;
import legendary.octo.sniffle.core.ICipher;
import legendary.octo.sniffle.core.IFileIO;
import legendary.octo.sniffle.core.IStegano;
import legendary.octo.sniffle.core.ISteganoFormatter;
import lombok.NonNull;

abstract class ALSBIntegrationBaseTest {

    @Inject
    protected IFileIO fileIOImpl;

    @Inject
    protected IBmpFileIO bmpFileIOImpl;

    @Inject 
    private ISteganoFormatter steganoFormatterImpl;

    @Inject
    private IStegano steganoImpl;

    @Inject
    private ICipher cipherImpl;

    protected Function<DCommonFile, byte[]> toPlaintext() {
        return cf -> steganoFormatterImpl.format(cf);
    }

    protected Function<byte[], DCommonFile> fromPlaintext() {
        return b -> steganoFormatterImpl.scan(b);
    }

    protected Function<DCommonFile, byte[]> toCiphertext(String password, ECipher cipher, EMode mode) {
        return cf -> {
            var formatted = steganoFormatterImpl.format(cf);
            var encrypted = cipherImpl.encrypt(formatted, password, cipher, mode);
            return steganoFormatterImpl.formatEncrypted(encrypted);
        };
    }

    protected Function<byte[], DCommonFile> fromCiphertext(String password, ECipher cipher, EMode mode) {
        return b -> {
            var encrypted = steganoFormatterImpl.scanEncrypted(b);
            var decrypted = cipherImpl.decrypt(encrypted, password, cipher, mode);
            return steganoFormatterImpl.scan(decrypted);
        };
    }

    protected void concealTestVector(
            @NonNull String carrier, 
            @NonNull String secret,
            @NonNull Function<DCommonFile, byte[]> confidentiality,
            @NonNull String testVector) {
        var carrierBmp = bmpFileIOImpl.read(getResource(carrier));
        var toHide = fileIOImpl.read(getResource(secret));

        var plainOrCiphertext = confidentiality.apply(toHide);
        steganoImpl.conceal(plainOrCiphertext, carrierBmp);

        var expected = bmpFileIOImpl.read(getResource(testVector));
        assertEquals(expected.getCommonFile(), carrierBmp.getCommonFile());
    }

    protected void revealTestVector(
            @NonNull String secret,
            @NonNull Function<byte[], DCommonFile> confidentiality,
            @NonNull String testVector) {
        var carrierBmp = bmpFileIOImpl.read(getResource(testVector));
        var formatted = steganoImpl.reveal(carrierBmp);
        var result = confidentiality.apply(formatted);

        var expected = fileIOImpl.read(getResource(secret));
        assertEquals(expected, result);
    }

    protected void concealAndReveal(
            @NonNull String carrier,
            @NonNull Function<DCommonFile, byte[]> toConfidentiality,
            @NonNull Function<byte[], DCommonFile> fromConfidentiality,
            @NonNull String secret) {
        var carrierBmp = bmpFileIOImpl.read(getResource(carrier));
        var toHide = fileIOImpl.read(getResource(secret));

        var plainOrCiphertext = toConfidentiality.apply(toHide);
        steganoImpl.conceal(plainOrCiphertext, carrierBmp);

        var formatted = steganoImpl.reveal(carrierBmp);
        var hidden = fromConfidentiality.apply(formatted);

        assertEquals(toHide, hidden);
    }

    protected void concealAndRevealEncrypted(
            @NonNull String carrier,
            @NonNull ECipher cipher, 
            @NonNull EMode mode,
            @NonNull String secret) {
        var password = "password";
        concealAndReveal(
            carrier, 
            toCiphertext(password, cipher, mode), 
            fromCiphertext(password, cipher, mode), 
            secret);
    }

    protected File getResource(String relativePath) {
        var absolutePath = Resources.getResource(relativePath).getPath();
        return new File(absolutePath);
    }
}
