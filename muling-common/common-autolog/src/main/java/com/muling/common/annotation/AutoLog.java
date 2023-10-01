package com.muling.common.annotation;

import com.muling.common.enums.LogOperateTypeEnum;
import com.muling.common.enums.LogTypeEnum;

import java.lang.annotation.*;

/**
 * 系统日志注解
 *
 * @Author scott
 * @email jeecgos@163.com
 * @Date 2019年1月14日
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AutoLog {

	/**
	 * 日志内容
	 *
	 * @return
	 */
	String value() default "";

	/**
	 * 日志类型
	 *
	 * @return 0:操作日志;1:登录日志;2:定时任务;
	 */
	LogTypeEnum logType() default LogTypeEnum.OPERATE;

	/**
	 * 操作日志类型
	 *
	 * @return （1查询，2添加，3修改，4删除）
	 */
	LogOperateTypeEnum operateType() default LogOperateTypeEnum.UNKNOWN;
}
