FROM gradle:jre11

COPY . /project
RUN  cd /project && gradle bootJar

#run the spring boot application
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom", "-Dblabla", "-jar","/project/build/libs/ag-grid-server-side--spring-boot--example-0.0.1-SNAPSHOT.jar"]
