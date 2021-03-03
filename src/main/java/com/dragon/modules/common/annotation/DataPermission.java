package com.dragon.modules.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 用于判断是否过滤数据权限
 * 1、如果没有用到 @OneToOne 这种关联关系，只需要填写 fieldName [参考：DeptQueryCriteria.class]
 * 2、如果用到了 @OneToOne，fieldName和joinName都需要填写，拿UserQueryCriteria.class
 * 举例: @DataPermission(joinName = "dept", fieldName = "id")
 *
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface DataPermission {

    /**
     * Entity中的字段名称
     */
    String fieldName() default "";

    /**
     * Entity中与关联表关联的字段名称
     */
    String joinName() default "";

}
