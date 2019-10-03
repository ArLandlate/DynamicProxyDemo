package org.ar.example.proxy.dynamic.classentitys;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

public class ArMethod {
	
	/**
	 * Ar Dynamic Demo
	 * custom method
	 * @author ArLandlate
	 */
	
	/**
	 * fundamentals
	 */
	private ArSignal.Permission permission;
	private ArSignal.BaseType baseType;
	private ArClsUtils.ClassName returnType;
	private String name;
	private ArClsUtils.Parameter[] parameters;
	private ArClsUtils.Code code;
	private boolean isStatic = false;
	private boolean isFinal = false;
	
	public ArMethod(ArSignal.Permission permission, String name) {
		this.permission = permission;
		this.name = name;
	}
	public ArMethod(ArSignal.Permission permission, Class<?> returnType, String name) {
		this.permission = permission;
		this.returnType = ArClsUtils.newClassNameInstance(returnType);
		this.name = name;
	}
	public ArMethod(ArSignal.Permission permission, ArSignal.BaseType baseType, String name) {
		this.permission = permission;
		this.baseType = baseType;
		this.name = name;
	}

	public ArSignal.Permission getPermission() {
		return permission;
	}

	public void setPermission(ArSignal.Permission permission) {
		this.permission = permission;
	}

	public Class<?> getReturnType() {
		return returnType.cls;
	}

	public void setReturnType(Class<?> returnType) {
		this.baseType = null;
		this.returnType = ArClsUtils.newClassNameInstance(returnType);
	}

	public ArClsUtils.Code getCode() {
		return code;
	}
	
	public void setCode(ArClsUtils.Code code) {
		this.code = code;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public ArClsUtils.Parameter[] getParameters() {
		return parameters;
	}

	public void setParameters(ArClsUtils.Parameter... parameters) {
		this.parameters = parameters;
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
	
	public ArSignal.BaseType getBaseType() {
		return baseType;
	}
	
	public void setBaseType(ArSignal.BaseType baseType) {
		this.returnType = null;
		this.baseType = baseType;
	}
	
	// setter and
	public ArMethod setPermissionAnd(ArSignal.Permission permission) {
		this.permission = permission;
		return this;
	}
	
	public ArMethod setReturnTypeAnd(Class<?> returnType) {
		this.baseType = null;
		this.returnType = ArClsUtils.newClassNameInstance(returnType);
		return this;
	}

	public ArMethod setNameAnd(String name) {
		this.name = name;
		return this;
	}
	
	public ArMethod setParameterAnd(ArClsUtils.Parameter... parameters) {
		this.parameters = parameters;
		return this;
	}
	
	public ArMethod setStaticAnd(boolean isStatic) {
		this.isStatic = isStatic;
		return this;
	}
	
	public ArMethod setFinalAnd(boolean isFinal) {
		this.isFinal = isFinal;
		return this;
	}
	
	public ArMethod setBaseTypeAnd(ArSignal.BaseType baseType) {
		this.returnType = null;
		this.baseType = baseType;
		return this;
	}
	
	public ArMethod setCodeAnd(ArClsUtils.Code code) {
		this.code = code;
		return this;
	}

	/**
	 * methods
	 */
	
	public static ArMethod newPublicInstance(ArSignal.BaseType baseType, String name) {
		return new ArMethod(ArSignal.Permission.PUBLIC, baseType, name);
	}
	
	public static ArMethod newPublicInstance(Class<?> returnType, String name) {
		return new ArMethod(ArSignal.Permission.PUBLIC, returnType, name);
	}
	
	public static ArMethod newPrivateInstance(ArSignal.BaseType baseType, String name) {
		return new ArMethod(ArSignal.Permission.PRIVATE, baseType, name);
	}
	
	public static ArMethod newPrivateInstance(Class<?> returnType, String name) {
		return new ArMethod(ArSignal.Permission.PRIVATE, returnType, name);
	}
	
	// 检查该属性是否为基本类型
	public boolean isBaseType() {
		return null!=baseType?true:false;
	}
	
	// 检查是否为内部类
	public boolean isInnerType() {
		return isBaseType()||!returnType.isInnerClass?false:true;
	}
	
	// 该方法是否需要引包
	public Set<String> getImportNeeds(String currentPath) {
		Set<String> ret = new HashSet<String>();
		
		//表达式引包需求
		ArClsUtils.ClassName[] exNeeds = null!=code?code.importNeeds:null;
		if(null!=exNeeds) {
			for (ArClsUtils.ClassName need : exNeeds) {
				String check = ArClsUtils.getImportNeeds(currentPath, need);
				if(null!=check) {
					ret.add(check);
				}
			}
		}
		//返回类型引包需求
		if(!isBaseType()&&null!=returnType) {
			String tpNeed = ArClsUtils.getImportNeeds(currentPath, returnType);
			if(StringUtils.isNotBlank(tpNeed)) {
				ret.add(tpNeed);
			}
		}
		//参数引包需求
		if(null!=parameters) {
			for (ArClsUtils.Parameter pm : parameters) {
				String pmNeed = ArClsUtils.getImportNeeds(currentPath, pm.cls);
				if(null!=pmNeed) {
					ret.add(pmNeed);
				}
			}
		}
		
		if(0==ret.size()) {
			return null;
		}
		
		return ret;
	}
	
	public String getParameterString() {
		StringBuffer pmStr = new StringBuffer("");
		if(null!=parameters) {
			for (int i = 0; i < parameters.length; i++) {
				ArClsUtils.Parameter pm = parameters[i];
				pmStr.append(pm.cls.simpleName);
				pmStr.append(pm.cls.isArray?"[] ":" ");
				pmStr.append(pm.name);
				pmStr.append(i!=(parameters.length-1)?", ":"");
			}
		}
		return pmStr.toString();
	}
	
	@Override
	public String toString() {
		String rtClsName = isBaseType()?(baseType.v + (baseType!=ArSignal.BaseType.CONSTRUCTOR?" ":"")):(null==returnType?"":returnType.simpleName + " ");
		return permission.v + " "
				+ (isStatic?(ArSignal.Others.STATIC.v+" "):"")
				+ (isFinal?(ArSignal.Others.FINAL.v+" "):"")
				+ rtClsName
				+ name
				+ ArSignal.Block.PARENTHESES.insert(getParameterString()) + " "
				+ ArSignal.Block.BRACES.insert(null!=code?code.content:null);
	}
	
}
