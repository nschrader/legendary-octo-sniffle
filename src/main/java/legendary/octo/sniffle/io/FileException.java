package legendary.octo.sniffle.io;

import java.io.IOException;

import org.jetbrains.annotations.NotNull;

/**
 * Basically an unchecked IO exception
 */
public class FileException extends RuntimeException {
    private static final long serialVersionUID = 8320187812296753533L;

    public FileException(@NotNull IOException exception) {
        super(exception);
    }

    public FileException(@NotNull String format, @NotNull Object... args) {
        super(String.format(format, args));
    }
}
