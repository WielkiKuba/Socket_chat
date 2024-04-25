package com.example.toDoList;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Description;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.context.annotation.Bean;

import java.awt.*;
import java.io.IOException;
import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;

@SpringBootApplication
public class ToDoListApplication {
	public static void main(String[] args) {
		SpringApplication.run(ToDoListApplication.class, args);
		System.out.println("=================================");
		System.out.println("GUI: http://127.0.0.1:8080");
		System.out.println("=================================");
	}
}
@Configuration
class CorsConfig implements WebMvcConfigurer {
	@Bean
	public DriverManagerDataSource dataSource() {
		DriverManagerDataSource dataSource = new DriverManagerDataSource();
		dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
		dataSource.setUrl("jdbc:mysql://jakubdomain.ddns.net:3306/Messenger");
		dataSource.setUsername("WORLD_CLIENT");
		dataSource.setPassword("Brzozowa5");
		return dataSource;
	}
}
@RestController
class HelloController {
	public static Server server = new Server();
	public static Files files = new Files();
	public static File file = files.main();
	Listener listener = new Listener();
	public Thread listenerThread = new Thread(listener);
	boolean isListenerOn = false;
	@PostMapping("/getLogin")
	public String getLogin(@RequestBody Message message) {
//		sprawdza w bazie danych czy login i hasło sie zgadzają
//		zwraca boolean pod postacią string
		String successfulLogin = "false";
		boolean userExist = false;
		String[] messageSplit = (message.getMessage()).split("#");
		String clientName = messageSplit[0];
		SQL sql = new SQL();
		String clientPassword = messageSplit[1];
		userExist = sql.isExist(clientName);
		if(userExist){
			if((sql.getPasswordSQL(clientName)).equals(clientPassword)){
				successfulLogin = "true";
				if(!isListenerOn){
					listenerThread.start();
					isListenerOn = true;
				}
			}
		}
		return successfulLogin;
	}
	@PutMapping("/addServer")
	public void addServer(@RequestBody String serverIp){
//		dodaje to lokalnej listy adres ip lub dns pośredniczącego
		String[] serverIp2 = serverIp.split("\"");
		String ip = serverIp2[1];
		files.folderCreate(files.osPath()+"Servers");
		files.createFile(files.osPath()+"Servers",ip);
	}
	@GetMapping("/getServers")
	public ArrayList<String> servers(){
//		zwraca liste adresów ip lub dns serwerów. Lista jest przechowywana lokalnie
		ArrayList<String> servers = new ArrayList<>();
		File folderWithIps = new File(files.osPath()+Files.fs+"Servers");
		File[] filesInFolder = folderWithIps.listFiles();
		try{
			for(File inFolder:filesInFolder){
				servers.add(inFolder.getName());
			}
		}catch (NullPointerException e){
//			nie ma tu nic bo to normalne ze bedzie stale waliło bładami ale to w niczym nie przeszkadza
		}
		return servers;
	}
	@PutMapping("/changeServer")
	public void changeServer(@RequestBody String serverIp){
//		zmienia serwer pośredniczący
		listener.stop();
		String[] convertedServerIp = serverIp.split("\"");
		server.changeServer(convertedServerIp[1]);
		listenerThread = new Thread(listener);
		listenerThread.start();
	}
	public static class Message {
		private String message;

		public String getMessage() {
			return message;
		}

		public void setMessage(String message) {
			this.message = message;
		}
	}
	@PutMapping("/send")
	public void send(@RequestBody String message){
//		wysyła wiadomość do innego użytkownika, która bedzie przekazana przez serwer
		String[] convertedMessage = message.split("\"");
		listener.pause();
		server.send(convertedMessage[1]);
		listener.resume();
	}
	@GetMapping("/myIP")
	public String myIp(){
//		zwraca address WAN użytkownika
		listener.pause();
		String myIp = server.myIp();
		listener.resume();
		return myIp;
	}
	@PutMapping("/bio")
	public boolean bio(@RequestBody String description) {
//		wysyła do serwera wiadomość o swoim 'istnieniu'
		listener.pause();
		boolean isFree1 = false;
		String message = "BIO#" + description;
		String isFree = null;
		server.send(message);
		isFree = server.simpleRead();
		if (isFree.equals("true")) {
			isFree1 = true;
		}
		listener.resume();
		return isFree1;
	}
	@GetMapping("/read")
	public String read(){
//		wysyła historie czatu przechowywaną lokalnie
		String read = files.fileReader(file);
		return read;
	}
	@PutMapping("/logOut")
	public void logOut(){
//		logOut zatrzymuje starego listenera i tworzy nowego gdyby użytkownik znowu sie zalogował
//		to wtedy użyje tego nowego
		listener.stop();
		listenerThread = new Thread(listener);
	}
	@GetMapping("/getList")
	public String[] getList() {
//		getList zwraca liste akutalnie aktywnych użytkowników
		listener.pause();
		String content=null;
		server.send("getList");
		while(content==null){
			content = server.simpleRead();
		}
		if(!(content==null)||!(content.equals(""))||!(content.equals(" "))){
			String[] elements = content.split("\n");
			if(content.equals(" ")){
				content = null;
				return null;
			}else {
				return elements;
			}
		}
		listener.resume();
		return null;
	}
	public static String chatMemory = "";
	public static class Listener implements Runnable{
		private final Object lock = new Object();
		private volatile boolean paused = false;
		private volatile boolean stopped = false;
		public void stop(){
			stopped=true;
		}
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
			while(!stopped){
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
					e.printStackTrace();
				}
			}
		}
	}
}
@Controller
class WebController{
	@GetMapping("/")
	public String index(){
		return "index.html";
	}
}