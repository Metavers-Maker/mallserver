package com.muling.common.web.aspect;//package com.muling.common.web.request;

import cn.hutool.core.util.StrUtil;
import com.muling.common.exception.BizException;
import com.muling.common.web.annotation.RequestLimit;
import com.muling.common.web.util.MemberUtils;
import com.muling.common.web.util.RequestUtils;
import com.muling.common.web.util.UserUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Aspect
@Component
@Slf4j
@RequiredArgsConstructor
public class RequestLimitAspect {

    private final StringRedisTemplate redisTemplate;

    @Around("within(@org.springframework.web.bind.annotation.RestController *) && @annotation(limit)")
    public Object requestLimit(final ProceedingJoinPoint joinPoint, RequestLimit limit) throws Throwable {

        Object result = null;
        try {
            log.debug("进入请求限制切面");

            Map<String, Object> maps = getFieldsName(joinPoint);

            RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
            ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) requestAttributes;
            HttpServletRequest request = servletRequestAttributes.getRequest();

//            String url = request.getRequestURL().toString();
            String uri = request.getRequestURI();

            RequestLimit.LimitFiledType limitFiledType = limit.limitFiledType();
            String limitFiled = limitFiledType + "";
            String field = "";
            if (RequestLimit.LimitFiledType.USER_ID == limitFiledType) {
                field = UserUtils.getUserId() + "";
            } else if (RequestLimit.LimitFiledType.MEMBER_ID == limitFiledType) {
                field = MemberUtils.getMemberId() + "";
            } else if (RequestLimit.LimitFiledType.DEVICE_ID == limitFiledType) {
                field = RequestUtils.getDeviceId();
            } else if (RequestLimit.LimitFiledType.PLATFORM == limitFiledType) {
                field = RequestUtils.getPlatform();
            } else if (RequestLimit.LimitFiledType.IP == limitFiledType) {
                field = RequestUtils.getIp();
            } else if (RequestLimit.LimitFiledType.CUSTOM_VALUE == limitFiledType) {
                field = String.valueOf(maps.get(limit.field()));
            }

            String key = "web-limit:";
            if (StringUtils.isNotBlank(field)) {
                key = key.concat(uri).concat(":").concat(StrUtil.swapCase(limitFiled)).concat(":")
                        .concat(field);
            } else {
                key = key.concat(uri).concat(":").concat(limitFiled);
            }

            log.debug("限制切面Key:{}", key);
            limit(limit, uri, limitFiled, field, key);
            log.debug("结束请求限制切面");

            result = joinPoint.proceed(joinPoint.getArgs());

        } catch (InvocationTargetException e) {
            throw e.getCause();
        } catch (BizException e) {
            throw e;
        } catch (Exception e) {
            log.error("发生异常: ", e);
            throw e;
        }
        return result;
    }

    private void limit(RequestLimit limit, String uri, String limitFiled, String field, String key) throws BizException {
        //频率，单位为秒/次，默认值为0，不限制。
        long waits = limit.waits();
        //时间段，单位为秒，默认值60秒
        int time = limit.time();
        if (waits > 0) {
            String frequencyKey = key + "_frequency";
            boolean hasKey = redisTemplate.hasKey(frequencyKey);
            if (hasKey) {
                String info = limitFiled + "[" + field + "]访问地址[" + uri + "]超过了访问频率[" + limit.waits() + "秒/次]";
                log.info(info);
                throw new BizException(limit.code());
            } else {
                redisTemplate.opsForValue().set(frequencyKey, waits + "", Duration.ofSeconds(waits));
            }
        }

        long count = redisTemplate.opsForValue().increment(key);
        if (count == 1) {
            redisTemplate.expire(key, Duration.ofSeconds(time));
        }

        if (count > limit.count()) {
            String info = limitFiled + "[" + field + "]访问地址[" + uri + "]超过了限定的次数[" + limit.count() + "]";
            log.info(info);
            throw new BizException(limit.code());
        }
    }


    /**
     * 通过反射机制 获取被切参数名以及参数值
     *
     * @param joinPoint
     * @return
     */
    protected Map<String, Object> getFieldsName(JoinPoint joinPoint) {

        Signature signature = joinPoint.getSignature();
        MethodSignature methodSignature = (MethodSignature) signature;
        Method targetMethod = methodSignature.getMethod();

        Object[] args = joinPoint.getArgs();
        return getFieldsName(targetMethod, args);
    }

    /**
     * 通过反射机制 获取被切参数名以及参数值
     *
     * @param targetMethod
     * @param args
     * @return
     */
    private Map<String, Object> getFieldsName(Method targetMethod, Object[] args) {

        LocalVariableTableParameterNameDiscoverer u = new LocalVariableTableParameterNameDiscoverer();
        String[] params = u.getParameterNames(targetMethod);
        Map<String, Object> maps = new HashMap();
        for (int i = 0; i < args.length; i++) {
            Object value = args[i];
            if (value instanceof HttpServletRequest || value instanceof HttpServletResponse) {
                continue;
            }
            maps.put(params[i], args[i]);
        }
        return maps;
    }
}
