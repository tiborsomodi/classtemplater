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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;

public class Class {
	static final Pattern getterPattern = Pattern.compile("(get|is)([A-Z])(.*)");
	static final Pattern setterSetter = Pattern.compile("(set)([A-Z])(.*)");

	String name;

	String packageName;

	List<Attribute> attributes = new ArrayList<Attribute>();

	List<Method> methods = new ArrayList<Method>();

	List<Property> properthies;

	Map<String, Annotation> annotations = new HashMap<String, Annotation>();

	String workspaceRelativePath;

	public Class(String name, String packageName) {

		super();
		this.name = name;
		this.packageName = packageName;
	}

	public Class(List<IField> jdtFields) throws Exception {

		this.name = jdtFields.get(0).getDeclaringType().getTypeQualifiedName();
		this.packageName = ((ICompilationUnit) jdtFields.get(0).getParent()
				.getParent()).getPackageDeclarations()[0].getElementName();
		for (IField field : jdtFields) {
			this.attributes.add(new Attribute(field));
		}
		this.annotations = Annotation.getAnnotationsOf(jdtFields.get(0)
				.getDeclaringType(), jdtFields.get(0).getCompilationUnit());
		this.workspaceRelativePath = ResourceUtil
				.getWorkspaceRelativePath((ICompilationUnit) jdtFields.get(0)
						.getParent().getParent());
	}

	public Class(List<IMethod> jdtMethods, boolean isMethods) throws Exception {

		this.name = jdtMethods.get(0).getDeclaringType().getTypeQualifiedName();
		this.packageName = ((ICompilationUnit) jdtMethods.get(0).getParent()
				.getParent()).getPackageDeclarations()[0].getElementName();
		for (IMethod method : jdtMethods) {
			this.methods.add(new Method(method));
		}
		this.annotations = Annotation.getAnnotationsOf(jdtMethods.get(0)
				.getDeclaringType(), jdtMethods.get(0).getCompilationUnit());
		this.workspaceRelativePath = ResourceUtil
				.getWorkspaceRelativePath((ICompilationUnit) jdtMethods.get(0)
						.getParent().getParent());
	}

	public Class(ICompilationUnit compunit) throws Exception {

		this.name = compunit.findPrimaryType().getTypeQualifiedName();
		this.packageName = compunit.getPackageDeclarations()[0]
				.getElementName();
		this.attributes = getAttrs(compunit);
		this.methods = getMethods(compunit);
		this.annotations = Annotation
				.getAnnotationsOf(compunit.getAllTypes()[0], compunit);
		this.workspaceRelativePath = ResourceUtil
				.getWorkspaceRelativePath(compunit);
		
	}

	private void buildProperthies() {
		Map<String, Property> props = new HashMap<String, Property>();

		for (Method method : this.methods) {
			Matcher matcher = getterPattern.matcher(method.name);
			if (matcher.matches()) {
				if (!method.getReturnType().equals("void")) {
					Property prop = getOrCreateProperty(matcher, props);
					prop.setGetter(method);
				}
			} else {
				matcher = setterSetter.matcher(method.name);
				if (matcher.matches() && method.getParameters().size() == 1) {
					Property prop = getOrCreateProperty(matcher, props);
					prop.setSetter(method);
				}
			}
		}
		this.properthies = new ArrayList<Property>(props.values());
	}

	private Property getOrCreateProperty(Matcher matcher,
			Map<String, Property> props) {

		String propertyName = matcher.group(2).toLowerCase()
				.concat(matcher.group(3));
		Property prop = props.get(propertyName);
		if (prop == null) {
			props.put(propertyName, prop = new Property(propertyName));
		}
		return prop;
	}

	private ArrayList<Attribute> getAttrs(ICompilationUnit unit)
			throws Exception {

		ArrayList<Attribute> attrs = new ArrayList<Attribute>();
		IType[] allTypes = unit.getAllTypes();
		for (IType type : allTypes) {
			for (IField field : type.getFields()) {
				attrs.add(new Attribute(field));
			}
		}
		return attrs;
	}

	private ArrayList<Method> getMethods(ICompilationUnit unit)
			throws Exception {

		ArrayList<Method> methods = new ArrayList<Method>();
		IType[] allTypes = unit.getAllTypes();
		for (IType type : allTypes) {
			for (IMethod method : type.getMethods()) {
				methods.add(new Method(method));
			}
		}
		return methods;
	}

	public String getName() {

		return this.name;
	}

	public void setName(String name) {

		this.name = name;
	}

	public String getPackageName() {

		return this.packageName;
	}

	public void setPackageName(String packageName) {

		this.packageName = packageName;
	}

	public List<Attribute> getAttributes() {

		return this.attributes;
	}

	public void setAttributes(List<Attribute> attributes) {

		this.attributes = attributes;
	}

	public Map<String, Annotation> getAnnotations() {

		return this.annotations;
	}

	public void setAnnotations(Map<String, Annotation> annotations) {

		this.annotations = annotations;
	}

	public boolean hasAnnotation(String name) {

		return (this.annotations.get(name) != null);
	}

	public String getWorkspaceRelativePath() {

		return this.workspaceRelativePath;
	}

	public void setWorkspaceRelativePath(String workspaceRelativePath) {

		this.workspaceRelativePath = workspaceRelativePath;
	}

	public List<Method> getMethods() {

		return this.methods;
	}

	public List<Property> getProperties() {
		if (this.properthies == null) {
			buildProperthies();
		}
		return this.properthies;
	}

	public String getParentPackage() {

		int index = getPackageName().lastIndexOf(".");
		if (index != -1) {
			return getPackageName().substring(0, index);
		} else {
			return "";
		}
	}

	/**
	 * returns package without the last level segments
	 * 
	 * @param level
	 * @return
	 */
	public String getParentPackage(int level) {
		String[] parts = getPackageName().split("\\.");
		if (level >= parts.length)
			return "";
		String parentPackage = "";
		for (int i = 0; i < parts.length - level; i++) {
			parentPackage += parts[i] + ".";
		}
		return parentPackage.substring(0, parentPackage.length() - 1);
	}
	

	/**
	 * returns path without the last level segments
	 * 
	 * @param level
	 * @return
	 */
	public String getParentRelativePath(int level) {
		String[] parts = getWorkspaceRelativePath().split("/");
		if (level >= parts.length)
			return "";
		String parentPath = "";
		for (int i = 0; i < parts.length - level; i++) {
			parentPath += parts[i] + "/";
		}
		return parentPath.substring(0, parentPath.length() - 1);
	}
}
