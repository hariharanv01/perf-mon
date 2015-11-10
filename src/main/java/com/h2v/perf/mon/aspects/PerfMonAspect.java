package com.h2v.perf.mon.aspects;

import static com.codahale.metrics.MetricRegistry.name;

import java.util.Collection;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;

import com.codahale.metrics.Counter;
import com.codahale.metrics.Histogram;
import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import com.h2v.perf.mon.PerfMonThreadLocal;
import com.h2v.perf.mon.annotations.PerfHistogram;
import com.h2v.perf.mon.annotations.PerfMeter;
import com.h2v.perf.mon.annotations.PerfTimer;
import com.h2v.perf.mon.mbean.PerfMonSwitchMBeanRegister;

@Aspect
public class PerfMonAspect {

	static {
		PerfMonSwitchMBeanRegister.register();
	}

	private static final MetricRegistry metrics = new MetricRegistry();

	private static boolean on = true;

	@Before("execution(* *(..)) && @annotation(perfMeter)")
	public void meterAdvice(JoinPoint joinPoint, PerfMeter perfMeter) throws Throwable {
		if (on) {
			String name = perfMeter.label().isEmpty() ? joinPoint.getSignature().getDeclaringTypeName() + "."
					+ joinPoint.getSignature().getName() : perfMeter.label();
			Meter meter = metrics.meter(name("Meter", PerfMonThreadLocal.getLabel(), name));
			meter.mark();
		}
	}

	@Before("execution(* *(..)) && (args(*, list)) && @annotation(perfHistogram)")
	public void histogramAdvice(JoinPoint joinPoint, Collection<?> list, PerfHistogram perfHistogram) throws Throwable {
		histAdvice(joinPoint, list, perfHistogram);
	}

	@Before("execution(* *(..)) && (args(list, ..)) && @annotation(perfHistogram)")
	public void histogramAdvice_2(JoinPoint joinPoint, Collection<?> list, PerfHistogram perfHistogram) throws Throwable {
		histAdvice(joinPoint, list, perfHistogram);
	}

	@Before("execution((@com.h2v.perf.mon.annotations.PerfExceptionCounter Exception+).new(..))")
	public void execptionAdvie(JoinPoint joinPoint) {
		if (on) {
			Counter counter = metrics.counter(name("Exception", PerfMonThreadLocal.getLabel(), joinPoint.getThis().getClass()
					.getName()));
			counter.inc();
		}
	}

	@Around("execution(* *(..)) && @annotation(perfTimer)")
	public Object timerAdvice(ProceedingJoinPoint joinPoint, PerfTimer perfTimer) throws Throwable {
		if (on) {
			String name = perfTimer.label().isEmpty() ? joinPoint.getSignature().getDeclaringTypeName() + "."
					+ joinPoint.getSignature().getName() : perfTimer.label();
			return time(joinPoint, name("Timer", PerfMonThreadLocal.getLabel(), name));
		} else {
			return joinPoint.proceed();
		}
	}

	@AfterReturning(pointcut = "execution(* *(..)) && @annotation(perfHistogram)", returning = "list")
	public void histogramAdvice_3(JoinPoint joinPoint, Collection<?> list, PerfHistogram perfHistogram) throws Throwable {
		histAdvice(joinPoint, list, perfHistogram);
	}

	@AfterThrowing(pointcut = "execution(* *(..)) && @annotation(com.h2v.perf.mon.annotations.PerfExceptionCounter)", throwing = "e")
	public void exceptionAdvice(JoinPoint joinPoint, Exception e) throws Throwable {
		if (on) {
			Counter counter = metrics.counter(name("Exception", PerfMonThreadLocal.getLabel(), e.getClass().getName()));
			counter.inc();
		}
	}

	private Object time(ProceedingJoinPoint joinPoint, String name) throws Throwable {
		Timer timer = metrics.timer(name);
		Timer.Context context = timer.time();
		try {
			return joinPoint.proceed();
		} finally {
			context.stop();
		}
	}

	private void histAdvice(JoinPoint joinPoint, Collection<?> list, PerfHistogram perfHistogram) {
		if (on) {
			String name = perfHistogram.label().isEmpty() ? joinPoint.getSignature().getDeclaringTypeName() + "."
					+ joinPoint.getSignature().getName() : perfHistogram.label();
			Histogram histogram = metrics.histogram(name("Hist", PerfMonThreadLocal.getLabel(), name));
			histogram.update(list != null ? list.size() : 0);
		}
	}

	public static MetricRegistry getMetrics() {
		return metrics;
	}

	public static boolean isOn() {
		return on;
	}

	public static void setOn(boolean on) {
		PerfMonAspect.on = on;
	}

}
