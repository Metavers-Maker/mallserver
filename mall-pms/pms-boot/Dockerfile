FROM openjdk:8-jre
VOLUME /tmp
ADD target/pms-boot.jar mall-pms.jar

COPY agent agent
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/mall-pms.jar"]
EXPOSE 9970
#EXPOSE 8370
RUN /bin/cp /usr/share/zoneinfo/Asia/Shanghai /etc/localtime \&& echo 'Asia/Shanghai' >/etc/timezone
