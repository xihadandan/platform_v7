/*
 * @(#)2019年10月12日 V1.0
 *
 * Copyright 2019 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dms.dp.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.wellsoft.context.component.select2.Select2QueryData;
import com.wellsoft.context.component.select2.Select2QueryInfo;
import com.wellsoft.context.enums.Separator;
import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.context.util.json.JsonUtils;
import com.wellsoft.pt.basicdata.iexport.suport.IexportMetaData.ColumnMetaData;
import com.wellsoft.pt.dms.dp.owner.AbstractCurrentUserDataOwnerProvider;
import com.wellsoft.pt.dms.dp.owner.DataOwnerProvider;
import com.wellsoft.pt.dms.dp.service.DmsDataPermissionQueryService;
import com.wellsoft.pt.dms.dp.support.*;
import com.wellsoft.pt.dms.entity.DmsDataPermissionDefinitionEntity;
import com.wellsoft.pt.dms.service.DmsDataPermissionDefinitionService;
import com.wellsoft.pt.jpa.criteria.CriteriaMetadata;
import com.wellsoft.pt.jpa.criteria.QueryContext;
import com.wellsoft.pt.jpa.service.impl.BaseServiceImpl;
import com.wellsoft.pt.jpa.util.BeanUtils;
import com.wellsoft.pt.multi.org.facade.service.OrgApiFacade;
import com.wellsoft.pt.security.core.userdetails.UserDetails;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
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
 * 2019年10月12日.1	zhulh		2019年10月12日		Create
 * </pre>
 * @date 2019年10月12日
 */
@Service
@Transactional(readOnly = true)
public class DmsDataPermissionQueryServiceImpl extends BaseServiceImpl implements DmsDataPermissionQueryService {

    @Autowired
    private DmsDataPermissionDefinitionService dmsDataPermissionDefinitionService;

    @Autowired
    private OrgApiFacade orgApiFacade;

    @Autowired
    private List<AbstractCurrentUserDataOwnerProvider> dataOwnerProviders;

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.dp.service.DmsDataPermissionQueryService#query(java.lang.String, com.wellsoft.pt.jpa.criteria.QueryContext)
     */
    @Override
    public List<QueryItem> query(String dpDefId, QueryContext queryContext) {
        Map<String, Object> queryParams = preQueryParams(dpDefId, queryContext);
        return this.nativeDao.namedQuery("dmsDataPermissionQuery", queryParams, QueryItem.class,
                queryContext.getPagingInfo());
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.dp.service.DmsDataPermissionQueryService#count(java.lang.String, com.wellsoft.pt.jpa.criteria.QueryContext)
     */
    @Override
    public long count(String dpDefId, QueryContext queryContext) {
        Map<String, Object> queryParams = preQueryParams(dpDefId, queryContext);
        return this.nativeDao.countByNamedQuery("dmsDataPermissionQuery", queryParams);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.dp.service.DmsDataPermissionQueryService#queryLatestVersion(java.lang.String, com.wellsoft.pt.jpa.criteria.QueryContext)
     */
    @Override
    public List<QueryItem> queryLatestVersion(String dpDefId, QueryContext queryContext) {
        Map<String, Object> queryParams = preQueryParams(dpDefId, queryContext);
        queryParams.put("isLatestVersion", true);
        return this.nativeDao.namedQuery("dmsDataPermissionLatestVersionQuery", queryParams, QueryItem.class,
                queryContext.getPagingInfo());
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.dp.service.DmsDataPermissionQueryService#countLatestVersion(java.lang.String, com.wellsoft.pt.jpa.criteria.QueryContext)
     */
    @Override
    public long countLatestVersion(String dpDefId, QueryContext queryContext) {
        Map<String, Object> queryParams = preQueryParams(dpDefId, queryContext);
        queryParams.put("isLatestVersion", true);
        return this.nativeDao.countByNamedQuery("dmsDataPermissionLatestVersionQuery", queryParams);
    }

    @Override
    public Select2QueryData queryAll4SelectOptions(Select2QueryInfo queryInfo) {
        return new Select2QueryData(dmsDataPermissionDefinitionService.listAll(), "id", "name");
    }

    /**
     * @param dpDefId
     * @param queryContext
     * @return
     */
    private Map<String, Object> preQueryParams(String dpDefId, QueryContext queryContext) {
        DataPermissionDefinition dataPermissionDefinition = getDataPermissionDefinition(dpDefId, queryContext);
        Map<String, Object> queryParams = queryContext.getQueryParams();
        queryParams.put("selectDataSetSql", getSelectDataSetSql(dataPermissionDefinition, queryContext));
        queryParams.put("whereSql", getWhereSql(dataPermissionDefinition, queryContext));
        queryParams.put("orderBy", getOrderSql(dataPermissionDefinition, queryContext));
        return queryParams;
    }

    /**
     * @param dpDefId
     * @return
     */
    private DataPermissionDefinition getDataPermissionDefinition(String dpDefId, QueryContext queryContext) {
        DmsDataPermissionDefinitionEntity entity = dmsDataPermissionDefinitionService.getById(dpDefId);
        try {
            JSONObject jsonObject = new JSONObject("{}");
            // 设置基本信息
            jsonObject.put("id", entity.getId());
            jsonObject.put("name", entity.getName());
            jsonObject.put("dataName", entity.getDataName());
            // 设置数据规则
            if (StringUtils.isNotBlank(entity.getRuleDefinition())) {
                JSONArray ruleJsonArray = new JSONArray(entity.getRuleDefinition());
                jsonObject.put("ruleDefinitions", ruleJsonArray);
            }
            // 设置数据范围
            if (StringUtils.isNotBlank(entity.getRangeDefinition())) {
                JSONArray rangeJsonArray = new JSONArray(entity.getRangeDefinition());
                jsonObject.put("rangeDefinitions", rangeJsonArray);
            }
            DataPermissionDefinition dataPermissionDefinition = JsonUtils.json2Object(jsonObject.toString(),
                    DataPermissionDefinition.class);
            // 设置数据列信息
            List<ColumnMetaData> columnMetaDatas = Lists.newArrayList();
            CriteriaMetadata metadata = queryContext.getCriteriaMetadata();
            String[] columnIndexs = metadata.getColumnIndexs();
            for (int index = 0; index < columnIndexs.length; index++) {
                ColumnMetaData columnMetaData = new ColumnMetaData();
                columnMetaData.setName(metadata.getMapColumnIndex(columnIndexs[index]));
                columnMetaData.setDataType(metadata.getColumnType(index));
                // 扩展列忽略掉
                if (StringUtils.contains(columnMetaData.getName(), Separator.DOT.getValue())) {
                    continue;
                }
                columnMetaDatas.add(columnMetaData);
            }
            dataPermissionDefinition.setColumnMetaDatas(columnMetaDatas);
            return dataPermissionDefinition;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    /**
     * @param dataPermissionDefinition
     * @param queryContext
     * @return
     */
    private String getSelectDataSetSql(DataPermissionDefinition dataPermissionDefinition, QueryContext queryContext) {
        StringBuilder sb = new StringBuilder();
        List<DataRangeDefinition> dataRangeDefinitions = dataPermissionDefinition.getRangeDefinitions();
        if (CollectionUtils.isNotEmpty(dataRangeDefinitions)) {
            DataRanges dataRanges = resolve(dataRangeDefinitions, dataPermissionDefinition);
            if (CollectionUtils.isNotEmpty(dataRanges.getDataRanges())) {
                // 数据集查询SQL
                sb.append(dataRanges.toSqlString(queryContext));
            } else {
                // 空查询SQL
                sb.append(getNoneSelectSql(dataPermissionDefinition));
            }
        } else {
            // 默认查询SQL
            sb.append(getDefaultSelectSql(dataPermissionDefinition));
        }
        return sb.toString();
    }

    /**
     * 返回空查询SQL
     *
     * @param dataPermissionDefinition
     * @return
     */
    private Object getNoneSelectSql(DataPermissionDefinition dataPermissionDefinition) {
        StringBuilder sb = new StringBuilder();
        sb.append("select * from(");
        sb.append("select").append(Separator.SPACE.getValue()).append(Separator.ASTERISK.getValue())
                .append(Separator.SPACE.getValue()).append("from").append(Separator.SPACE.getValue())
                .append(dataPermissionDefinition.getDataName()).append(Separator.SPACE.getValue())
                .append("where 1 = 2");
        sb.append(") this_");
        return sb.toString();
    }

    /**
     * 返回默认查询SQL
     *
     * @param dataPermissionDefinition
     */
    private String getDefaultSelectSql(DataPermissionDefinition dataPermissionDefinition) {
        StringBuilder sb = new StringBuilder();
        sb.append("select").append(Separator.SPACE.getValue()).append(Separator.ASTERISK.getValue())
                .append(Separator.SPACE.getValue()).append("from").append(Separator.SPACE.getValue())
                .append(dataPermissionDefinition.getDataName());
        return sb.toString();
    }

    /**
     * 解析数据范围定义
     *
     * @param dataRangeDefinitions
     * @param dataPermissionDefinition
     * @return
     */
    private DataRanges resolve(List<DataRangeDefinition> dataRangeDefinitions,
                               DataPermissionDefinition dataPermissionDefinition) {
        DataRanges dataRanges = new DataRanges();
        List<DataRange> ranges = Lists.newArrayList();
        UserDetails userDetails = SpringSecurityUtils.getCurrentUser();
        for (DataRangeDefinition dataRangeDefinition : dataRangeDefinitions) {
            DataRange dataRange = resolve(dataRangeDefinition, dataPermissionDefinition);
            // 角色权限判断
            String roleId = dataRange.getRoleId();
            if (!hasRole(userDetails, roleId)) {
                continue;
            }
            ranges.add(dataRange);
        }
        dataRanges.setDataRanges(splitIfRequried(ranges));
        return dataRanges;
    }

    /**
     * @param userDetails
     * @param configRoleId
     * @return
     */
    private boolean hasRole(UserDetails userDetails, String configRoleId) {
        if (StringUtils.isBlank(configRoleId)) {
            return true;
        }
        if (userDetails != null) {
            for (GrantedAuthority authority : userDetails.getAuthorities()) {
                if (StringUtils.equals(authority.getAuthority(), configRoleId)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 拆分需要处理的数据范围
     *
     * @param ranges
     * @return
     */
    private List<DataRange> splitIfRequried(List<DataRange> ranges) {
        Set<DataRange> dataRangeSet = Sets.newLinkedHashSet();
        // ownerIds大于1000，拆分为多条
        for (DataRange dataRange : ranges) {
            List<String> ownerIds = dataRange.getOwnerIds();
            if (ownerIds.size() > 1000) {
                int num = 0;
                List<String> tmpOwnerIds = Lists.newArrayList();
                for (String ownerId : ownerIds) {
                    num++;
                    tmpOwnerIds.add(ownerId);
                    if (num % 1000 == 0 || num == ownerIds.size()) {
                        DataRange newDataRange = new DataRange();
                        BeanUtils.copyProperties(dataRange, newDataRange);
                        // 设置新的数据范围ID及所有者
                        newDataRange.setId(dataRange.getId() + "_" + num);
                        newDataRange.setOwnerIds(tmpOwnerIds);
                        dataRangeSet.add(newDataRange);
                        tmpOwnerIds = Lists.newArrayList();
                    }
                }
            } else {
                dataRangeSet.add(dataRange);
            }
        }
        return Arrays.asList(dataRangeSet.toArray(new DataRange[0]));
    }

    /**
     * 解析数据范围定义
     *
     * @param dataRangeDefinition
     * @param dataPermissionDefinition
     * @return
     */
    private DataRange resolve(DataRangeDefinition dataRangeDefinition, DataPermissionDefinition dataPermissionDefinition) {
        DataRange dataRange = new DataRange();
        // ID
        dataRange.setId(dataRangeDefinition.getId());
        // type
        dataRange.setType(dataRangeDefinition.getType());
        // 表名或视图名
        dataRange.setDataName(dataPermissionDefinition.getDataName());
        // 列信息
        dataRange.setColumnMetaDatas(dataPermissionDefinition.getColumnMetaDatas());
        // 数据所有者字段
        dataRange.setOwnerFieldName(dataRangeDefinition.getOwnerFieldName());
        // 所有者字段值
        dataRange.setOwnerIds(resolveOwnerIds(dataRangeDefinition));
        // 归属角色
        dataRange.setRoleId(dataRangeDefinition.getRoleId());
        // 数据规则
        dataRange.setDataRuleDefinitions(dataRangeDefinition.getRules());
        return dataRange;
    }

    /**
     * 解析数据范围所有者ID
     *
     * @param dataRangeDefinition
     * @return
     */
    private List<String> resolveOwnerIds(DataRangeDefinition dataRangeDefinition) {
        Set<String> retOwnerIds = Sets.newLinkedHashSet();
        List<String> ownerIds = Lists.newArrayList();
        // 数据所有者
        String owner = dataRangeDefinition.getOwner();
        // 当前用户组织信息
        if (DataRangeDefinition.CURRENT_USER_INFO_TYPE.equals(dataRangeDefinition.getType())) {
            String[] ownerInfos = StringUtils.split(owner, Separator.SEMICOLON.getValue());
            for (String ownerInfo : ownerInfos) {
                DataOwnerProvider dataOwnerProvider = getDataOwnerProvider(ownerInfo);
                if (dataOwnerProvider != null) {
                    ownerIds.addAll(dataOwnerProvider.getOwnerIds());
                }
            }
        } else if (DataRangeDefinition.SPECIFY_ORG_NODE_TYPE.equals(dataRangeDefinition.getType())) {
            // 指定组织结点
            ownerIds.addAll(Arrays.asList(StringUtils.split(owner, Separator.SEMICOLON.getValue())));
        }
        // 移队空数据
        ownerIds.remove(StringUtils.EMPTY);

        retOwnerIds.addAll(ownerIds);
        // 包含上级组织节点
        boolean includeSuperiorOrg = dataRangeDefinition.isIncludeSuperiorOrg();
        if (includeSuperiorOrg) {
            retOwnerIds.addAll(orgApiFacade.getSuperiorOrgIdsByOrgEleIdAndEleTypes(ownerIds));
        }
        // 包含同级组织节点
        boolean includeSiblingOrg = dataRangeDefinition.isIncludeSiblingOrg();
        if (includeSiblingOrg) {
            retOwnerIds.addAll(orgApiFacade.getSiblingOrgIdsByOrgEleIdAndEleTypes(ownerIds));
        }
        // 包含下级组织节点
        boolean includeSubordinateOrg = dataRangeDefinition.isIncludeSubordinateOrg();
        if (includeSubordinateOrg) {
            retOwnerIds.addAll(orgApiFacade.getSubordinateOrgIdsByOrgEleIdAndEleTypes(ownerIds));
        }
        return Arrays.asList(retOwnerIds.toArray(new String[0]));
    }

    /**
     * @param ownerInfo
     * @return
     */
    private DataOwnerProvider getDataOwnerProvider(String ownerInfo) {
        if (CollectionUtils.isEmpty(dataOwnerProviders)) {
            return null;
        }
        for (DataOwnerProvider dataOwnerProvider : dataOwnerProviders) {
            if (StringUtils.equals(ownerInfo, dataOwnerProvider.getType())) {
                return dataOwnerProvider;
            }
        }
        return null;
    }

    /**
     * @param dataPermissionDefinition
     * @param queryContext
     * @return
     */
    private String getWhereSql(DataPermissionDefinition dataPermissionDefinition, QueryContext queryContext) {
        StringBuilder sb = new StringBuilder();
        sb.append(queryContext.getWhereSqlString());
        List<DataRuleDefinition> ruleDefinitions = dataPermissionDefinition.getRuleDefinitions();
        if (CollectionUtils.isNotEmpty(ruleDefinitions)) {
            sb.append(" and ");
            sb.append("(");
            sb.append(toWhereSqlConditionString(ruleDefinitions));
            sb.append(")");
        }
        return sb.toString();
    }

    /**
     * 数据规则转为SQL查询条件语句
     *
     * @param dataRules
     * @return
     */
    private String toWhereSqlConditionString(List<DataRuleDefinition> dataRules) {
        StringBuilder sb = new StringBuilder();
        for (DataRuleDefinition dataRule : dataRules) {
            sb.append(dataRule.toSqlString());
        }
        return sb.toString();
    }

    /**
     * @param dataPermissionDefinition
     * @param queryContext
     * @return
     */
    private String getOrderSql(DataPermissionDefinition dataPermissionDefinition, QueryContext queryContext) {
        return queryContext.getOrderString();
    }

}
