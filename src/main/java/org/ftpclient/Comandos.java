package org.ftpclient;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.ftpclient.Utils.Exists;
import org.ftpclient.Utils.FTPConnections;
import org.ftpclient.Utils.FileHandle;

import java.io.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Comandos
{
    static FTPClient client;

    // Archivos de un directorio para uso en otras funciones
    FTPFile[] files;

    public String defaultPath;

    public Comandos(FTPClient client) {
        Comandos.client = client;
    }

    // Prueba si existe
    public boolean commandExists(String c) {
        if (!Exists.commandExistsInComandos(c)) {
            System.err.println(c + " no existe");
            return false;
        }
        return true;
    }

    // Ejecucion de comandos con un switch simple
    public void executeCommand(String c) {
        boolean hasParams = Exists.hasParameters(c);
        if (!hasParams) {
            switch (c) {
                case "ls" -> commandLs();
                case "cd" -> commandCd();
                case "help" -> commandHelp();
                case "exit" -> commandExit();
                case "clear" -> commandClear();
                case "pwd" -> commandPWD();
                case "prueba" -> {

                }
                default -> System.err.println(c + " no existe");
            }
        } else {
            String params = Exists.getParameters(c);
            c = Exists.getCommandNoParams(c);
            switch (c) {
                case "cd" -> commandCd(params);
                case "get" -> commandGet(params);
                default -> System.err.println(c + " no acepta parametros");
            }
        }
    }

    public void commandLs() {
        try {
            System.out.println("Directorio actual: " + client.printWorkingDirectory());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try {
            files = client.listFiles();
            for (FTPFile file :
                    files) {
                if (file.isDirectory()) {
                    System.out.println("DIR  " + file.getName());
                } else {
                    System.out.println("FILE " + file.getName());
                }
            }
        } catch (IOException e) {
            System.err.println("No se ha podido listar el directorio actual");
            throw new RuntimeException(e);
        }
    }

    public void commandExit() {
        FTPConnections.closeConn(client);
        System.exit(1);
    }

    public void commandPWD() {
        try {
            System.out.println(client.printWorkingDirectory());
        } catch (IOException e) {
            System.err.println("No se ha podido listar el directorio actual");
            throw new RuntimeException(e);
        }
    }

    public void commandCd() {
        try {
            System.out.println("Cambiando directorio a: " + defaultPath);
            client.changeWorkingDirectory(defaultPath);
        } catch (IOException e) {
            System.err.println("No se ha podido mover al directorio por defecto");
            throw new RuntimeException(e);
        }
    }

    public void commandCd(String path) {
        String ruta = path.substring(path.indexOf(" ") + 1);
        boolean esFichero = false;
        boolean coincide = false;
        // Coge todos los archivos del directorio
        try {
            files = client.listFiles();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // Comprobacion de fichero y si existe
        for (FTPFile file : files) {
            if (file.getName().equals(ruta)) {
                coincide = file.getName().equals(ruta);
                esFichero = !file.isDirectory();
            }
        }
        // Diferentes casos de existencia
        if (!esFichero && coincide) {  // Cambiar directorio
            System.out.println("Cambiando directorio: " + ruta);
            try {
                client.changeWorkingDirectory(ruta);
            } catch (IOException e) {
                System.err.println(ruta + " no existe");
                throw new RuntimeException(e);
            }
        } else if (ruta.equals("..")) { // Se mueve al directorio anterior/superior
            try {
                System.out.println("Cambiando directorio: " + ruta);
                client.changeToParentDirectory();
            } catch (IOException e) {
                System.err.println("Ha ocurrido un error al cambiar de directorio");
                throw new RuntimeException(e);
            }
        } else if (esFichero && coincide) { // Error porque se intenta mover a un fichero
            System.out.println(ruta + " es un archivo");
        } else { // Error porque el archivo no existe
            System.err.println(ruta + " no existe");
        }
    }

    public static void commandHelp() {
        Map<String, String> comandoHelpStrings = new HashMap<>();
        for (eComandos com :
                eComandos.values()) {
            switch (com) {
                case ls -> comandoHelpStrings.put(com.toString(), "Este comando lista todos los archivos del directorio");
                case cd -> comandoHelpStrings.put(com.toString(), """
                        Se utiliza para cambiar de directorio, estos son los casos de uso:
                            - cd sin parametros te manda al directorio personal, que suele ser /
                            - cd (param1) sirve para entrar a un directorio.
                            - cd .. se utiliza para volver al directorio anterior
                        """);
                case help ->
                        comandoHelpStrings.put(com.toString(), "Muestra todos los comandos y como utilizarlos en la consola");
                case exit -> comandoHelpStrings.put(com.toString(), "Con exit sales del programa");
                case clear -> comandoHelpStrings.put(com.toString(), "Limpia la consola");
                case pwd -> comandoHelpStrings.put(com.toString(), "Muestra el directorio actual");
                case get -> {
                    comandoHelpStrings.put(com.toString(),
                            """
                            Este comando se descarga archivos. Puedes utilizarlo escribiendo solo un parametro donde este 
                            sería el nombre del archivo a descargar y dejando el directorio donde lo quieras guardar por 
                            defecto (que es src/main/resources/); O utilizar 2 parametros donde especifiques el nombre del 
                            fichero y el directorio donde lo quieras guardar
                            """);
                }
            }
        }

        System.out.println("Mostrando todos los comandos disponibles: \n");
        comandoHelpStrings.forEach((key, value) -> System.out.println("- " + key + ":\n\t" + value + "\n"));
    }

    public void commandClear() {
        for (int i = 0; i < 30; i++) {
            System.out.println("\n");
        }
    }

    public void commandGet(String params) {
        String[] allParams = params.split(" ");
        String filename = "";
        String localFile = "src/main/resources/";
        if (allParams.length == 1) {
            filename = allParams[0];
            localFile += filename;
            FileHandle.getFile(client, filename, localFile);
        } else if (allParams.length == 2) {
            filename = allParams[0];
            localFile = allParams[1] + '/' + filename;
            FileHandle.getFile(client, filename, localFile);
        } else {
            System.err.println("Este comando solo acepta como minimo 1 parametro. Leete la documentacion escribiendo el comando 'help'");
        }
    }
}
