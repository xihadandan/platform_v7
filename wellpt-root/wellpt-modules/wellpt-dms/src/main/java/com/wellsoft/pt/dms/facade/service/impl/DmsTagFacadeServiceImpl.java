/*
 * @(#)Apr 13, 2018 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dms.facade.service.impl;

import com.wellsoft.context.jdbc.entity.IdEntity;
import com.wellsoft.pt.dms.entity.DmsTagDataEntity;
import com.wellsoft.pt.dms.entity.DmsTagEntity;
import com.wellsoft.pt.dms.facade.service.DmsTagFacadeService;
import com.wellsoft.pt.dms.model.DmsTagData;
import com.wellsoft.pt.dms.service.DmsTagDataService;
import com.wellsoft.pt.dms.service.DmsTagService;
import com.wellsoft.pt.jpa.service.impl.BaseServiceImpl;
import com.wellsoft.pt.jpa.util.BeanUtils;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * Apr 13, 2018.1	zhulh		Apr 13, 2018		Create
 * </pre>
 * @date Apr 13, 2018
 */
@Service
public class DmsTagFacadeServiceImpl extends BaseServiceImpl implements DmsTagFacadeService {

    @Autowired
    private DmsTagService dmsTagService;

    @Autowired
    private DmsTagDataService dmsTagDataService;

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.facade.service.DmsTagFacadeService#createTag(java.lang.String, java.lang.String)
     */
    @Override
    @Transactional
    public String createTag(String name, String color) {
        // 检测标签是否已存在
        checkTagName(null, name);

        String ownerId = SpringSecurityUtils.getCurrentUserId();
        DmsTagEntity dmsTag = new DmsTagEntity();
        String id = UUID.randomUUID().toString();
        dmsTag.setName(name);
        dmsTag.setId(id);
        dmsTag.setCode(id);
        dmsTag.setColor(color);
        dmsTag.setOwnerId(ownerId);
        dmsTagService.save(dmsTag);
        return dmsTag.getUuid();
    }

    /**
     * @param name
     */
    private void checkTagName(String uuid, String name) {
        String ownerId = SpringSecurityUtils.getCurrentUserId();
        if (StringUtils.isBlank(uuid)) {
            // 检测标签是否已存在
            DmsTagEntity example = new DmsTagEntity();
            example.setName(name);
            example.setOwnerId(ownerId);
            if (dmsTagService.countByExample(example) > 0) {
                throw new RuntimeException("标签创建失败，已存在同名的标签！");
            }
        } else {
            // 检测标签是否已存在
            DmsTagEntity example = new DmsTagEntity();
            example.setName(name);
            example.setOwnerId(ownerId);
            if (dmsTagService.countByExample(example) > 0) {
                throw new RuntimeException("标签更新失败，已存在同名的标签！");
            }
        }
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.facade.service.DmsTagFacadeService#createTagAndTagData(java.lang.String, java.lang.String, java.util.List)
     */
    @Override
    @Transactional
    public String createTagAndTagData(String name, String color, List<String> dataUuids) {
        String tagUuid = createTag(name, color);
        tagData(dataUuids, tagUuid);
        return tagUuid;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.facade.service.DmsTagFacadeService#updateTag(java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    @Transactional
    public void updateTagColor(String uuid, String color) {
        DmsTagEntity dmsTag = dmsTagService.get(uuid);
        dmsTag.setColor(color);
        dmsTagService.save(dmsTag);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.facade.service.DmsTagFacadeService#renameTag(java.lang.String, java.lang.String)
     */
    @Override
    @Transactional
    public void renameTag(String uuid, String name) {
        // 检测标签是否已存在
        checkTagName(uuid, name);

        DmsTagEntity dmsTag = dmsTagService.get(uuid);
        dmsTag.setName(name);
        dmsTagService.save(dmsTag);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.facade.service.DmsTagFacadeService#deleteAll(java.util.List)
     */
    @Override
    @Transactional
    public void deleteAll(List<String> tagUuids) {
        for (String tagUuid : tagUuids) {
            dmsTagDataService.removeByTagUuid(tagUuid);
            dmsTagService.remove(tagUuid);
        }
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.facade.service.DmsTagFacadeService#tagData(java.util.List, java.lang.String)
     */
    @Override
    @Transactional
    public void tagData(List<String> dataUuids, String tagUuid) {
        for (String dataUuid : dataUuids) {
            DmsTagDataEntity example = new DmsTagDataEntity();
            example.setDataUuid(dataUuid);
            example.setTagUuid(tagUuid);
            if (dmsTagDataService.countByExample(example).intValue() == 0) {
                dmsTagDataService.save(example);
            }
        }
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.facade.service.DmsTagFacadeService#untagData(java.util.List, java.lang.String)
     */
    @Override
    public void untagData(List<String> dataUuids, String tagUuid) {
        for (String dataUuid : dataUuids) {
            dmsTagDataService.removeByDataUuidAndTagUuid(dataUuid, tagUuid);
        }
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.facade.service.DmsTagFacadeService#untagDataAll(java.util.List)
     */
    @Override
    @Transactional
    public void untagDataAll(List<String> dataUuids) {
        dmsTagDataService.removeAllByDataUuid(dataUuids);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.facade.service.DmsTagFacadeService#queryUserTags(java.lang.String)
     */
    @Override
    public List<DmsTagEntity> queryUserTags(String userId) {
        DmsTagEntity example = new DmsTagEntity();
        example.setOwnerId(userId);
        List<DmsTagEntity> dmsTags = dmsTagService.findByExample(example, IdEntity.CREATE_TIME_ASC);
        return BeanUtils.copyCollection(dmsTags, DmsTagEntity.class);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.facade.service.DmsTagFacadeService#queryCurrentUserTags()
     */
    @Override
    public List<DmsTagEntity> queryCurrentUserTags() {
        return queryUserTags(SpringSecurityUtils.getCurrentUserId());
    }

    /**
     * @see com.wellsoft.pt.dms.facade.service.DmsTagFacadeService#queryDataTags(java.util.List)
     */
    @Override
    public Map<String, List<DmsTagData>> queryDataTags(List<String> dataUuids) {
        Map<String, List<DmsTagData>> dmsTagDataMap = new HashMap<String, List<DmsTagData>>();
        for (String dataUuid : dataUuids) {
            dmsTagDataMap.put(dataUuid, new ArrayList<DmsTagData>(0));
        }
        List<DmsTagData> dmsTagDatas = dmsTagDataService.findByDataUuids(dataUuids);
        for (DmsTagData dmsTagData : dmsTagDatas) {
            dmsTagDataMap.get(dmsTagData.getDataUuid()).add(dmsTagData);
        }
        return dmsTagDataMap;
    }

}
