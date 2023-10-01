FROM openjdk:8-jre
VOLUME /tmp
ADD target/bms-boot.jar mall-bms.jar

COPY agent agent
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/mall-bms.jar"]
EXPOSE 9911
RUN /bin/cp /usr/share/zoneinfo/Asia/Shanghai /etc/localtime \&& echo 'Asia/Shanghai' >/etc/timezone
