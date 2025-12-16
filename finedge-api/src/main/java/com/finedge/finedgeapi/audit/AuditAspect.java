package com.finedge.finedgeapi.audit;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.finedge.finedgeapi.service.AuditService;
import com.finedge.finedgeapi.entity.AuditLog;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.time.LocalDateTime;

import static org.hibernate.internal.util.StringHelper.truncate;

@Aspect
@Component
public class AuditAspect {

    private final AuditService auditService;
    private final ObjectMapper objectMapper;

    public AuditAspect(AuditService auditService, ObjectMapper objectMapper) {
        this.auditService = auditService;
        this.objectMapper = objectMapper;
    }

    @Around("@annotation(com.finedge.finedgeapi.audit.Audit)")
    public Object auditMethod(ProceedingJoinPoint pjp) throws Throwable {
        MethodSignature sig = (MethodSignature) pjp.getSignature();
        Method method = sig.getMethod();
        Audit audit = method.getAnnotation(Audit.class);

        String username = "anonymous";
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated()) {
            username = auth.getName();
        }

        AuditLog log = new AuditLog();
        log.setAction(audit.action());
        log.setUsername(username);
        log.setCreatedAt(LocalDateTime.now());

        if (audit.logRequest()) {
            try {
                Object[] args = pjp.getArgs();
                String request = objectMapper.writeValueAsString(args);
                log.setRequestPayload(truncate(request, 2000));
            } catch (Exception e) {
                log.setRequestPayload(e.getMessage());
            }
        }

        Object result = null;
        try{
            result = pjp.proceed();

            log.setSuccess(true);

            if(audit.logResponse()){
                try{
                    String resp = objectMapper.writeValueAsString(result);
                    log.setResponsePayload(truncate(resp, 2000));
                } catch (Exception e) {
                    log.setResponsePayload(e.getMessage());
                }
            }
            auditService.persist(log);
            return result;
        } catch (Throwable e) {
            log.setSuccess(false);
            log.setErrorMessage(truncate(e.getMessage(), 2000));
            auditService.persist(log);
            throw e;
        }
    }

    private String truncate(String s, int max){
        if (s == null ) return null;
        return s.length() <= max ? s : s.substring(0, max);
    }
}
