package me.libme.module.spring.mybatis;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * Created by J on 2018/9/12.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import({MybatisCppAutoConfiguration.class})
public @interface EnableCppMybatis {



}
