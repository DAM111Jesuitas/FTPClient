package org.ftpclient.Utils;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.ftpclient.Comandos;

import java.io.IOException;
import java.net.SocketException;
import java.util.Scanner;

public abstract class FTPConnections
{
    private String user = "";
    private String pass ="";
    public static void showServerReply(FTPClient client) {
        String[] replies = client.getReplyStrings();
        if (replies != null && replies.length > 0) {
            for (String reply:
                    replies) {
                System.out.println("SERVER: " + reply);
            }
        }
    }
    public static FTPClient initConnPassive(String server, String user, String pass) {
        System.out.println("Conectando a " + server);
        FTPClient client = new FTPClient();
        try {
            client.connect(server);
            showServerReply(client);
            int replyCode = client.getReplyCode();
            if (!FTPReply.isPositiveCompletion(replyCode)) {
                System.err.println("Codigo de Error: " + replyCode);
            }
            boolean success = client.login(user, pass);
            showServerReply(client);
            if (!success) {
                System.err.println("Ha ocurrido un error al conectarse con el servidor");
                System.exit(1);
            } else {
                System.out.println("LOGGED as " + user);
            }
        } catch (SocketException e) {
            System.err.println("El servidor no responde");
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Ha ocurrido un error con el servidor");
            System.exit(1);
        }
        return client;
    }

    public static void closeConn(FTPClient client) {
        try {
            client.disconnect();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        System.out.println("Desconectado");
    }

    public static void connectToServer(String server, String user, String pass) {
        FTPClient client = initConnPassive(server, user, pass);
        Comandos comandos = new Comandos(client);
        try {
            comandos.defaultPath = client.printWorkingDirectory();
        } catch (IOException e) {
            System.err.println("Ha ocurrido un error guardando el directorio por defecto");
            throw new RuntimeException(e);
        }
        Scanner sc = new Scanner(System.in);
        boolean exitUser = false;
        while (!exitUser) {
            String comando = sc.nextLine();
            if (comandos.commandExists(comando)) {
                if (comando.equals("exit")) {
                    exitUser = true;
                    comandos.executeCommand(comando);
                } else {
                    comandos.executeCommand(comando);
                }
            }
        }
        sc.close();
    }
}
