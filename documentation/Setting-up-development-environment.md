## Setting up Development Environment

This document describes how a developer's workstation can be setup.

### Software Requirements

* Linux or Windows
* Java 7
* Tomcat 6 or 7 or 8
* Maven 3.x

### Getting the code

There are several of ways to get code, e.g. download it as a [zip](https://github.com/petkivim/x-road-test-client/archive/master.zip) file or clone the git repository.

```
git clone https://github.com/petkivim/x-road-test-client.git
```

The code is located in the ```src``` folder.

### Building the code

Test Client uses Maven as the build management tool. In order to build the whole project and generate the war  file (test-client-x.x.x-SNAPSHOT.jar), you must run the maven command below from the ```src``` directory.

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
[ERROR] Failed to execute goal on project test-client: Could not resolve dependencies for project com.pkrete.xrd4j.tools:test-client:jar:0.0.1-SNAPSHOT: Failed to collect dependencies at org.niis.xrd4j:common:jar:0.3.0: Failed to read artifact descriptor for org.niis.xrd4j:common:jar:0.3.0: Could not transfer artifact org.niis.xrd4j:common:pom:0.3.0 from/to niis-repo (https://artifactory.niis.org/): sun.security.validator.ValidatorException: PKIX path building failed: sun.security.provider.certpath.SunCertPathBuilderException: unable to find valid certification path to requested target -> [Help 1]
```

##### Solution 1

Skip certificate validation:

```
mvn install -Dmaven.wagon.http.ssl.insecure=true -Dmaven.wagon.http.ssl.allowall=true
```

##### Solution 2

Import NIIS's Maven repository's certificate as a trusted certificate into ```cacerts``` keystore. See full [instructions](Import-a-certificate-as-a-trusted-certificate.md). NIIS's Maven release repository's URL is ```https://artifactory.niis.org/xroad-maven-releases```.

### IDE Setup

The project can be imported into different IDEs, but currently this section covers only Netbeans. However, some modifications are required regardless of the IDE that's being used.

#### Netbeans

Opening the project in Netbeans.

* File -> Open Project -> path of the src folder -> Click Open Project button
