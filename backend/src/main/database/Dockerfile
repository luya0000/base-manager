FROM jboss/base-jdk:8

MAINTAINER luya

RUN mkdir /data
RUN mkdir /data/docfile

ENV DB_HOST=140.143.141.105
ENV DB_PORT=13306
ENV DB_NAME=jeeplatform
ENV DB_USER=root
ENV DB_PASS=123456
ENV FILE_PATH=/data/docfile

COPY backend-1.0-SNAPSHOT.jar /data/app.jar

EXPOSE 8090

ENTRYPOINT ["java","-jar","/data/app.jar"]
