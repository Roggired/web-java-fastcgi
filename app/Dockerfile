FROM openjdk:17

COPY ./fastcgi-lib/build/libs/*.jar /app/
COPY ./app/build/libs/*.jar /app/

CMD ["java", "-DFCGI_PORT=9000", "-jar", "/app/server.jar"]
