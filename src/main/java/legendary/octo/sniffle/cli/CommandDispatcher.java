package legendary.octo.sniffle.cli;

import java.io.File;

import com.google.inject.Inject;

import legendary.octo.sniffle.core.DCommonFile;
import legendary.octo.sniffle.core.ECipher;
import legendary.octo.sniffle.core.EMode;
import legendary.octo.sniffle.core.EStegano;
import legendary.octo.sniffle.core.IBmpFileIO;
import legendary.octo.sniffle.core.ICipher;
import legendary.octo.sniffle.core.IFileIO;
import legendary.octo.sniffle.core.ISteganoFormatter;
import lombok.RequiredArgsConstructor;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

/**
 * CLI command skeleton using Picocli
 */
@Command
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class CommandDispatcher implements Runnable {
    private final SteganoResolver steganoResolver;
    private final ICipher cipherImpl;
    private final IFileIO fileIOImpl;
    private final IBmpFileIO BmpFileIOImpl;
    private final ISteganoFormatter steganoFormImpl;

    @Command(name = "-embed", showDefaultValues = true)
    void embed(@Option(names = "-help", usageHelp = true) Boolean help,
               @Option(names = "-in", required = true, paramLabel = "file") File inFile,
               @Option(names = {"-p", "-bitmap"}, required = true, paramLabel = "file") File bitmapFile,
               @Option(names = "-out", required = true, paramLabel = "file") File outFile,
               @Option(names = "-steg", required = true, paramLabel = "method", description = "${COMPLETION-CANDIDATES}") EStegano steganography,
               @Option(names = {"-a", "-cipher"}, defaultValue = "aes128", paramLabel = "cipher", description = "${COMPLETION-CANDIDATES}") ECipher cipher,
               @Option(names = {"-m", "-mode"}, defaultValue = "cbc", paramLabel = "mode", description = "${COMPLETION-CANDIDATES}") EMode mode,
               @Option(names = "-pass", paramLabel = "password") String password) {
        var steganoImpl = steganoResolver.getSteganoFor(steganography);
        var in = fileIOImpl.read(inFile);
        var formatted = steganoFormImpl.format(in);

        byte[] secret;
        if (password != null) {
            var ciphertext = cipherImpl.encrypt(formatted, password, cipher, mode);
            secret = steganoFormImpl.formatEncrypted(ciphertext);
        } else {
            secret = formatted;
        }
        
        var bitmap = BmpFileIOImpl.read(bitmapFile);
        steganoImpl.conceal(secret, bitmap);    
        BmpFileIOImpl.write(outFile, bitmap);
    }

    @Command(name = "-extract")
    void extract(@Option(names = "-help", usageHelp = true) Boolean help,
                 @Option(names = {"-p", "-bitmap"}, required = true, paramLabel = "file") File bitmapFile,
                 @Option(names = "-out", required = true, paramLabel = "file") File outFile,
                 @Option(names = "-steg", required = true, paramLabel = "method", description = "${COMPLETION-CANDIDATES}") EStegano steganography,
                 @Option(names = {"-a", "-cipher"}, defaultValue = "aes128", paramLabel = "cipher", description = "${COMPLETION-CANDIDATES}") ECipher cipher,
                 @Option(names = {"-m", "-mode"}, defaultValue = "cbc", paramLabel = "mode", description = "${COMPLETION-CANDIDATES}") EMode mode,
                 @Option(names = "-pass", paramLabel = "password") String password) {
        var steganoImpl = steganoResolver.getSteganoFor(steganography);
        var bitmap = BmpFileIOImpl.read(bitmapFile);
        var formatted = steganoImpl.reveal(bitmap);

        DCommonFile secret;
        if (password != null) {
            var ciphertext = steganoFormImpl.scanEncrypted(formatted);
            var plaintext = cipherImpl.decrypt(ciphertext, password, cipher, mode);
            secret = steganoFormImpl.scan(plaintext);
        } else {
            secret = steganoFormImpl.scan(formatted);
        }

        fileIOImpl.write(outFile, secret);
    }

    @Option(names = "-help", usageHelp = true, description = "Display help and exit")
    public Boolean help;

    public void run() { 
        CommandLine.usage(this, System.out);
    }
}
