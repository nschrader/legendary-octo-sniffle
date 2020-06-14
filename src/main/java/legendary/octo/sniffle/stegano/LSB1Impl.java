package legendary.octo.sniffle.stegano;

import static legendary.octo.sniffle.stegano.LSBnUtils.*;

import legendary.octo.sniffle.core.DCommonFile;
import legendary.octo.sniffle.core.IBmpFile;
import legendary.octo.sniffle.core.IStegano;
import lombok.NonNull;

public class LSB1Impl implements IStegano {

    @Override
    public void conceal(@NonNull DCommonFile in, @NonNull IBmpFile bitmap) {
        var extension = prepareFileExtension(in.getExtension());
        var toHide = prepareBytesToHide(in.getBytes(), extension);
        putBytesToHide(1, toHide, bitmap);
    }

    @Override
    public @NonNull DCommonFile reveal(@NonNull IBmpFile bitmap) {
        var imageData = bitmap.getImageDataView();
        var limit = getLimit(1, imageData);
        var bytes = getHiddenBytes(1, limit, imageData);
        var extension = getExtension(1, imageData);
        return new DCommonFile(extension, bytes);        
    }
}
