package com.h2v.perf.mon;

public class PerfMonThreadLocal {

	private static final ThreadLocal<String> THREAD_LOCAL = new ThreadLocal<String>() {
		protected String initialValue() {
			return "";
		}
	};

	public static void setLabel(String label) {
		THREAD_LOCAL.set(label);
	}

	public static String getLabel() {
		return THREAD_LOCAL.get();
	}

	public static void removeLabel() {
		THREAD_LOCAL.remove();
	}

}
