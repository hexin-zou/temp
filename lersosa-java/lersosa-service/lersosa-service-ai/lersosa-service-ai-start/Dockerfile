FROM bellsoft/liberica-openjdk-alpine:23.0.1-cds

LABEL maintainer="Miraitowa_zcx"

RUN mkdir -p /lersosa/service/ai/logs \
    /lersosa/skywalking/agent

WORKDIR /lersosa/service/ai

ENV SERVER_PORT=9207 JAVA_OPTS=""

EXPOSE ${SERVER_PORT}

ADD ./target/lersosa-service-ai.jar ./app.jar

ENTRYPOINT java -Djava.security.egd=file:/dev/./urandom -Dserver.port=${SERVER_PORT} \
            -Dskywalking.agent.service_name=lersosa-service-ai \
            -javaagent:app.jar="password=Zcx@223852//,/lersosa/skywalking/agent/skywalking-agent.jar" \
            -XX:+HeapDumpOnOutOfMemoryError -XX:+UseZGC ${JAVA_OPTS} \
            -jar app.jar

