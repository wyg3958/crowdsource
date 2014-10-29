FROM java:8
MAINTAINER asideas

EXPOSE 8080

ADD ${project.build.finalName}.jar /srv/

ENTRYPOINT java -jar /srv/${project.build.finalName}.jar
