FROM openjdk:8-jre
MAINTAINER muling@163.com

RUN /bin/cp /usr/share/zoneinfo/Asia/Shanghai /etc/localtime \&& echo 'Asia/Shanghai' >/etc/timezone

# /tmp 目录作为容器数据卷目录，SpringBoot内嵌Tomcat容器默认使用/tmp作为工作目录，任何向 /tmp 中写入的信息不会记录进容器存储层，从而保证容器存储层的无状态化
# 在宿主机的/var/lib/docker目录下创建一个临时文件并把它链接到容器中的/tmp目录
VOLUME /tmp

# 复制jar到镜像
ADD target/muling-global.jar muling-global.jar

COPY agent agent
#ADD jmx_prometheus_javaagent-0.16.1.jar jmx_prometheus_javaagent-0.16.1.jar
#ADD config.yaml config.yaml

#ENTRYPOINT ["java", "-Xmx128m", "-Djava.security.egd=file:/dev/./urandom", "-javaagent:jmx_prometheus_javaagent-0.16.1.jar=8380:config.yaml", "-jar", "/app.jar"]
ENTRYPOINT ["java", "-Xmx512m","-Djava.security.egd=file:/dev/./urandom", "-jar", "/muling-global.jar"]


EXPOSE 9980



