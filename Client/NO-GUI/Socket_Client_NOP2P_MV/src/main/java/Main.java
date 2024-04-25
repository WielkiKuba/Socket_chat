import java.io.IOException;
import java.util.Scanner;
import java.io.File;
public class Main {
    public static Server server = new Server();
    public static Files files = new Files();
    public static File file = files.main();
//    trzeba też dodać funkcje która bedzie wysyłać kopie historii czatów na inne komputery użytkownika
    public static Scanner scanner = new Scanner(System.in);
    public static SQL sql = new SQL();
    public static int times = 50;
    public static void main(String[] args) {
        while(true){
            clear(times);
            System.out.println("=========================");
            System.out.println("Enter a login (Big letters are included)");
            System.out.println("=========================");
            String login = scanner.nextLine();
            System.out.println("=========================");
            System.out.println("Enter a password");
            System.out.println("=========================");
            String password = scanner.nextLine();
            if(sql.Login(login,password)){
                clear(times);
                String myIp = server.myIp();
                System.out.println("=========================");
                System.out.println("Enter description");
                System.out.println("=========================");
                final String description = (scanner.nextLine()).toUpperCase();
                clear(times);
                Listener listener = new Listener();
                Thread listenerThread = new Thread(listener);
                while(true){
                    server.waitThread(200);
                    clear(times);
                    Boolean shutdown = false;
                    if(!server.bio(description)){
                        System.out.println("=========================");
                        System.out.println("This nickname(description) is already occupied or server is not responding");
                        System.out.println("=========================");
                        System.out.println("1-Change server");
                        System.out.println("2-Exit");
                        System.out.println("=========================");
                        String option0 = scanner.nextLine();
                        try{
                            int option0Int = Integer.parseInt(option0);
                            if(option0Int==1){
                                shutdown = case5();
//                            listenerThread.stop();
                            }else {
                                if(option0Int!=2){
                                    System.out.println("Enter a number between 1-2");
                                }
                            }
                        }catch (NumberFormatException e){
                            System.out.println("Enter a number between 1-2");
                        }
                        System.out.println("App will shutdown in 5s");
                        server.waitThread(5000);
                        shutdown = true;
                    }else {
                        while(true){
                            boolean refreshing = false;
                            server.waitThread(200);
                            String anotherClients = server.getList();
                            listenerThread.start();
                            while(true){
                                clear(times);
                                System.out.println("=========================");
                                if(anotherClients.equals("")||anotherClients.equals(" ")){
                                    System.out.println("There's no online users");
                                }
                                else {
                                    System.out.println(anotherClients);
                                }
                                System.out.println("=========================\n");
                                System.out.println("<Enter an option");
                                System.out.println("=========================");
                                System.out.println("1-Send message");
                                System.out.println("2-Read new messageses");
                                System.out.println("3-Read all messageses");
                                System.out.println("4-Reload clients list");
                                System.out.println("5-Change server's ip address");
                                System.out.println("6-exit");
                                System.out.println("=========================");
                                String option = scanner.nextLine();
                                int optionInt=0;
                                try{
                                    optionInt = Integer.parseInt(option);
                                    switch (optionInt){
                                        case (1)->{
                                            System.out.println("=========================");
                                            System.out.println("Enter client's ip");
                                            System.out.println("=========================");
                                            String ip = scanner.nextLine();
                                            clear(times);
                                            System.out.println("=========================");
                                            System.out.println("Enter a message or enter \"<quit>\" to exit");
                                            System.out.println("=========================");
                                            while(true){
                                                String message = scanner.nextLine();
                                                if(message.equals("<quit>")){
                                                    break;
                                                }else{
                                                    String convertedMessage = "MESSAGE#"+ip+"#"+message+"#"+myIp;
                                                    listener.pause();
                                                    server.send(convertedMessage);
                                                    listener.resume();
                                                }
                                            }
                                        }
                                        case (2)->{
                                            System.out.println("=========================");
                                            if(chatMemory.equals("")){
                                                System.out.println("There's no new messages");
                                            }else {
                                                System.out.println(chatMemory);
                                            }
                                            System.out.println("=========================");
                                        }
                                        case (3)->{
                                            System.out.println("=========================");
                                            String history = files.fileReader(file,true);
                                            System.out.println("=========================");
                                        }
                                        case (4)->{
                                            listener.pause();
                                            anotherClients = server.getList();
                                            refreshing = true;
                                            listener.resume();
                                        }
                                        case (5)->{
                                            shutdown = case5();
//                                        listenerThread.stop();
                                        }
                                        case (6)->{
                                            shutdown = true;
                                        }
                                    }
                                    if(shutdown){
                                        break;
                                    }
                                    if(optionInt>5||optionInt<1){
                                        System.out.println("Enter a number between 1-5");
                                        String x = scanner.nextLine();
                                    }
                                    if(!refreshing){
                                        System.out.println("Press any key to restart");
                                        String x = scanner.nextLine();
                                    }
                                }catch (Exception e){
                                    System.out.println("Enter a number between 1-5");
                                    String x = scanner.nextLine();
                                }
                            }
                            if(shutdown){
                                break;
                            }
                        }
                    }
                    if(shutdown){
                        listenerThread.stop();
                        break;
                    }
                }
            }
            else{
                System.out.println("Wrong login or password");
                System.out.println("=========================");
                System.out.println("App will restart in 5s");
                server.waitThread(5000);
            }
        }
    }
    public static boolean case5(){
        boolean shutdown = false;
        System.out.println("<Enter an option");
        System.out.println("=========================");
        System.out.println("1-Select a server");
        System.out.println("2-Add a new server");
        System.out.println("2-Delete a server");
        System.out.println("=========================");
        String option2 = scanner.nextLine();
        int option2Int = 0;
        try{
            clear(times);
            option2Int = Integer.parseInt(option2);
            switch (option2Int){
                case (1)->{
                    File folderWithIps = new File(files.osPath()+Files.fs+"Servers");
                    File[] filesInFolder = folderWithIps.listFiles();
                    System.out.println("=========================");
                    System.out.println("default");
                    for(File inFolder:filesInFolder){
                        System.out.println(inFolder.getName());
                    }
                    System.out.println("=========================");
                    System.out.println("Type server's ip or dns");
                    System.out.println("=========================");
                    String ip = scanner.nextLine();
                    if((ip.toUpperCase()).toUpperCase().equals("DEFAULT")){
                        server.changeServer("jakubdomain.ddns.net");
                        shutdown = true;
                    }else {
                        server.changeServer(ip);
                        shutdown = true;
                    }
                }
                case (2)->{
                    System.out.println("=========================");
                    System.out.println("Enter a ip or dns of server");
                    System.out.println("=========================");
                    String ip = scanner.nextLine();
                    files.folderCreate(files.osPath()+Files.fs+"Servers");
                    files.createFile(files.osPath()+Files.fs+"Servers",ip);
                }
            }
        }catch (NumberFormatException e){
            e.printStackTrace();
        }
        return shutdown;
    }
    public static void clear(int times){
        for(int i=0;i<times;i++){
            System.out.println(" ");
        }
    }
    public static String chatMemory = "";
    public static class Listener implements Runnable{
        private final Object lock = new Object();
        private volatile boolean paused = false;

        public void pause() {
            paused = true;
        }

        public void resume() {
            synchronized (lock) {
                paused = false;
                lock.notify();
            }
        }
        @Override
        public void run(){
            while(true){
                try{
                    if(server.getInputStream().available()!=0){
                        String read = server.simpleRead();
                        if(read!=null){
                            String[] readSplited = read.split("#");
                            if(readSplited[0].equals("RMESSAGE")){
                                if(chatMemory.equals("")){
                                    chatMemory = "Message: "+readSplited[1]+" FROM "+readSplited[2];
                                    files.writeFile(file,chatMemory,false);
                                }
                                else {
                                    chatMemory = chatMemory + "\nMessage: "+readSplited[1]+" FROM "+readSplited[2];
                                    files.writeFile(file,"Message: "+readSplited[1]+" FROM "+readSplited[2],false);
                                }
                            }else {
                                server.waitThread(100);
                                server.send("PING");
                            }
                        }
                    }
                }catch (IOException e){
                    System.out.println("ERROR 101");
                }
            }
        }
    }
}