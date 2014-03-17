package io;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

public class Logger {
	private static ArrayList<LogLevel> levels = new ArrayList<LogLevel>();
	private static boolean persistData = false;
	private static PrintWriter pw = null;
	
	public static void setPersist(boolean persist) {
		persistData = persist;
	}
	
	public static void addLogLevel(LogLevel level) {
		if(!levels.contains(level)) {
			levels.add(level);
		}
	}
	
	public static void removeLogLevel(LogLevel level) {
		if(levels.contains(level))
			levels.remove(level);
	}
	
	public static void log(LogLevel level, String message) {
		if(levels.contains(level)) {
			System.out.println("[" + level.toString() + "]: " + message);
		}
		
		if(persistData) {
			if(pw == null) {
				try {
					pw = new PrintWriter("log.txt", "UTF-8");
				} catch (FileNotFoundException | UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			pw.println("[" + level.toString() + "]: " + message);
		}
	}
	
	public static void exit() {
		if(pw != null) {
			pw.flush();
			pw.close();
		}
	}
	
}
