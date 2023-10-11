# Base openjdk:11
FROM openjdk:11

# Add X-Road Test Client jar to container
ADD src/target/x-road-test-client-*.jar test-client.jar

# Entry with exec
ENTRYPOINT exec java $JAVA_OPTS -jar /test-client.jar