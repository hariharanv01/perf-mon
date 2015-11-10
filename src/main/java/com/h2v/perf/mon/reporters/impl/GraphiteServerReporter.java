package com.h2v.perf.mon.reporters.impl;

import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.graphite.Graphite;
import com.codahale.metrics.graphite.GraphiteReporter;
import com.h2v.perf.mon.aspects.PerfMonAspect;
import com.h2v.perf.mon.reporters.IPerfMonReporter;

public class GraphiteServerReporter implements IPerfMonReporter {

	private MetricRegistry registry = PerfMonAspect.getMetrics();
	private GraphiteReporter graphiteReporter;
	private long pollingDuration;
	private TimeUnit pollingDurationTimeUnit;

	public GraphiteServerReporter(InetSocketAddress graphiteSocketAddress, long pollingDuration, TimeUnit pollingDurationTimeUnit,
			String prefix) {
		Graphite graphite = new Graphite(graphiteSocketAddress);
		graphiteReporter = GraphiteReporter.forRegistry(registry).prefixedWith(prefix).build(graphite);
		this.pollingDuration = pollingDuration;
		this.pollingDurationTimeUnit = pollingDurationTimeUnit;
	}

	public GraphiteServerReporter(InetSocketAddress graphiteSocketAddress, long pollingDuration, TimeUnit pollingDurationTimeUnit) {
		Graphite graphite = new Graphite(graphiteSocketAddress);
		graphiteReporter = GraphiteReporter.forRegistry(registry).build(graphite);
		this.pollingDuration = pollingDuration;
		this.pollingDurationTimeUnit = pollingDurationTimeUnit;
	}

	public void start() {
		graphiteReporter.start(pollingDuration, pollingDurationTimeUnit);
	}

	public void stop() {
		graphiteReporter.stop();
	}

}
