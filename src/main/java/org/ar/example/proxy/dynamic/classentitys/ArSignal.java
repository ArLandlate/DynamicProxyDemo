package org.ar.example.proxy.dynamic.classentitys;

import org.apache.commons.lang.StringUtils;

public class ArSignal {
	
	/**
	 * Ar Dynamic Demo
	 * 定义一系列类中要使用的符号
	 * @author ArLandlate
	 */
	
	public static final String NEWLINE = "\n";
	public static final String TAB = "\t";
	public static final String SEMICOLON = ";";
	
	public static final String PACKAGE = "package";
	public static final String IMPORT = "import";
	
	public enum Permission {
		
		PRIVATE("private"),
		PROTECTED("protected"),
		PUBLIC("public");
		
		public final String v;
		
		private Permission(String v) {
			this.v = v;
		}
		
	}
	
	public enum Type {
		
		ENUM("enum"),
		INTERFACE("interface"),
		CLASS("class");
		
		public final String v;
		
		private Type(String v) {
			this.v = v;
		}
		
	}
	
	public enum BaseType {
		
		INT("int"),
		BYTE("byte"),
		LONG("long"),
		SHORT("short"),
		CHAR("char"),
		FLOAT("float"),
		DOUBLE("double"),
		BOOLEAN("boolean"),
		VOID("void"),
		CONSTRUCTOR("");
		
		public final String v;
		
		private BaseType(String v) {
			this.v = v;
		}
		
	}
	
	public enum Block {
		
		BRACES("{", "}"),
		BRACKETS("[", "]"),
		PARENTHESES("(", ")");
		
		public final String b;
		public final String e;
		
		private Block(String b, String e) {
			this.b = b;
			this.e = e;
		}
		
		public String insert(String codes) {
			if(StringUtils.isBlank(codes)) {
				return this.b + this.e;
			}
			if(this!=BRACES) {
				return this.b + codes + this.e;
			}
			String[] lines = codes.split("\n");
			if(null==lines||0==lines.length) {
				return this.b + NEWLINE + TAB + codes + NEWLINE + this.e;
			}
			StringBuffer ret = new StringBuffer();
			for (String line : lines) {
				ret.append(TAB + line + NEWLINE);
			}
			return this.b + NEWLINE + ret + this.e;
		}
		
		public String insert(String[] lines) {
			if(null==lines||0==lines.length) {
				return this.b + this.e;
			}
			StringBuffer ret = new StringBuffer();
			for (String line : lines) {
				ret.append(TAB + line + NEWLINE);
			}
			return this.b + NEWLINE + ret + this.e;
		}
		
	}
	
	public enum Others {
		
		STATIC("static"),
		FINAL("final"),
		
		IMPLEMENTS("implements"),
		EXTENDS("extends");
		
		public final String v;
		
		private Others(String v) {
			this.v = v;
		}
		
	}
		
}
