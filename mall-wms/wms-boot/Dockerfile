FROM openjdk:8-jre
VOLUME /tmp
ADD target/wms-boot.jar mall-wms.jar

COPY agent agent
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/mall-wms.jar"]
EXPOSE 9950
RUN /bin/cp /usr/share/zoneinfo/Asia/Shanghai /etc/localtime \&& echo 'Asia/Shanghai' >/etc/timezone
