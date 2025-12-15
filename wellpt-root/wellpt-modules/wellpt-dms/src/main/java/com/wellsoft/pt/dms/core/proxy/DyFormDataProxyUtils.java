/*
 * @(#)2018年7月11日 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dms.core.proxy;

import com.wellsoft.context.base.BaseObject;
import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.pt.dyform.facade.dto.DyFormData;
import com.wellsoft.pt.dyform.facade.service.DyFormFacade;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2018年7月11日.1	zhulh		2018年7月11日		Create
 * </pre>
 * @date 2018年7月11日
 */
public class DyFormDataProxyUtils {

    /**
     * 获取表单数据代理对象
     *
     * @param proxy
     * @return
     */
    public static <T extends BaseObject> T getProxy(Class<T> clazz, DyFormData dyFormData) {
        return DyFormDataProxyFactory.getProxy(clazz, dyFormData);
    }

    /**
     * 保存代理对象的表单数据
     *
     * @param proxy
     * @return
     */
    public static String saveFormData(Object proxy) {
        try {
            DyFormFacade dyFormApiFacade = ApplicationContextHolder.getBean(DyFormFacade.class);
            DyFormDataProxy dyFormDataProxy = DyFormDataProxyFactory.getDyFormDataProxyTargetObject(proxy);
            return dyFormApiFacade.saveFormData(dyFormDataProxy.getDyFormData());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
