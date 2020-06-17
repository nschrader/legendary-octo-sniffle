package legendary.octo.sniffle.error;

import lombok.NonNull;

/**
 * Problems linked to steganography methods
 */
public class SteganoException extends RuntimeException {
    private static final long serialVersionUID = -6862349064474020632L;

    public SteganoException(@NonNull Exception exception, @NonNull String format, @NonNull Object... args) {
        super(String.format(format, args), exception);
    }

    public SteganoException(@NonNull String format, @NonNull Object... args) {
        super(String.format(format, args));
    }
}