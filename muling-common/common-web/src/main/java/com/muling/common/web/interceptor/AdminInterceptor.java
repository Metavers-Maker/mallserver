package com.muling.common.web.interceptor;

import com.muling.common.web.util.UserUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class AdminInterceptor implements HandlerInterceptor {
    Logger log = LoggerFactory.getLogger(AdminInterceptor.class);

    /**
     * 进入controller方法之前
     *
     * @param request
     * @param response
     * @param handler
     * @return
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
                             Object handler) throws Exception {
        log.info("拦截器------------------prehandle");

        //true放行
        //false不放行
        //业务处理判断拦截,后台登录部门ID不可能为空
        Long deptId = UserUtils.getDeptId();
        log.info("用户相关信息：{}.{}", UserUtils.getUserId(), deptId);
        if (deptId == null) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * 方法内部处理完成，页面渲染之前
     *
     * @param request
     * @param response
     * @param handler
     * @param modelAndView
     * @throws Exception
     */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
                           ModelAndView modelAndView) throws Exception {
        log.info("postHandle");
    }

    /**
     * 页面渲染之后
     *
     * @param request
     * @param response
     * @param handler
     * @param ex
     * @throws Exception
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                                Object handler, Exception ex) throws Exception {
        log.info("afterCompletion");

    }

}
