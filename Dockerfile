FROM openjdk:14-slim AS builder

COPY . /javasee/
WORKDIR /javasee
RUN ./gradlew shadowjar

FROM openjdk:14-slim

COPY --from=builder /javasee/build/libs/JavaSee-all.jar /javasee/JavaSee-all.jar
COPY --from=builder /javasee/scripts/javasee /javasee/scripts/
COPY --from=builder /javasee/scripts/javasee.bat /javasee/scripts/
COPY --from=builder /javasee/CHANGELOG.md /javasee/
COPY --from=builder /javasee/CREDITS /javasee/
COPY --from=builder /javasee/LICENSE /javasee/
COPY --from=builder /javasee/README.md /javasee/

WORKDIR /work
ENV JAR_PATH /javasee/JavaSee-all.jar
ENV PATH /javasee/scripts:${PATH}

ENTRYPOINT ["javasee"]
