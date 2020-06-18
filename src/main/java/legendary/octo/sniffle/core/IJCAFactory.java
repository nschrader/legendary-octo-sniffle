package legendary.octo.sniffle.core;

import java.security.GeneralSecurityException;

/**
 * Simple abstraction of a JCA Factory 
 * @param <T> Typically a factory of {@link java.security}
 */
@FunctionalInterface
public interface IJCAFactory<T> {
    T getInstance(String param) throws GeneralSecurityException;
}
