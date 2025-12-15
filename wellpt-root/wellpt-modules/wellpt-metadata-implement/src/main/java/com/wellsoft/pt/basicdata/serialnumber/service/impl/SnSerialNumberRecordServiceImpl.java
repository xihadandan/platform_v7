/*
 * @(#)7/22/22 V1.0
 *
 * Copyright 2022 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.serialnumber.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.wellsoft.context.jdbc.support.PagingInfo;
import com.wellsoft.pt.basicdata.serialnumber.dao.SnSerialNumberRecordDao;
import com.wellsoft.pt.basicdata.serialnumber.entity.SnSerialNumberRecordEntity;
import com.wellsoft.pt.basicdata.serialnumber.entity.SnSerialNumberRelationEntity;
import com.wellsoft.pt.basicdata.serialnumber.service.SnSerialNumberRecordService;
import com.wellsoft.pt.basicdata.serialnumber.service.SnSerialNumberRelationService;
import com.wellsoft.pt.basicdata.serialnumber.support.SerialNumberInfo;
import com.wellsoft.pt.dyform.implement.repository.usertable.metadata.ColumnMetadata;
import com.wellsoft.pt.dyform.implement.repository.usertable.service.UserTableMetadataService;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 7/22/22.1	zhulh		7/22/22		Create
 * </pre>
 * @date 7/22/22
 */
@Service
public class SnSerialNumberRecordServiceImpl extends AbstractJpaServiceImpl<SnSerialNumberRecordEntity, SnSerialNumberRecordDao, String>
        implements SnSerialNumberRecordService {

    @Autowired
    private SnSerialNumberRelationService snSerialNumberRelationService;

    @Autowired
    private UserTableMetadataService userTableMetadataService;

    /**
     * 保存流水号记录
     *
     * @param relationUuid
     * @param maintainUuid
     * @param serialNumberInfo
     * @return
     */
    @Override
    @Transactional
    public String save(String relationUuid, String maintainUuid, SerialNumberInfo serialNumberInfo) {
        SnSerialNumberRecordEntity entity = new SnSerialNumberRecordEntity();
        entity.setRelationUuid(relationUuid);
        entity.setMaintainUuid(maintainUuid);
        entity.setPrefix(serialNumberInfo.getPrefix());
        entity.setPointer(serialNumberInfo.getPointer());
        entity.setSuffix(serialNumberInfo.getSuffix());
        entity.setSerialNo(serialNumberInfo.getSerialNo());
        this.dao.save(entity);
        return entity.getUuid();
    }

    /**
     * 获取最小的流水号补号记录
     *
     * @param serialNumberDefId
     * @param maintainUuid
     * @return
     */
    @Override
    public SnSerialNumberRecordEntity getMinimalMissingSerialNumberRecord(String serialNumberDefId, String maintainUuid) {
        Map<String, Object> values = Maps.newHashMap();
        String relationTableSql = getQueryRelationTableSql(serialNumberDefId);
        values.put("relationTableSql", relationTableSql);
        values.put("maintainUuid", maintainUuid);

        List<SnSerialNumberRecordEntity> recordEntities = this.dao.listByNameSQLQuery("listMissingSerialNumberRecord", values, new PagingInfo(1, 1));
        if (CollectionUtils.isNotEmpty(recordEntities)) {
            return recordEntities.get(0);
        }
        return null;
    }

    /**
     * 根据UUID更新关系表UUID
     *
     * @param uuid
     * @param relationUuid
     */
    @Override
    @Transactional
    public void updateRelationUuidByUuid(String uuid, String relationUuid) {
        Assert.hasLength(uuid, "流水号记录UUID不能为空！");
        Assert.hasLength(relationUuid, "流水号关系UUID不能为空！");

        Map<String, Object> values = Maps.newHashMap();
        values.put("uuid", uuid);
        values.put("relationUuid", relationUuid);
        String hql = "update SnSerialNumberRecordEntity t set t.relationUuid = :relationUuid where t.uuid = :uuid";
        this.dao.updateByHQL(hql, values);
    }

    /**
     * 获取可补的流水号记录
     *
     * @param serialNumberDefId
     * @param maintainUuids
     * @return
     */
    @Override
    public List<SnSerialNumberRecordEntity> listAvailableSerialNumberRecord(String serialNumberDefId, String... maintainUuids) {
        Map<String, Object> values = Maps.newHashMap();
        String relationTableSql = getQueryRelationTableSql(serialNumberDefId);
        values.put("relationTableSql", relationTableSql);
        values.put("maintainUuids", maintainUuids);

        List<SnSerialNumberRecordEntity> recordEntities = this.dao.listByNameSQLQuery("listMissingSerialNumberRecord", values, new PagingInfo(1, Integer.MAX_VALUE));
        return recordEntities;
    }

    /**
     * 是否不存在流水号
     *
     * @param recordUuid
     * @param serialNumberDefId
     * @param maintainUuid
     * @return
     */
    @Override
    public boolean isMissingSerialNumberRecord(String recordUuid, String serialNumberDefId, String maintainUuid) {
        Map<String, Object> values = Maps.newHashMap();
        String relationTableSql = getQueryRelationTableSql(serialNumberDefId);
        values.put("relationTableSql", relationTableSql);
        values.put("maintainUuid", maintainUuid);
        values.put("uuid", recordUuid);
        // 流水号漏号中是否存在
        List<SnSerialNumberRecordEntity> recordEntities = this.dao.listByNameSQLQuery("listMissingSerialNumberRecord", values, new PagingInfo(1, 1));
        if (CollectionUtils.isNotEmpty(recordEntities)) {
            return true;
        }
        return false;
    }

    /**
     * 是否不存在流水号
     *
     * @param serialNo
     * @param serialNumberDefId
     * @param maintainUuid
     * @return
     */
    @Override
    public boolean isMissingSerialNo(String serialNo, String serialNumberDefId, String maintainUuid) {
        Map<String, Object> values = Maps.newHashMap();
        String relationTableSql = getQueryRelationTableSql(serialNumberDefId);
        values.put("relationTableSql", relationTableSql);
        values.put("maintainUuid", maintainUuid);
        values.put("serialNo", serialNo);
        // 流水号漏号中是否存在
        List<SnSerialNumberRecordEntity> recordEntities = this.dao.listByNameSQLQuery("listMissingSerialNumberRecord", values, new PagingInfo(1, 1));
        if (CollectionUtils.isNotEmpty(recordEntities)) {
            return true;
        }

        // 流水号记录中是否存在
        SnSerialNumberRecordEntity entity = new SnSerialNumberRecordEntity();
        entity.setMaintainUuid(maintainUuid);
        entity.setSerialNo(serialNo);
        return this.dao.countByEntity(entity) == 0;
    }

    /**
     * 列出跳过的指针
     *
     * @param initialValue
     * @param increment
     * @param currentPointer
     * @param maintainUuid
     * @return
     */
    @Override
    public List<Long> listSkipPointers(Integer initialValue, Integer increment, Long currentPointer, String maintainUuid) {
        List<Long> checkPointers = getCheckPointers(initialValue, increment, currentPointer);
        if (CollectionUtils.isEmpty(checkPointers)) {
            return checkPointers;
        }
        String hql = "select t.pointer as pointer from SnSerialNumberRecordEntity t where t.maintainUuid = :maintainUuid and t.pointer in (:checkPointers)";
        Map<String, Object> values = Maps.newHashMap();
        values.put("checkPointers", checkPointers);
        values.put("maintainUuid", maintainUuid);
        Set<Long> existsPointers = Sets.newHashSet();

        // 分离1000个集合参数
        if (checkPointers.size() < 1000) {
            existsPointers.addAll(this.dao.listNumberByHQL(hql, values));
        } else {
            List<Long> tmpList = Lists.newArrayList();
            int i = 0;
            for (Long checkPointer : checkPointers) {
                i++;
                tmpList.add(checkPointer);
                if (i % 1000 == 0) {
                    values.put("checkPointers", tmpList);
                    existsPointers.addAll(this.dao.listNumberByHQL(hql, values));
                    tmpList.clear();
                    continue;
                }
            }
            if (CollectionUtils.isNotEmpty(tmpList)) {
                values.put("checkPointers", tmpList);
                existsPointers.addAll(this.dao.listNumberByHQL(hql, values));
            }
        }
        checkPointers.removeAll(existsPointers);
        return checkPointers;
    }

    /**
     * 根据指针及流水号维护UUID，判断指针是否存在
     *
     * @param pointer
     * @param maintainUuid
     * @return
     */
    @Override
    public boolean isExistsPointer(Long pointer, String maintainUuid) {
        SnSerialNumberRecordEntity recordEntity = new SnSerialNumberRecordEntity();
        recordEntity.setPointer(pointer);
        recordEntity.setMaintainUuid(maintainUuid);
        return this.dao.countByEntity(recordEntity) > 0;
    }

    /**
     * 根据流水号关系删除不存在的记录
     *
     * @param relationUuid
     * @param objectName
     * @param fieldName
     */
    @Override
    @Transactional
    public void deleteAbsentRecordByRelation(String relationUuid, String objectName, String fieldName) {
        Assert.hasLength(relationUuid, "流水号关系UUID不能为空！");
        Assert.hasLength(objectName, "流水号关系对象名不能为空！");
        Assert.hasLength(fieldName, "流水号关系字段名不能为空！");

        String sql = "delete from sn_serial_number_record t where t.relation_uuid = :relationUuid and t.serial_no not in" +
                "(select d." + fieldName + " from " + objectName + " d where d." + fieldName + " is not null)";
        Map<String, Object> params = Maps.newHashMap();
        params.put("relationUuid", relationUuid);
        params.put("objectName", objectName);
        params.put("fieldName", fieldName);
        int count = this.dao.deleteBySQL(sql, params);
        logger.info("delete absent serial number record count {}", count);
    }

    private List<Long> getCheckPointers(Integer initialValue, Integer increment, Long currentPointer) {
        List<Long> checkPointers = Lists.newArrayList();
        if (initialValue <= currentPointer) {
            for (long index = initialValue; index <= currentPointer; index += increment) {
                checkPointers.add(index);
            }
        }
        return checkPointers;
    }

    private String getQueryRelationTableSql(String serialNumberDefId) {
        List<SnSerialNumberRelationEntity> relationEntities = snSerialNumberRelationService.listBySnId(serialNumberDefId);
        // 关系表流水号查询sql
        if (CollectionUtils.isNotEmpty(relationEntities)) {
            return buildRelationTableSql(relationEntities);
        }
        return StringUtils.EMPTY;
    }


    private String buildRelationTableSql(List<SnSerialNumberRelationEntity> relationEntities) {
        Iterator<SnSerialNumberRelationEntity> it = relationEntities.iterator();
        Map<String, Object> existTableColParams = Maps.newHashMap();
        List<String> tableSqls = Lists.newArrayList();
        while (it.hasNext()) {
            StringBuilder inner = new StringBuilder();
            SnSerialNumberRelationEntity entity = it.next();
            existTableColParams.put("columnName", entity.getFieldName());
            existTableColParams.put("tableName", entity.getObjectName());
            if (CollectionUtils.isEmpty(this.dao.listByNameSQLQuery("existTableColumn", existTableColParams))) {
                // TODO: 表不存在，通知进行清理数据
                // 系统字典数据可能存在错误，查询不到表字段信息时，从当前连接表读取
                ColumnMetadata columnMetadata = userTableMetadataService.getColumnMetadata(entity.getFieldName(), entity.getObjectName());
                if (columnMetadata == null) {
                    logger.error("数据库表{}不存在字段{}", entity.getObjectName(), entity.getFieldName());
                    continue;
                }
            }
            inner.append("select ").append(entity.getFieldName()).append(" as lsh from ").append(entity.getObjectName())
                    .append(" where ").append(entity.getFieldName()).append(" is not null ");
            tableSqls.add(inner.toString());
        }
        if (CollectionUtils.isNotEmpty(tableSqls)) {
            StringBuilder sb = new StringBuilder("select lsh from (");
            sb.append(StringUtils.join(tableSqls, " union all "));
            sb.append(") rl");
            return sb.toString();
        }
        return StringUtils.EMPTY;
    }


}
