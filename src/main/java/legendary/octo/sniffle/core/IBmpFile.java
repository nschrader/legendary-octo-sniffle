package legendary.octo.sniffle.core;

import lombok.NonNull;

/**
 * Represents a BMP file, gives you access to the header metadata and image
 * data.
 */
public interface IBmpFile {
    public @NonNull Integer getFileSize();
    public @NonNull Integer getImageOffset();
    public @NonNull Integer getDibSize();
    public @NonNull Integer getWidth();
    public @NonNull Integer getHeight();
    public @NonNull Integer getHorizontalResolution();
    public @NonNull Integer getVerticalResolution();
    public @NonNull byte[] getBytes();

    /**
     * Get zero indexed image data
     * @throws IndexOutOfBoundsException
     */
    public @NonNull byte getImageData(@NonNull Integer index);
}
