package com.h2v.perf.mon.mbean;

import com.h2v.perf.mon.aspects.PerfMonAspect;

public class PerfMonSwitchMBeanImpl implements PerfMonSwitchMBean {

	@Override
	public void switchOn() {
		PerfMonAspect.setOn(true);
	}

	@Override
	public void switchOff() {
		PerfMonAspect.setOn(false);
	}

}
