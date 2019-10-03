package org.ar.example.proxy.dynamic.proxy;

import java.util.ArrayList;

import org.ar.example.proxy.dynamic.classentitys.ArClass;
import org.ar.example.proxy.dynamic.classentitys.ArClsUtils;
import org.ar.example.proxy.dynamic.classentitys.ArField;
import org.ar.example.proxy.dynamic.utils.ArCommonUtils;

public class TimerProxy extends Proxy {
	
	/**
	 * Ar Dynamic Demo
	 * proxy utils
	 * @author ArLandlate
	 */
	
	private enum Repository {
		INSTANCE;
		private final TimerProxy v = new TimerProxy();
	}
	
	private TimerProxy() {
		String logicBeforeCode = "clock.timingStart();";
		String logicAfterCode = "clock.timingStop();";
		logicBefore.setCode(ArClsUtils.newCodeInstance(logicBeforeCode, new ArClsUtils.ClassName[] {ArClsUtils.newClassNameInstance(ArCommonUtils.class)}));
		logicAfter.setCode(ArClsUtils.newCodeInstance(logicAfterCode, new ArClsUtils.ClassName[] {ArClsUtils.newClassNameInstance(ArCommonUtils.class)}));
		importNeeds = new ArrayList<ArClsUtils.ClassName>();
		importNeeds.add(ArClsUtils.newClassNameInstance(ArClsUtils.class));
		importNeeds.add(ArClsUtils.newClassNameInstance(ArCommonUtils.class));
	}

	@Override
	public <T> T constructProxyAndGotIt(Class<T> interfaceClass, T obj) {
		ArClass clazz = constructProxyCode(interfaceClass, this.getClass(), obj);
		
		clazz.addFieldAnd(ArField.newPrivateInstance(ArCommonUtils.Clock.class, "clock").setExpressionAnd(ArClsUtils.newCodeInstance("ArCommonUtils.getAClock()", null)));
		
		return generateProxyClass(clazz, obj);
	}
	
	public static TimerProxy getInstance() {
		return Repository.INSTANCE.v;
	}
	
}
