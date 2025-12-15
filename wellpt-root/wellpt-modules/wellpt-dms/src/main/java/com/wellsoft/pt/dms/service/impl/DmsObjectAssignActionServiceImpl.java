/*
 * @(#)5/28/24 V1.0
 *
 * Copyright 2024 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dms.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.wellsoft.context.enums.Separator;
import com.wellsoft.context.jdbc.support.PagingInfo;
import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.pt.dms.dao.DmsObjectAssignActionDao;
import com.wellsoft.pt.dms.entity.DmsFileEntity;
import com.wellsoft.pt.dms.entity.DmsObjectAssignActionEntity;
import com.wellsoft.pt.dms.entity.DmsObjectIdentityEntity;
import com.wellsoft.pt.dms.file.action.FileActions;
import com.wellsoft.pt.dms.service.DmsObjectAssignActionService;
import com.wellsoft.pt.dms.service.DmsObjectIdentityService;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 5/28/24.1	zhulh		5/28/24		Create
 * </pre>
 * @date 5/28/24
 */
@Service
public class DmsObjectAssignActionServiceImpl extends AbstractJpaServiceImpl<DmsObjectAssignActionEntity, DmsObjectAssignActionDao, Long> implements DmsObjectAssignActionService {

    @Autowired
    private DmsObjectIdentityService dmsObjectIdentityService;

    /**
     * 保存文件阅读者、编辑者
     *
     * @param fileUuid
     * @param readerIds
     * @param editorIds
     */
    @Override
    @Transactional
    public void saveFileReaderAndEditors(String fileUuid, Set<String> readerIds, Set<String> editorIds) {
        this.removeByObjectIdIdentity(fileUuid);

        if (CollectionUtils.isEmpty(readerIds) && CollectionUtils.isEmpty(editorIds)) {
            return;
        }

        DmsFileEntity fileEntity = new DmsFileEntity();
        fileEntity.setUuid(fileUuid);
        DmsObjectIdentityEntity dmsObjectIdentity = dmsObjectIdentityService.getOrCreate(fileEntity);

        List<DmsObjectAssignActionEntity> actionEntities = Lists.newArrayList();
        if (CollectionUtils.isNotEmpty(readerIds)) {
            readerIds.forEach(readerId -> {
                DmsObjectAssignActionEntity entity = new DmsObjectAssignActionEntity();
                entity.setObjectIdentityUuid(dmsObjectIdentity.getUuid());
                entity.setObjectIdIdentity(dmsObjectIdentity.getObjectIdIdentity());
                entity.setOrgId(readerId);
                entity.setAction(FileActions.READ_FILE);
                actionEntities.add(entity);
            });
        }
        if (CollectionUtils.isNotEmpty(editorIds)) {
            editorIds.forEach(editorId -> {
                DmsObjectAssignActionEntity entity = new DmsObjectAssignActionEntity();
                entity.setObjectIdentityUuid(dmsObjectIdentity.getUuid());
                entity.setObjectIdIdentity(dmsObjectIdentity.getObjectIdIdentity());
                entity.setOrgId(editorId);
                entity.setAction(FileActions.READ_FILE);
                actionEntities.add(entity);

                entity = new DmsObjectAssignActionEntity();
                entity.setObjectIdentityUuid(dmsObjectIdentity.getUuid());
                entity.setObjectIdIdentity(dmsObjectIdentity.getObjectIdIdentity());
                entity.setOrgId(editorId);
                entity.setAction(FileActions.EDIT_FILE);
                actionEntities.add(entity);
            });
        }
        this.dao.saveAll(actionEntities);
    }

    /**
     * 根据对象数据标识删除数据
     *
     * @param objectIdIdentity
     */
    @Override
    @Transactional
    public void removeByObjectIdIdentity(String objectIdIdentity) {
        Assert.hasLength(objectIdIdentity, "数据对象标识不能为空！");

        String hql = "delete from DmsObjectAssignActionEntity t where t.objectIdIdentity = :objectIdIdentity";
        Map<String, Object> params = Maps.newHashMap();
        params.put("objectIdIdentity", objectIdIdentity);
        this.dao.deleteByHQL(hql, params);
    }

    /**
     * 根据文件UUID，添加文件阅读者
     *
     * @param fileUuid
     * @param readerIds
     */
    @Override
    @Transactional
    public void addFileReader(String fileUuid, List<String> readerIds) {
        DmsFileEntity fileEntity = new DmsFileEntity();
        fileEntity.setUuid(fileUuid);
        DmsObjectIdentityEntity dmsObjectIdentity = dmsObjectIdentityService.getOrCreate(fileEntity);

        List<DmsObjectAssignActionEntity> actionEntities = Lists.newArrayList();
        if (CollectionUtils.isNotEmpty(readerIds)) {
            readerIds.forEach(readerId -> {
                DmsObjectAssignActionEntity entity = new DmsObjectAssignActionEntity();
                entity.setObjectIdentityUuid(dmsObjectIdentity.getUuid());
                entity.setObjectIdIdentity(dmsObjectIdentity.getObjectIdIdentity());
                entity.setOrgId(readerId);
                entity.setAction(FileActions.READ_FILE);
                actionEntities.add(entity);
            });
        }
        this.dao.saveAll(actionEntities);
    }

    @Override
    public List<String> listFileActionUser(String fileUuid, String... fileActions) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("fileUuid", fileUuid);
        params.put("actions", fileActions);
        List<QueryItem> items = this.dao.listQueryItemByNameSQLQuery("dmsFileAssignActionUserQuery", params, new PagingInfo(1, Integer.MAX_VALUE));
        return items.stream().flatMap(item -> {
            String orgId = Objects.toString(item.get("orgId"), StringUtils.EMPTY);
            if (StringUtils.isBlank(orgId)) {
                return Stream.empty();
            }
            return Stream.of(StringUtils.split(orgId, Separator.SEMICOLON.getValue()));
        }).distinct().collect(Collectors.toList());
    }

    /**
     * 根据文件UUID，删除文件阅读者
     *
     * @param fileUuid
     * @param readerIds
     */
    @Override
    @Transactional
    public void deleteFileReader(String fileUuid, List<String> readerIds) {
        Assert.hasLength(fileUuid, "文件UUID不能为空！");
        Assert.notEmpty(readerIds, "阅读人员ID列表不能为空！");

        String hql = "delete from DmsObjectAssignActionEntity t where t.objectIdIdentity = :objectIdIdentity and t.orgId in(:orgIds) and t.action = :action";
        Map<String, Object> params = Maps.newHashMap();
        params.put("objectIdIdentity", fileUuid);
        params.put("orgIds", readerIds);
        params.put("action", FileActions.READ_FILE);
        this.dao.deleteByHQL(hql, params);
    }

}
