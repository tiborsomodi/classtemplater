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

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IType;

public class Class {

	String name;
	String packageName;
	List<Attribute> attributes = new ArrayList<Attribute>();
	Map<String, Annotation> annotations = new HashMap<String, Annotation>();
	String workspaceRelativePath;
	
	public Class(String name, String packageName) {
		super();
		this.name = name;
		this.packageName = packageName;
	}
	
	public Class(List<IField> jdtFields) throws Exception {
		name = jdtFields.get(0).getDeclaringType().getTypeQualifiedName();
		packageName = ((ICompilationUnit)jdtFields.get(0).getParent().getParent()).getPackageDeclarations()[0].getElementName();
		for (IField field : jdtFields){
			attributes.add(new Attribute(field));
		}
		annotations = Annotation.getAnnotationsOf(jdtFields.get(0).getDeclaringType());
		workspaceRelativePath = getWorkspaceRelatevePath((ICompilationUnit)jdtFields.get(0).getParent().getParent());
	}
	
	public Class(ICompilationUnit compunit) throws Exception {
		name = compunit.findPrimaryType().getTypeQualifiedName();
		packageName = compunit.getPackageDeclarations()[0].getElementName();
		attributes = getAttrs(compunit);
		annotations = Annotation.getAnnotationsOf(compunit.getAllTypes()[0]);
		workspaceRelativePath = getWorkspaceRelatevePath(compunit);
	}
	
	private String getWorkspaceRelatevePath(ICompilationUnit compunit){
		return compunit.getPath().removeLastSegments(1).addTrailingSeparator().toString();
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
	
	public Map<String, Annotation> getAnnotations() {
		return annotations;
	}
	public void setAnnotations(Map<String, Annotation> annotations) {
		this.annotations = annotations;
	}
	public boolean hasAnnotation(String name){
		return (annotations.get(name) != null);
	}

	public String getWorkspaceRelativePath() {
		return workspaceRelativePath;
	}

	public void setWorkspaceRelativePath(String workspaceRelativePath) {
		this.workspaceRelativePath = workspaceRelativePath;
	}
	
	
}
