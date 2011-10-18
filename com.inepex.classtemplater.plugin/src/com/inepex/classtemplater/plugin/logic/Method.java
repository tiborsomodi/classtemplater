package com.inepex.classtemplater.plugin.logic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.jdt.core.Flags;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.JavaModelException;

public class Method {

	String name;
	List<Attribute> parameters = new ArrayList<Attribute>();
	String returnType;
	
	String visibility;
	boolean isPrivate = false;
	boolean isPublic = false;
	boolean isProtected = false;
	boolean isStatic = false;
	boolean isAbstract = false;
	boolean isFinal = false;
	
	Set<Importable> typesInGenerics = new HashSet<Importable>();
	Map<String, Annotation> annotations = new HashMap<String, Annotation>();
	List<String> exceptions = new ArrayList<String>();
	String firstException = "";

	public Method(IMethod method) throws Exception {
		name = method.getElementName();
		
		String visibility = "";
		if (Flags.isPublic(method.getFlags())) visibility = "public";
		else if (Flags.isPrivate(method.getFlags())) visibility = "private";
		else if (Flags.isProtected(method.getFlags())) visibility = "protected";
		else visibility = "public";
		
		this.visibility = visibility;
		setVisibility(visibility);
		isStatic = Flags.isStatic(method.getFlags());
		isAbstract = Flags.isAbstract(method.getFlags());
		isFinal = Flags.isFinal(method.getFlags());
		
		for (int i=0; i<method.getParameterNames().length; i++){
			parameters.add(new Attribute(
					method.getParameterNames()[i]
					, method.getParameterTypes()[i]));
		}
		
		Attribute returnAttr = new Attribute("return", method.getReturnType());
		returnType = returnAttr.getType();
		
		annotations = Annotation.getAnnotationsOf(method, method.getCompilationUnit().getPrimary());
		
		processExceptions(method);
	}
	
	private void processExceptions(IMethod method) {
		try {
			for (String s : method.getExceptionTypes()){
				if (firstException.equals("")) firstException = s.substring(1, s.length()-1);
				exceptions.add(s.substring(1, s.length()-1));
			}
		} catch (JavaModelException e) {
			// TODO: handle exception
		}
	}

	public String getVisibility() {
		return visibility;
	}
	public void setVisibility(String visibility) {
		this.visibility = visibility;
		if (visibility.equals("private")) isPrivate = true;
		else if (visibility.equals("public")) isPublic = true;
		else if (visibility.equals("protected")) isProtected = true;
	}

	public String getName() {
		return name;
	}
	
	public String getNameU1() {
		return StringUtil.getU1(name);
	}
	
	public String getNameL1() {
		return StringUtil.getL1(name);
	}

	public List<Attribute> getParameters() {
		return parameters;
	}

	public String getReturnType() {
		return returnType;
	}

	public boolean isPrivate() {
		return isPrivate;
	}

	public boolean isPublic() {
		return isPublic;
	}

	public boolean isProtected() {
		return isProtected;
	}

	public boolean isStatic() {
		return isStatic;
	}

	public boolean isAbstract() {
		return isAbstract;
	}

	public boolean isFinal() {
		return isFinal;
	}

	public Set<Importable> getTypesInGenerics() {
		return typesInGenerics;
	}

	public Map<String, Annotation> getAnnotations() {
		return annotations;
	}
	
	public boolean hasAnnotation(String name){
		return (annotations.get(name) != null);
	}
	
	public String getAnnotationParamValue(String annotationName, String paramName){
		if (!annotations.containsKey(annotationName))
			return "";
		
		String paramValue = annotations.get(annotationName).getParamValue(paramName);
		
		return paramValue == null ? "" : paramValue;
			
	}

	public List<String> getExceptions() {
		return exceptions;
	}

	public String getFirstException() {
		return firstException;
	}	
	
}
