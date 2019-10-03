package org.ar.example.proxy.dynamic;

import java.lang.reflect.Method;

import org.ar.example.proxy.dynamic.proxy2.InvocationHandler;
import org.ar.example.proxy.dynamic.utils.ArCommonUtils;

public class CustomHandler<T> extends InvocationHandler<T> {

	/**
	 * Ar Dynamic Demo
	 * custom handler implement
	 * @author ArLandlate
	 */
	
	public CustomHandler(T target) {
		super(target);
	}
	
	@Override
	public Object invoke(Method method, Object... args) throws Exception {
		ArCommonUtils.Clock clock = ArCommonUtils.getAClock();
		clock.timingStart();
		Object ret = method.invoke(target, args);
		clock.timingStop();
		return ret;
	}
	
}
