package org.ar.example.proxy.dynamic.Services;

public class ExamServiceImpl implements ExamService {
	
	/**
	 * Ar Dynamic Demo
	 * service example
	 * @author ArLandlate
	 */
	
	public void selectAllExam() {
		System.out.println("execute some program in selectAllExam");
	}

	@Override
	public String t1(Integer[] itg) {
		System.out.println("execute some program in t1");
		return null;
	}

	@Override
	public int t2(String[] str) {
		System.out.println("execute some program in t2");
		return 0;
	}
	
}
