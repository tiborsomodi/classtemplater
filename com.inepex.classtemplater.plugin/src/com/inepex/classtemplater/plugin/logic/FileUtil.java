package com.inepex.classtemplater.plugin.logic;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.eclipse.core.resources.IFile;

public class FileUtil {

	
	public static String readFile(IFile file) throws Exception{
		BufferedReader breader = new BufferedReader(new InputStreamReader(file.getContents(), "UTF-8"));
		String content = "";
		String strLine = "";
		while ((strLine = breader.readLine()) != null) {
			content += strLine + "\n";
		}
		breader.close();
		return content;
	}
	
}
