/*
 * @(#)2018年3月9日 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.webmail.service;

import com.wellsoft.pt.jpa.service.JpaService;
import com.wellsoft.pt.webmail.bean.WmMailSignatureDto;
import com.wellsoft.pt.webmail.dao.WmMailSignatureDao;
import com.wellsoft.pt.webmail.entity.WmMailSignatureEntity;

import java.util.List;

/**
 * Description: 邮件签名服务
 *
 * @author chenqiong
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2018年3月9日.1	chenqiong		2018年3月9日		Create
 * </pre>
 * @date 2018年3月9日
 */
public interface WmMailSignatureService extends JpaService<WmMailSignatureEntity, WmMailSignatureDao, String> {

    /**
     * 更新签名默认状态
     *
     * @param uuid
     */
    void updateSignatureDefault(String uuid, String userId, Boolean isDefault);

    /**
     * 查询指定用户的签名列表(并不包含大字段签名内容）
     *
     * @param currentUserId
     * @return
     */
    List<WmMailSignatureDto> queryUserMailSignatures(String currentUserId);

    WmMailSignatureDto getMailSignatureByNameAndUserId(String signatureName, String userId);

}
