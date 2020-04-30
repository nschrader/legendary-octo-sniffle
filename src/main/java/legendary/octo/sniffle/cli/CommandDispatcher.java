package legendary.octo.sniffle.cli;

import java.io.File;

import com.google.inject.Inject;

import legendary.octo.sniffle.core.ECipher;
import legendary.octo.sniffle.core.EMode;
import legendary.octo.sniffle.core.EStegano;
import legendary.octo.sniffle.core.IBmpFileIO;
import legendary.octo.sniffle.core.ICipher;
import legendary.octo.sniffle.core.IFileIO;
import lombok.RequiredArgsConstructor;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class CommandDispatcher {
    private final SteganoResolver steganoResolver;
    private final ICipher cipherImpl;
    private final IFileIO fileIOImpl;
    private final IBmpFileIO BmpFileIOImpl;

    @Command(name = "-embed")
    void embed(@Option(names = "-in", required = true) File inFile,
               @Option(names = "-p", required = true) File bitmapFile,
               @Option(names = "-out", required = true) File outFile,
               @Option(names = "-steg", required = true) EStegano steganography,
               @Option(names = "-a") ECipher cipher,
               @Option(names = "-m") EMode mode,
               @Option(names = "-pass") String password) {
        var steganoImpl = steganoResolver.getSteganoFor(steganography);
        var in = fileIOImpl.read(inFile);
        var bitmap = BmpFileIOImpl.read(bitmapFile);
        cipherImpl.encrypt(in, "password", ECipher.aes128, EMode.cbc); //TODO: Default?
        steganoImpl.conceal(in, bitmap);    
        BmpFileIOImpl.write(outFile, bitmap);
    }

    @Command(name = "-extract")
    void extract(@Option(names = "-p", required = true) File bitmapFile,
                 @Option(names = "-out", required = true) File outFile,
                 @Option(names = "-steg", required = true) EStegano steganography,
                 @Option(names = "-a") ECipher cipher,
                 @Option(names = "-m") EMode mode,
                 @Option(names = "-pass") String password) {
        var steganoImpl = steganoResolver.getSteganoFor(steganography);
        var bitmap = BmpFileIOImpl.read(bitmapFile);
        var out = steganoImpl.reveal(bitmap);
        cipherImpl.decrypt(out, "password", ECipher.aes128, EMode.cbc); //TODO: Default?
        fileIOImpl.write(outFile, out);
    }
}
