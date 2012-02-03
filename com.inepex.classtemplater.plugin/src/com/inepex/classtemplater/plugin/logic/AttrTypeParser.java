package com.inepex.classtemplater.plugin.logic;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AttrTypeParser {

	private String string;
	private String separator;
	private String hierarchOpen;
	private String hierarchClose;
	
	private Set<String> items = new HashSet<String>();
	private List<String> firstLevelItemsInOrder = new ArrayList<String>();
	
	public AttrTypeParser(String string, String separator, String hierarchOpen, String hierarchClose) {
		super();
		if (string.endsWith(separator)) this.string = string.substring(0, string.length()-1);
		else this.string = string;
		this.separator = separator;
		this.hierarchOpen = hierarchOpen;
		this.hierarchClose = hierarchClose;
		if (string != null && !string.equals("")){
			calcItems();
			calcFirstLevelItems();
		}
	}
	
	private void calcItems(){
		calcItemsHelper(string);
	}
	
	private void calcItemsHelper(String part){
		if (checkCompound(part)){
			for (String s : hierarchicalSplit(part)){
				calcItemsHelper(s);
			}
		} else {
			items.add(getBase(part));
			String nextLevel = nextLevel(part);
			if (nextLevel != null) calcItemsHelper(nextLevel);
		}
	}
	
	private void calcFirstLevelItems(){
		if (nextLevel(string) != null)
		for (String s : hierarchicalSplit(nextLevel(string))){
			firstLevelItemsInOrder.add(s);
		}
	}

	public String[] hierarchicalSplit(String item){
		List<String> result = new ArrayList<String>();
		StringBuffer part = new StringBuffer();
		int hierarchy = 0;
		for (int i=0; i<item.length(); i++){
			String actual = item.substring(i, i+1);
			if (actual.equals(separator)){
				if (hierarchy == 0){
					result.add(part.toString());
					part = new StringBuffer();
				}
			} else if (actual.equals(hierarchOpen)){
				hierarchy++;
			} else if (actual.equals(hierarchClose)){
				hierarchy--;
			}
			
			if (hierarchy == 0 && !actual.equals(separator)
					|| hierarchy > 0)
			{
				part.append(actual);
			}
		}
		if (part.length() > 0) result.add(part.toString());
		return result.toArray(new String[0]);
	}
	
	public String getBase(String item){
		if (checkCompound(item)) throw new RuntimeException("invalid getBase call");
		else if (item.indexOf(hierarchOpen) == -1) return item;
		return item.substring(0, item.indexOf(hierarchOpen));
	}
	
	public String nextLevel(String item){
		if (checkCompound(item)) throw new RuntimeException("invalid nextLevel call");
		else if (item.indexOf(hierarchOpen) == -1) return null;
		else return item.substring(item.indexOf(hierarchOpen)+1, item.lastIndexOf(hierarchClose));
	}
	
	private boolean checkCompound(String item){
		int hierarchy = 0;
		for (int i=0; i<item.length(); i++){
			String actual = item.substring(i, i+1);
			if (actual.equals(separator)){
				if (hierarchy == 0){
					return true;
				}
			} else if (actual.equals(hierarchOpen)){
				hierarchy++;
			} else if (actual.equals(hierarchClose)){
				hierarchy--;
			}
		}
		return false;
	}

	public Set<String> getItems() {
		return items;
	}

	public List<String> getFirstLevelItemsInOrder() {
		return firstLevelItemsInOrder;
	}
	
	public static String clean(String s){
		return s.replace("Q", "").replace(";>", ">").replace(";", ",");
	}
	
}
