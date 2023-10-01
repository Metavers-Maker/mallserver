package com.muling.admin.listener;

import cn.hutool.json.JSONUtil;
import com.muling.admin.pojo.entity.SysLog;
import com.muling.admin.service.ISysLogService;
import com.muling.common.constant.GlobalConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;
import java.time.LocalDateTime;

/**
 * Created by Given on 2022/1/27
 */
@Component
@Slf4j
public class AutoLogListener {

    @Resource
    private ISysLogService sysLogService;

    @RabbitListener(bindings = {
            @QueueBinding(
                    value = @Queue(value = GlobalConstants.MQ_AUTO_LOG_QUEUE, durable = "true"),
                    exchange = @Exchange(value = GlobalConstants.MQ_AUTO_LOG_EXCHANGE),
                    key = GlobalConstants.MQ_AUTO_LOG_KEY
            )
    })
    @RabbitHandler
    public void handleSysLog(String message) throws IOException {
        SysLog msg = JSONUtil.toBean(message, SysLog.class);
        log.info("rabbbitmq listene message:{}", msg);
        sysLogService.save(msg);
    }

    public static void main(String[] args) {
        String message = "{logType:OPERATE,operateType:EDIT}";
        SysLog msg = JSONUtil.toBean(message, SysLog.class);
        System.out.println(msg);
    }
}
