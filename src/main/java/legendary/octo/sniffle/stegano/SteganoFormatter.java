package legendary.octo.sniffle.stegano;

import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import legendary.octo.sniffle.core.DCommonFile;
import legendary.octo.sniffle.core.ISteganoFormatter;
import legendary.octo.sniffle.error.SteganoException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;

public class SteganoFormatter implements ISteganoFormatter {

    @Override
    public @NonNull byte[] format(@NonNull DCommonFile content) {
        var extension = prepareFileExtension(content.getExtension());
         var bytes = prepareBytesToHide(content.getBytes(), extension);
        return bytes.array();
    }

    @Override
    public @NonNull byte[] formatEncrypted(@NonNull byte[] encryptedContent) {
         var bytes = prepareBytesToHide(encryptedContent, new byte[0]);
        return bytes.array();
    }

    private @NonNull byte[] prepareFileExtension(@NonNull String rawExtension) {
        if (rawExtension.isEmpty()) {
            throw new SteganoException("No file extension to embed, please use a file that has one");
        }
        return ('.' + rawExtension + '\0').getBytes();
    }

    private @NonNull ByteBuffer prepareBytesToHide(@NonNull byte[] data, @NonNull byte[] extension) {
        var lengthToHide = Integer.BYTES + data.length + extension.length;
        var toHide = ByteBuffer.allocate(lengthToHide).order(ByteOrder.BIG_ENDIAN);

        toHide.putInt(data.length);
        toHide.put(data);
        toHide.put(extension);

        return toHide;
    }

    @Override
    public @NonNull DCommonFile scan(@NonNull byte[] data) {
        var results = getDataAndExtension(data, true); 
        return new DCommonFile(results.getRight(), results.getLeft());
    }

    @Override
    public @NonNull byte[] scanEncrypted(@NonNull byte[] encryptedData) {
        return getDataAndExtension(encryptedData, false).getLeft();
    }

    // Oh, Lord...
    @Data
    @AllArgsConstructor(staticName = "of")
    private static class Pair<L, R> {
        private final L left;
        private final R right;
    }

    private @NonNull Pair<@NonNull byte[], String> getDataAndExtension(
            @NonNull byte[] encryptedData, 
            @NonNull Boolean hasExtension) {
        var byteBuffer = ByteBuffer.wrap(encryptedData).order(ByteOrder.BIG_ENDIAN);
        var len = getLength(byteBuffer);
        var unformattedData = getData(len, byteBuffer);

        if (hasExtension) {
            return Pair.of(unformattedData, getExtension(byteBuffer));
        } else {
            return Pair.of(unformattedData, null);
        }
        
    }

    private int getLength(@NonNull ByteBuffer byteBuffer) {
        try {
           var length = byteBuffer.getInt();

           if (length <= 0) {
               throw new SteganoException("Malformed stegano header: Size field negative or zero");
           }

           return length;
        } catch (BufferUnderflowException e) {
            throw new SteganoException(e, "Bitmap too short to hide any data");
        }
    }

    private @NonNull byte[] getData(int length, @NonNull ByteBuffer byteBuffer) {
        var data = new byte[length];

        try {
            byteBuffer.get(data, 0, length);
         } catch (BufferUnderflowException e) {
             throw new SteganoException(e, "Malformed stegano header: Size field indicates more bytes (%d) than there are (%d)", length, byteBuffer.capacity());
         }

         return data;
    }

    private @NonNull String getExtension(@NonNull ByteBuffer byteBuffer) {
        var sb = new StringBuilder();

        if (byteBuffer.get() != 0x2E) {
            throw new SteganoException("Hidden data does not contain any extension information");
        }
            
        while (true) {
            var b = safeGetExtensionChar(byteBuffer);
            if (b == 0x00) {
                break;
            }
            sb.append(b);
        }

        return sb.toString();
    }

    private char safeGetExtensionChar(@NonNull ByteBuffer byteBuffer) {
        try {
            return (char) byteBuffer.get();
        } catch (BufferUnderflowException e) {
            throw new SteganoException(e, "Malformed extension footer");
        }
    }
}
