package com.inepex.classtemplater.plugin.logic;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.AnnotationTypeMemberDeclaration;
import org.eclipse.jdt.core.dom.ArrayInitializer;
import org.eclipse.jdt.core.dom.BooleanLiteral;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.PrimitiveType;
import org.eclipse.jdt.core.dom.PrimitiveType.Code;
import org.eclipse.jdt.core.dom.StringLiteral;

public class AnnotationASTVisitor extends ASTVisitor {

	Map<String, Object> defaultValueObjects = new HashMap<String, Object>();
	
	@Override
	public boolean visit(AnnotationTypeMemberDeclaration node) {
		String name = node.getName().getIdentifier();
		Expression defaultValue = node.getDefault();
		defaultValueObjects.put(name, getDefaultValueObject(node));
		return super.visit(node);
	}
	
	public Map<String, Object> getDefaultValueObjects() {
		return defaultValueObjects;
	}
	
	private Object getDefaultValueObject(AnnotationTypeMemberDeclaration decl){
		if (decl.getDefault() == null) return null;
		if (decl.getType().isPrimitiveType()){
			Code primTypeCode = ((PrimitiveType)decl.getType()).getPrimitiveTypeCode();
			if (primTypeCode == PrimitiveType.BOOLEAN){
				return ((BooleanLiteral)decl.getDefault()).booleanValue();
			} else if (primTypeCode == PrimitiveType.INT){
				return Integer.parseInt(decl.getDefault().toString());
			} else if (primTypeCode == PrimitiveType.DOUBLE){
				return Double.parseDouble(decl.getDefault().toString());
			} else if (primTypeCode == PrimitiveType.LONG){
				return Long.parseLong(decl.getDefault().toString());
			}  else if (primTypeCode == PrimitiveType.FLOAT){
				return Float.parseFloat(decl.getDefault().toString());
			} else {
				return null;
			}
		} else if (decl.getType().isArrayType()) {
			return ((ArrayInitializer)(decl.getDefault())).expressions().toArray();
		} else if (decl.getType().isSimpleType()){
			if (decl.getDefault() instanceof StringLiteral){
				return ((StringLiteral)decl.getDefault()).getLiteralValue();
			} else return decl.getDefault().toString();
		} else return null;
	}
	
	
}
