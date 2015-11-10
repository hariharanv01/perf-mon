[![Build Status](https://travis-ci.org/hariharanv01/perf-mon.svg)](https://travis-ci.org/hariharanv01/perf-mon.svg)


# Perf-Mon

### About
Performance Monitor(perf-mon) is a Metrics API library based framework that can be used in Java applications to record and monitor performance. The framework comes with a host of annotations and functionalities that can be very easily bound to any java application. It's a wrapper over Metrics api library to get rid of all the boiler plate code just using annotations most of the time. It also provides additional functionalities for tracking performance recordings.
 
### How easy is it to use perf-mon?
Add the perf-mon dependency to your maven POM, and a maven aspectj plugin to your POM. Once that's done, all you need to do is add the required perf-mon annotations to the methods that you want to monitor.
 
Integrating your application with perf-mon
```xml
<!-- 1. Add the perf-mon dependency -->
<!-- 2. Add it inside build.plugins node in pom.xml, the below snippet -->
<plugin>
    <groupId>org.codehaus.mojo</groupId>
    <artifactId>aspectj-maven-plugin</artifactId>
    <version>1.7</version>
    <configuration>
        <complianceLevel>1.6</complianceLevel>
        <source>1.6</source>
        <target>1.6</target>
        <aspectLibraries>
            <aspectLibrary>
                <groupId>com.h2v</groupId>
                <artifactId>perf-mon</artifactId>
            </aspectLibrary>
        </aspectLibraries>
    </configuration>
    <executions>
        <execution>
            <goals>
                <goal>compile</goal>
                <goal>test-compile</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```

### Measuring the Metrics

To measure the performance metrics, annotate the methods with the appropriate annotations.
There are four types of metrics annotations that are currently available (we will discuss in details about these later):
* @PerfMeter 
* @PerfTimer
* @PerfExceptionCounter
* @PerfHistogram

The below will measure the performance metrics for the doSomething() method. A method can be annotated with more than one perf-mon annotations.
```java
@PerfTimer
public void doSomething() {
    //do something
}
```
 
### Monitoring the measured metrics

Now that we have measured the metrics, we need to monitor it. The perf-mon framework provides four reporters to report the metrics to. The reporter interface - IPerfMonReporter has four implementations
```java
com.h2v.perf.mon.reporters.impl.JMXReporter                //The metrics are available to the registered JMX Managed bean
com.h2v.perf.mon.reporters.impl.SLF4JReporter              //The metrics are logged using a SLF4J logger
com.h2v.perf.mon.reporters.impl.CSVReporter                //The metrics are persisted in a CSV file
com.h2v.perf.mon.reporters.impl.GraphiteServerReporter     //The metrics are pushed to a Graphite server
```
These reporters can be registered at server startup.
For eg. If it's a web application whose performance needs to be monitored, we can create instance(s) of the IPerfMonReporter  in a ServletContextListener instance and register this listener in web.xml
```java
public class PerfMonitorContextListener implements ServletContextListener {
    private JMXReporter jmxReporter;
    @Override
    public void contextDestroyed(ServletContextEvent arg0) {
        jmxReporter.stop();
    }
    @Override
    public void contextInitialized(ServletContextEvent arg0) {
        jmxReporter = new JMXReporter();
        jmxReporter.start();
    }
}
``` 

### Perf-mon Annotations

Let's discuss in brief about the perf-mon annotations.
 
1. @PerfTimer

    This annotation is used to measure the time-performance metrics for any     method. By default, the method name along with the fully qualified class name is used as the label for this metrics. eg. com.MyClass.mymethod. This annotation has the following properties.
    
    Property | Description
    :---------:|:-------------------
    label |	By default the label for this metrics is the fully qualified class name with the method name. If you want to override this behavior you can use this property

    ```java
    @PerfTimer(label = "mymethod")
    public void doSomething() {
        // do something
    }
    ```

2. @PerfMeter

    This annotation is used to measure the rate of invocation of methods over time(i.e. invocation of methods per unit time). By default, the method name along with the fully qualified class name is used as the label for this metrics. eg. com.MyClass.mymethod. This annotation has the following properties.

    Property | Description
    :---------:|:-------------------
    label |	By default the label for this metrics is the fully qualified class name with the method name. If you want to override this behavior you can use this property
 
 
3. @PerfHistogram

    
    This annotation is used to measure the statistical distribution of values in a stream of data. e.g. Distribution of list of elements returned from the DB. By default, the method name along with the fully qualified class name is used as the label for this metrics. eg. com.MyClass.mymethod. 
    
    This annotation can only be used in methods that has a java.util.Collection as the first or last argument in the method definition or as the return type. Value for null collections will be considered zero.
    
    This annotation has the following properties.

    Property | Description
    :---------:|:-------------------
    label |	By default the label for this metrics is the fully qualified class name with the method name. If you want to override this behavior you can use this property
 
 
4. @PerfExceptionCounter

    This annotation is used to count the number of times an exception is thrown. This can be used to get the statistics on various exceptions that the system is subjected to. By default, the fully qualified exception class name is used as the label for this metrics. eg. com.MyException.
    
    This annotation if used on methods will measure the number of times an Exception is thrown from that method itself or from any of the nested method calls inside that method.
This annotation can be used on Exception classes as well(annotation at Type definition). When used like this, whenever an instance of this exception class is created the metrics are measured
 
### Dynamically change label and Adding context info to track requests

There may be scenarios where we want to track the request when measuring and monitoring performance. This can be easily done by using the PerfMonThreadLocal class. You can put label into this class during the entry method and all subsequent perf-mon annotated method calls invoked by the same thread will have whatever is put in the PerfMonThreadLocal label as the prefix.

```java
class MyClass {
 
    public static void main(String[] args) {
        new MyClass().doSomething()
    }
 
    @PerfTimer(label = "firstmethod")
    public void doSomething() {
        try {
            PerfMonThreadLocal.setLabel("Children");
            doOneMoreThing();
            doOneAdditionalThing()
        } finally {
            PerfMonThreadLocal.removeLabel();
        }
    }
 
    @PerfTimer(label = "secondmethod")
    private void doOneMoreThing() {
    }
  
    @PerfTimer
    private void doOneAdditionalThing() {
    }
 
}
```

The above method call will record three metrics - one for each method with the following labels
* firstmethod
* Children.secondmethod
* Children.MyClass.doOneAdditionalThing

The PerfMonThreadLocal can be used to dynamically change the label for metrics, which are otherwise not possible. For, eg. Let's say we want to measure the rate at which an HTTP API/URL is invoked. We can create a Servlet Filter as below and register the same in web.xml

```java
public class PerfInterceptor implements Filter {
 
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }
 
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        try {
            PerfMonThreadLocal.setLabel(((HttpServletRequest) request).getPathInfo());
            execute(request, response, chain);
        } finally {
            PerfMonThreadLocal.removeLabel();
        }
    }
    
    @PerfMeter(label = "")
    private void execute(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        chain.doFilter(request, response);
    }
 
    @Override
    public void destroy() {
    }
}
```

### Performance of perf-mon

Since perf-mon framework uses compile time aspect weaving, no reflection or proxy based invocations are done. Hence latency introduced by perf-mon is negligible.
 
### Switching off/on Perf-mon dynamically

Perf-mon can be switched on/off dynamically(the default is ON). The  switch is accessible as a JMX Managed bean operation.

Bean name is ```com.h2v.perf.mon.mbean:type=PerfMonSwitchMBeanImpl```. 

The bean can be accessed through JConsole or any other JMX client.
The managed operations - switchOn and switchOff are used for respective purposes.

### JMX recording

The metrics statistics are available in as JMX bean named "metrics".
