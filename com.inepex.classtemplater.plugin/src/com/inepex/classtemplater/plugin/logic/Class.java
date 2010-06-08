package com.inepex.classtemplater.plugin.logic;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IType;

public class Class {

	String name;
	String packageName;
	List<Attribute> attributes = new ArrayList<Attribute>();
	
	public Class(String name, String packageName) {
		super();
		this.name = name;
		this.packageName = packageName;
	}
	
	public Class(String name, String packageName, List<IField> fields) throws Exception {
		this.name = name;
		this.packageName = packageName;
		for (IField field : fields){
			attributes.add(new Attribute(field));
		}
	}
	
	public Class(ICompilationUnit compunit) throws Exception {
		name = compunit.findPrimaryType().getTypeQualifiedName();
		packageName = compunit.getPackageDeclarations()[0].getElementName();
		attributes = getAttrs(compunit);
	}

	private ArrayList<Attribute> getAttrs(ICompilationUnit unit) throws Exception {
		ArrayList<Attribute> attrs = new ArrayList<Attribute>();
		IType[] allTypes = unit.getAllTypes();
		for (IType type : allTypes) {
			for (IField field : type.getFields()) {
				attrs.add(new Attribute(field));
			}
		}		
		return attrs;
	}
	
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	public List<Attribute> getAttributes() {
		return attributes;
	}

	public void setAttributes(List<Attribute> attributes) {
		this.attributes = attributes;
	}
	
	
	
}
