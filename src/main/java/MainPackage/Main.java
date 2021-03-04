package MainPackage;

import org.apache.commons.codec.binary.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class Main
{
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("");
        System.out.println("Wybierz czy to ma być server czy klient?");
        System.out.println("1. Server");
        System.out.println("2. Klient");
        System.out.println("");
        switch (scanner.nextInt()) {
            case 1:
                try {
                    System.out.print("Podaj port: ");
                    ServerSocket welcomeSocket = new ServerSocket(scanner.nextInt());
                    System.out.println("");
                    System.out.println("Server nasłuchuje na " + welcomeSocket.getLocalSocketAddress());
                    System.out.println("");
                    Socket connectionSocket = welcomeSocket.accept();
                    System.out.println("Połączono z " + connectionSocket.getRemoteSocketAddress());
                    System.out.println("");
                    DataInputStream inFromClient = new DataInputStream(connectionSocket.getInputStream());
                    DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream());
                    String initVector = inFromClient.readUTF();
                    String key = inFromClient.readUTF();
                    String hash = inFromClient.readUTF();
                    IvParameterSpec iv = new IvParameterSpec(initVector.getBytes("UTF-8"));
                    SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes("UTF-8"), "AES");
                    Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
                    cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);
                    byte[] original = cipher.doFinal(Base64.decodeBase64(hash));
                    String effect = new String(original);
                    System.out.println();
                    System.out.println("------------------------------------");
                    System.out.println("--------------Odebrano--------------");
                    System.out.println();
                    System.out.println("IV: "+initVector);
                    System.out.println();
                    System.out.println("Klucz: "+key);
                    System.out.println();
                    System.out.println("Hash: "+hash);
                    System.out.println("------------------------------------");
                    System.out.println("------Odszyfrowana wiadomosc--------");
                    System.out.println();
                    System.out.println("Wiadomosc: "+effect);
                    System.out.println("------------------------------------");
                    inFromClient.close();
                    outToClient.close();
                    welcomeSocket.close();
                    connectionSocket.close();
                } catch (Exception e) {
                }

                break;
            case 2:
                try {
                    System.out.print("Podaj adres servera: ");
                    String adres = scanner.nextLine();
                    adres = scanner.nextLine();
                    System.out.print("Podaj port: ");
                    int port = scanner.nextInt();
                    scanner.nextLine();
                    Socket connectionSocket = new Socket(adres, port);
                    DataInputStream inFromServer = new DataInputStream(connectionSocket.getInputStream());
                    DataOutputStream outToServer = new DataOutputStream(connectionSocket.getOutputStream());
                    String initVector;
                    do
                    {
                        System.out.print("Podaj iv lub pozostaw pusty jeśli ma to być iv losowy (16 znaków): ");
                        initVector = scanner.nextLine();
                        if (initVector.equals("")) initVector = org.apache.commons.lang3.RandomStringUtils.randomAlphanumeric(16);
                        System.out.println("");
                    }
                    while (!(initVector.length()==16));
                    String key;
                    do
                    {
                        System.out.print("Podaj klucz lub pozostaw pusty jeśli ma to być klucz losowy (16 znakow): ");
                        key = scanner.nextLine();
                        if (key.equals("")) key = org.apache.commons.lang3.RandomStringUtils.randomAlphanumeric(16);
                        System.out.println("");
                    }
                    while (!(key.length()==16));
                    System.out.print("Podaj wiadomość: ");
                    String value = scanner.nextLine();
                    System.out.println("");
                    IvParameterSpec iv = new IvParameterSpec(initVector.getBytes("UTF-8"));
                    SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes("UTF-8"), "AES");
                    Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
                    cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);
                    byte[] encrypted = cipher.doFinal(value.getBytes());
                    String hash = Base64.encodeBase64String(encrypted);
                    System.out.println("------------------------------------");
                    System.out.println("---------------Wysłano--------------");
                    System.out.println();
                    System.out.println("IV: "+initVector);
                    System.out.println();
                    System.out.println("Klucz: "+key);
                    System.out.println();
                    System.out.println("Hash: "+hash);
                    System.out.println("------------------------------------");
                    System.out.println("-------Niewyslana wiadomosc---------");
                    System.out.println();
                    System.out.println("Czysta wiadomosc: "+value);
                    System.out.println("------------------------------------");
                    outToServer.writeUTF(initVector);
                    outToServer.writeUTF(key);
                    outToServer.writeUTF(hash);
                    inFromServer.close();
                    outToServer.close();
                    connectionSocket.close();
                } catch (Exception e) {
                }
                break;
        }
        System.out.println("");
    }
}
