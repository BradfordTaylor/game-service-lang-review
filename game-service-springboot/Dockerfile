FROM openjdk:8
RUN wget -q https://services.gradle.org/distributions/gradle-3.5.1-bin.zip \
    && unzip gradle-3.5.1-bin.zip -d /opt \
    && rm gradle-3.5.1-bin.zip
ENV GRADLE_HOME /opt/gradle-3.5.1
ENV PATH $PATH:/opt/gradle-3.5.1/bin
ADD . ./
RUN gradle clean build test jar
RUN pwd
RUN ls -l build/libs

FROM openjdk:8-jdk-alpine
VOLUME /tmp
ARG RUN_ENV
ENV RUN_ENV ${RUN_ENV:-test}
COPY --from=0 /build/libs/game-service.jar app.jar
ENTRYPOINT java "-Dspring.profiles.active=$RUN_ENV" -Djava.security.egd=file:/dev/./urandom -jar /app.jar
