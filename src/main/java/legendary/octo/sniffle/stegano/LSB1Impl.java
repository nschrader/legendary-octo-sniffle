package legendary.octo.sniffle.stegano;

import static legendary.octo.sniffle.stegano.LSBUtils.*;

import legendary.octo.sniffle.core.IBmpFile;
import legendary.octo.sniffle.core.IStegano;
import lombok.NonNull;

public class LSB1Impl implements IStegano {

    @Override
    public void conceal(@NonNull byte[] in, @NonNull IBmpFile bitmap) {
        var imageData = bitmap.getImageDataView();
        putBytesToHide(1, in, imageData.capacity(), getSteadyImageDataGetter(imageData), imageData::put);
    }

    @Override
    public @NonNull byte[] reveal(@NonNull IBmpFile bitmap) {
        var imageData = bitmap.getImageDataView();
        return getHiddenBytes(1, imageData.capacity(), imageData::get);
    }
}
