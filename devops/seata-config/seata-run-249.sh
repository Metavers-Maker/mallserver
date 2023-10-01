#!/usr/bin/env bash

docker pull registry.cn-shenzhen.aliyuncs.com/meta2link/seata-server:1.4.2

docker run --name seata-server -v /opt/meta2link/seata/config/registry.conf:/seata-server/resources/registry.conf -v /opt/meta2link/seata/logs:/root/logs -d --network host -p 8091:8091 -e SEATA_IP=10.168.168.99 -e SEATA_CONFIG_NAME=file:/seata-server/resources/registry.conf registry.cn-shenzhen.aliyuncs.com/meta2link/seata-server:1.4.2
