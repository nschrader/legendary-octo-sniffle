package legendary.octo.sniffle.io;

import org.jetbrains.annotations.NotNull;

enum BmpFileFieldSize {
    _1(1), _2(2), _4(4);

    private @NotNull Integer val;

    BmpFileFieldSize(@NotNull Integer val) {
        this.val = val;
    }

    public @NotNull Integer val() {
        return val;
    }
}
