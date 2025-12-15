/*
 * @(#)2020年1月10日 V1.0
 *
 * Copyright 2020 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.datastore.support.renderer;

import com.wellsoft.pt.basicdata.datastore.support.RendererParam;
import com.wellsoft.pt.multi.org.bean.OrgUserVo;
import com.wellsoft.pt.multi.org.facade.service.MultiOrgUserService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Description: 如何描述该类
 *
 * @author wangrf
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2020年1月10日.1	wangrf		2020年1月10日		Create
 * </pre>
 * @date 2020年1月10日
 */
@Component
public class SenderDataStoreRenderer extends AbstractDataStoreRenderer {

    @Autowired
    private MultiOrgUserService multiOrgUserService;

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.datastore.support.renderer.DataStoreRenderer#getType()
     */
    @Override
    public String getType() {
        return "senderRenderer";
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.datastore.support.renderer.DataStoreRenderer#getName()
     */
    @Override
    public String getName() {
        return "发送人渲染器";
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.datastore.support.renderer.AbstractDataStoreRenderer#doRenderData(java.lang.String, java.lang.Object, java.util.Map, com.wellsoft.pt.basicdata.datastore.support.RendererParam)
     */
    @Override
    public Object doRenderData(String columnIndex, Object value, Map<String, Object> rowData, RendererParam param) {
        // 发送人手机号码
        String sendMobilePhone = (String) rowData.get("sendMobilePhone");

        // 获取发送人id
        String senderId = (String) value;
        String sender = null;
        // 查询发送人信息
        if (StringUtils.isNotEmpty(senderId)) {
            OrgUserVo vo = multiOrgUserService.getUserById(senderId);
            if (vo != null) {
                String userName = vo.getUserName();
                sender = userName;
            }
        }
        // 按照前端需求，返回指定格式
        if (StringUtils.isBlank(sender)) {
            if (StringUtils.isBlank(sendMobilePhone)) {
                return "";
            } else {
                return sendMobilePhone;
            }
        } else {
            if (StringUtils.isBlank(sendMobilePhone)) {
                return sender;
            } else {
                return sender + "/" + sendMobilePhone;
            }
        }
    }
}
