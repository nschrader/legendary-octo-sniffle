package legendary.octo.sniffle.core;

import lombok.Data;
import lombok.NonNull;

@Data
public class DCommonFile {
    private final @NonNull String extension;
    private final @NonNull byte[] bytes;
}
