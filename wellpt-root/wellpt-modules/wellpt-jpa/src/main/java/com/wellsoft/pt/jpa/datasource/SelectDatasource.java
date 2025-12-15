package com.wellsoft.pt.jpa.datasource;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Description: 数据源切换注解
 *
 * @author liuxj
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本  修改人    修改日期      修改内容
 * V1.0   liuxj    2025/1/14    Create
 * </pre>
 * @date 2025/1/14
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface SelectDatasource {
}