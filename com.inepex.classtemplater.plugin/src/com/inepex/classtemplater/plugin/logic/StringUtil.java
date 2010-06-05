package com.inepex.classtemplater.plugin.logic;

public class StringUtil {

	public static String getL1(String s){
		StringBuffer sb = new StringBuffer(s);
		sb.setCharAt(0, Character.toLowerCase(sb.charAt(0)));
		return sb.toString(); 
	}
	
	public static String getU1(String s){
		StringBuffer sb = new StringBuffer(s);
		sb.setCharAt(0, Character.toUpperCase(sb.charAt(0)));
		return sb.toString(); 
	}	
	
}
