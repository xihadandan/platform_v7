/*
 * @(#)2014-8-10 V1.0
 *
 * Copyright 2014 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.api;

import com.wellsoft.context.util.reflection.ReflectionUtils;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2014-8-10.1	zhulh		2014-8-10		Create
 * </pre>
 * @date 2014-8-10
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class WellptRequest<T extends WellptResponse> {
    public abstract String getApiServiceName();

    @JsonIgnore
    public Class<T> getResponseClass() {
        Class<T> responseClass = ReflectionUtils.getSuperClassGenricType(this.getClass());
        if (responseClass != null) {
            return responseClass;
        }
        return ReflectionUtils.getSuperClassGenricType(this.getClass().getSuperclass());
    }

}
