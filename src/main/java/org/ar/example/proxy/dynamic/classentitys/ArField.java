package org.ar.example.proxy.dynamic.classentitys;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

public class ArField {
	
	/**
	 * Ar Dynamic Demo
	 * custom field
	 * 
	 * 实用正则：
	 * void ([s])et(.*)\((.*\n.*\n)
	 * ArField $1et$2And($3		return this;\n
	 * public.*get.*\n.*\n.*\n.*
	 * public boolean is.*\n.*\n.*\n.*
	 * @author ArLandlate
	 */
	
	/**
	 * fundamentals
	 */
	private ArSignal.Permission permission;
	private ArSignal.BaseType baseType;
	private ArClsUtils.ClassName clazz;
	private String name;
	private ArClsUtils.Code expression;
	private boolean isStatic = false;
	private boolean isFinal = false;
	
	public ArField(ArSignal.Permission permission, Class<?> clazz, String name) {
		this.permission = permission;
		this.clazz = ArClsUtils.newClassNameInstance(clazz);
		this.name = name;
	}
	public ArField(ArSignal.Permission permission, ArSignal.BaseType baseType, String name) {
		if(baseType==ArSignal.BaseType.VOID) {
			try {
				throw new Exception("you can not set a void type in your field");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
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

	public Class<?> getClazz() {
		return clazz.cls;
	}

	public void setClazz(Class<?> clazz) {
		this.baseType = null;
		this.clazz = ArClsUtils.newClassNameInstance(clazz);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public ArClsUtils.Code getExpression() {
		return expression;
	}

	public void setExpression(ArClsUtils.Code expression) {
		if(isFinal) {
			try {
				throw new Exception("Can not change a final field: " + name);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		this.expression = expression;
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
		if(baseType==ArSignal.BaseType.VOID) {
			try {
				throw new Exception("you can not set a void type in your field");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		this.clazz = null;
		this.baseType = baseType;
	}
	
	// setter and
	public ArField setPermissionAnd(ArSignal.Permission permission) {
		this.permission = permission;
		return this;
	}
	
	public ArField setClazzAnd(Class<?> clazz) {
		this.baseType = null;
		this.clazz = ArClsUtils.newClassNameInstance(clazz);
		return this;
	}

	public ArField setNameAnd(String name) {
		this.name = name;
		return this;
	}
	
	public ArField setExpressionAnd(ArClsUtils.Code expression) {
		if(isFinal) {
			try {
				throw new Exception("Can not change a final field: " + name);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		this.expression = expression;
		return this;
	}
	
	public ArField setStaticAnd(boolean isStatic) {
		this.isStatic = isStatic;
		return this;
	}
	
	public ArField setFinalAnd(boolean isFinal) {
		this.isFinal = isFinal;
		return this;
	}
	
	public ArField setBaseTypeAnd(ArSignal.BaseType baseType) {
		if(baseType==ArSignal.BaseType.VOID) {
			try {
				throw new Exception("you can not set a void type in your field");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		this.clazz = null;
		this.baseType = baseType;
		return this;
	}

	/**
	 * methods
	 */
	
	public static ArField newPublicInstance(ArSignal.BaseType baseType, String name) {
		return new ArField(ArSignal.Permission.PUBLIC, baseType, name);
	}
	
	public static ArField newPublicInstance(Class<?> clazz, String name) {
		return new ArField(ArSignal.Permission.PUBLIC, clazz, name);
	}
	
	public static ArField newPrivateInstance(ArSignal.BaseType baseType, String name) {
		return new ArField(ArSignal.Permission.PRIVATE, baseType, name);
	}
	
	public static ArField newPrivateInstance(Class<?> clazz, String name) {
		return new ArField(ArSignal.Permission.PRIVATE, clazz, name);
	}
	
	//检查该属性是否为基本类型
	public boolean isBaseType() {
		return null!=baseType?true:false;
	}
	
	//检查是否为内部类
	public boolean isInnerType() {
		return isBaseType()||!clazz.isInnerClass?false:true;
	}
	
	//该属性是否需要引包
	public Set<String> getImportNeeds(String currentPath) {
		Set<String> ret = new HashSet<String>();
		String tpNeeds = null;
		boolean exHasNeeds = false;
		boolean tpHasNeeds = false;
		ArClsUtils.ClassName[] exNeeds = null;
		
		//表达式引包需求
		if(null!=expression && (exNeeds = expression.importNeeds)!=null) {
			exHasNeeds = null!=exNeeds&&0!=exNeeds.length;
		}
		//属性类型引包需求
		if(!isBaseType()) {
			tpNeeds = ArClsUtils.getImportNeeds(currentPath, clazz);
			tpHasNeeds = StringUtils.isNotBlank(tpNeeds);
		}
		if(exHasNeeds) {
			for (ArClsUtils.ClassName need : exNeeds) {
				String check = ArClsUtils.getImportNeeds(currentPath, need);
				if(null!=check) {
					ret.add(check);
				}
			}
			if(tpHasNeeds) {
				ret.add(tpNeeds);
			}
		}else if(tpHasNeeds) {
			ret = new HashSet<String>();
			ret.add(tpNeeds);
		}
		
		return ret;
//		return isBaseType()		//基本类型不需要引包
//				||clazz.path.startsWith("java.lang.")	//lang包不需要引
//				||currentPath.equals(clazz.path)	//同路径不需要引
//				?null:ArSignal.IMPORT+" "+clazz.outerQualifiedName;	//去掉内部类
	}
	
	@Override
	public String toString() {
		String clsName = isBaseType()?baseType.v:clazz.simpleName;
		return permission.v + " "
				+ (isStatic?(ArSignal.Others.STATIC.v+" "):"")
				+ (isFinal?(ArSignal.Others.FINAL.v+" "):"")
				+ clsName + " "
				+ name
				+ (null!=expression&&expression.contentIsNotBlank()?" = "+expression:"") + ";";
	}
	
}
