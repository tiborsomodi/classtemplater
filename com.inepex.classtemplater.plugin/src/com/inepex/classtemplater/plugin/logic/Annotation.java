package com.inepex.classtemplater.plugin.logic;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jdt.core.IMemberValuePair;

public class Annotation {

	String name;
	Map<String, String> params = new HashMap<String, String>();
	
	public Annotation(String name, Map<String, String> params) {
		super();
		this.name = name;
		this.params = params;
	}
	
	public Annotation(String name, IMemberValuePair[] paramsAsPairs) {
		super();
		this.name = name;
		for (IMemberValuePair pair : paramsAsPairs){
			params.put(pair.getMemberName(), (String)pair.getValue());
		}
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Map<String, String> getParams() {
		return params;
	}
	public void setParams(Map<String, String> params) {
		this.params = params;
	}
	
	public String getParamValue(String name){
		return params.get("name");
	}
	
}
