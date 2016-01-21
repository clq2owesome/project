package com.clq.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 依赖对象(关联对象)
 * @author chenliqiang
 * @update date 2015-09-07
 */
@Target(value = {ElementType.FIELD})
@Retention(value = RetentionPolicy.RUNTIME)
public @interface Refer {
	Class type();  //依赖对象类型
    String field();//依赖对象所用属性
    //boolean suggest() default false;
}
