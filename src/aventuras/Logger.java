package aventuras;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Logger{

	private static final DateTimeFormatter FILE_DATE = DateTimeFormatter.ofPattern("dd_MM_yyyy");
	private static final DateTimeFormatter LOG_DATE = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

	private Logger(){
	}

	private static String getFileName(){
		return "logs\\movimientos" + LocalDateTime.now().format(FILE_DATE) + ".log";
	}

	public static void error(String user, String message){
		write("ERROR", user, message);
	}

	public static void log(String user, String message){
		write("Log", user, message);
	}

	private static void write(String level, String user, String message){
		String fileName = getFileName();

		if(!new java.io.File("logs").exists()){
			new java.io.File("logs").mkdir();
		}

		try(FileWriter fw = new FileWriter(fileName, true);
		    BufferedWriter bw = new BufferedWriter(fw);
		    PrintWriter pw = new PrintWriter(bw)){
			String date = LocalDateTime.now().format(LOG_DATE);
			pw.println(String.format("[%s][%s]%s: %s", date, user, level, message));
		}catch(Exception e){
			System.out.println("Error: " + e.getMessage());
		}
	}
}
