# X-Road Test Client

X-Road Test Client is a testing tool and load generator for X-Road 6 and X-Road 7. The implementation is based on the [XRd4J](https://github.com/nordic-institute/xrd4j) library. 

By default Test Client calls `testService` service of [X-Road Test Service](https://github.com/nordic-institute/X-Road-test-service) project according to given parameters that include: 

- message body size
- message attachment size
- response body size
- response attachment size
- number of client threads
- interval between messages
- number of messages to be sent per client
- maximum run time per client.

A random String is used as a payload and the same String is used in all the requests in a single execution. However, unique message ID is automatically generated for each request.   

### Customization

Test Client can be customized and used for calling other services besides X-Road Test Service as well. Test Client can be used as a base or starting point when starting to build a testing tool for X-Road services. [Instructions for customizing](documentation/Customizing-the-test-client.md) Test Client can be found in the documentation.

### Prerequisites

Before using the Test Client, the [X-Road Test Service](https://github.com/nordic-institute/X-Road-test-service) application must be installed on a server and configured as a X-Road service.

The installation instructions for X-Road Test Service can be found at:

https://github.com/nordic-institute/X-Road-test-service#installation

### Try It Out

If you already have access to [X-Road Test Service](https://github.com/nordic-institute/X-Road-test-service)'s `testService` service, the fastest and easiest way to try out the application is to [download](https://github.com/petkivim/x-road-test-client/releases/download/v0.0.8/x-road-test-client-0.0.8.jar) the executable jar version (`x-road-test-client-0.0.8.jar`), copy `settings.properties` and `clients.properties` configuration files in the same directory (specified by the system property `propertiesDirectory`), modify the default configuration (Security Server or X-Road Test Service URL/IP: `settings.properties` => `proxy.url`) and finally run the jar: 

```bash
java -jar -DpropertiesDirectory=/my/custom/path x-road-test-client-0.0.8.jar
```

### Configuration

Test Client has three configuration files: `settings.properties`, `clients.properties` and `log4j.xml`.

By default, Test Client uses the configuration files that are packaged inside the jar file. It's possible to override the default configuration copying one or all the configuration files (`settings.properties`, `clients.properties`, `log4j.xml`) and placing them in the same directory that's specified using the system property `propertiesDirectory`. When the jar file is run, it first looks for the configuration files from the `propertiesDirectory` directory, and for the configuration files that can can not be found it uses the default configuration. For example, it's possible to override `settings.properties` and `clients.properties` placing modified versions in the `propertiesDirectory` directory, but use the default configuration for logging.

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

```properties
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
    <th>Descrition</th>   
  </tr>
  <tr>
    <td>client</td>
    <td>Identifier of the X-Road client that initiates the service call: instance.memberClass.memberId.subsystem</td>
  </tr>
  <tr>
    <td>client.requestBodySize</td>
    <td>Request body character count.</td>
  </tr>
  <tr>
    <td>client.requestAttachmentSize</td>
    <td>Request attachment character count.</td>
  </tr>
  <tr>	
    <td>client.responseBodySize</td>
    <td>Response body character count´.</td>
  </tr>
  <tr>	
    <td>client.responseAttachmentSize</td>
    <td>Response attachment character count. </td>
  </tr>
  <tr>	
    <td>service</td>
    <td>Identifier of the X-Road service that's called : instance.memberClass.memberId.subsystem.service.version</td>
  </tr>
  <tr>	
    <td>service.namespace</td>
    <td>Namespace of the service to be called.</td>
  </tr>  
</table>

**Example**

```properties
# Client ID: instance.memberClass.member.subsystem
client=NIIS-TEST.GOV.123456-7.TestClient
# Request body size (character cnt)
client.requestBodySize=2000
# Request attachment size (character cnt)
client.requestAttachmentSize=0
# Response body size (character cnt)
client.responseBodySize=4000
# Response attachment size (character cnt)
client.responseAttachmentSize=0
# Service ID: instance.memberClass.member.subsystem.service.version
service=NIIS-TEST.GOV.0245437-2.TestService.testService.v1
# Service namespace
service.namespace=http://test.x-road.global/producer
```

### log4j.xml

By default all the output generated by Test Client is printed on console. The default output includes the following information.

* thread id - id number of the thread that produced the output
* message id - id of the message
* throughput - message processing time in milliseconds (processing time = time between sending the request and receiving the response)
* processingTime - processing time that the X-Road Test Service uses for generating response body and response attachment in milliseconds
* successSend - was the message succesfully sent
* successReceive - does the response include SOAP fault

**Example logging in console**

```
10.07.2015 08:19:52 INFO  TestClientLoggerImpl : 1    5f47de76-40f0-49c5-b786-6efed802a80c    218    199    true    true
10.07.2015 08:19:52 INFO  TestClientLoggerImpl : 9    131ed36f-cde9-4eb4-8338-9cc4cc46f654    283    233    true    true
10.07.2015 08:19:52 INFO  TestClientLoggerImpl : 0    ce04fc65-c0b5-43b6-9027-15261cedde7d    272    255    true    true
10.07.2015 08:19:52 INFO  TestClientLoggerImpl : 1    81b61f2d-6018-4ba5-a92a-8db1f2a60d54    221    199    true    true 
```

**Default configuration**

```xml
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
    <logger name="org.niis.xrd4j.common" additivity="false">
        <level value="WARN"/>
        <appender-ref ref="console"/>
    </logger>
    <logger name="org.niis.xrd4j.client" additivity="false">
        <level value="ERROR"/>
        <appender-ref ref="console"/>
    </logger>
    <logger name="java.lang.Runnable" additivity="false">
        <level value="WARN"/>
        <appender-ref ref="console"/>
    </logger>
    <logger name="com.pkrete.xroadtestclient" additivity="false">
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

Test Client uses Maven as the build management tool. Instructions for [building the code](documentation/Building-the-code.md) with Maven and [setting up a development environment](documentation/Setting-up-development-environment.md) can be found in the documentation.

### Docker

You can create a Docker image to run X-Road Test Client inside a container, using the provided Dockerfile.
Before building the image, build the jar file inside `src` directory

```bash
mvn clean install
```
If you have not built the jar, building the Docker image will fail with message
```bash
Step 2 : ADD src/target/x-road-test-client-*.jar test-client.jar
No source files were specified
```

While you are in the project root directory, build the image using the `docker build` command. The `-t` parameter gives your image a tag, so you can run it more easily later. Don’t forget the `.` command, which tells the `docker build` command to look in the current directory for a file called Dockerfile.

```bash
docker build -t x-road-test-client .
```

After building the image, you can run X-Road Test Client:

```bash
docker run --rm x-road-test-client
```

If customized configuration files are used, the host directory containing the configuration files must be mounted as a data directory. In addition, the directory containing the configuration files inside the container must be set using `JAVA_OPTS` and `propertiesDirectory` property.

```bash
docker run --rm -v /host/dir/conf:/my/conf -e "JAVA_OPTS=-DpropertiesDirectory=/my/conf" x-road-test-client
```
