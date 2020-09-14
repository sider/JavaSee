FROM openjdk:14-slim AS builder

COPY . /javasee/
WORKDIR /javasee
RUN ./gradlew assemble && \
    mkdir dist && \
    tar -xvf build/distributions/javasee-shadow-*.tar -C dist --strip-components 1

FROM openjdk:14-slim

COPY --from=builder /javasee/dist/bin /javasee/bin
COPY --from=builder /javasee/dist/lib /javasee/lib
COPY --from=builder /javasee/CREDITS /javasee/
COPY --from=builder /javasee/LICENSE /javasee/
COPY --from=builder /javasee/README.md /javasee/

WORKDIR /work
ENV PATH /javasee/bin:${PATH}

ENTRYPOINT ["javasee"]
