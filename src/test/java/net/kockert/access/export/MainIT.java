package net.kockert.access.export;

import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static org.junit.Assume.assumeTrue;

public class MainIT {

    @Test(expected = SystemExitRuntimeException.class)
    public void shouldExitIfParametersAreInsufficient() {
        Main main = new Main(new TestSystemExitHandler());
        main.run();
    }

    @Test(expected = SystemExitRuntimeException.class)
    public void shouldExitIfHelpWasRequest() {
        Main main = new Main(new TestSystemExitHandler());
        main.run("-h");
    }

    @Test(expected = SystemExitRuntimeException.class)
    public void shouldExitIfSourceFileDoesNotExist() {
        Main main = new Main(new TestSystemExitHandler());
        main.run("aSourceFileThatDoesNotExist", "targetFile");
    }

    @Test(expected = SystemExitRuntimeException.class)
    public void shouldExitIfTargetFileDoesAlreadyExist() {
        Main main = new Main(new TestSystemExitHandler());
        main.run("src/test/resources/source.mdb", "src/test/resources/target.sqlite");
    }

    @Test(expected = SystemExitRuntimeException.class)
    public void shouldExitIfSourceFileIsEmpty() {
        Main main = new Main(new TestSystemExitHandler());
        main.run("src/test/resources/empty.mdb", "targetFile");
    }

    @Test(expected = SystemExitRuntimeException.class)
    public void shouldExitIfTargetFileIsNotWriteable() throws IOException {
        File tempDirectory = Files.createTempDirectory("access-export-").toFile();
        tempDirectory.deleteOnExit();
        assumeTrue(tempDirectory.setReadOnly());
        Main main = new Main(new TestSystemExitHandler());
        main.run("src/test/resources/source.mdb", tempDirectory + "/aTargetFileThatDoesNotExist");
    }

    class TestSystemExitHandler implements Main.SystemExitHandler {

        @Override
        public void handle(Main.SystemExitException systemExitException) {
            throw new SystemExitRuntimeException(systemExitException);
        }

    }

    class SystemExitRuntimeException extends RuntimeException {

        public SystemExitRuntimeException(Throwable cause) {
            super(cause);
        }

    }

}
