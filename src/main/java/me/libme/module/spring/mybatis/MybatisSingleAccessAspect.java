package me.libme.module.spring.mybatis;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Order(value= MybatisSingleAccessAspect.ORDER_NUMBER)
public class MybatisSingleAccessAspect {

	public static final int ORDER_NUMBER= Ordered.LOWEST_PRECEDENCE+100;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(MybatisSingleAccessAspect.class);

	@Autowired
	private EntityOnSaveListener entityOnSaveListener;

	@Autowired
	private EntityOnUpdateListener entityOnUpdateListener;


	@Autowired
	private EntityOnDeleteListener entityOnDeleteListener;


	@Autowired
	private SessionUserFactory sessionUserFactory;

	@Pointcut("execution(@me.libme.module.spring.mybatis.SingleAccess * *(..))")
	public void singleAccess() {
	}
	
	@Around(value = "singleAccess()")
	public Object aroundLog(ProceedingJoinPoint pjd) throws Throwable {
		try {
			MethodSignature methodSignature= (MethodSignature)pjd.getSignature();
//			String methodName=methodSignature.getName();
			SingleAccess singleAccess= methodSignature.getMethod().getDeclaredAnnotation(SingleAccess.class);
			Class listenerClass=singleAccess.listener();
			if(listenerClass!=void.class){
				SessionUser sessionUser=sessionUserFactory.sessionUser();
				for(Object arg:pjd.getArgs()){
					if(IEntityModel.class.isInstance(arg)){
						IEntityModel model=(IEntityModel)arg;
						if(EntityOnSaveListener.class==listenerClass){
							entityOnSaveListener.onSave(model,sessionUser);
						}else if(EntityOnUpdateListener.class==listenerClass){
							entityOnUpdateListener.onUpdate(model,sessionUser);
						}else if(EntityOnDeleteListener.class==listenerClass){
							entityOnDeleteListener.onDelete(model,sessionUser);
						}
					}
				}
			}
			return pjd.proceed();
		}catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			throw e;
		}catch (Throwable e) {
			LOGGER.error(e.getMessage(), e);
			throw e;
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
}
