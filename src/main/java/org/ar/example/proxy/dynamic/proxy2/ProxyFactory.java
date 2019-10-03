package org.ar.example.proxy.dynamic.proxy2;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

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

public class ProxyFactory {
	
	/**
	 * Ar Dynamic Demo
	 * proxy factory 2.0
	 * @author ArLandlate
	 */
	
	private ProxyFactory() {}
	
	// 同一实现的代理对象singleton
	private final Map<String, Object> proxyMap = new HashMap<String, Object>();
	
	private enum Repository {
		INSTANCE;
		private final ProxyFactory factory = new ProxyFactory();
	}
	
	/**
	 * fields
	 */
	
	/**
	 * methods
	 */
	//construct a proxy class
	@SuppressWarnings("unchecked")
	public static  <T> T getProxy(Class<T> interfaceClass, InvocationHandler<T> handler) {
		
		ProxyFactory factory = Repository.INSTANCE.factory;
		T target = handler.getTarget();
		Class<?> targetClass = target.getClass();
		String className = targetClass.getSimpleName() + handler.getClass().getSimpleName();
		Object ret = factory.proxyMap.get(className);
		if(null!=ret) {
			return (T) ret;
		}
		
		//first invocation
		ArClass clazz = factory.constructProxyCode(interfaceClass, handler, className, target);
		ret = factory.generateProxyClass(clazz, handler);
		factory.proxyMap.put(className, ret);
		
		return (T) ret;
		
	}
	
	private <T> ArClass constructProxyCode(Class<T> interfaceClass, InvocationHandler<T> handler, String className, T target) {
		Class<?> targetClass = target.getClass();
		ArClass clazz = new ArClass(className, targetClass.getPackage().getName())		//动态创建的代理类被创建在原型所在的路径下
				.addImportAnd(interfaceClass).addImportAnd(targetClass).addImportAnd(Method.class)
				.addFieldAnd(ArField.newPrivateInstance(InvocationHandler.class, "handler").setClazzGenericAnd(interfaceClass))
				.addMethodAnd(ArMethod.newPublicInstance(ArSignal.BaseType.CONSTRUCTOR, className)
						.setCodeAnd(ArClsUtils.newCodeInstance("this.handler = handler;", null))
						.setParameterAnd(ArClsUtils.newParameterInstance(InvocationHandler.class, "handler").addGenericAnd(interfaceClass)))
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
			StringBuffer invokeParams = new StringBuffer();		//参数调用表：(param1, param2...)
			StringBuffer invokeTypes = new StringBuffer();		//参数类型表：(int, String...)
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
				invokeTypes.append(", " + params[i].cls.simpleName + ".class");
			}
			// 去除末尾的逗号
			if(0<typelen) {
				int len = invokeParams.length();
				invokeParams.delete(len-2, len);
			}else {
				invokeParams = new StringBuffer();
			}
			String returnType =  (armethod.isBaseType()?
					(armethod.getBaseType()==ArSignal.BaseType.VOID?"":"return ("+armethod.getBaseType().v+") "):
						(null==armethod.getReturnType()||armethod.getReturnType().getName().equals("void")?
								"":"return ("+armethod.getReturnTypeClassName().simpleName+") "));
			String methodCode = "Method method = handler.getTarget().getClass().getMethod(\""+armethod.getName()+"\""+invokeTypes.toString()+");\n"
					+"Object[] args = {"+invokeParams.toString()+"};\n"
					+returnType+"handler."+"invoke"+ArSignal.Block.PARENTHESES.insert("method, args")+";";
			String catchReturn = (0==returnType.length()?"":("\nreturn " + (armethod.isBaseType()?armethod.getBaseType().defaultVal + ";":"null;")));
			methodCode = "try " + ArSignal.Block.BRACES.insert(methodCode) + "\n catch (Exception e) " 
					+ ArSignal.Block.BRACES.insert("e.printStackTrace();"+catchReturn);
			armethod.setParameterAnd(params)
				.setCode(ArClsUtils.newCodeInstance(methodCode, null));
			
			clazz.addMethodAnd(armethod);
		}
		return clazz;
	}
	
	@SuppressWarnings("unchecked")
	// 利用clazz模板toString，通过io文件流创建.java文件，再编译成.class字节码，通过classloader加载到jvm，反射创建对象，最后把对象return回去
	private <T> T generateProxyClass(ArClass clazz, T handler) {
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
			Constructor<?> proxyConstructor = proxyCls.getConstructor(InvocationHandler.class);
			return (T) proxyConstructor.newInstance(handler);
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
