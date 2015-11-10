package com.h2v.perf.mon.reporters.impl;

import javax.management.MBeanServer;

import com.codahale.metrics.JmxReporter;
import com.codahale.metrics.MetricRegistry;
import com.h2v.perf.mon.aspects.PerfMonAspect;
import com.h2v.perf.mon.reporters.IPerfMonReporter;

public class JMXReporter implements IPerfMonReporter {

	private MetricRegistry registry = PerfMonAspect.getMetrics();
	private JmxReporter jmxReporter;

	public JMXReporter() {
		this.jmxReporter = JmxReporter.forRegistry(registry).build();
	}

	/*
	 * public JMXReporter(TimeUnit timeUnit) { this();
	 * this.jmxReporterBuilder.convertDurationsTo
	 * (timeUnit).convertRatesTo(timeUnit); }
	 */

	public JMXReporter(MBeanServer mBeanServer) {
		this.jmxReporter = JmxReporter.forRegistry(registry).registerWith(mBeanServer).build();
	}

	/*
	 * public JMXReporter(MBeanServer mBeanServer, TimeUnit timeUnit) {
	 * this(timeUnit); this.jmxReporterBuilder.registerWith(mBeanServer); }
	 */
	public void start() {
		jmxReporter.start();
	}

	public void stop() {
		jmxReporter.stop();
	}

}
