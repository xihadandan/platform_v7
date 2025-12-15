/*
 * @(#)11/18/22 V1.0
 *
 * Copyright 2022 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.biz.facade.service.impl;

import com.google.common.collect.Lists;
import com.wellsoft.context.enums.Separator;
import com.wellsoft.context.service.AbstractApiFacade;
import com.wellsoft.pt.biz.dto.BizMilestoneDto;
import com.wellsoft.pt.biz.entity.BizMilestoneEntity;
import com.wellsoft.pt.biz.entity.BizMilestoneResultEntity;
import com.wellsoft.pt.biz.enums.EnumBizMilestoneResultType;
import com.wellsoft.pt.biz.facade.service.BizMilestoneFacadeService;
import com.wellsoft.pt.biz.service.BizMilestoneResultService;
import com.wellsoft.pt.biz.service.BizMilestoneService;
import com.wellsoft.pt.dyform.facade.dto.DyFormData;
import com.wellsoft.pt.jpa.util.BeanUtils;
import com.wellsoft.pt.repository.entity.LogicFileInfo;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 11/18/22.1	zhulh		11/18/22		Create
 * </pre>
 * @date 11/18/22
 */
@Service
public class BizMilestoneFacadeServiceImpl extends AbstractApiFacade implements BizMilestoneFacadeService {

    @Autowired
    private BizMilestoneService milestoneService;

    @Autowired
    private BizMilestoneResultService milestoneResultService;

    /**
     * 保存里程碑事件信息
     *
     * @param dto
     */
    @Override
    @Transactional
    public void saveDto(BizMilestoneDto dto) {
        // 里程碑信息
        BizMilestoneEntity entity = new BizMilestoneEntity();
        BeanUtils.copyProperties(dto, entity);
        milestoneService.save(entity);

        // 保存交付物信息
        saveResultFiles(entity.getUuid(), dto);
    }

    /**
     * 保存里程碑事件信息
     *
     * @param dtos
     */
    @Override
    @Transactional
    public void saveAllDto(List<BizMilestoneDto> dtos) {
        if (CollectionUtils.isEmpty(dtos)) {
            return;
        }

        for (BizMilestoneDto dto : dtos) {
            saveDto(dto);
        }
    }

    /**
     * 保存交付物信息
     *
     * @param milestoneUuid
     * @param dto
     */
    private void saveResultFiles(String milestoneUuid, BizMilestoneDto dto) {
        // 交付物信息
        DyFormData dyFormData = dto.getDyFormData();
        List<String> resultFields = dto.getResultFields();
        List<BizMilestoneResultEntity> entities = Lists.newArrayList();
        for (String resultField : resultFields) {
            if (!dyFormData.isFieldExist(resultField)) {
                continue;
            }
            // 交付物类型
            String resultType = EnumBizMilestoneResultType.Conclusion.getValue();
            String content = null;
            String repoFileUuids = null;
            // 附件
            if (dyFormData.isFileField(resultField)) {
                resultType = EnumBizMilestoneResultType.Attachment.getValue();
                List<Object> logicFileInfos = (List<Object>) dyFormData.getFieldValue(resultField);
                if (CollectionUtils.isNotEmpty(logicFileInfos)) {
                    List<String> fileUuids = Lists.newArrayList();
                    for (Object logicFileInfo : logicFileInfos) {
                        if (logicFileInfo instanceof LogicFileInfo) {
                            fileUuids.add(((LogicFileInfo) logicFileInfo).getFileID());
                        } else {
                            Map<String, Object> map = (Map<String, Object>) logicFileInfo;
                            fileUuids.add(Objects.toString(map.get("fileID")));
                        }
                    }
                    repoFileUuids = StringUtils.join(fileUuids, Separator.SEMICOLON.getValue());
                }
            } else {
                // 结论
                content = Objects.toString(dyFormData.getFieldValue(resultField), StringUtils.EMPTY);
            }
            BizMilestoneResultEntity entity = new BizMilestoneResultEntity();
            entity.setMilestoneUuid(milestoneUuid);
            entity.setResultType(resultType);
            entity.setContent(content);
            entity.setRepoFileUuids(repoFileUuids);
            entities.add(entity);
        }

        milestoneResultService.saveAll(entities);
    }

}
