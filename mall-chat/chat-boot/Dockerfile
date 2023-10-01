FROM openjdk:8-jre
VOLUME /tmp
ADD target/chat-boot.jar mall-chat.jar

COPY agent agent
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/mall-chat.jar"]
EXPOSE 9940
RUN /bin/cp /usr/share/zoneinfo/Asia/Shanghai /etc/localtime \&& echo 'Asia/Shanghai' >/etc/timezone
