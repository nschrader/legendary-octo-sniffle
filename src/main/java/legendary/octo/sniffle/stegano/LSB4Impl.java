package legendary.octo.sniffle.stegano;

import static legendary.octo.sniffle.stegano.LSBnUtils.*;

import legendary.octo.sniffle.core.IBmpFile;
import legendary.octo.sniffle.core.IStegano;
import lombok.NonNull;

public class LSB4Impl implements IStegano {

    @Override
    public void conceal(@NonNull byte[] in, @NonNull IBmpFile bitmap) {
        //TODO: Get filename from somewhere
        var extension = prepareFileExtension("file.png");
        var toHide = prepareBytesToHide(in, extension);
        putBytesToHide(4, toHide, bitmap);
    }

    @Override
    public @NonNull byte[] reveal(@NonNull IBmpFile bitmap) {
        var imageData = bitmap.getImageDataView();
        var limit = getLimit(4, imageData);
        var bytes = getHiddenBytes(4, limit, imageData);

        //TODO: Do something with extension
        var extension = getExtension(4, imageData);

        return bytes;
    }
}
