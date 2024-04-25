import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
public class Main {
    public static BlockingQueue<String> connectedClients = new LinkedBlockingQueue<>();
    public static BlockingQueue<Socket> sockets = new LinkedBlockingQueue<>();
    public static Files files = new Files();
    public static String notify = "";
    public static File file = fileStart();
    public static File fileStart(){
        files.defaultPath();
        File localFile =  new File(files.isDefault(),"["+files.formattedTime()+"] logs.txt");
        return localFile;
    }
    public static void main(String[] args){
        int times = 50;
        Scanner scanner = new Scanner(System.in);
        int port = 55555;
        Thread mainThread = new Thread(()->{
            String notify = "";
            try{
                ServerSocket serverSocket = new ServerSocket(port);
                System.out.println("=========================");
                System.out.println("TURNING ON SERVER");
                System.out.println("=========================");
                notify = "["+files.formattedTime()+"] Server started";
                files.writeFile(file,notify,false);
                while(true){
                    Socket socket = serverSocket.accept();
                    notify = "["+files.formattedTime()+"] Connection established with client "+socket.getInetAddress().getHostAddress();
                    System.out.println(notify);
                    files.writeFile(file,notify,false);
                    Thread process = new Thread(new ClientThread(socket));
                    waitThread(200);
                    process.start();
                }
            } catch (IOException e) {
                System.out.println("["+files.formattedTime()+"] Server start failed");
            }
        });
        clear(times);
        System.out.println("<Enter an option");
        System.out.println("=========================");
        System.out.println("1-TURN ON SERVER");
        System.out.println("2-Change src to log file");
        System.out.println("3-Exit");
        System.out.println("=========================");
        String option = scanner.nextLine();
        int optionInt = 0;
        try{
            optionInt = Integer.parseInt(option);
            switch (optionInt){
                case (1)->{
                    clear(times);
                    mainThread.start();
                }
                case (2)->{
                    System.out.println("=========================");
                    System.out.println("Enter a new path to log folder");
                    System.out.println("e.g. C:\\server\\logs");
                    System.out.println("=========================");
                    String path = scanner.nextLine();
                    File confFile = new File(files.osPath()+File.separator+"conf","config");
                    files.writeFile(confFile,path,true);
                }
            }
            if(optionInt>3||optionInt<0){
                System.out.println("Enter a number between 1-3");
            }
        }catch (NumberFormatException e){
            System.out.println("Enter a number between 1-3");
        }
    }
    public static void clear(int times){
        for(int i = 0;i<times;i++){
            System.out.println(" ");
        }
    }

    public static void waitThread(int timeToWait){
        try{
            Thread.sleep(timeToWait);
        }catch (InterruptedException e){
            notify = ("["+files.formattedTime()+"] \"waitThread\" failure");
            System.out.println(notify);
            files.writeFile(file,notify,false);
        }
    }
    public static class ClientThread implements Runnable{
        private OutputStream outputStream;
        private final Socket socket2;
        private InputStream inputStream;
        private boolean isConnected = true;
        String notify = "";
        private synchronized DataInputStream getInputStream() throws IOException {
            if (inputStream == null||socket2.isInputShutdown()) {
                inputStream = socket2.getInputStream();
            }
            return new DataInputStream(inputStream);
        }
        private synchronized DataOutputStream getOutputStream() throws IOException {
            if (outputStream == null||socket2.isOutputShutdown()) {
                outputStream = socket2.getOutputStream();
            }
            return new DataOutputStream(outputStream);
        }
        public void pinger(){
            while(isConnected){
                try{
                    waitThread(60000);
                    int PING = 0;
                    System.out.println("["+files.formattedTime()+"] Ping "+clientIP);
                    for(int i = 0;i<5;i++){
                        waitThread(1000);
                        send(socket2,"PING",false);
                        if(simpleRead(false).equals("PING")){
                            PING++;
                        }
                    }
                    notify = ("["+files.formattedTime()+"] Received "+PING+"/5 packages from "+clientIP);
                    System.out.println(notify);
                    files.writeFile(file,notify,false);
                    if(PING==0){
                        socket2.close();
                        isConnected = false;
                        break;
                    }
                }catch (IOException e){
                    e.printStackTrace();
                }
            }
        }
        public ClientThread(Socket socket){
            this.socket2 = socket;
        }
        public String clientIP;
        @Override
        public void run(){
            clientIP = socket2.getInetAddress().getHostAddress();
            notify = ("["+files.formattedTime()+"] Connection request with "+clientIP+" processed successfully");
            System.out.println(notify);
            files.writeFile(file,notify,false);
            Thread pingerThread = new Thread(()->{
                pinger();
            });
            pingerThread.start();
            String clientName = "";
            while(isConnected){
                try{
                    if(getInputStream().available()!=0){
                        String read = simpleRead(true);
                        if(read!=null){
                            String[] message = read.split("#");
                            boolean isFree = true;
                            if(message[0].equals("MESSAGE")){
                                String receiverIp = message[1];
                                String content = message[2];
                                String senderIp = message[3];
                                Socket localSocket = null;
                                for(String line:connectedClients){
                                    String[] lineSplited = line.split("#");
                                    if(lineSplited[1].equals(receiverIp)){
                                        try{
                                            localSocket = existingSocket(lineSplited[2]);
                                            if(localSocket!=null){
                                                send(localSocket,"RMESSAGE#"+content+"#"+senderIp,true);
                                            }
                                            else {
                                                send(socket2,"RMESSAGE#Connection with client("+receiverIp+") has failed#SERVER",true);
                                            }
                                            break;
                                        }catch (IOException e){}
                                    }
                                    else {
                                        notify = ("["+files.formattedTime()+"] Receiver {"+receiverIp+"} not found");
                                        System.out.println(notify);
                                        files.writeFile(file,notify,false);
                                        send(socket2,"RMESSAGE#Connection with client("+receiverIp+") has failed#SERVER",true);
                                    }
                                }
                            } else if(message[0].equals("BIO")){
                                for(String nickname:connectedClients){
                                    String[] nickname2 = nickname.split("#");
                                    if(nickname2[0].equals(message[1])){
                                        isFree = false;
                                    }
                                }
                                String word=" ";
                                if(isFree){
                                    try{
                                        clientName = message[1];
                                        connectedClients.put(message[1]+"#"+clientIP+"#"+socket2);
                                        sockets.put(socket2);
                                        word = "true";
                                        send(socket2,word,true);
                                    }catch (InterruptedException e){
                                        e.printStackTrace();
                                    }
                                }else {
                                    word = "false";
                                    send(socket2,word,true);
                                }
                            }else if(message[0].equals("getList")){
                                String fullMessage = " ";
                                for(String word:connectedClients){
                                    String[] wordSplited = word.split("#");
                                    String name = wordSplited[0];
                                    String ip = wordSplited[1];
                                    if((ip.equals(clientIP))&&(name.equals(clientName))){
                                        continue;
                                    }
                                    String finalWord = name+"#"+ip;
                                    if(fullMessage.equals(" ")){
                                        fullMessage = finalWord;
                                    }
                                    else{
                                        fullMessage = fullMessage +"\n"+ finalWord;
                                    }
                                }
                                send(socket2,fullMessage,true);
                            } else if (message[0].equals("clientIp")) {
                                String ip = clientIP;
                                send(socket2,ip,true);
                            }
                            if(!isFree){
                                try{
                                    socket2.close();
                                }catch (IOException e){
                                    notify = ("["+files.formattedTime()+"] Closing socket {"+socket2+"} failed");
                                    System.out.println(notify);
                                    files.writeFile(file,notify,false);
                                }
                                break;
                            }
                        }
                    }
                }catch (IOException e){
                    notify = ("["+files.formattedTime()+"] Connection with "+clientIP+" lost");
                    System.out.println(notify);
                    files.writeFile(file,notify,false);
                }
            }
            notify = ("["+files.formattedTime()+"] Connection with "+clientIP+" closed");
            System.out.println(notify);
            files.writeFile(file,notify,false);
            for(String client:connectedClients){
                String[] clientSplited = client.split("#");
                String socketToString = socket2.toString();
                if(clientSplited[2].equals(socketToString)){
                    connectedClients.remove(client);
                }
            }
            sockets.remove(socket2);
        }
        private Socket existingSocket(String socketString) throws IOException {
            Socket oldSocket = null;
            for(Socket thisSocket:sockets){
                String thisSocketString = thisSocket.toString();
                if(socketString.equals(thisSocketString)){
                    oldSocket = thisSocket;
                    break;
                }
            }
            return oldSocket;
        }
        public synchronized String simpleRead(boolean printLog){
            String message="";
            try{
                byte[] buffer = new byte[1024];
                int bytesRead = getInputStream().read(buffer);
                if(bytesRead > 0){
                    message = new String(buffer, 0, bytesRead);
                }
                if(printLog){
                    notify = ("["+files.formattedTime()+"] Data received from "+clientIP);
                    System.out.println(notify);
                    files.writeFile(file,notify,false);
                }
            }
            catch (IOException e){
                if(printLog){
                    notify = ("["+files.formattedTime()+"] Data receiving from "+clientIP+" failed");
                    System.out.println(notify);
                    files.writeFile(file,notify,false);
                }
            }
            return message;
        }
        public synchronized void send(Socket localSocket,String what,boolean printLog){
            try{
                if(localSocket == socket2){
                    getOutputStream().write(what.getBytes());
                }else{
                    OutputStream localOutputStream = localSocket.getOutputStream();
                    localOutputStream.write(what.getBytes());
                }
                if(printLog){
                    notify = ("["+files.formattedTime()+"] Data sent to "+localSocket.getInetAddress().getHostAddress());
                    System.out.println(notify);
                    files.writeFile(file,notify,false);
                }
            }catch (IOException e){
                if(printLog){
                    notify = ("["+files.formattedTime()+"] Data sending to "+localSocket.getInetAddress().getHostAddress()+" failed");
                    System.out.println(notify);
                    files.writeFile(file,notify,false);
                }
            }
        }
    }
}