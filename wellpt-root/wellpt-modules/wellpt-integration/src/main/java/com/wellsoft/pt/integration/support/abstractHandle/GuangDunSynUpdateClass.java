/*
 * @(#)2017年5月18日 V1.0
 *
 * Copyright 2017 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.integration.support.abstractHandle;

import com.wellsoft.pt.integration.provider.SynUpdateClass;
import com.wellsoft.pt.integration.service.ZongXianExchangeService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Description: 如何描述该类
 *
 * @author xujianjia
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2017年5月18日.1	xujianjia		2017年5月18日		Create
 * </pre>
 * @date 2017年5月18日
 */
@Component
public class GuangDunSynUpdateClass implements SynUpdateClass {
    @Autowired
    private ZongXianExchangeService zongXianExchangeService;

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.integration.provider.SynUpdateClass#synUpdate(java.lang.String, java.lang.String)
     */
    @Override
    public String synUpdate(String json) {
        String msg = "";
        if (StringUtils.isNotBlank(json)) {
            zongXianExchangeService.updateGuangDunReceiveAndPoint(json);
            msg = "更新成功！";
        }
        return msg;
    }

}
