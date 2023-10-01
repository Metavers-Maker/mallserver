package com.muling.common.web.filter;

import com.muling.common.util.BeanUtil;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class AbstractFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

        this.filter((HttpServletRequest) servletRequest, (HttpServletResponse) servletResponse, filterChain);
    }

    @Override
    public void destroy() {

    }

    public void filter(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {

    }

    /**
     * 读取配置参数
     *
     * @param filterConfig
     * @param target
     * @param parameters
     */
    protected void initParams(FilterConfig filterConfig, Object target, String... parameters) {

        for (String parameter : parameters) {
            String value = filterConfig.getInitParameter(parameter);
            if (value != null) {
                BeanUtil.declared.setProperty(target, parameter, value);
            }
        }
    }

    /**
     * 执行过滤器链
     *
     * @param request
     * @param response
     * @param chain
     * @throws IOException
     */
    protected void executeChain(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        chain.doFilter(request, response);
    }
}
