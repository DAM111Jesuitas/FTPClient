package org.ftpclient;

import org.ftpclient.Utils.FTPConnections;

import java.net.InetAddress;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class App
{
    static boolean exit;
    public static void main(String[] args) {
        exit = false;
        Scanner sc = new Scanner(System.in);
        while (!exit) {
            System.out.println("""
                    ┏┓┏┳┓┏┓┏┓┓ •    \s
                    ┣  ┃ ┃┃┃ ┃ ┓┏┓┏┓╋
                    ┻  ┻ ┣┛┗┛┗┛┗┗ ┛┗┗
                                    \s
                    1. Iniciar Sesion
                    2. Salir
                    """);
            String res;
            res = sc.nextLine();
            // Comprobacion respuesta
            switch (res.toLowerCase()) {
                case "exit", "2" -> {
                    System.out.println("Saliendo");
                    exit = true;
                    sc.close();
                    System.exit(1);
                }
                case "login", "iniciar sesion", "1" -> {
                    System.out.println("URL del servidor: ");
                    String server = sc.nextLine();
                    System.out.println("Usuario: ");
                    String user = sc.nextLine();
                    System.out.println("Contraseña: ");
                    String pass = sc.nextLine();
                    FTPConnections.connectToServer(server, user, pass);
                    // TODO: Corregir error al escribir exit
                }
                default -> {
                    System.err.println("Debes elegir una de las dos opciones");
                }
            }
        }
        sc.close();
    }
}

