package org.ar.example.proxy.dynamic;

import org.ar.example.proxy.dynamic.Services.ExamService;
import org.ar.example.proxy.dynamic.Services.ExamServiceImpl;
import org.ar.example.proxy.dynamic.proxy.TimerProxy;

public class Main {
	
	/**
	 * Ar Dynamic Demo
	 * 通过io流用程序动态构建类并将其加载到内存之中
	 * @author ArLandlate
	 */
	
	public static void main(String[] args) {
		
		ExamService service = TimerProxy.getInstance().constructProxyAndGotIt(ExamService.class, new ExamServiceImpl());
		service.selectAllExam();
		
	}
	
}
