FROM openjdk:8-jre
VOLUME /tmp
ADD target/task-boot.jar mall-task.jar

COPY agent agent
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/mall-task.jar"]
EXPOSE 9914
RUN /bin/cp /usr/share/zoneinfo/Asia/Shanghai /etc/localtime \&& echo 'Asia/Shanghai' >/etc/timezone
