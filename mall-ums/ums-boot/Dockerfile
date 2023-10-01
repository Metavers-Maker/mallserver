FROM openjdk:8-jre
VOLUME /tmp
ADD target/ums-boot.jar mall-ums.jar

COPY agent agent
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom", "-jar","/mall-ums.jar"]
EXPOSE 9930
RUN /bin/cp /usr/share/zoneinfo/Asia/Shanghai /etc/localtime \&& echo 'Asia/Shanghai' >/etc/timezone

