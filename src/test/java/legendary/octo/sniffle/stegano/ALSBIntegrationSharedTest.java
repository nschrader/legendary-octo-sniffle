package legendary.octo.sniffle.stegano;

import org.junit.jupiter.api.Test;

import legendary.octo.sniffle.core.ECipher;
import legendary.octo.sniffle.core.EMode;

public abstract class ALSBIntegrationSharedTest extends ALSBIntegrationBaseTest {

    @Test
    public void invertible() {
        concealAndReveal("lado.bmp", toPlaintext(), fromPlaintext(), "itba.png");
    }
    
    @Test
    public void invertibleAes128Cbc() {
        concealAndRevealEncrypted("lado.bmp", ECipher.aes128, EMode.cbc, "itba.png");
    }   

    @Test
    public void invertibleAes192Cbc() {
        concealAndRevealEncrypted("lado.bmp", ECipher.aes192, EMode.cbc, "itba.png");
    }

    @Test
    public void invertibleAes256Cbc() {
        concealAndRevealEncrypted("lado.bmp", ECipher.aes256, EMode.cbc, "itba.png");
    }  
    
    @Test
    public void invertibleDesCbc() {
        concealAndRevealEncrypted("lado.bmp", ECipher.des, EMode.cbc, "itba.png");
    }   

    @Test
    public void invertibleAes128Cfb() {
        concealAndRevealEncrypted("lado.bmp", ECipher.aes128, EMode.cfb, "itba.png");
    }   

    @Test
    public void invertibleAes192Cfb() {
        concealAndRevealEncrypted("lado.bmp", ECipher.aes192, EMode.cfb, "itba.png");
    }   

    @Test
    public void invertibleAes256Cfb() {
        concealAndRevealEncrypted("lado.bmp", ECipher.aes256, EMode.cfb, "itba.png");
    }   

    @Test
    public void invertibleDesCfb() {
        concealAndRevealEncrypted("lado.bmp", ECipher.des, EMode.cfb, "itba.png");
    }   

    @Test
    public void invertibleAes128Ecb() {
        concealAndRevealEncrypted("lado.bmp", ECipher.aes128, EMode.ecb, "itba.png");
    }

    @Test
    public void invertibleAes192Ecb() {
        concealAndRevealEncrypted("lado.bmp", ECipher.aes192, EMode.ecb, "itba.png");
    }

    @Test
    public void invertibleAes256Ecb() {
        concealAndRevealEncrypted("lado.bmp", ECipher.aes256, EMode.ecb, "itba.png");
    }

    @Test
    public void invertibleDesEcb() {
        concealAndRevealEncrypted("lado.bmp", ECipher.des, EMode.ecb, "itba.png");
    }

    @Test
    public void invertibleAes128Ofb() {
        concealAndRevealEncrypted("lado.bmp", ECipher.aes128, EMode.ofb, "itba.png");
    }

    @Test
    public void invertibleAes192Ofb() {
        concealAndRevealEncrypted("lado.bmp", ECipher.aes192, EMode.ofb, "itba.png");
    }

    @Test
    public void invertibleAes256Ofb() {
        concealAndRevealEncrypted("lado.bmp", ECipher.aes256, EMode.ofb, "itba.png");
    }

    @Test
    public void invertibleDesOfb() {
        concealAndRevealEncrypted("lado.bmp", ECipher.des, EMode.ofb, "itba.png");
    }    
}
