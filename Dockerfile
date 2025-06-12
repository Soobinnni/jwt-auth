FROM openjdk:17-jdk-slim

# 시간대 설정에 필요한 패키지 설치
RUN apt-get update && apt-get install -y tzdata && \
    ln -sf /usr/share/zoneinfo/Asia/Seoul /etc/localtime && \
    echo "Asia/Seoul" > /etc/timezone && \
    apt-get clean

WORKDIR /app
COPY build/libs/*.jar app.jar

ENTRYPOINT ["java", "-Dspring.profiles.active=prod", "-Dlogging.file.name=", "-jar", "app.jar"]