/*
 * @(#)2020年5月27日 V1.0
 *
 * Copyright 2020 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dyform.implement.definition.listener;

import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.pt.basicdata.params.entity.SysParamItem;
import com.wellsoft.pt.basicdata.params.event.SysPatamItemRemoveEvent;
import com.wellsoft.pt.jpa.event.WellptTransactionalEventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Description: 如何描述该类
 *
 * @author zhongzh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2020年5月27日.1	zhongzh		2020年5月27日		Create
 * </pre>
 * @date 2020年5月27日
 */
@Component
public class SysPatamItemRemoveListener extends WellptTransactionalEventListener<SysPatamItemRemoveEvent> {

    protected Logger logger = LoggerFactory.getLogger(getClass());

    /**
     *
     */
    @Override
    public void onApplicationEvent(SysPatamItemRemoveEvent event) {
        SysParamItem sysParamItem = event.getSysParamItem();
        String warpperKey = "${" + sysParamItem.getName() + "(sys." + sysParamItem.getKey() + ")}";
        FormFieldsRemovedListener listener = ApplicationContextHolder.getBean(FormFieldsRemovedListener.class);
        listener.onDyformTitlePropertyChange(warpperKey, null);
    }
}
