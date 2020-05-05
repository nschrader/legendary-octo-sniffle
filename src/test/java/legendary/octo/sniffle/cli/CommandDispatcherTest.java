package legendary.octo.sniffle.cli;

import org.junit.jupiter.api.Test;

public class CommandDispatcherTest extends ACommandDispatcherBaseTest {

    @Test
    public void testHelp() {
        executeCommandLineAndAssert(SUCCESS, "-help");
    }

    @Test
    public void testNotExisting() {
        executeCommandLineAndAssert(USAGE_FAILURE, "-x");
    }
}
