package legendary.octo.sniffle.core;

import com.google.inject.name.Named;
import com.google.inject.name.Names;

import lombok.NonNull;

/**
 * Available steganography methods
 */
public enum EStegano {
    LSB1, LSB4, LSBI;

    /**
     * Helper to retrieve an annotated Guice binding
     */
    public @NonNull Named named() {
        return Names.named(this.name());
    }
}
