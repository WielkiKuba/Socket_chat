package com.example.socket_client_nop2p_jfx;

import java.io.*;

public class Files {
    public static File main() {
        folderCreate(osPath());
        File file = createFile(osPath(),"chatHistory");
        return file;
    }
    public static void folderCreate(String localPath){
        File folder = new File(localPath);
        folder.mkdir();
    }
    public static String osPath(){
        String os = System.getProperty("os.name");
        String localPath = "";
        if(!(os.equals("Linux"))){
            localPath = System.getenv("APPDATA")+fs+"unbrandedMessenger_byKuba"+fs;
        }else {
            localPath = System.getProperty("user.home")+"/unbrandedMessenger_byKuba/";
        }
        return localPath;
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
    public static final String fs = File.separator;
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
    public static File deleteFile(String localPath,String name){
        File file = new File(localPath,name);
        if(file.exists()){
            file.delete();
        }
        return file;
    }
    public static String fileReader(File file, boolean print){
        String content = "";
        try{
            FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String line;
            while((line = bufferedReader.readLine())!=null){
                if(content.equals("")){
                    content = line;
                }else{
                    content += "\n"+line;
                }
                if(!(line.equals("")||line.equals(" "))&&print){
                    System.out.println(line);
                }
            }
            fileReader.close();
        }catch (IOException e){
            e.printStackTrace();
        }
        return content;
    }
}
