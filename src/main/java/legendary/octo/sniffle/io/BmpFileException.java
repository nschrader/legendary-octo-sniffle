package legendary.octo.sniffle.io;

import lombok.NonNull;

/**
 * Problem linked to BMP file format
 */
public class BmpFileException extends RuntimeException {
    private static final long serialVersionUID = 8393273887831143841L;

    public BmpFileException(@NonNull IndexOutOfBoundsException exception) {
        super(exception);
    }

    public BmpFileException(@NonNull String format, @NonNull Object... args) {
        super(String.format(format, args));
    }
}
