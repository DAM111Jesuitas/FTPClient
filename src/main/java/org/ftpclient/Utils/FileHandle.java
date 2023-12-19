package org.ftpclient.Utils;

import org.apache.commons.net.ftp.FTPClient;

import java.io.*;

public class FileHandle
{
    public static void getFile(FTPClient client, String file, String localFile) {
        try {
            OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(localFile));
            boolean success = false;
            success = client.retrieveFile(file, outputStream);
            if (success) {
                System.out.println("Archivo " + file + " descargado a: " + localFile);
            }
            outputStream.close();
            outputStream.flush();
        } catch (IOException e) {
            System.err.println("Ha ocurrido un error al descargar un fichero");
            throw new RuntimeException(e);
        }
    }

    public static void uploadFile(FTPClient client,  String filename) {
        File file = new File("src/main/resources/prueba.txt");
        try {
            InputStream inputStream = new FileInputStream(file);
            System.out.println("Subiendo archivo..");
            OutputStream outputStream = client.storeFileStream(filename);
            byte[] bytes = new byte[4096];
            int read = 0;
            while ( (read = inputStream.read(bytes)) != -1) {
                outputStream.write(bytes, 0, read);
            }
            inputStream.close();
            outputStream.close();

            boolean completed = client.completePendingCommand();
            if (completed) {
                System.out.println("Archivo subido correctamente");
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void createDirectory(FTPClient ftpClient) {
        String dirToCreate = "/upload123";
        boolean success;
        try {
            success = ftpClient.makeDirectory(dirToCreate);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        FTPConnections.showServerReply(ftpClient);
        if (success) {
            System.out.println("Successfully created directory: " + dirToCreate);
        } else {
            System.out.println("Failed to create directory. See server's reply.");
        }
    }
}
