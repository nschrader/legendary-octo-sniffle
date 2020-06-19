package legendary.octo.sniffle.crypto;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.security.MessageDigest;

import com.google.common.io.BaseEncoding;

import org.junit.jupiter.api.Test;

import lombok.SneakyThrows;

public class OpenSSLPBKDFTest {

    /**
     * Passwords, salts, keys and IVs generated with
     * #! /bin/fish
     * set R (pwgen -y 7 1); echo $R; openssl enc -des-ofb -e -P -md sha1 -k $R
     * set R (pwgen -y 7 1); echo $R; openssl enc -des-ofb -e -P -md sha1 -k $R -nosalt
     */

    @Test
    @SneakyThrows
    public void testLadoLSB4aes256ofbVector() {
        var md = MessageDigest.getInstance("SHA-256");
        var pbkdf = new OpenSSLPBKDF().of("AES", md, "secreto", null, 32, 16);

        var expectedKey = BaseEncoding.base16().decode("DF733656293A19C54F69093BA916F0A1A2A3C151FC95C13F3A794C2631EEB3A6");
        assertArrayEquals(expectedKey, pbkdf.deriveSecretKey().getEncoded());

        var expectedIv = BaseEncoding.base16().decode("DED43A2C63978A792FB7151ACD66FF67");
        assertArrayEquals(expectedIv, pbkdf.deriveIV().getIV());
    }

    @Test
    @SneakyThrows
    public void testAes256OfbVector() {
        var md = MessageDigest.getInstance("SHA-256");
        var salt = BaseEncoding.base16().decode("B147AE529FAFA4C4");
        var pbkdf = new OpenSSLPBKDF().of("AES", md, "op$ui2I", salt, 32, 16);

        var expectedKey = BaseEncoding.base16().decode("84465629551C0CAC548EF14969BFD5A15AB16C56C73A1D2942477E7AB940DBE1");
        assertArrayEquals(expectedKey, pbkdf.deriveSecretKey().getEncoded());

        var expectedIv = BaseEncoding.base16().decode("475528D41E6C9C7C053CFB0DEA8D21AF");
        assertArrayEquals(expectedIv, pbkdf.deriveIV().getIV());
    }

    @Test
    @SneakyThrows
    public void testAes192CbcVector() {
        var md = MessageDigest.getInstance("MD5");
        var salt = BaseEncoding.base16().decode("D148E4529EFCF646");
        var pbkdf = new OpenSSLPBKDF().of("AES", md, "Ush'u4G", salt, 24, 16);

        var expectedKey = BaseEncoding.base16().decode("968074FE613833016A476C4212E154D9B11788D1A72641F5");
        assertArrayEquals(expectedKey, pbkdf.deriveSecretKey().getEncoded());

        var expectedIv = BaseEncoding.base16().decode("0512CC337DA233538CFDE3FB719449D5");
        assertArrayEquals(expectedIv, pbkdf.deriveIV().getIV());
    }

    @Test
    @SneakyThrows
    public void testAes128cfb8Vector() {
        var md = MessageDigest.getInstance("SHA-1");
        var salt = BaseEncoding.base16().decode("12A3A6E4C8495F2B");
        var pbkdf = new OpenSSLPBKDF().of("AES", md, "wi|v2Fa", salt, 16, 16);

        var expectedKey = BaseEncoding.base16().decode("6FF41A185519AACFCA54D38993C6AE0A");
        assertArrayEquals(expectedKey, pbkdf.deriveSecretKey().getEncoded());

        var expectedIv = BaseEncoding.base16().decode("6EE77B594430655C692B9FBCD07E057D");
        assertArrayEquals(expectedIv, pbkdf.deriveIV().getIV());
    }

    @Test
    @SneakyThrows
    public void testDesOfbVector() {
        var md = MessageDigest.getInstance("SHA-1");
        var salt = BaseEncoding.base16().decode("1E9F9D8AEB0AED54");
        var pbkdf = new OpenSSLPBKDF().of("DES", md, "Eo1aa'z", salt, 8, 8);

        var expectedKey = BaseEncoding.base16().decode("E50BB77EC3A5BD00");
        assertArrayEquals(expectedKey, pbkdf.deriveSecretKey().getEncoded());

        var expectedIv = BaseEncoding.base16().decode("19241CDC5ED6AB0D");
        assertArrayEquals(expectedIv, pbkdf.deriveIV().getIV());
    }

    @Test
    @SneakyThrows
    public void testAes256EcbVector() {
        var md = MessageDigest.getInstance("SHA-256");
        var pbkdf = new OpenSSLPBKDF().of("DES", md, "ȩáÈçµöüä", null, 32, 0);

        var expectedKey = BaseEncoding.base16().decode("2AD00ADDB4D3B08AAD0AA0E06AA607C9F87B150EF643CC0A57B6F2714884A394");
        assertArrayEquals(expectedKey, pbkdf.deriveSecretKey().getEncoded());
        assertNull(pbkdf.deriveIV());
    }
}
