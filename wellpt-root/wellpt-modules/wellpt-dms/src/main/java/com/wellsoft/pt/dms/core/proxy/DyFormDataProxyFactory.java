/*
 * @(#)Feb 21, 2017 V1.0
 *
 * Copyright 2017 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dms.core.proxy;

import com.wellsoft.context.base.BaseObject;
import com.wellsoft.pt.dyform.facade.dto.DyFormData;
import org.springframework.cglib.proxy.Enhancer;

import java.lang.reflect.Field;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * Feb 21, 2017.1	zhulh		Feb 21, 2017		Create
 * </pre>
 * @date Feb 21, 2017
 */
public class DyFormDataProxyFactory {

    @SuppressWarnings("unchecked")
    public static <T extends BaseObject> T getProxy(Class<T> clazz, DyFormData dyFormData) {
        Enhancer enhancer = new Enhancer();
        // 设置需要创建子类的类
        enhancer.setSuperclass(clazz);
        enhancer.setCallback(new DyFormDataProxy(dyFormData));
        // 通过字节码技术动态创建子类实例
        return (T) enhancer.create();
    }

    /**
     * @param proxy
     * @return
     * @throws Exception
     */
    public static DyFormDataProxy getDyFormDataProxyTargetObject(Object proxy) throws Exception {
        Field h = proxy.getClass().getDeclaredField("CGLIB$CALLBACK_0");
        h.setAccessible(true);
        return (DyFormDataProxy) h.get(proxy);
    }

}
