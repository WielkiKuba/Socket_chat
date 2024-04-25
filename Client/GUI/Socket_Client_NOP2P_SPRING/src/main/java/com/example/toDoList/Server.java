package com.example.toDoList;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class Server {
    public static void waitThread(int timeToWait){
        try{
            Thread.sleep(timeToWait);
        }catch (InterruptedException e){
            System.out.println("ERROR 221");
        }
    }
    public static int port = 55555;
    public static String server = "jakubdomain.ddns.net";
    private static InputStream inputStream;
    private static OutputStream outputStream;
    public static Socket socket;
    public synchronized Socket getSocket() throws IOException {
        if (socket == null || socket.isClosed()) {
            socket = new Socket(server, port);
            waitThread(200);
        }
        return socket;
    }
    public synchronized InputStream getInputStream() throws IOException {
        if (inputStream == null || getSocket().isInputShutdown()) {
            inputStream = getSocket().getInputStream();
        }
        return inputStream;
    }
    public synchronized OutputStream getOutputStream() throws IOException {
        if (outputStream == null || getSocket().isOutputShutdown()) {
            outputStream = getSocket().getOutputStream();
        }
        return outputStream;
    }
    public synchronized String simpleRead(){
        String message="";
        try{
            byte[] buffer = new byte[1024];
            int bytesRead = getInputStream().read(buffer);
            if(bytesRead > 0){
                message = new String(buffer, 0, bytesRead);
            }
        }
        catch (IOException e){
            System.out.println("ERROR 211");
        }
        return message;
    }
    public void send(String what) {
        try {
            getOutputStream().write(what.getBytes());
        } catch (IOException e) {
            System.out.println("ERROR 201");
        }
    }
    public String myIp() {
        send("clientIp");
        String ip = simpleRead();
        return ip;
    }
    public void changeServer(String ip){
        if(!(ip.equals(server))){
            this.server = ip;
            try{
                socket.close();
            }catch (IOException e){
                e.printStackTrace();
            }
        }
    }
}
