package com.h2v.perf.mon.mbean;

import javax.management.MXBean;

@MXBean
public interface PerfMonSwitchMBean {

	public void switchOn();

	public void switchOff();

}
