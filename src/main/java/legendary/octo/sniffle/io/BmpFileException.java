package legendary.octo.sniffle.io;

import org.jetbrains.annotations.NotNull;

public class BmpFileException extends RuntimeException {
    private static final long serialVersionUID = 8393273887831143841L;

    public BmpFileException(@NotNull Exception exception) {
        super(exception);
    }

    public BmpFileException(@NotNull String format, @NotNull Object... args) {
        super(String.format(format, args));
    }
}
