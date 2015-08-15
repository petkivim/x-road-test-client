# X-Road Test Client

X-Road Test Client is a testing tool and load generator for X-Road v6.4 and above. By default Test Client calls GetRandom service of [X-Road Adapter Example](https://github.com/petkivim/x-road-adapter-example) project according to given parameters that include: message body size, message attachment size, number of client threads, interval between messages, number of messages to be sent per client and maximum run time per client. However, Test Client can be customized and used for calling other services besides X-Road Adapter Example as well. Test Client can be used as a base or starting point when starting to build a testing tool for X-Road services.

### Prerequisites

Before using the Test Client [X-Road Adapter Example](https://github.com/petkivim/x-road-adapter-example) application must be installed on a server and configured as a X-Road service. X-Road Example Adapter can be downloaded from GitHub:

https://github.com/petkivim/x-road-adapter-example/releases/tag/example-adapter-0.0.3

The installation instructions for X-Road Adapter Example can be found at:

https://github.com/petkivim/x-road-adapter-example#installation

### Configuration

Test Client has three configuration files: ```settings.properties```, ```clients.properties``` and ```log4j.xml```.

By default Test Client uses the configuration files that are packaged inside the jar file. It's possible to override the default configuration copying one or all the configuration files (```settings.properties```, ```clients.properties```, ```log4j.xml```) and placing them in the same directory with the jar file. When the jar file is run it first looks for the configuration files from the working directory, and for the configuration files that can can not be found it uses the default configuration. For example, it's possible to override ```settings.properties``` and ```clients.properties``` placing modified versions in the same directory with the jar file, but use the default configuration for logging.

#### settings.properties

<table>
  <tr>
    <th>Property</th>
    <th>Default value</th>
    <th>Description</th>
  </tr>
  <tr>
    <td>proxy.url</td>
    <td>-</td>
    <td>Security server URL/IP, e.g. http://123.456.78.9/</td>
  </tr>
  <tr>
    <td>thread.executor.count</td>
    <td>10</td>
    <td>
      Number of thread executors. Defines the number of threads that are run in parallel.<br /><br />
      If thread.executor.count == thread.count, all the threads are run in parallel.
    </td>
  </tr>
  <tr>
    <td>thread.count</td>
    <td>10</td>
    <td>Number of threads.</td>
  </tr>
  <tr>
    <td>thread.sleep</td>
    <td>200</td>
    <td>Request interval in milliseconds</td>
  </tr>
  <tr>
    <td>thread.request.count</td>
    <td>25</td>
    <td>
      Number of requests per thread. A single thread runs until thread.request.count OR thread.request.maxtime is     reached.<br /><br />
      If thread.request.count > 0 and thread.request.maxtime == 0, each thread sends the number of requests defined by       thread.request.count without any time limit.<br /><br />
      If thread.request.count > 0 and thread.request.maxtime > 0, each thread runs until it has sent   thread.request.count requests OR thread.request.maxtime is reached.<br /><br />
      If thread.request.count == 0 and thread.request.maxtime > 0, each thread runs until thread.request.maxtime is  reached. Number of sent requests depends on the total run time and request interval.<br /><br />
    </td>
  </tr>
  <tr>
    <td>thread.request.maxtime</td>
    <td>0</td>
    <td>Maximum time in milliseconds that a single thread runs. A single thread runs until thread.request.count OR thread.request.maxtime is reached.</td>
  </tr>
</table>

**Example**

```
# Security server URL/IP
proxy.url=http://123.456.78.9/
# Number of thread executors
thread.executor.count=10
# Number of threads
thread.count=10
# Thread sleep time in milliseconds between requests
thread.sleep=200
# Number of requests per thread
thread.request.count=0
# Maximum time in milliseconds that a single thread runs
thread.request.maxtime=0 
```

#### clients.properties

<table>
  <tr>
    <th>Property</th>
    <th>instance</th>
    <th>memberClass</th>
    <th>member</th>
    <th>subsystem</th>
    <th>body size (character count)</th>
    <th>attachment size (character count</th>
    <th></th>
  </tr>
  <tr>
    <td>client</td>
    <td>instance identifier</td>
    <td>member class</td>
    <td>member code</td>
    <td>subsystem code</td>
    <td>message body character count</td>
    <td>message attachment character count</td>
    <td></td>
  </tr>
  <tr>
    <th>Property</th>
    <th>instance</th>
    <th>memberClass</th>
    <th>member</th>
    <th>subsystem</th>
    <th>service</th>
    <th>version</th>
    <th>namespace</th>
  </tr>    
  <tr>
    <td>service</td>
    <td>instance identifier</td>
    <td>member class</td>
    <td>member code</td>
    <td>subsystem code</td>
    <td>service code</td>
    <td>service version</td>
    <td>namespace of the service</td>
  </tr>
</table>

**Example**

```
# instance | memberClass | member | subsystem | msg body size (character cnt) | msg attachment size (character cnt)
client=FI-DEV63|GOV|0245437-2|TestClient|50000|0
# instance | memberClass | member | subsystem | service | version | namespace
service=FI-DEV63|GOV|0245437-2|TestService|getRandom|v1|http://test.x-road.fi/producer
```

### log4j.xml

By default all the output generated by Test Client is printed on console. The default output includes the following information.

* thread id - id number of the thread that produced the output
* message id - id of the message
* throughput - message processing time in milliseconds (processing time = time between sending the request and receiving the response)
* successSend - was the message succesfully sent
* successReceive - does the response include SOAP fault

**Example logging in console**

```
10.07.2015 08:19:52 INFO  TestClientLoggerImpl : 1    5f47de76-40f0-49c5-b786-6efed802a80c    218    true    true
10.07.2015 08:19:52 INFO  TestClientLoggerImpl : 9    131ed36f-cde9-4eb4-8338-9cc4cc46f654    283    true    true
10.07.2015 08:19:52 INFO  TestClientLoggerImpl : 0    ce04fc65-c0b5-43b6-9027-15261cedde7d    272    true    true
10.07.2015 08:19:52 INFO  TestClientLoggerImpl : 1    81b61f2d-6018-4ba5-a92a-8db1f2a60d54    221    true    true 
```

**Default configuration**

```
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE log4j:configuration SYSTEM "log4j.dtd">
<log4j:configuration xmlns:log4j="http://jakarta.apache.org/log4j/" debug="false">
    <appender name="console" class="org.apache.log4j.ConsoleAppender">
        <param name="Target" value="System.out"/>
        <layout class="org.apache.log4j.PatternLayout">
            <param name="ConversionPattern" value="%d{dd.MM.yyyy HH:mm:ss} %-5p %c{1} : %m%n"/>
        </layout>
    </appender>
    <appender name="file" class="org.apache.log4j.FileAppender">
        <param name="File" value="xrd-servlet.log"/>
        <param name="Append" value="false"/>
        <layout class="org.apache.log4j.PatternLayout">
            <param name="ConversionPattern" value="%m"/>
        </layout>
    </appender>
    <logger name="com.pkrete.xrd4j.common" additivity="false">
        <level value="WARN"/>
        <appender-ref ref="console"/>
    </logger>
    <logger name="com.pkrete.xrd4j.client" additivity="false">
        <level value="WARN"/>
        <appender-ref ref="console"/>
    </logger>
    <logger name="java.lang.Runnable" additivity="false">
        <level value="WARN"/>
        <appender-ref ref="console"/>
    </logger>
    <logger name="com.pkrete.xrd4j.tools.test_client.log" additivity="false">
        <level value="INFO"/>
        <appender-ref ref="console"/>
    </logger>
    <root>
        <priority value="ALL" />
        <appender-ref ref="console" />
    </root>
</log4j:configuration>
```

### Building the code

Test Client uses Maven as the build management tool. In order to build the whole project and generate the jar file (test-client-x.x.x-SNAPSHOT.jar), you must run the maven command below from the ```src``` directory.

```
mvn clean install
```

Running the above maven command generates the war file under the directory presented below:

```
src/target/test-client-x.x.x-SNAPSHOT.jar
```
#### Error on building the code

If running ```mvn clean install``` generates the error presented below, there are two possible solutions.

```
[ERROR] Failed to execute goal on project example-adapter: Could not resolve dependencies for project com.pkrete.xrd4j.tools:example-adapter:war:0.0.1-SNAPSHOT: Failed to collect dependencies at com.pkrete.xrd4j:common:jar:0.0.6: Failed to read artifact descriptor for com.pkrete.xrd4j:common:jar:0.0.6: Could not transfer artifact com.pkrete.xrd4j:common:pom:0.0.6 from/to csc-repo (https://maven.csc.fi/repository/internal/): sun.security.validator.ValidatorException: PKIX path building failed: sun.security.provider.certpath.SunCertPathBuilderException: unable to find valid certification path to requested target -> [Help 1]
```

##### Solution 1

Skip certificate validation:

```
mvn install -Dmaven.wagon.http.ssl.insecure=true -Dmaven.wagon.http.ssl.allowall=true
```

##### Solution 2

Import CSC's Maven repository's certificate as a trusted certificate into ```cacerts``` keystore. See full [instructions](https://github.com/petkivim/x-road-adapter-example/wiki/Import-a-Certificate-as-a-Trusted-Certificate). CSC's Maven repository's URL is ```https://maven.csc.fi```.
