FROM java:8

WORKDIR /usr/src/app

COPY gradle/wrapper ./gradle/wrapper/
COPY gradlew ./
COPY build.gradle ./

COPY src ./src/
COPY buildSrc ./buildSrc/

COPY codenarc.groovy ./
COPY bin/ensure_app_started.sh ./bin/

COPY infrastructure/configuration.yml ./infrastructure/

RUN ./gradlew shadowJar

EXPOSE 8080
EXPOSE 8081

ENTRYPOINT ["./gradlew"]
CMD [ "runFinalJar" ]