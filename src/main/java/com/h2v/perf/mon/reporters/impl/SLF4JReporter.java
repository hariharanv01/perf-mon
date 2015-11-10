package com.h2v.perf.mon.reporters.impl;

import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Slf4jReporter;
import com.codahale.metrics.Slf4jReporter.LoggingLevel;
import com.h2v.perf.mon.aspects.PerfMonAspect;
import com.h2v.perf.mon.reporters.IPerfMonReporter;

public class SLF4JReporter implements IPerfMonReporter {

	private MetricRegistry registry = PerfMonAspect.getMetrics();
	private Slf4jReporter slf4jReporter;
	private long pollingDuration;
	private TimeUnit pollingDurationTimeUnit;

	public SLF4JReporter(Logger logger, long pollingDuration, TimeUnit pollingDurationTimeUnit) {
		slf4jReporter = Slf4jReporter.forRegistry(registry).outputTo(logger).withLoggingLevel(LoggingLevel.INFO).build();
		this.pollingDuration = pollingDuration;
		this.pollingDurationTimeUnit = pollingDurationTimeUnit;
	}

	public void start() {
		slf4jReporter.start(pollingDuration, pollingDurationTimeUnit);
	}

	public void stop() {
		slf4jReporter.stop();
	}

}
