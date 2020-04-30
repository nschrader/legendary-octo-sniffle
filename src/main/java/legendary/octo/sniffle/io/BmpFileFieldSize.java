package legendary.octo.sniffle.io;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
enum BmpFileFieldSize {
    _1(1), _2(2), _4(4);

    @Getter
    private final @NonNull Integer value;
}
