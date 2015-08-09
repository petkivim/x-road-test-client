# X-Road Test Client

X-Road Test Client is a testing tool and load generator for X-Road v6.4 and above. By default Test Client calls GetRandom service of [X-Road Adapter Example](https://github.com/petkivim/x-road-adapter-example) project according to given parameters that include: message body size, message attachment size, number of client threads, interval between messages, number of messages to be sent per client and maximum run time per client. However, Test Client can be customized and used for calling other services besides X-Road Adapter Example as well. Test Client can be used as a base or starting point when starting to build a testing tool for X-Road services.

### Prerequisites

Before using the Test Client [X-Road Adapter Example](https://github.com/petkivim/x-road-adapter-example) application must be installed on a server and configured as a X-Road service. X-Road Example Adapter can be downloaded from GitHub:

https://github.com/petkivim/x-road-adapter-example/releases/tag/example-adapter-0.0.3

The installation instructions for X-Road Adapter Example can be found at:

https://github.com/petkivim/x-road-adapter-example#installation

### Configuration

Test Client has three configuration files: ```settings.properties```, ```clients.properties``` and ```log4j.xml```.

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
    <td>Security server URL/IP, e.g. http://193.166.25.53/</td>
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
