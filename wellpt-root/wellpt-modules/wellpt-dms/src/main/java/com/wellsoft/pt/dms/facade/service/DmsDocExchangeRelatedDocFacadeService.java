/*
 * @(#)2021-07-22 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dms.facade.service;


import com.wellsoft.context.service.Facade;
import com.wellsoft.pt.dms.dto.DmsDocExchangeRelatedDocDto;

/**
 * Description: 数据库表DMS_DOC_EXCHANGE_RELATED_DOC的门面服务接口，提供给其他模块以及前端调用的业务接口
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
public interface DmsDocExchangeRelatedDocFacadeService extends Facade {


    /**
     * 添加相关文档
     *
     * @param relatedDocDto
     */
    public void addRelatedDoc(DmsDocExchangeRelatedDocDto relatedDocDto);

}
