package org.ftpclient;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.ftpclient.Utils.Exists;
import org.ftpclient.Utils.FTPConnections;

import java.io.*;
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
            System.err.println(c + " doesn't exist");
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
                    getFile();
                }
                default -> System.err.println(c + " doesn't exist");
            }
        } else {
            String params = Exists.getParameters(c);
            c = Exists.getCommandNoParams(c);
            switch (c) {
                case "cd" -> commandCd(params);
                default -> System.err.println(c + " doesen't accept parameters");
            }
        }
    }

    public void commandLs() {
        try {
            System.out.println("Current dir: " + client.printWorkingDirectory());
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
            System.err.println("Couldn't get a list of all the files");
            throw new RuntimeException(e);
        }
    }

    public void commandExit() {
        FTPConnections.closeConn(client);
    }

    public void commandPWD() {
        try {
            System.out.println(client.printWorkingDirectory());
        } catch (IOException e) {
            System.err.println("Couldn't list the current directory");
            throw new RuntimeException(e);
        }
    }

    public void commandCd() {
        try {
            System.out.println("Changing dir to " + defaultPath);
            client.changeWorkingDirectory(defaultPath);
        } catch (IOException e) {
            System.err.println("Couldn't change to default directory");
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
            System.out.println("Changing directory: " + ruta);
            try {
                client.changeWorkingDirectory(ruta);
            } catch (IOException e) {
                System.err.println(ruta + " doesn't exist");
                throw new RuntimeException(e);
            }
        } else if (ruta.equals("..")) { // Se mueve al directorio anterior/superior
            try {
                System.out.println("Changing directory: " + ruta);
                client.changeToParentDirectory();
            } catch (IOException e) {
                System.err.println("An error ocurred when changing to the parent directory");
                throw new RuntimeException(e);
            }
        } else if (esFichero && coincide) { // Error porque se intenta mover a un fichero
            System.out.println(ruta + " is a file");
        } else { // Error porque el archivo no existe
            System.err.println(ruta + " doesn't exist");
        }
    }

    public static void commandHelp() {
        Map<String, String> comandoHelpStrings = new HashMap<>();
        for (eComandos com :
                eComandos.values()) {
            switch (com) {
                case ls -> comandoHelpStrings.put(com.toString(), "This command lists all the files in a directory");
                case cd -> comandoHelpStrings.put(com.toString(), "This command is used to change directories");
                case help ->
                        comandoHelpStrings.put(com.toString(), "This command is used to display a message with all the available commands");
                case exit -> comandoHelpStrings.put(com.toString(), "Command to exit user and close session");
                case clear -> comandoHelpStrings.put(com.toString(), "Command that clears the console");
                case pwd -> comandoHelpStrings.put(com.toString(), "Displays the current working directory");
            }
        }

        System.out.println("Displaying all the available commands: \n");
        comandoHelpStrings.forEach((key, value) -> System.out.println("- " + key + ":\n\t" + value + "\n"));
    }

    public void commandClear() {
        for (int i = 0; i < 30; i++) {
            System.out.println("\n");
        }
    }

    public void getFile() {
        String filename = "welcome.msg";
        File localFile = new File("src/main/resources/welcome.msg");
        try {
            OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(localFile));
            boolean success = false;
            success = client.retrieveFile(filename, outputStream);
            if (success) {
                System.out.println("File downloaded successfully");
            }
            outputStream.close();
            outputStream.flush();
        } catch (IOException e) {
            System.err.println("An error ocurred when downloading a file");
            throw new RuntimeException(e);
        }
    }
}
