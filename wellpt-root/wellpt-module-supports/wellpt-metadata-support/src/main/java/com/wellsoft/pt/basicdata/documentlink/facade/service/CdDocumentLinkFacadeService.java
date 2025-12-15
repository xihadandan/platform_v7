/*
 * @(#)Mar 14, 2022 V1.0
 *
 * Copyright 2022 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.documentlink.facade.service;

import com.wellsoft.context.service.Facade;
import com.wellsoft.pt.basicdata.documentlink.dto.CdDocumentLinkDto;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * Mar 14, 2022.1	zhulh		Mar 14, 2022		Create
 * </pre>
 * @date Mar 14, 2022
 */
public interface CdDocumentLinkFacadeService extends Facade {

    /**
     * 保存文档链接关系
     *
     * @param documentLinkDto
     * @return
     */
    String saveDto(CdDocumentLinkDto documentLinkDto);

    /**
     * 保存文档链接关系
     *
     * @param businessType
     * @param accessStrategy
     * @param sourceDataUuid
     * @param sourceDataChecker
     * @param targetDataUuid
     * @param targetDataChecker
     * @param targetUrl
     * @return
     */
    String saveDocumentLink(String businessType, String accessStrategy, String sourceDataUuid, String sourceDataChecker,
                            String targetDataUuid, String targetDataChecker, String targetUrl);

    /**
     * 通过UUID获取文档链接关系
     *
     * @param uuid
     * @return
     */
    CdDocumentLinkDto getDto(String uuid);

    /**
     * 通过源、目标数据UUID获取文档链接关系
     *
     * @param sourceDataUuid
     * @param targetDataUuid
     * @return
     */
    CdDocumentLinkDto getDtoBySourceAndTargetDataUuid(String sourceDataUuid, String targetDataUuid);

    /**
     * 通过源、目标数据UUID判断文档链接关系是否存在
     *
     * @param sourceDataUuid
     * @param targetDataUuid
     * @return
     */
    boolean existsBySourceAndTargetDataUuid(String sourceDataUuid, String targetDataUuid);

    /**
     * 检验文档链接关系
     *
     * @param documentLinkDto
     * @return
     */
    boolean check(CdDocumentLinkDto documentLinkDto);

    /**
     * 通过源、目标数据UUID检验文档链接关系
     *
     * @param sourceDataUuid
     * @param targetDataUuid
     * @return
     */
    boolean checkBySourceAndTargetDataUuid(String sourceDataUuid, String targetDataUuid);

    /**
     * 检验源数据
     *
     * @param uuid
     * @return
     */
    boolean checkSourceDataByUuid(String uuid);

    /**
     * 检验目标数据
     *
     * @param uuid
     * @return
     */
    boolean checkTargetDataByUuid(String uuid);

}
