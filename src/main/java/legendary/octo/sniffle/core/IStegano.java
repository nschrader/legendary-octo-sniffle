package legendary.octo.sniffle.core;

import lombok.NonNull;

public interface IStegano {
    /**
     * Conceal "in" into "bitmap", at once and in-place
     * @throws SteganoException if "in" is too big for "bitmap"
     */
    public void conceal(@NonNull byte[] in, @NonNull IBmpFile bitmap);

    /**
     * Reveal what was conceal in "bitmap", at once and in-place
     * @throws SteganoException if no concealed data could be detected
     */
    public @NonNull byte[] reveal(@NonNull IBmpFile bitmap);
}
