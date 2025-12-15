/*
 * @(#)2021-12-02 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.layoutdocument.service;


import com.wellsoft.pt.basicdata.layoutdocument.dao.LayoutDocumentServiceConfDao;
import com.wellsoft.pt.basicdata.layoutdocument.dto.LayoutDocumentServiceConfDto;
import com.wellsoft.pt.basicdata.layoutdocument.entity.LayoutDocumentServiceConfEntity;
import com.wellsoft.pt.jpa.service.JpaService;

/**
 * Description: 数据库表LAYOUT_DOCUMENT_SERVICE_CONF的service服务接口
 *
 * @author shenhb
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2021-12-02.1	shenhb		2021-12-02		Create
 * </pre>
 * @date 2021-12-02
 */
public interface LayoutDocumentServiceConfService extends JpaService<LayoutDocumentServiceConfEntity, LayoutDocumentServiceConfDao, String> {

    void saveBean(LayoutDocumentServiceConfDto layoutDocumentServiceConfDto);

    void changeLayoutDocumentConfigStatus(String uuid, String status);

    String beforeEnableLayoutDocumentConfig(String uuid);
}
