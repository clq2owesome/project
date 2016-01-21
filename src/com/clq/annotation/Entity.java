package com.clq.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 自定义实体注解
 * @author chenliqiang
 * @update date 2015-09-06
 */
@Target(value = {ElementType.TYPE})
@Retention(value = RetentionPolicy.RUNTIME)
public @interface Entity {
    boolean memCache() default true;
    String tableName() default "";
    /**
     * Split format as follow:
     * Mod:splitField:tableCount
     * Date:splitField:(y:m)
     * @return split define
     */
    String split() default "";
}
