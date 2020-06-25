package legendary.octo.sniffle.core;

import legendary.octo.sniffle.error.SteganoException;
import lombok.NonNull;

public interface IStegano {
    /**
     * Conceal {@code in} into {@code bitmap} in-place
     * @throws SteganoException if {@code in} is too big for {@code bitmap}
     */
    void conceal(@NonNull byte[] in, @NonNull IBmpFile bitmap);

    /**
     * Reveal what was conceal in {@code bitmap}
     * @throws SteganoException if no concealed data could be detected
     */
    @NonNull byte[] reveal(@NonNull IBmpFile bitmap);
}
