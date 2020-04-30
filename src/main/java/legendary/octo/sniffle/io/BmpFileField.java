package legendary.octo.sniffle.io;

import java.util.Arrays;

import lombok.Getter;
import lombok.NonNull;

import static legendary.octo.sniffle.io.BmpFileFieldSize.*;

enum BmpFileField {
    // 1. Bitmap File Header
    MAGIC_NUMBER_B(_1, 0x42),
    MAGIC_NUMBER_M(_1, 0x4D),
    FILE_SIZE(_4),
    RSVD1(_2),
    RSVD2(_2),
    IMAGE_OFFSET(_4),

    // 2. DIB BITMAPINFOHEADER
    DIB_SIZE(_4),
    IMAGE_WIDTH(_4),
    IMAGE_HEIGHT(_4),
    COLOR_PLANES(_2, 1),
    COLOR_DEPTH(_2, 24), // 24 bits = 1 byte per RGB color
    COMPRESSION(_4, 0), // uncompressed
    RSVD3(_4),
    HORIZONTAL_RES(_4), // px/m
    VERTICAL_RES(_4), // px/m
    COLORS(_4), // 2^n
    RSVD4(_4);

    // 3. Color table
    // Not implemented

    // 4. Image data

    public final @NonNull BmpFileFieldSize size;
    public final Integer value;

    @Getter(lazy = true)
    private final @NonNull Integer offset = offset();

    BmpFileField(@NonNull BmpFileFieldSize size, Integer value) {
        this.size = size;
        this.value = value;
    }

    BmpFileField(@NonNull BmpFileFieldSize size) {
        this(size, null);
    }

    private @NonNull Integer offset() {
        return Arrays.stream(values())
            .limit(ordinal())
            .map((f) -> f.size.getValue())
            .reduce(0, Integer::sum);
    }
}
