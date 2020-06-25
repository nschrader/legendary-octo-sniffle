package legendary.octo.sniffle.crypto.internal;

import java.security.GeneralSecurityException;

/**
 * Simple abstraction of a JCA Factory 
 * @param <T> Typically a factory of {@code java.security} or {@code javax.crypto}
 */
@FunctionalInterface
public interface IJCAFactory<T> {
    T getInstance(String param) throws GeneralSecurityException;
}
