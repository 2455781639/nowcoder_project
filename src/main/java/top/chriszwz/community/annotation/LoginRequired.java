package top.chriszwz.community.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/*
 * @Description:
 * @Author: Chris(张文卓)
 * @Date: 2022/6/27 14:02
 */
@Target(ElementType.METHOD)//注解的作用目标是方法
@Retention(RetentionPolicy.RUNTIME)//注解的生命周期是运行时
public @interface LoginRequired { //注解的类型是注解类


}
