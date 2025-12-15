/*
 * @(#)Mar 14, 2022 V1.0
 *
 * Copyright 2022 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.documentlink.facade.service.impl;

import com.wellsoft.pt.basicdata.documentlink.dto.CdDocumentLinkDto;
import com.wellsoft.pt.basicdata.documentlink.entity.CdDocumentLinkEntity;
import com.wellsoft.pt.basicdata.documentlink.enums.EnumAccessStrategy;
import com.wellsoft.pt.basicdata.documentlink.facade.service.CdDocumentLinkFacadeService;
import com.wellsoft.pt.basicdata.documentlink.service.CdDocumentLinkService;
import com.wellsoft.pt.basicdata.documentlink.support.DocumentLinkAccessChecker;
import com.wellsoft.pt.basicdata.documentlink.support.DocumentLinkInfo;
import com.wellsoft.pt.jpa.util.BeanUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

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
@Service
public class CdDocumentLinkFacadeServiceImpl implements CdDocumentLinkFacadeService {

    @Autowired
    private CdDocumentLinkService cdDocumentLinkService;

    @Autowired(required = false)
    private Map<String, DocumentLinkAccessChecker> checkerMap;

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.documentlink.facade.service.CdDocumentLinkFacadeService#saveDto(com.wellsoft.pt.basicdata.documentlink.dto.CdDocumentLinkDto)
     */
    @Override
    public String saveDto(CdDocumentLinkDto documentLinkDto) {
        return this.saveDocumentLink(documentLinkDto.getBusinessType(), documentLinkDto.getAccessStrategy(),
                documentLinkDto.getSourceDataUuid(), documentLinkDto.getSourceDataChecker(),
                documentLinkDto.getTargetDataUuid(), documentLinkDto.getTargetDataChecker(),
                documentLinkDto.getTargetUrl());
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.documentlink.facade.service.CdDocumentLinkFacadeService#saveDocumentLink(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public String saveDocumentLink(String businessType, String accessStrategy, String sourceDataUuid,
                                   String sourceDataChecker, String targetDataUuid, String targetDataChecker, String targetUrl) {
        CdDocumentLinkEntity entity = new CdDocumentLinkEntity();
        entity.setBusinessType(businessType);
        entity.setAccessStrategy(accessStrategy);
        entity.setSourceDataUuid(sourceDataUuid);
        entity.setSourceDataChecker(sourceDataChecker);
        entity.setTargetDataUuid(targetDataUuid);
        entity.setTargetDataChecker(targetDataChecker);
        entity.setTargetUrl(targetUrl);
        cdDocumentLinkService.save(entity);
        return entity.getUuid();
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.documentlink.facade.service.CdDocumentLinkFacadeService#getDto(java.lang.String)
     */
    @Override
    public CdDocumentLinkDto getDto(String uuid) {
        Assert.notNull(uuid, "uuid is null");
        CdDocumentLinkDto documentLinkDto = new CdDocumentLinkDto();
        CdDocumentLinkEntity documentLinkEntity = cdDocumentLinkService.getOne(uuid);
        if (documentLinkEntity != null) {
            BeanUtils.copyProperties(documentLinkEntity, documentLinkDto);
        }
        return documentLinkDto;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.documentlink.facade.service.CdDocumentLinkFacadeService#getDtoBySourceAndTargetDataUuid(java.lang.String, java.lang.String)
     */
    @Override
    public CdDocumentLinkDto getDtoBySourceAndTargetDataUuid(String sourceDataUuid, String targetDataUuid) {
        Assert.notNull(sourceDataUuid, "sourceDataUuid is null");
        Assert.notNull(targetDataUuid, "targetDataUuid  is null");
        CdDocumentLinkDto documentLinkDto = new CdDocumentLinkDto();
        CdDocumentLinkEntity entity = new CdDocumentLinkEntity();
        entity.setSourceDataUuid(sourceDataUuid);
        entity.setTargetDataUuid(targetDataUuid);
        List<CdDocumentLinkEntity> entities = cdDocumentLinkService.listByEntity(documentLinkDto);
        if (CollectionUtils.isNotEmpty(entities)) {
            BeanUtils.copyProperties(entities.get(0), documentLinkDto);
        }
        return documentLinkDto;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.documentlink.facade.service.CdDocumentLinkFacadeService#existsBySourceAndTargetDataUuid(java.lang.String, java.lang.String)
     */
    @Override
    public boolean existsBySourceAndTargetDataUuid(String sourceDataUuid, String targetDataUuid) {
        return cdDocumentLinkService.existsBySourceAndTargetDataUuid(sourceDataUuid, targetDataUuid);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.documentlink.facade.service.CdDocumentLinkFacadeService#check(com.wellsoft.pt.basicdata.documentlink.dto.CdDocumentLinkDto)
     */
    @Override
    public boolean check(CdDocumentLinkDto documentLinkDto) {
        if (MapUtils.isEmpty(checkerMap)) {
            return true;
        }

        DocumentLinkInfo documentLinkInfo = new DocumentLinkInfo();
        BeanUtils.copyProperties(documentLinkDto, documentLinkInfo);

        EnumAccessStrategy accessStrategy = EnumAccessStrategy.getByValue(documentLinkInfo.getAccessStrategy());
        if (accessStrategy == null) {
            return true;
        }
        return checkByAccessStrategy(accessStrategy, documentLinkInfo);
    }

    /**
     * @param accessStrategy
     * @param documentLinkInfo
     * @return
     */
    private boolean checkByAccessStrategy(EnumAccessStrategy accessStrategy, DocumentLinkInfo documentLinkInfo) {
        boolean result = false;
        String sourceDataUuid = documentLinkInfo.getSourceDataUuid();
        String targetDataUuid = documentLinkInfo.getTargetDataUuid();
        // 源数据检验器
        DocumentLinkAccessChecker sourceAccessChecker = getAccessChecker(documentLinkInfo.getSourceDataChecker());
        // 目标数据检验器
        DocumentLinkAccessChecker targetAccessChecker = getAccessChecker(documentLinkInfo.getTargetDataChecker());
        switch (accessStrategy) {
            case None:
                result = true;
                break;
            case SourceData:
                result = checkAccess(sourceAccessChecker, sourceDataUuid, documentLinkInfo);
                break;
            case TargetData:
                result = checkAccess(targetAccessChecker, targetDataUuid, documentLinkInfo);
                break;
            case Anyone:
                result = checkAccess(sourceAccessChecker, sourceDataUuid, documentLinkInfo)
                        || checkAccess(targetAccessChecker, targetDataUuid, documentLinkInfo);
                break;
            case All:
                result = checkAccess(sourceAccessChecker, sourceDataUuid, documentLinkInfo)
                        && checkAccess(targetAccessChecker, targetDataUuid, documentLinkInfo);
                break;
            default:
                break;
        }
        return result;
    }

    /**
     * @param accessChecker
     * @param dataUuid
     * @param documentLinkInfo
     * @return
     */
    private boolean checkAccess(DocumentLinkAccessChecker accessChecker, String dataUuid,
                                DocumentLinkInfo documentLinkInfo) {
        return accessChecker != null && accessChecker.check(dataUuid, documentLinkInfo);
    }

    /**
     * @param sourceDataChecker
     * @return
     */
    private DocumentLinkAccessChecker getAccessChecker(String sourceDataChecker) {
        if (StringUtils.isBlank(sourceDataChecker)) {
            return null;
        }

        // 按bean名称取
        DocumentLinkAccessChecker accessChecker = checkerMap.get(sourceDataChecker);
        if (accessChecker != null) {
            return accessChecker;
        }
        // 按接口返回的name取
        for (Entry<String, DocumentLinkAccessChecker> entry : checkerMap.entrySet()) {
            DocumentLinkAccessChecker checker = entry.getValue();
            if (StringUtils.equals(sourceDataChecker, checker.getName())) {
                return checker;
            }
        }

        return null;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.documentlink.facade.service.CdDocumentLinkFacadeService#checkBySourceAndTargetDataUuid(java.lang.String, java.lang.String)
     */
    @Override
    public boolean checkBySourceAndTargetDataUuid(String sourceDataUuid, String targetDataUuid) {
        return check(getDtoBySourceAndTargetDataUuid(sourceDataUuid, targetDataUuid));
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.documentlink.facade.service.CdDocumentLinkFacadeService#checkSourceDataByUuid(java.lang.String)
     */
    @Override
    public boolean checkSourceDataByUuid(String uuid) {
        CdDocumentLinkDto documentLinkDto = getDto(uuid);
        DocumentLinkInfo documentLinkInfo = new DocumentLinkInfo();
        BeanUtils.copyProperties(documentLinkDto, documentLinkInfo);
        String sourceDataUuid = documentLinkInfo.getSourceDataUuid();
        // 源数据检验器
        DocumentLinkAccessChecker sourceAccessChecker = getAccessChecker(documentLinkInfo.getSourceDataChecker());
        return checkAccess(sourceAccessChecker, sourceDataUuid, documentLinkInfo);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.documentlink.facade.service.CdDocumentLinkFacadeService#checkTargetDataByUuid(java.lang.String)
     */
    @Override
    public boolean checkTargetDataByUuid(String uuid) {
        CdDocumentLinkDto documentLinkDto = getDto(uuid);
        DocumentLinkInfo documentLinkInfo = new DocumentLinkInfo();
        BeanUtils.copyProperties(documentLinkDto, documentLinkInfo);
        String targetDataUuid = documentLinkInfo.getTargetDataUuid();
        // 目标数据检验器
        DocumentLinkAccessChecker targetAccessChecker = getAccessChecker(documentLinkInfo.getTargetDataChecker());
        return checkAccess(targetAccessChecker, targetDataUuid, documentLinkInfo);
    }

}
