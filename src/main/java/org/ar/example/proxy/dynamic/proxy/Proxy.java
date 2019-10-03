package org.ar.example.proxy.dynamic.proxy;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.List;

import javax.tools.JavaCompiler;
import javax.tools.JavaCompiler.CompilationTask;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

import org.ar.example.proxy.dynamic.classentitys.ArClass;
import org.ar.example.proxy.dynamic.classentitys.ArClsUtils;
import org.ar.example.proxy.dynamic.classentitys.ArField;
import org.ar.example.proxy.dynamic.classentitys.ArMethod;
import org.ar.example.proxy.dynamic.classentitys.ArSignal;
import org.ar.example.proxy.dynamic.utils.ArCommonUtils;

public abstract class Proxy {
	
	/**
	 * Ar Dynamic Demo
	 * proxy utils
	 * 这种实现有所不足：代理逻辑是根据静态字符串（logicBefore、logicAfter）编译的，未免太过死板，那么在下一个版本中我们使用用户自定义的Handler动态实现
	 * @author ArLandlate
	 */
	
	protected Proxy() {}
	
	/**
	 * fields
	 */
	protected ArMethod logicBefore = ArMethod.newPrivateInstance(ArSignal.BaseType.VOID, "logicBefore");
	protected ArMethod logicAfter = ArMethod.newPrivateInstance(ArSignal.BaseType.VOID, "logicAfter");
	protected List<ArClsUtils.ClassName> importNeeds;
	
	/**
	 * methods
	 */
	//construct a proxy class
	public abstract <T> T constructProxyAndGotIt(Class<T> interfaceClass, T obj);
	
	protected <T> ArClass constructProxyCode(Class<T> interfaceClass, Class<?> implClass, T obj) {
		Class<?> objClass = obj.getClass();
		String className = objClass.getSimpleName() + implClass.getSimpleName();
		ArClass clazz = new ArClass(className, objClass.getPackage().getName())		//动态创建的代理类被创建在原型所在的路径下
				.addImportAnd(interfaceClass).addImportAnd(objClass)
				.addFieldAnd(ArField.newPrivateInstance(objClass, "protoType"))
				.addMethodAnd(ArMethod.newPublicInstance(ArSignal.BaseType.CONSTRUCTOR, className)
						.setCodeAnd(ArClsUtils.newCodeInstance("this.protoType = protoType;", null))
						.setParameterAnd(ArClsUtils.newParameterInstance(objClass, "protoType")))
				.addMethodAnd(logicBefore).addMethodAnd(logicAfter)
				.addImplementsAnd(interfaceClass);
		
		//遍历接口要实现的方法
		for (Method method : interfaceClass.getMethods()) {
			ArSignal.BaseType retype = ArClsUtils.isBaseType(method.getReturnType());
			ArMethod armethod;
			if(null!=retype) {
				armethod = ArMethod.newPublicInstance(retype, method.getName());
			}else {
				armethod = ArMethod.newPublicInstance(method.getReturnType(), method.getName());
			}
			
			Class<?>[] types = method.getParameterTypes();
			int typelen = types.length;
			ArClsUtils.Parameter[] params = new ArClsUtils.Parameter[typelen];
			StringBuffer invokeParams = new StringBuffer();		//参数调用表：(param1, param2)
			for (int i = 0; i < typelen; i++) {
				Class<?> type = types[i];
				String typeName = type.getName();
				String paramName = "param" + (i+1);
				//处理参数类型为数组的情况
				if(typeName.startsWith("[L")) {
					try {
						type = Class.forName(typeName.substring(2, typeName.length()-1));
					} catch (ClassNotFoundException e) {
						e.printStackTrace();
					}
					params[i] = ArClsUtils.newParameterInstance(type, true, paramName);
				}else {
					params[i] = ArClsUtils.newParameterInstance(type, paramName);
				}
				invokeParams.append(paramName + ", ");
			}
			// 去除末尾的逗号
			if(0<typelen) {
				int len = invokeParams.length();
				invokeParams.delete(len-2, len);
			}
			String returnType =  (armethod.isBaseType()?
					(armethod.getBaseType()==ArSignal.BaseType.VOID?"":armethod.getBaseType().v+" ret = "):
						(null==armethod.getReturnType()||armethod.getReturnType().getName().equals("void")?
								"":ArClsUtils.newClassNameInstance(armethod.getReturnType()).simpleName+" ret = "));
			armethod.setParameterAnd(params)
				.setCode(ArClsUtils.newCodeInstance(
						"logicBefore();\n"
						+ returnType
						+ "protoType."+armethod.getName()+ArSignal.Block.PARENTHESES.insert(invokeParams.toString())+";\n"
						+ "logicAfter();"
						+ (0==returnType.length()?"":"\nreturn ret;"), null));
			
			clazz.addMethodAnd(armethod);
		}
		return clazz;
	}
	
	@SuppressWarnings("unchecked")
	// 利用clazz模板toString，通过io文件流创建.java文件，再编译成.class字节码，通过classloader加载到jvm，反射创建对象，最后把对象return回去
	protected <T> T generateProxyClass(ArClass clazz, T obj) {
		FileWriter fw = null;
		try {
			// .java
			String path = ArCommonUtils.classpath + clazz.getPath().replaceAll("\\.", "/") + "/";
			File javaFile = new File(path + clazz.getName() + ".java");
			fw = new FileWriter(javaFile);
			fw.write(clazz.toString());
			fw.flush();
			
			// .class
            JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();

            StandardJavaFileManager fileMgr = compiler.getStandardFileManager(null, null, null);
            Iterable<? extends JavaFileObject> units = fileMgr.getJavaFileObjects(javaFile);

            CompilationTask t = compiler.getTask(null, fileMgr, null, null, null, units);
            t.call();
            fileMgr.close();
			
			// loading
			Class<?> proxyCls = Class.forName(clazz.getPath() + "." + clazz.getName());
			Constructor<?> proxyConstructor = proxyCls.getConstructor(obj.getClass());
			return (T) proxyConstructor.newInstance(obj);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if(null!=fw) {
					fw.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		return null;
	}
	
}
