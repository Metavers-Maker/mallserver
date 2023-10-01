package com.muling.common.web.filter;

import com.muling.common.web.access.AccessLog;
import com.muling.common.web.util.ServletUtil;
import com.muling.common.util.StringPool;
import com.muling.common.util.ValidateUtil;
import jodd.util.StringUtil;
import jodd.util.Wildcard;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StreamUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class AccessFilter extends ExcludeWildcardFilter {

    private static Logger logger = LoggerFactory.getLogger(AccessFilter.class.getSimpleName());

    private static final String ACCESS_LOG_NAME = "API_REQUEST";

    protected boolean recordBodyPart = true;

    protected int slowThreshold = 300;

    protected String excludeBodyPart;

    protected String excludeApiCall;

    /**
     * 不需要记录body的请求
     */
    protected String[] excludeBodyPartWildcards;

    /**
     * 不需要记录access log的请求
     */
    protected String[] excludeApiCallWildcards;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        super.init(filterConfig);

        initParams(filterConfig, this, "recordBodyPart", "slowThreshold",  "excludeBodyPart", "excludeApiCall");

        if (StringUtil.isNotEmpty(excludeBodyPart)) {
            excludeBodyPartWildcards = excludeBodyPart.split(StringPool.SEMICOLON);
        }

        if (StringUtil.isNotEmpty(excludeApiCall)) {
            excludeApiCallWildcards = excludeApiCall.split(StringPool.SEMICOLON);
        }
    }

    @Override
    public void filter(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {

        // if we do not need record access log
        if (shouldSkip(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        long start = System.currentTimeMillis();
        AccessLog log = AccessLog.ofDefaults(ACCESS_LOG_NAME, request);

        try {

            // 1M body part
            if (isPlanContentType(request) && request.getContentLength() >= 1048576) {

                // wrap the request for repeat read , if exception occurs use the original
                request = new RepeatReadHttpServletRequest(request);

                logger.warn("[ACCESS] {} has more than 1M content length", log.getUrl());
                return;
            }

            if (shouldRecordBodyPart(request)) {
                try {
                    // wrap the request for repeat read , if exception occurs use the original
                    request = new RepeatReadHttpServletRequest(request);
                    log.setBody(ServletUtil.readRequestBody(request));
                } catch (Exception e) {
                    logger.error("record body part error", e);
                }
            }

            // record the request at first
            handleAccessLog(log);

        } finally {
            filterChain.doFilter(request, response);

            // at last record the access time cross the whole chain
            handleAccessTime(log, System.currentTimeMillis() - start);
        }
    }

    protected String[] skipUrls() {
        return new String[]{
                "/actuator/health",
                "/metrics"
        };
    }

    protected boolean isPlanContentType(HttpServletRequest request) {
        return request.getContentType() != null && (
                request.getContentType().contains(StringPool.JSON) || request.getContentType().contains(StringPool.XML));
    }

    @SuppressWarnings("unused")
    protected boolean shouldRecordBodyPart(HttpServletRequest request) {
        boolean shouldRecord = recordBodyPart && isPlanContentType(request);

        if (shouldRecord && ValidateUtil.isNotEmpty(excludeBodyPartWildcards)) {
            for (String each : excludeBodyPartWildcards) {
                if (Wildcard.match(ServletUtil.getRequestURI(request), each)) {
                    return false;
                }
            }
        }

        return shouldRecord;
    }

    protected void handleAccessLog(AccessLog log) {
        // special case: 健康检查等接口不打印日志，调用太多且无意义,以及其他的一些接口
        if (!skip(log) && !skipApiCall(log)) {
            try {
                String logStr = log.toLogStr();
                logger.info(logStr);
            } finally {
            }
        }
    }

    private boolean skip(AccessLog log) {
        if (log.getUrl() != null && skipUrls() != null) {
            for (String each : skipUrls()) {
                if (each.equals(log.getUrl())) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean skipApiCall(AccessLog log) {
        if (log.getUrl() != null && excludeApiCallWildcards != null) {
            for (String each : excludeApiCallWildcards) {
                if (each.equals(log.getUrl())) {
                    return true;
                }
            }
        }
        return false;
    }

    protected void handleAccessTime(AccessLog log, long elapsed) {

        try {
            if (elapsed > slowThreshold) {
                // special case: 对于健康检查接口耗时比较久的，也打印一下WARN日志
                if (elapsed >= 1000) {
                    logger.warn("[ACCESS-VERY-SLOW] {} cost {}", log.getUrl(), elapsed);
                } else {
                    logger.warn("[ACCESS-SLOW] {} cost {}", log.getUrl(), elapsed);
                }
            } else {
                // special case: 健康检查等接口不打印日志，调用太多且无意义
                if (!skip(log)) {
                    logger.info("[ACCESS] {} cost {}", log.getUrl(), elapsed);
                }
            }

        } finally {
        }
    }

    public static class RepeatReadHttpServletRequest extends HttpServletRequestWrapper {

        private byte[] requestBody = null;

        public RepeatReadHttpServletRequest(HttpServletRequest request) {

            super(request);

            try {
                requestBody = StreamUtils.copyToByteArray(request.getInputStream());
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
            }
        }

        @Override
        public ServletInputStream getInputStream() throws IOException {
            if (requestBody == null) {
                requestBody = new byte[0];
            }
            final ByteArrayInputStream input = new ByteArrayInputStream(requestBody);
            return new ServletInputStream() {

                @Override
                public boolean isFinished() {
                    return true;
                }

                @Override
                public boolean isReady() {
                    return false;
                }

                @Override
                public void setReadListener(ReadListener listener) {
                    // empty
                }

                @Override
                public int read() throws IOException {
                    return input.read();
                }
            };
        }

        @Override
        public BufferedReader getReader() throws IOException {
            return new BufferedReader(new InputStreamReader(getInputStream(), StandardCharsets.UTF_8));
        }
    }
}
