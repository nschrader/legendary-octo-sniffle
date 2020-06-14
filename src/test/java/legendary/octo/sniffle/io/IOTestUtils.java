package legendary.octo.sniffle.io;

import java.io.File;

import com.google.common.io.Files;
import com.google.common.io.Resources;

import legendary.octo.sniffle.core.DCommonFile;
import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;

@UtilityClass
class IOTestUtils {

    @SneakyThrows
    @NonNull DCommonFile getCommonFile(@NonNull String path) {
        var file = new File(Resources.getResource(path).getPath());
        return getCommonFile(file);
    } 

    @SneakyThrows
    @NonNull DCommonFile getCommonFile(@NonNull File file) {
        var bytes = Files.toByteArray(file);
        var extension = Files.getFileExtension(file.getPath());
        return new DCommonFile(extension, bytes);
    } 
}
