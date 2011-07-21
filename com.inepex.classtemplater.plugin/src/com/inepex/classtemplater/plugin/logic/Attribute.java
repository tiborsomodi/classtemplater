/*
 * Copyright:
 * 2010 Tibor Somodi, Inepex, Hungary, http://www.inepex.com
 * License:
 * EPL: http://www.eclipse.org/legal/epl-v10.html
 */

package com.inepex.classtemplater.plugin.logic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.jdt.core.Flags;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IField;

public class Attribute {

	String name;
	String type;
	String visibility;
	boolean isPrivate = false;
	boolean isPublic = false;
	boolean isProtected = false;
	boolean isStatic = false;
	boolean isAbstract = false;
	boolean isFinal = false;
	boolean isList = false;
	boolean isGeneric = false;
	boolean isEnum = false;
	String fistGenType;
	Set<Importable> typesInGenerics = new HashSet<Importable>();
	List<Importable> typesInGenericsList = new ArrayList<Importable>();	
	Map<String, Annotation> annotations = new HashMap<String, Annotation>();
	private String workspaceRelativePath;
	private String packageName;
	
	
	public Attribute(String name, String type, String visibility,
			boolean isStatic, boolean isAbstract, boolean isFinal, boolean isList, boolean isGeneric
			, boolean isEnum) {
		super();
		this.name = name;
		this.type = type;
		setVisibility(visibility);
		this.isStatic = isStatic;
		this.isAbstract = isAbstract;
		this.isFinal = isFinal;
		this.isList = isList;
		this.isGeneric = isGeneric;
		this.isEnum = isEnum;
	}
	
	public Attribute(IField field) throws Exception {
		String sign = field.getTypeSignature();
		processSignature(sign);
		
		String visibility = "";
		if (Flags.isPublic(field.getFlags())) visibility = "public";
		else if (Flags.isPrivate(field.getFlags())) visibility = "private";
		else if (Flags.isProtected(field.getFlags())) visibility = "protected";
		else visibility = "public";
		
		name = field.getElementName();
		this.visibility = visibility;
		setVisibility(visibility);
		isStatic = Flags.isStatic(field.getFlags());
		isAbstract = Flags.isAbstract(field.getFlags());
		isFinal = Flags.isFinal(field.getFlags());
		
		try {
			String[][] type = field.getDeclaringType().resolveType(field.getTypeSignature().substring(1, field.getTypeSignature().length()-1));
			isEnum = field.getJavaProject().findType(type[0][0] + "." + type[0][1]).isEnum();
		} catch (Exception e) {
			System.out.println("Error at enum check" + e.getMessage());
		}

		//annotations
		annotations = Annotation.getAnnotationsOf(field, field.getCompilationUnit());
		
		workspaceRelativePath = ResourceUtil.getWorkspaceRelativePath(field.getCompilationUnit());
		
		packageName = ((ICompilationUnit)field.getParent().getParent()).getPackageDeclarations()[0].getElementName();
	}
	
	/**
	 * Used for method parameters
	 * @param name
	 * @param signature
	 */
	public Attribute(String name, String signature){
		this.name = name;
		processSignature(signature);
	}
	
	private void processSignature(String signature){
		if (signature.startsWith("Q")) {
			if (signature.indexOf("<") == -1) signature = signature.substring(1, signature.length()-1);
			else {
				////process generic types 
				String basetype = signature.substring(1, signature.indexOf("<"));
				String gentype = signature.substring(signature.indexOf("<") + 1, signature.indexOf(">"));
				String[] parts = gentype.split(";");
				String partString = "";
				for (String s : parts){
					String type = s.substring(1);
					if (typesInGenerics.size() == 0) fistGenType = type;
					typesInGenerics.add(new Importable(type));
					typesInGenericsList.add(new Importable(type));
					partString +=  type + ", ";
				}
				if (typesInGenerics.size() > 0) isGeneric = true;
				partString = partString.substring(0, partString.length() - 2);
				signature = basetype + "<" + partString + ">";				
			}
		}
		else if (signature.equals("I")) signature = "int";
		else if (signature.equals("J")) signature = "long";
		else if (signature.equals("Z")) signature = "boolean";
		else if (signature.equals("D")) signature = "double";
		else if (signature.equals("B")) signature = "byte";
		else if (signature.equals("S")) signature = "short";
		else if (signature.equals("F")) signature = "float";
		else if (signature.equals("C")) signature = "char";
		else if (signature.equals("V")) signature = "void";
		type = signature;
		
		isList = signature.contains("List");
				
	}
	
	public String getName() {
		return name;
	}
	
	public String getNameL1(){
		return StringUtil.getL1(name); 
	}
	
	public String getNameU1(){
		return StringUtil.getU1(name);
	}	
	
	public void setName(String name) {
		this.name = name;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	
	public String getTypeL1(){
		return StringUtil.getL1(type); 
	}
	
	public String getTypeU1(){
		return StringUtil.getU1(type);
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
	public boolean isPrivate() {
		return isPrivate;
	}
	public void setPrivate(boolean isPrivate) {
		this.isPrivate = isPrivate;
	}
	public boolean isPublic() {
		return isPublic;
	}
	public void setPublic(boolean isPublic) {
		this.isPublic = isPublic;
	}
	public boolean isProtected() {
		return isProtected;
	}
	public void setProtected(boolean isProtected) {
		this.isProtected = isProtected;
	}
	public boolean isStatic() {
		return isStatic;
	}
	public void setStatic(boolean isStatic) {
		this.isStatic = isStatic;
	}
	public boolean isAbstract() {
		return isAbstract;
	}
	public void setAbstract(boolean isAbstract) {
		this.isAbstract = isAbstract;
	}
	public boolean isFinal() {
		return isFinal;
	}
	public void setFinal(boolean isFinal) {
		this.isFinal = isFinal;
	}
	
	public boolean isList() {
		return isList;
	}
	public void setList(boolean isList) {
		this.isList = isList;
	}
	
	public Set<Importable> getTypesInGenerics() {
		return typesInGenerics;
	}

	public void setTypesInGenerics(Set<Importable> typesInGenerics) {
		this.typesInGenerics = typesInGenerics;
	}

	public Map<String, Annotation> getAnnotations() {
		return annotations;
	}
	public void setAnnotations(Map<String, Annotation> annotations) {
		this.annotations = annotations;
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
	
	public Annotation getAnnotation(String name){
		return annotations.get(name);
	}

	public String getFistGenType() {
		return fistGenType;
	}
	
	public String getFistGenTypeU1() {
		return StringUtil.getU1(fistGenType);
	}
	
	public String getFistGenTypeL1() {
		return StringUtil.getL1(fistGenType);
	}

	public void setFistGenType(String fistGenType) {
		this.fistGenType = fistGenType;
	}

	public boolean isGeneric() {
		return isGeneric;
	}

	public void setGeneric(boolean isGeneric) {
		this.isGeneric = isGeneric;
	}

	public boolean isEnum() {
		return isEnum;
	}

	public void setEnum(boolean isEnum) {
		this.isEnum = isEnum;
	}

	public String getWorkspaceRelativePath() {
		return workspaceRelativePath;
	}

	public String getPackageName() {
		return packageName;
	}

	/**
	 * 
	 * @return generic types in order of declaration
	 */
	public List<Importable> getTypesInGenericsList() {
		return typesInGenericsList;
	}
	
	
	
}
