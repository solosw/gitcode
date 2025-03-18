package com.solosw.codelab;

import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class EchoShell extends CommandExecutionHelper {
    public EchoShell() {
        super();

    }

    @Override
    protected boolean handleCommandLine(String command) throws Exception {



        return !"exit".equals(command);

    }
}
