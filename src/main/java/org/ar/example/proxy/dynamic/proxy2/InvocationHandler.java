package org.ar.example.proxy.dynamic.proxy2;

import java.lang.reflect.Method;

public abstract class InvocationHandler<T> {
	
	/**
	 * Ar Dynamic Demo
	 * declare invoke method
	 * implement this interface to define your own handler
	 * @author ArLandlate
	 */
	
	//proxy target object
	protected final T target;
	
	public InvocationHandler(T target) {
		this.target = target;
	}
	
	public T getTarget() {
		return target;
	}
	
	public abstract Object invoke(Method method, Object... args) throws Exception;
	
}
