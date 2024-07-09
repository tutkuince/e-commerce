package com.incetutku.productservice.config;

import com.amazonaws.xray.entities.Subsegment;
import com.amazonaws.xray.spring.aop.BaseAbstractXRayInterceptor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.util.Map;

@Aspect
@Component
public class XRayInspector extends BaseAbstractXRayInterceptor {

    @Override
    @Pointcut("@within(com.amazonaws.xray.spring.aop.XRayEnabled)")
    protected void xrayEnabledClasses() {

    }

    @Override
    protected Map<String, Map<String, Object>> generateMetadata(ProceedingJoinPoint pjp, Subsegment subsegment) {
        return super.generateMetadata(pjp, subsegment);
    }
}
