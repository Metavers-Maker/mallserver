package com.muling.common.web.access;

import com.muling.common.util.QueryStringUtil;
import com.muling.common.util.StringPool;
import com.muling.common.util.ValidateUtil;
import com.muling.common.web.util.ServletUtil;
import jodd.json.meta.JSON;
import jodd.util.StringUtil;

import javax.servlet.ServletRequest;
import java.util.HashMap;
import java.util.Map;

import static jodd.util.StringPool.EMPTY;
import static jodd.util.StringPool.NEWLINE;

public class AccessLog {

    private static final int MAX_BODY_LENGTH = 2000;
    /**
     * log 名称
     */
    private String name;

    /**
     * http method
     */
    private String method;

    /**
     * 调用的url
     */
    private String url;

    /**
     * 调用者ip
     */
    private String ip;

    /**
     * 调用参数
     */
    private String params;

    /**
     * body部分
     */
    private String body;

    /**
     * 用户代理
     */
    private String userAgent;

    /**
     * 访问时间点
     */
    private long accessDate;

    /**
     * 当前登录用户
     */
    private Object userId;

    /**
     * 其他属性
     */
    private Map attrs = new HashMap();

    public String getName() {
        return name;
    }

    public AccessLog setName(String name) {
        this.name = name;
        return this;
    }

    public String getMethod() {
        return method;
    }

    public AccessLog setMethod(String method) {
        this.method = method;
        return this;
    }

    public String getUrl() {
        return url;
    }

    public AccessLog setUrl(String url) {
        this.url = url;
        return this;
    }

    public String getIp() {
        return ip;
    }

    public AccessLog setIp(String ip) {
        this.ip = ip;
        return this;
    }

    public String getParams() {
        return params;
    }

    public AccessLog setParams(String params) {
        this.params = params;
        return this;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public AccessLog setUserAgent(String userAgent) {
        this.userAgent = userAgent;
        return this;
    }

    public long getAccessDate() {
        return accessDate;
    }

    public AccessLog setAccessDate(long accessDate) {
        this.accessDate = accessDate;
        return this;
    }

    public Map getAttrs() {
        return attrs;
    }

    public AccessLog setAttrs(Map attrs) {
        this.attrs = attrs;
        return this;
    }

    public Object getUserId() {
        return userId;
    }

    public AccessLog setUserId(Object userId) {
        this.userId = userId;
        return this;
    }

    public AccessLog addAttr(String key, Object value) {
        this.attrs.put(key, value);
        return this;
    }

    public String getBody() {
        return body;
    }

    public AccessLog setBody(String body) {
        this.body = body;
        return this;
    }

    @JSON(include = false)
    public String toLogStr() {

        StringBuilder sb = new StringBuilder();
        // url
        sb.append("url: ").append(method).append(StringPool.SPACE).append(url).append(NEWLINE);

        if (ValidateUtil.isNotEmpty(params)) sb.append("params : ").append(params).append(NEWLINE);

        // ip
        sb.append("ip: ").append(ip).append(NEWLINE);

        // body
        if (ValidateUtil.isNotEmpty(body)) {
            sb.append("body: ").append(body).append(NEWLINE);
        }

        if (StringUtil.isEmpty(sb.toString())) return EMPTY;

        StringBuilder result = new StringBuilder();

        result.append(NEWLINE).append("--[").append(name).append("]--").append(NEWLINE);
        result.append(sb.toString());
        result.append("--[/").append(name).append("]--").append(NEWLINE);

        return result.toString();
    }


    //---------------------------static-------------------------

    public static AccessLog ofDefaults(String name, ServletRequest request) {
        return new AccessLog().setName(name)
                .setUrl(ServletUtil.getRequestURI(request))
                .setParams(QueryStringUtil.getQueryString(request.getParameterMap()))
                .setMethod(ServletUtil.getMethod(request))
                .setUserAgent(ServletUtil.getRequest(request).getHeader(Headers.USER_AGENT))
                .setAccessDate(System.currentTimeMillis())
                .addAttr(Headers.AUTHORIZATION, ServletUtil.getHeader(Headers.AUTHORIZATION))
                .addAttr(Headers.ACCEPT_ENCODING, ServletUtil.getHeader(Headers.ACCEPT_ENCODING))
                .addAttr(Headers.TOKEN_FIELD, ServletUtil.getHeader(Headers.TOKEN_FIELD))
                .addAttr(Headers.X_FROM, ServletUtil.getHeader(Headers.X_FROM))
                .addAttr(Headers.CONTENT_TYPE, ServletUtil.getHeader(Headers.CONTENT_TYPE))
                .addAttr(Headers.ETAG, ServletUtil.getHeader(Headers.ETAG))
                .addAttr(Headers.CONTENT_MD5, ServletUtil.getHeader(Headers.CONTENT_MD5))
                .addAttr(Headers.X_Forwarded_For, ServletUtil.getHeader(Headers.X_Forwarded_For))
                .addAttr(Headers.X_Y_REQUEST_ID, ServletUtil.getHeader(Headers.X_Y_REQUEST_ID))
                .addAttr(Headers.X_Y_SESSION_ID, ServletUtil.getHeader(Headers.X_Y_SESSION_ID))
                .setIp(ServletUtil.getClientIP());
    }
}
