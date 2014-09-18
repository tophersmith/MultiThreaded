package spytools.multi.helpers;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Logger {

	private static SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private static boolean debugEnabled = true;

	private static final Object infoLock = new Object();
	public void info(String message) {
		synchronized(infoLock){
			printMessage("INFO", message);
		}
	}

	private static final Object debugLock = new Object();
	public void debug(String message) {
		if(debugEnabled){
			synchronized(debugLock){
				printMessage("DEBUG", message);
			}
		}
	}

	private static final Object warningLock = new Object();
	public void warning(String message) {
		synchronized(warningLock){
			printMessage("DEBUG", message);
		}
	}

	private static final Object errorLock = new Object();
	public void error(String message) {
		synchronized(errorLock){
			printMessage("DEBUG", message);
		}
	}

	private static void printMessage(String type, String message){
		System.out.println(type + ": " + getTime() + " " + message);
	}

	private static String getTime(){
		return sdfDate.format(new Date());
	}

}
