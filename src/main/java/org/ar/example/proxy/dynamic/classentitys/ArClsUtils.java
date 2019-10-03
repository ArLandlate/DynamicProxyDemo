package org.ar.example.proxy.dynamic.classentitys;

import org.apache.commons.lang.StringUtils;

public class ArClsUtils {
	
	/**
	 * Ar Dynamic Demo
	 * 工具类
	 * @author ArLandlate
	 */
	
	/**
	 * inner classes
	 */
	//封装一个class用于处理类名路径名
	public static class ClassName {
		public ClassName(Class<?> cls, boolean isArray) {
			this.cls = cls;
			this.isArray = isArray;
			typeName = cls.getTypeName();
			canonicalName = cls.getCanonicalName();
			path = cls.getPackage().getName();
			innerName = cls.getSimpleName();
			
			simpleName = canonicalName.substring(path.length()+1);
			int io = simpleName.indexOf(".");
			outerName = 0<=io?simpleName.substring(0, io):simpleName;
			isInnerClass = simpleName.length() != innerName.length();
			outerQualifiedName = path + "." + outerName;
		}
		public final Class<?> cls;
		public final String typeName;				//com.example.JavaDemo.spring.demo004.classentitys.ArSignal$test$tt
		public final String canonicalName;		//com.example.JavaDemo.spring.demo004.classentitys.ArSignal.test.tt
		public final String simpleName;			//ArSignal.test.tt
		public final String path;						//com.example.JavaDemo.spring.demo004.classentitys
		public final String outerName;			//ArSignal
		public final String outerQualifiedName;			//com.example.JavaDemo.spring.demo004.classentitys.ArSignal
		public final String innerName;			//tt
		public final boolean isInnerClass;		//true
		public final boolean isArray;				//false
	}
	
	//封装一个参数对象
	public static class Parameter {
		public Parameter(ClassName cls, String name) {
			this.cls = cls;
			this.name = name;
		}
		public final ClassName cls;
		public final String name;
		
		@Override
		public String toString() {
			return cls.simpleName + " " + name;
		}
	}
	
	//封装一个代码对象
	public static class Code {
		public Code(String[] content, ClassName[] importNeeds) {
			importNeeds = null==importNeeds?new ClassName[] {}:importNeeds;
			this.content = content;
			this.importNeeds = importNeeds;
		}
		public final String[] content;
		public final ClassName[] importNeeds;
		
		public boolean contentIsNotBlank() {
			return null!=content && 0!=content.length;
		}
		@Override
		public String toString() {
			String NEWLINE = ArSignal.NEWLINE;
			StringBuffer ret = new StringBuffer();
			if(contentIsNotBlank()) {
				for (String line : content) {
					ret.append(line + NEWLINE);
				}
				ret.deleteCharAt(ret.length()-1);
			}
			return ret.toString();
		}
	}
	
	/**
	 * methods
	 */
	// 获得类名对象
	public static ClassName newClassNameInstance(Class<?> cls) {
		return new ClassName(cls, false);
	}
	public static ClassName newClassNameInstance(Class<?> cls, boolean isArray) {
		return new ClassName(cls, isArray);
	}
	public static Parameter newParameterInstance(Class<?> cls, String name) {
		return new Parameter(newClassNameInstance(cls), name);
	}
	public static Parameter newParameterInstance(Class<?> cls, boolean isArray, String name) {
		return new Parameter(newClassNameInstance(cls, isArray), name);
	}
	public static Code newCodeInstance(String content, ClassName[] importNeeds) {
		if(StringUtils.isBlank(content)) {
			return new Code(new String[] {}, importNeeds);
		}
		String[] lines = content.split("\n");
		if(null==lines || 0==lines.length) {
			lines = new String[] {content};
		}
		return new Code(lines, importNeeds);
	}
	
	// 统计引包需求(单包)
	public static String getImportNeeds(String currentPath, ClassName cls) {
		//去除可能的基本类型
		for (ArSignal.BaseType type : ArSignal.BaseType.values()) {
			if(type.v.equals(cls.typeName)) {
				return null;
			}
		}
		return cls.path.startsWith("java.lang")	//lang包不需要引
				||currentPath.equals(cls.path)	//同路径不需要引
				?null:ArSignal.IMPORT+" "+cls.outerQualifiedName+";";	//去掉内部类
	}
	public static String getImportNeeds(String currentPath, String outerQualifiedName) {
		//去除可能的基本类型
		for (ArSignal.BaseType type : ArSignal.BaseType.values()) {
			if(type.v.equals(outerQualifiedName)) {
				return null;
			}
		}
		String path = outerQualifiedName.substring(0, outerQualifiedName.lastIndexOf("."));
		return path.startsWith("java.lang")	//lang包不需要引
				||currentPath.equals(path)	//同路径不需要引
				?null:ArSignal.IMPORT+" "+outerQualifiedName+";";
	}
	
	//判断是否为基本数据类型（是：返回对应枚举对象，否：返回null）
	public static ArSignal.BaseType isBaseType(Class<?> cls) {
		for (ArSignal.BaseType type : ArSignal.BaseType.values()) {
			if(type.v.equals(cls.getName())) {
				return type;
			}
		}
		return null;
	}
	
}
