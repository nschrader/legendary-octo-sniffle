package legendary.octo.sniffle.io;

import lombok.NonNull;

enum BmpFileFieldSize {
    _1(1), _2(2), _4(4);

    private @NonNull Integer val;

    BmpFileFieldSize(@NonNull Integer val) {
        this.val = val;
    }

    public @NonNull Integer val() {
        return val;
    }
}
