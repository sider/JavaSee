FROM openjdk:12

RUN mkdir /work /javasee

COPY . /javasee/
WORKDIR /javasee
RUN ./gradlew shadowjar && \
  mv build/libs/JavaSee-all.jar JavaSee-all.jar && \
  ./gradlew clean && \
  rm -rf ~/.gradle

WORKDIR /work

ENV JAR_PATH=/javasee/JavaSee-all.jar

ENTRYPOINT ["/javasee/scripts/javasee"]
