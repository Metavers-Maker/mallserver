FROM openjdk:8-jre
VOLUME /tmp
ADD target/oms-boot.jar mall-oms.jar

COPY agent agent
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/mall-oms.jar"]
EXPOSE 9960
RUN /bin/cp /usr/share/zoneinfo/Asia/Shanghai /etc/localtime \&& echo 'Asia/Shanghai' >/etc/timezone
