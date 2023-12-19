package org.ftpclient.Utils;

import org.ftpclient.eComandos;

public class Exists
{
    public static boolean commandExistsInComandos(String s) {
        boolean existe = false;
        boolean hasParams = hasParameters(s);

        String command;
        if (hasParams) {
            command = s.substring(0, s.indexOf(" "));
        } else {
            command = s;
        }

        for (eComandos eComando :
                eComandos.values()) {
            if (!(eComando.name().length() < command.length()) && eComando.name().equals(command)) {
                existe = true;
                break;
            }
        }
        return existe;
    }

    public static boolean hasParameters(String s) {        // Comprueba si es solo comando o si tiene parametros
        boolean params = false;
        try {
            String command = s.substring(0, s.indexOf(" "));
            params = true;
        } catch (StringIndexOutOfBoundsException e) {
            params = false;
        }

        return params;
    }

    public static String getParameters(String s) { // Coge los parametros
        boolean parametros = hasParameters(s);

        return parametros ? s.substring(s.indexOf(' ') + 1) : "";
    }

    public static String getCommandNoParams(String s) {
        String command = null;
        if (hasParameters(s)) {
            command = s.substring(0, s.indexOf(' '));
        }

        return command;
    }
}
