package legendary.octo.sniffle.io;

import static legendary.octo.sniffle.io.BmpFileField.*;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import org.jetbrains.annotations.NotNull;

public class BmpFile {
    public static final @NotNull Integer HEADER_BYTES = 14;

    public final @NotNull Integer fileSize;
    public final @NotNull Integer imageOffset;
    public final @NotNull Integer dibSize;
    public final @NotNull Integer width;
    public final @NotNull Integer height;
    public final @NotNull Integer horizontalResolution;
    public final @NotNull Integer verticalResolution;

    protected final @NotNull ByteBuffer byteBuffer;

    protected BmpFile(@NotNull byte[] raw) {
        byteBuffer = ByteBuffer.wrap(raw);
        byteBuffer.order(ByteOrder.LITTLE_ENDIAN);

        try {
            expect(MAGIC_NUMBER_B);
            expect(MAGIC_NUMBER_M);

            fileSize = accept(FILE_SIZE);
            imageOffset = accept(IMAGE_OFFSET);
            dibSize = accept(DIB_SIZE);
            width = accept(IMAGE_WIDTH);
            height = accept(IMAGE_HEIGHT);

            expect(COLOR_PLANES);
            expect(COLOR_DEPTH);
            expect(COMPRESSION);

            horizontalResolution = accept(HORIZONTAL_RES);
            verticalResolution = accept(VERTICAL_RES);
        } catch (IndexOutOfBoundsException e) {
            throw new BmpFileException(e); 
        }

        validate(fileSize, raw.length, 
            "File size (%d bytes) does not correspond to what the BMP header (%d bytes) indicates",
            raw.length, fileSize);

        var headers = HEADER_BYTES + dibSize;
        validate(headers, imageOffset, 
            "Inconsistent header sizes (%d and %d bytes)",
            headers, imageOffset);

        var expectedRgbBytes = width*height*3;
        var actualRgbBytes = fileSize - imageOffset;
        validate(expectedRgbBytes, actualRgbBytes,
            "Image data looks incomplete, should be %d bytes, but is %d bytes",
            expectedRgbBytes, actualRgbBytes);
    }

    private void expect(@NotNull BmpFileField field) {
        validate(field.value, accept(field), "Expected value 0x%02X at for field %s (at offset 0x%02X)", field.value, field, field.offset());
    }

    private void validate(@NotNull Integer expected, @NotNull Integer actual, @NotNull String format, Object... args) {
        if (!expected.equals(actual)) {
            throw new BmpFileException(format, args);
        }
    }

    private @NotNull Integer accept(@NotNull BmpFileField field) {
        return switch (field.size) {
            case _1 -> (int) byteBuffer.get(field.offset());
            case _2 -> (int) byteBuffer.getShort(field.offset());
            case _4 -> (int) byteBuffer.getInt(field.offset());
        };
    }

    public byte getImageData(@NotNull Integer index) {
        return byteBuffer.get(imageOffset + index);
    }
}
