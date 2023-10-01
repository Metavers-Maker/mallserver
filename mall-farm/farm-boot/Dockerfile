FROM openjdk:8-jre
VOLUME /tmp
ADD target/farm-boot.jar mall-farm.jar

COPY agent agent
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/mall-farm.jar"]
EXPOSE 9912
RUN /bin/cp /usr/share/zoneinfo/Asia/Shanghai /etc/localtime \&& echo 'Asia/Shanghai' >/etc/timezone
