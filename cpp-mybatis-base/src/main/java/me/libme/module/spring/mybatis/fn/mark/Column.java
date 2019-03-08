package me.libme.module.spring.mybatis.fn.mark;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by J on 2019/3/7.
 */
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface Column {


    String name();

    String label() default "";

    String jdbcType() default "VARCHAR";



}
