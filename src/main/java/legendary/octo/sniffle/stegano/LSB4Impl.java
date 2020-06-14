package legendary.octo.sniffle.stegano;

import static legendary.octo.sniffle.stegano.LSBnUtils.*;

import legendary.octo.sniffle.core.DCommonFile;
import legendary.octo.sniffle.core.IBmpFile;
import legendary.octo.sniffle.core.IStegano;
import lombok.NonNull;

public class LSB4Impl implements IStegano {

    @Override
    public void conceal(@NonNull DCommonFile in, @NonNull IBmpFile bitmap) {
        var extension = prepareFileExtension(in.getExtension());
        var toHide = prepareBytesToHide(in.getBytes(), extension);
        putBytesToHide(4, toHide, bitmap);
    }

    @Override
    public @NonNull DCommonFile reveal(@NonNull IBmpFile bitmap) {
        var imageData = bitmap.getImageDataView();
        var limit = getLimit(4, imageData);
        var bytes = getHiddenBytes(4, limit, imageData);
        var extension = getExtension(4, imageData);

        return new DCommonFile(extension, bytes);
    }
}
