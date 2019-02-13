############################################################
### Top Scope                                            ###
############################################################

ARG MAVEN_VERSION=3-jdk-8-alpine
ARG JRE_VERSION=8-jre-alpine

############################################################
### Build Container                                      ###
############################################################
FROM maven:${MAVEN_VERSION} AS build

WORKDIR /build

# Pre-cache most Maven dependencies
COPY pom.xml .
RUN mvn dependency:go-offline

# Build the project
COPY . ./
RUN mvn package

############################################################
### Runtime Container                                    ###
############################################################
FROM openjdk:${JRE_VERSION}

COPY --from=build /build/target/access-export-*.jar /app/access-export.jar

WORKDIR /data
VOLUME [ "/data" ]
ENTRYPOINT [ "java", "-jar", "/app/access-export.jar" ]