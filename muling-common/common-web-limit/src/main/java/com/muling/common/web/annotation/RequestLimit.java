package com.muling.common.web.annotation;

import com.muling.common.result.ResultCode;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
// 最高优先级
@Order(Ordered.HIGHEST_PRECEDENCE)
public @interface RequestLimit {
    /**
     * 允许访问的次数，默认值MAX_VALUE
     */
    int count() default Integer.MAX_VALUE;

    /**
     * 时间段，单位为秒，默认值60秒
     */
    int time() default 60;

    /**
     * 频率，单位为秒/次，默认值为0，不限制。
     */
    int waits() default 0;

    LimitMode limitMode() default LimitMode.BEFORE;

    /**
     * 限制字段
     *
     * @return
     */
    String field() default "";

    /**
     * 返回提示信息
     *
     * @return
     */
    ResultCode code() default ResultCode.REQUEST_TOO_FAST;

    LimitFiledType limitFiledType() default LimitFiledType.MEMBER_ID;

    public enum LimitFiledType {
        USER_ID, MEMBER_ID, DEVICE_ID, PLATFORM, IP, CUSTOM_VALUE
    }

    ;

    public enum LimitMode {
        BEFORE, AROUND
    }

    ;
}
