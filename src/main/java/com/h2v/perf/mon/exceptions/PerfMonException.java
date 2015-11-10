package com.h2v.perf.mon.exceptions;

public class PerfMonException extends RuntimeException {

	private static final long serialVersionUID = -4129636660697327603L;

	public PerfMonException() {
		super();
	}

	public PerfMonException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	public PerfMonException(String arg0) {
		super(arg0);
	}

	public PerfMonException(Throwable arg0) {
		super(arg0);
	}

}
