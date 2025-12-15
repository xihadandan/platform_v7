/*
 * @(#)2021-07-22 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dms.facade.service.impl;

import com.wellsoft.context.service.AbstractApiFacade;
import com.wellsoft.pt.dms.dto.DmsDocExchangeRelatedDocDto;
import com.wellsoft.pt.dms.entity.DmsDocExchangeRelatedDocEntity;
import com.wellsoft.pt.dms.facade.service.DmsDocExchangeRelatedDocFacadeService;
import com.wellsoft.pt.dms.service.DmsDocExchangeRelatedDocService;
import com.wellsoft.pt.jpa.util.BeanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Description: 数据库表DMS_DOC_EXCHANGE_RELATED_DOC的门面服务实现类
 *
 * @author leo
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2021-07-22.1	leo		2021-07-22		Create
 * </pre>
 * @date 2021-07-22
 */
@Service
public class DmsDocExchangeRelatedDocFacadeServiceImpl extends AbstractApiFacade implements DmsDocExchangeRelatedDocFacadeService {

    @Autowired
    private DmsDocExchangeRelatedDocService dmsDocExchangeRelatedDocService;


    @Override
    @Transactional
    public void addRelatedDoc(DmsDocExchangeRelatedDocDto relatedDocDto) {
        if (StringUtils.isBlank(relatedDocDto.getDocExchangeRecordUuid())) {
            throw new RuntimeException("文档交换-记录uuid 不能为空");
        }
        if (StringUtils.isBlank(relatedDocDto.getFromRecordDetailUuid())) {
            throw new RuntimeException("来源文档交换-记录明细uuid 不能为空");
        }
        if (StringUtils.isBlank(relatedDocDto.getProcessingMethod())) {
            throw new RuntimeException("处理方式 不能为空");
        }
        if (StringUtils.isBlank(relatedDocDto.getDocTitle())) {
            throw new RuntimeException("文档标题 不能为空");
        }
        if (StringUtils.isBlank(relatedDocDto.getDocLink())) {
            throw new RuntimeException("文档链接 不能为空");
        }
        DmsDocExchangeRelatedDocEntity relatedDocEntity = new DmsDocExchangeRelatedDocEntity();
        BeanUtils.copyProperties(relatedDocDto, relatedDocEntity);
        dmsDocExchangeRelatedDocService.save(relatedDocEntity);
    }
}
