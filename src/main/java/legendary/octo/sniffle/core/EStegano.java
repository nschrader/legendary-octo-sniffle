package legendary.octo.sniffle.core;

import com.google.inject.name.Named;
import com.google.inject.name.Names;

import lombok.NonNull;

public enum EStegano {
    LSB1, LSB2, LSBI;

    public @NonNull Named named() {
        return Names.named(this.name());
    }
}
