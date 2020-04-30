package legendary.octo.sniffle.cli;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Key;

import legendary.octo.sniffle.core.EStegano;
import legendary.octo.sniffle.core.IStegano;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(onConstructor_ = @Inject)
public class SteganoResolver {
    private final @NonNull Injector injector;
    
    public IStegano getSteganoFor(@NonNull EStegano stegano) {
        var key = Key.get(IStegano.class, stegano.named());
        return injector.getInstance(key);
    }
}
