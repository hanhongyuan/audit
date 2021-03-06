package com.jn.audit.spring.webmvc;

import com.jn.audit.core.AuditRequest;
import com.jn.audit.core.Auditor;
import com.jn.audit.core.model.OperationResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

public class AuditHttpHandlerInterceptor implements HandlerInterceptor {

    @Autowired
    private Auditor<HttpServletRequest, Method> auditor;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            Method method = handlerMethod.getMethod();
            auditor.startAudit(request, method);
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        if (handler instanceof HandlerMethod) {
            AuditRequest<HttpServletRequest, Method> wrappedRequest = Auditor.auditRequestHolder.get();
            if (wrappedRequest != null) {
                wrappedRequest.setResult(ex == null ? OperationResult.SUCCESS : OperationResult.FAIL);
                auditor.finishAudit(wrappedRequest);
            }
        }
    }
}
