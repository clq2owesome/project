package com.clq.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 表示不需要持久化，即不会对应数据库表字段
 * @author chenliqiang
 * @update date 2015-09-07
 */

@Target(value = {ElementType.FIELD})
@Retention(value = RetentionPolicy.RUNTIME)
public @interface Transient {
	boolean cache() default true;
}
