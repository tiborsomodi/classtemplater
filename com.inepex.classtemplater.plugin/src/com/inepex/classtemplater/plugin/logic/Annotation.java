/*
 * Copyright:
 * 2010 Tibor Somodi, Inepex, Hungary, http://www.inepex.com
 * License:
 * EPL: http://www.eclipse.org/legal/epl-v10.html
 */

package com.inepex.classtemplater.plugin.logic;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jdt.core.IAnnotatable;
import org.eclipse.jdt.core.IAnnotation;
import org.eclipse.jdt.core.IMemberValuePair;

import com.inepex.classtemplater.plugin.Log;

public class Annotation {

	String name;
	Map<String, String> params = new HashMap<String, String>();
	
	public Annotation(IAnnotation jdtAnnotation) throws Exception {
		name = jdtAnnotation.getElementName();
		for (IMemberValuePair pair : jdtAnnotation.getMemberValuePairs()){
			try {
				if (pair.getValue() instanceof String)
					params.put(pair.getMemberName(), (String)pair.getValue());
				else if (pair.getValue() instanceof Boolean)
					params.put(pair.getMemberName(), String.valueOf(pair.getValue()));
				else if (pair.getValue() instanceof Integer)
					params.put(pair.getMemberName(), String.valueOf(pair.getValue()));
				
			} catch (ClassCastException e) {
				Log.log("Could not cast value of annotation-attribute: " + name + ", " + pair.getMemberName() + ". \n" +
						"Only string values can be used for annotation-attribute");
			}
		}
	}
	
	public Annotation(String name, Map<String, String> params) {
		super();
		this.name = name;
		this.params = params;
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
		return params.get(name);
	}
	
	public static Map<String, Annotation> getAnnotationsOf(IAnnotatable annotable) throws Exception {
		Map<String, Annotation> annotations = new HashMap<String, Annotation>();
		for(IAnnotation annotation : annotable.getAnnotations()){
			Annotation a = new Annotation(annotation);
			annotations.put(a.getName(), a);
		}		
		return annotations;
	}
	
}
