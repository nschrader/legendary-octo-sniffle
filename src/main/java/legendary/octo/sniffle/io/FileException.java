package legendary.octo.sniffle.io;

import java.io.IOException;

import lombok.NonNull;

/**
 * Basically an unchecked IO exception
 */
public class FileException extends RuntimeException {
    private static final long serialVersionUID = 8320187812296753533L;

    public FileException(@NonNull IOException exception) {
        super(exception);
    }

    public FileException(@NonNull String format, @NonNull Object... args) {
        super(String.format(format, args));
    }
}
