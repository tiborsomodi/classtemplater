package com.inepex.classtemplater.plugin.logic;

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
	
	public Attribute(String name, String type, String visibility,
			boolean isStatic, boolean isAbstract, boolean isFinal) {
		super();
		this.name = name;
		this.type = type;
		setVisibility(visibility);
		this.isStatic = isStatic;
		this.isAbstract = isAbstract;
		this.isFinal = isFinal;
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
		
	
}
