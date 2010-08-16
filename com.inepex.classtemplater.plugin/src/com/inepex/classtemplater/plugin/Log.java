package com.inepex.classtemplater.plugin;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;

public class Log {

	//Log
	
	public static void log(String message){
		Platform.getLog(Platform.getBundle("com.inepex.classtemplater.plugin")).log(
				new Status(Status.WARNING, "com.inepex.classtemplater.plugin", message));	
	}
	
}
