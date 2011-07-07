/*
 * Copyright:
 * 2010 Tibor Somodi, Inepex, Hungary, http://www.inepex.com
 * License:
 * EPL: http://www.eclipse.org/legal/epl-v10.html
 */

package com.inepex.classtemplater.plugin.logic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jdt.core.IAnnotatable;
import org.eclipse.jdt.core.IAnnotation;
import org.eclipse.jdt.core.IMemberValuePair;

import com.inepex.classtemplater.plugin.Log;

public class Annotation {

	String name;
	Map<String, String> params = new HashMap<String, String>();
	Map<String, Object> paramObjects = new HashMap<String, Object>();
	
	public Annotation(IAnnotation jdtAnnotation) throws Exception {
		name = jdtAnnotation.getElementName();
		for (IMemberValuePair pair : jdtAnnotation.getMemberValuePairs()){
			try {
				params.put(pair.getMemberName(), String.valueOf(pair.getValue()));
				paramObjects.put(pair.getMemberName(), pair.getValue());
				
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
	
	/**
	 * 
	 * @param name
	 * @return String.valueOf(param)
	 */
	public String getParamValue(String name){
		return params.get(name);
	}
	
	public List<Annotation> getNestedAnnotationList(String name) throws Exception {
		List<Annotation> nestedAnnotations = new ArrayList<Annotation>();
		
		for (Object annotation : (Object[])paramObjects.get(name)){
			nestedAnnotations.add(new Annotation((IAnnotation)annotation));
		}
		return nestedAnnotations;
	}
	
	public Object getParamObject(String name){
		return paramObjects.get(name);
	}
	
	public Boolean getParamBoolean(String name){
		return (Boolean)paramObjects.get(name);
	}
	
	public Double getParamDouble(String name){
		return (Double)paramObjects.get(name);
	}
	
	public Integer getParamInteger(String name){
		return (Integer)paramObjects.get(name);
	}
	
	public Long getParamLong(String name){
		return (Long)paramObjects.get(name);
	}
	
	public List<String> getParamStringList(String name){
		List<String> stringList = new ArrayList<String>();
		for (Object o : (Object[])paramObjects.get(name)){
			stringList.add((String)o);
		}
		return stringList;
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
