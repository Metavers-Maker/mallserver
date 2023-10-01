package com.muling.common.web.aspect;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.muling.common.annotation.AutoLog;
import com.muling.common.constant.GlobalConstants;
import com.muling.common.enums.LogOperateTypeEnum;
import com.muling.common.enums.LogTypeEnum;
import com.muling.common.web.util.IPUtils;
import com.muling.common.web.util.UserUtils;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.Date;


/**
 * 系统日志，切面处理类
 */
@Slf4j
@Aspect
@Component
public class AutoLogAspect {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Pointcut("@annotation(com.muling.common.annotation.AutoLog)")
    public void logPointCut() {

    }

    @Around("logPointCut()")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        log.info("==========enter log aspect==============");
        long beginTime = System.currentTimeMillis();
        //执行方法
        Object result = point.proceed();
        //执行时长(毫秒)
        long time = System.currentTimeMillis() - beginTime;
        //保存日志
        saveSysLog(point, time);

        return result;
    }

    private void saveSysLog(ProceedingJoinPoint joinPoint, long time) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();

        JSONObject sysLog = JSONUtil.createObj();
        AutoLog syslog = method.getAnnotation(AutoLog.class);
        if (syslog != null) {
            //注解上的描述,操作日志内容
            sysLog.set("logContent", syslog.value());
            sysLog.set("logType", syslog.logType());

        }

        //请求的方法名
        String className = joinPoint.getTarget().getClass().getName();
        String methodName = signature.getName();
        sysLog.set("method", className + "." + methodName + "()");


        //设置操作类型
        if (syslog.logType() == LogTypeEnum.OPERATE) {
            sysLog.set("operateType", syslog.operateType());
        }

        //获取request
        HttpServletRequest request = ((ServletRequestAttributes)
                RequestContextHolder.getRequestAttributes()).getRequest();
        //请求的参数
        sysLog.set("requestParam", getRequestParams(request, joinPoint));

        //请求URL
        sysLog.set("requestUrl", request.getRequestURL());

        //请求类型
        sysLog.set("requestType", request.getMethod());

        //设置IP地址
        sysLog.set("ip", IPUtils.getIpAddr(request));

        //获取登录用户信息
        String username = UserUtils.getUsername();
        sysLog.set("userid", UserUtils.getUserId());
        sysLog.set("username", username);
        sysLog.set("createBy", username);

        //耗时
        sysLog.set("costTime", time);
        sysLog.set("created", new Date());
        //保存系统日志
        String str = sysLog.toString();

        rabbitTemplate.convertAndSend(GlobalConstants.MQ_AUTO_LOG_QUEUE, str);
        log.info("auto log to rabbitmq.=========" + str);
    }

    /**
     * 获取操作类型
     */
    private LogOperateTypeEnum getOperateType(String methodName, LogOperateTypeEnum operateType) {
        if (operateType != null) {
            return operateType;
        } else {
            return LogOperateTypeEnum.UNKNOWN;
        }
    }

    /**
     * @param request:   request
     * @param joinPoint: joinPoint
     * @Description: 获取请求参数
     * @author: scott
     * @date: 2020/4/16 0:10
     * @Return: java.lang.String
     */
    private String getRequestParams(HttpServletRequest request, JoinPoint joinPoint) {
        String httpMethod = request.getMethod();
        String params = "";
        if ("POST".equals(httpMethod) || "PUT".equals(httpMethod) || "PATCH".equals(httpMethod)) {
            Object[] paramsArray = joinPoint.getArgs();
            params = JSONUtil.toJsonStr(paramsArray);
        } else {
            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            Method method = signature.getMethod();
            // 请求的方法参数值
            Object[] args = joinPoint.getArgs();
            // 请求的方法参数名称
            LocalVariableTableParameterNameDiscoverer u = new LocalVariableTableParameterNameDiscoverer();
            String[] paramNames = u.getParameterNames(method);
            if (args != null && paramNames != null) {
                for (int i = 0; i < args.length; i++) {
                    params += "  " + paramNames[i] + ": " + args[i];
                }
            }
        }
        return params;
    }
}
