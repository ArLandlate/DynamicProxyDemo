package org.ar.example.proxy.dynamic;

import org.ar.example.proxy.dynamic.Services.ExamService;
import org.ar.example.proxy.dynamic.Services.ExamServiceImpl;
//import org.ar.example.proxy.dynamic.proxy.TimerProxy;
import org.ar.example.proxy.dynamic.proxy2.ProxyFactory;

public class Main {
	
	/**
	 * Ar Dynamic Demo
	 * 通过io流用程序动态构建类并将其加载到内存之中
	 * @author ArLandlate
	 */
	public static void main(String[] args) {
		
		// proxy 1.0
//		ExamService service = TimerProxy.getInstance().constructProxyAndGotIt(ExamService.class, new ExamServiceImpl());
//		service.selectAllExam();
		
		// proxy 2.0
		ExamService service = ProxyFactory.getProxy(ExamService.class, new CustomHandler<ExamService>(new ExamServiceImpl()));
		service.selectAllExam();
		
	}
	
}
