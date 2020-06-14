package legendary.octo.sniffle.core;

import java.nio.ByteBuffer;

import lombok.NonNull;

/**
 * Represents a BMP file, gives you access to the header metadata and image
 * data.
 */
public interface IBmpFile {
    @NonNull Integer getFileSize();
    @NonNull Integer getImageOffset();
    @NonNull Integer getDibSize();
    @NonNull Integer getWidth();
    @NonNull Integer getHeight();
    @NonNull Integer getHorizontalResolution();
    @NonNull Integer getVerticalResolution();
    @NonNull DCommonFile getCommonFile();

    /**
     * Get zero indexed image data
     * @throws IndexOutOfBoundsException
     */
    @NonNull byte getImageData(@NonNull Integer index);

    /**
     * Put zero indexed image data
     * @throws IndexOutOfBoundsException
     */
    void putImageData(@NonNull Integer index, @NonNull Byte data);

    /**
     * @return A new ByteBuffer of the image data
     */
    @NonNull ByteBuffer getImageDataView();
}
