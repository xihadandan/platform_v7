/*
 * @(#)2018年3月15日 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.webmail.service;

import com.wellsoft.pt.jpa.service.JpaService;
import com.wellsoft.pt.webmail.bean.WmMailRevocationDto;
import com.wellsoft.pt.webmail.dao.WmMailRevocationDao;
import com.wellsoft.pt.webmail.entity.WmMailRevocationEntity;

import java.util.List;

/**
 * Description: 邮件撤回服务接口
 *
 * @author chenqiong
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2018年3月15日.1	chenqiong		2018年3月15日		Create
 * </pre>
 * @date 2018年3月15日
 */
public interface WmMailRevocationService extends
        JpaService<WmMailRevocationEntity, WmMailRevocationDao, String> {

    /**
     * 保存邮件撤回记录
     */
    WmMailRevocationEntity saveMailRevocation(String mailUuid, String toMailAddress,
                                              Boolean isSuccess);

    /**
     * 查询邮件撤回记录
     */
    List<WmMailRevocationEntity> queryMailRevocation(WmMailRevocationDto paramDto);

}
