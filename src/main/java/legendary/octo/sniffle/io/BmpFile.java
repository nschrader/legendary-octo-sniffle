package legendary.octo.sniffle.io;

import static legendary.octo.sniffle.io.BmpFileField.*;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import lombok.NonNull;

public class BmpFile {
    public static final @NonNull Integer HEADER_BYTES = 14;

    public final @NonNull Integer fileSize;
    public final @NonNull Integer imageOffset;
    public final @NonNull Integer dibSize;
    public final @NonNull Integer width;
    public final @NonNull Integer height;
    public final @NonNull Integer horizontalResolution;
    public final @NonNull Integer verticalResolution;

    protected final @NonNull ByteBuffer byteBuffer;

    /**
     * Represents a BMP file and makes sure it is valid.
     * Gives you access to the header metadata and image data.
     * @throws BmpFileException
     */
    protected BmpFile(@NonNull byte[] raw) {
        byteBuffer = ByteBuffer.wrap(raw);
        byteBuffer.order(ByteOrder.LITTLE_ENDIAN);

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

    private void expect(@NonNull BmpFileField field) {
        validate(field.value, accept(field), "Expected value 0x%02X at for field %s (at offset 0x%02X)", field.value, field, field.getOffset());
    }

    private void validate(@NonNull Integer expected, @NonNull Integer actual, @NonNull String format, Object... args) {
        if (!expected.equals(actual)) {
            throw new BmpFileException(format, args);
        }
    }

    private @NonNull Integer accept(@NonNull BmpFileField field) {
        try {
            return switch (field.size) {
                case _1 -> (int) byteBuffer.get(field.getOffset());
                case _2 -> (int) byteBuffer.getShort(field.getOffset());
                case _4 -> (int) byteBuffer.getInt(field.getOffset());
            };
        } catch (IndexOutOfBoundsException e) {
            throw new BmpFileException(e); 
        }
    }

    /**
     * Get zero indexed image data
     * @throws IndexOutOfBoundsException
     */
    public byte getImageData(@NonNull Integer index) {
        return byteBuffer.get(imageOffset + index);
    }
}
