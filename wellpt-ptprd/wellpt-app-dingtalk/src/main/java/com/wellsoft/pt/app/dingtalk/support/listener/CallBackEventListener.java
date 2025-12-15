/*
 * @(#)2020年6月5日 V1.0
 *
 * Copyright 2020 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.dingtalk.support.listener;

import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.pt.app.dingtalk.facade.DingtalkOrgSyncApi;
import com.wellsoft.pt.app.dingtalk.support.event.CallBackEvent;
import com.wellsoft.pt.jpa.event.WellptEventListener;

/**
 * Description: 如何描述该类
 *
 * @author zhongzh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2020年6月5日.1	zhongzh		2020年6月5日		Create
 * </pre>
 * @date 2020年6月5日
 */
// @Component
@Deprecated
public class CallBackEventListener extends WellptEventListener<CallBackEvent> {

    /**
     * 异步执行钉钉回调事件，保障接口及时返回
     */
    @Override
    public void onApplicationEvent(CallBackEvent event) {
        ApplicationContextHolder.getBean(DingtalkOrgSyncApi.class).executeCallBackEvent(event.getEventCallBack());
    }

}
