package com.h2v.perf.mon.reporters.impl;

import java.io.File;
import java.util.concurrent.TimeUnit;

import com.codahale.metrics.CsvReporter;
import com.codahale.metrics.MetricRegistry;
import com.h2v.perf.mon.aspects.PerfMonAspect;
import com.h2v.perf.mon.exceptions.PerfMonException;
import com.h2v.perf.mon.reporters.IPerfMonReporter;

public class CSVReporter implements IPerfMonReporter {

	private MetricRegistry registry = PerfMonAspect.getMetrics();
	private CsvReporter csvReporter;
	private long pollingDuration;
	private TimeUnit pollingDurationTimeUnit;

	public CSVReporter(File csvFilesDirectory, long pollingDuration, TimeUnit pollingDurationTimeUnit) {
		if (!csvFilesDirectory.exists() || csvFilesDirectory.isFile()) {
			throw new PerfMonException("The csvFileDirectory given needs to be a directory, not a normal file and the directory must already exist");
		}
		csvReporter = CsvReporter.forRegistry(registry).build(csvFilesDirectory);
		this.pollingDuration = pollingDuration;
		this.pollingDurationTimeUnit = pollingDurationTimeUnit;
	}

	public void start() {
		csvReporter.start(pollingDuration, pollingDurationTimeUnit);
	}

	public void stop() {
		csvReporter.stop();
	}

}
