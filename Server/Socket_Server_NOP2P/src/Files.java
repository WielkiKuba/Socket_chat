import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Files {
    private static String path = "";
    public void newPath(String path){
        this.path = path;
    }
    public void defaultPath(){
        File file = new File(osPath()+"conf"+fs,"config");
        if(file.exists()){
            newPath(fileReader(file));
            System.out.println(path);
        }else{
            newPath(osPath());
            System.out.println(path);
        }
    }
    public static final String fs = File.separator;
    public static String osPath(){
        String os = System.getProperty("os.name");
        String localPath = "";
        if(!(os.equals("Linux"))){
            localPath = System.getenv("APPDATA")+fs+"Roaming"+fs+"messengerServer_byKuba"+fs;
        }else {
            localPath = System.getProperty("user.home")+"/messengerServer_byKuba/";
        }
        return localPath;
    }
    public static String formattedTime(){
        LocalDateTime currentTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        String dateTime = currentTime.format(formatter);
        return dateTime;
    }
    public static File createFile(String localPath,String name){
        File file = new File(localPath,name);
        if(!file.exists()){
            try{
                file.createNewFile();
            }catch (IOException e){
                e.printStackTrace();
            }
        }
        return file;
    }
//    public static void createLogFile(){
//        createFile(path,"["+formattedTime()+"] logs.txt");
//    }
    public String isDefault(){
        File file = new File(osPath()+"conf"+fs,"config");
        if(file.exists()){
            String localPath = fileReader(file);
            newPath(localPath);
        }else{
            folderCreate(path);
            folderCreate(path+fs+"conf");
            createFile(path+fs+"conf","config");
            writeFile(file,osPath(),true);
        }
        System.out.println("PATH = "+path);
        String pathToLogFile = path;
        return pathToLogFile;
    }
    public static void folderCreate(String localPath){
        File folder = new File(localPath);
        folder.mkdir();
    }
    public static String fileReader(File file){
        String content = "";
        try{
            FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String line;
            while((line = bufferedReader.readLine())!=null){
                if(content.equals("")){
                    content = line;
                }else{
                    content += "\n"+bufferedReader.readLine();
                }
            }
            fileReader.close();
        }catch (IOException e){
            e.printStackTrace();
        }
        return content;
    }
    public static void writeFile(File file,String what,boolean overwrite){
        try{
            FileWriter fileWriter = new FileWriter(file,!overwrite);
            fileWriter.write("\n"+what);
            fileWriter.close();
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}
