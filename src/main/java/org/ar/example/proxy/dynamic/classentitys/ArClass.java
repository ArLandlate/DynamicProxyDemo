package org.ar.example.proxy.dynamic.classentitys;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ArClass {
	
	/**
	 * Ar Dynamic Demo
	 * custom class
	 * @author ArLandlate
	 */
	
	/**
	 * fundamentals
	 */
	private ArSignal.Permission permission;
	public static final ArSignal.Type TYPE = ArSignal.Type.CLASS;
	private String name;
	private String path;
	private boolean isStatic = false;
	private boolean isFinal = false;
	private List<ArClsUtils.ClassName> impls = new ArrayList<ArClsUtils.ClassName>();	//实现接口
//	private ArClsUtils.ClassName extd;	// 逻辑留白 继承暂不实现
	private Set<ArClsUtils.ClassName> imports = new HashSet<ArClsUtils.ClassName>();
	private List<ArField> fields = new ArrayList<ArField>();
	private List<ArMethod> methods = new ArrayList<ArMethod>();
	// 逻辑留白  静态代码块、synchronized关键字、内部类，以及接口、枚举、注解、泛型云云暂不实现
	
	public ArClass(String name, String path) {
		permission = ArSignal.Permission.PUBLIC;
		this.name = name;
		this.path = path;
	}
	public ArClass(String name, String path, ArSignal.Permission permission) {
		this.permission = permission;
		this.name = name;
		this.path = path;
	}
	
	public ArSignal.Permission getPermission() {
		return permission;
	}
	public void setPermission(ArSignal.Permission permission) {
		this.permission = permission;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public boolean isStatic() {
		return isStatic;
	}
	public void setStatic(boolean isStatic) {
		this.isStatic = isStatic;
	}
	public boolean isFinal() {
		return isFinal;
	}
	public void setFinal(boolean isFinal) {
		this.isFinal = isFinal;
	}
	public List<ArField> getFields() {
		return fields;
	}
	public void setFields(List<ArField> fields) {
		this.fields = fields;
	}
	public List<ArMethod> getMethods() {
		return methods;
	}
	public void setMethods(List<ArMethod> methods) {
		this.methods = methods;
	}
	public List<ArClsUtils.ClassName> getImpls() {
		return impls;
	}
	public void setImpls(List<ArClsUtils.ClassName> impls) {
		this.impls = impls;
	}

	/**
	 * setter and
	 */
	public ArClass setPermissionAnd(ArSignal.Permission permission) {
		this.permission = permission;
		return this;
	}
	
	public ArClass setNameAnd(String name) {
		this.name = name;
		return this;
	}
	
	public ArClass setPathAnd(String path) {
		this.path = path;
		return this;
	}
	
	public ArClass setStaticAnd(boolean isStatic) {
		this.isStatic = isStatic;
		return this;
	}
	
	public ArClass setFinalAnd(boolean isFinal) {
		this.isFinal = isFinal;
		return this;
	}
	
	public ArClass setFieldsAnd(List<ArField> fields) {
		this.fields = fields;
		return this;
	}
	
	public ArClass setMethodsAnd(List<ArMethod> methods) {
		this.methods = methods;
		return this;
	}
	
	/**
	 * methods
	 */
	// add field
	public ArClass addFieldAnd(ArField field) {
		fields.add(field);
		return this;
	}
	
	// add method
	public ArClass addMethodAnd(ArMethod method) {
		methods.add(method);
		return this;
	}
	
	// add implements
	public ArClass addImplementsAnd(ArClsUtils.ClassName clsn) {
		impls.add(clsn);
		return this;
	}
	public ArClass addImplementsAnd(Class<?> cls) {
		impls.add(ArClsUtils.newClassNameInstance(cls));
		return this;
	}
	
	// add import needs
	public ArClass addImportAnd(Class<?> cls) {
		imports.add(ArClsUtils.newClassNameInstance(cls));
		return this;
	}
	
	// aggregate import needs
	public Set<String> importNeedsAggregate() {
		Set<String> importSet = new HashSet<String>();
		for (ArClsUtils.ClassName clsn : imports) {
			String imp = ArClsUtils.getImportNeeds(path, clsn);
			if(null!=imp) {
				importSet.add(imp);
			}
		}
		for (ArField field : fields) {
			Set<String> set = field.getImportNeeds(path);
			if(null!=set) {
				importSet.addAll(set);
			}
		}
		for (ArMethod method : methods) {
			Set<String> set = method.getImportNeeds(path);
			if(null!=set) {
				importSet.addAll(set);
			}
		}
		for(ArClsUtils.ClassName clsn : impls) {
			String imp = ArClsUtils.getImportNeeds(path, clsn);
			if(null!=imp) {
				importSet.add(imp);
			}
		}
		return importSet;
	}
	
	@Override
	public String toString() {
		final String SEMICOLON = ArSignal.SEMICOLON;
		final String NEWLINE = ArSignal.NEWLINE;
		
		String packageStr = ArSignal.PACKAGE + " " + path + SEMICOLON;
		StringBuffer importStr = new StringBuffer();
		for (String imp : importNeedsAggregate()) {
			importStr.append(imp + NEWLINE);
		}
		
		String declare = permission.v + " " + (isStatic?(ArSignal.Others.STATIC.v+" "):"") + (isFinal?(ArSignal.Others.FINAL.v+" "):"") + TYPE.v + " " + name;
		
		StringBuffer contentStr = new StringBuffer(NEWLINE);
		for (ArField field : fields) {
			contentStr.append(field.toString() + NEWLINE);
		}
		for (ArMethod method : methods) {
			contentStr.append(method.toString() + NEWLINE);
		}
		
		StringBuffer implStr = new StringBuffer();
		if(0!=impls.size()) {
			implStr.append(ArSignal.Others.IMPLEMENTS.v + " ");
			for (ArClsUtils.ClassName clsn : impls) {
				implStr.append(clsn.simpleName + ", ");
			}
			int len = implStr.length();
			implStr.delete(len-2, len-1);
		}
		
		String contentBlock = ArSignal.Block.BRACES.insert(contentStr.toString());
		
		return packageStr + NEWLINE + NEWLINE 
				+ importStr.toString() + NEWLINE + NEWLINE 
				+ declare + " " 
				+ implStr.toString()
				+ contentBlock + NEWLINE;
	}
	
}
