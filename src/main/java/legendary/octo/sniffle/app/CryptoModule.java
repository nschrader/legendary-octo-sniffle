package legendary.octo.sniffle.app;

import java.security.MessageDigest;

import javax.crypto.Cipher;

import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;

import legendary.octo.sniffle.core.IJCAFactory;
import legendary.octo.sniffle.core.IOpenSSLPBKDF;
import legendary.octo.sniffle.crypto.OpenSSLPBKDF;

/**
 * Configure Guice dependency injection as an abstraction for 
 * JCA Factories and internal crypto implementations.
 */
public class CryptoModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(new TypeLiteral<IJCAFactory<Cipher>>() {}).toInstance(Cipher::getInstance);
        bind(new TypeLiteral<IJCAFactory<MessageDigest>>() {}).toInstance(MessageDigest::getInstance);
        bind(IOpenSSLPBKDF.class).to(OpenSSLPBKDF.class);
    }
}
