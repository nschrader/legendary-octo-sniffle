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
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

/**
 * CLI command skeleton using Picocli
 */
@Command(mixinStandardHelpOptions = true, version = "0.9")
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class CommandDispatcher implements Runnable {
    private final SteganoResolver steganoResolver;
    private final ICipher cipherImpl;
    private final IFileIO fileIOImpl;
    private final IBmpFileIO BmpFileIOImpl;

    @Command(name = "-embed", showDefaultValues = true)
    void embed(@Option(names = "-help", usageHelp = true) boolean help,
               @Option(names = "-in", required = true, paramLabel = "file") File inFile,
               @Option(names = {"-p", "-bitmap"}, required = true, paramLabel = "file") File bitmapFile,
               @Option(names = "-out", required = true, paramLabel = "file") File outFile,
               @Option(names = "-steg", required = true, paramLabel = "method", description = "${COMPLETION-CANDIDATES}") EStegano steganography,
               @Option(names = {"-a", "-cipher"}, defaultValue = "aes128", paramLabel = "cipher", description = "${COMPLETION-CANDIDATES}") ECipher cipher,
               @Option(names = {"-m", "-mode"}, defaultValue = "cbc", paramLabel = "mode", description = "${COMPLETION-CANDIDATES}") EMode mode,
               @Option(names = "-pass", paramLabel = "password") String password) {
        var steganoImpl = steganoResolver.getSteganoFor(steganography);
        var in = fileIOImpl.read(inFile);
        var bitmap = BmpFileIOImpl.read(bitmapFile);

        if (password != null) {
            cipherImpl.encrypt(in, password, cipher, mode);
        }
        
        steganoImpl.conceal(in, bitmap);    
        BmpFileIOImpl.write(outFile, bitmap);
    }

    @Command(name = "-extract")
    void extract(@Option(names = "-help", usageHelp = true) boolean help,
                 @Option(names = {"-p", "-bitmap"}, required = true, paramLabel = "file") File bitmapFile,
                 @Option(names = "-out", required = true, paramLabel = "file") File outFile,
                 @Option(names = "-steg", required = true, paramLabel = "method", description = "${COMPLETION-CANDIDATES}") EStegano steganography,
                 @Option(names = {"-a", "-cipher"}, defaultValue = "aes128", paramLabel = "cipher", description = "${COMPLETION-CANDIDATES}") ECipher cipher,
                 @Option(names = {"-m", "-mode"}, defaultValue = "cbc", paramLabel = "mode", description = "${COMPLETION-CANDIDATES}") EMode mode,
                 @Option(names = "-pass", paramLabel = "password") String password) {
        var steganoImpl = steganoResolver.getSteganoFor(steganography);
        var bitmap = BmpFileIOImpl.read(bitmapFile);
        var out = steganoImpl.reveal(bitmap);

        if (password != null) {
            cipherImpl.decrypt(out, password, cipher, mode);
        }

        fileIOImpl.write(outFile, out);
    }

    public void run() { 
        CommandLine.usage(this, System.out);
    }
}
