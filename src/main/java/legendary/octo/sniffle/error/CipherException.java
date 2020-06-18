package legendary.octo.sniffle.error;

import java.security.GeneralSecurityException;

import lombok.NonNull;

/**
 * Wraps checked exceptions raised by the JCA
 */
public class CipherException extends RuntimeException {
    private static final long serialVersionUID = -3087110505439943813L;

    public CipherException(@NonNull GeneralSecurityException exception) {
        super(exception);
    }

}