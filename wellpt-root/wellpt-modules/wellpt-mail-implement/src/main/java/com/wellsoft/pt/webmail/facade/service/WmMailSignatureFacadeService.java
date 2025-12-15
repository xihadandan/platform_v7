/*
 * @(#)2018年3月9日 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.webmail.facade.service;

import com.wellsoft.context.service.BaseService;
import com.wellsoft.pt.webmail.bean.WmMailSignatureDto;

import java.util.List;

/**
 * Description: 邮件签名门面服务
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
public interface WmMailSignatureFacadeService extends BaseService {

    /**
     * 删除签名
     *
     * @param uuids
     */
    void deleteSignatures(List<String> uuids);

    /**
     * 更新签名
     *
     * @param dto
     */
    void updateSignature(WmMailSignatureDto dto);

    /**
     * 更新签名默认状态
     *
     * @param uuid
     */
    void updateSignatureDefault(String uuid, Boolean isDefault);

    /**
     * 新增信纸签名
     *
     * @param dto
     * @return
     */
    String addSinagure(WmMailSignatureDto dto);

    /**
     * 查询当前用户的所有签名（并包含大字段签名内容）
     *
     * @return
     */
    List<WmMailSignatureDto> queryCurrentUserMailSignatures();

    /**
     * 查询签名的详情，包含大字段的内容
     *
     * @param uuid
     * @return
     */
    WmMailSignatureDto getSignatureDetail(String uuid);
}
