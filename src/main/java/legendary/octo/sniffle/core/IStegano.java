package legendary.octo.sniffle.core;

import lombok.NonNull;

public interface IStegano {
    /**
     * Conceal {@code in} into {@code bitmap}, at once and in-place
     * @throws SteganoException if {@code in} is too big for {@code bitmap}
     */
    void conceal(@NonNull DCommonFile in, @NonNull IBmpFile bitmap);

    /**
     * Reveal what was conceal in {@code bitmap}, at once and in-place
     * @throws SteganoException if no concealed data could be detected
     */
    @NonNull DCommonFile reveal(@NonNull IBmpFile bitmap);
}
