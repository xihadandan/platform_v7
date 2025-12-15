/*
 * @(#)2019年10月12日 V1.0
 *
 * Copyright 2019 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dms.dp.store;

import com.google.common.base.CaseFormat;
import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.pt.basicdata.datastore.support.AbstractDataStoreQueryInterface;
import com.wellsoft.pt.dms.dp.service.DmsDataPermissionQueryService;
import com.wellsoft.pt.dms.entity.DmsDataPermissionDefinitionEntity;
import com.wellsoft.pt.dms.service.DmsDataPermissionDefinitionService;
import com.wellsoft.pt.jpa.criteria.Criteria;
import com.wellsoft.pt.jpa.criteria.CriteriaMetadata;
import com.wellsoft.pt.jpa.criteria.InterfaceParam;
import com.wellsoft.pt.jpa.criteria.QueryContext;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

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
@Component
public class DmsDataPermissionDataStoreQuery extends AbstractDataStoreQueryInterface {

    @Autowired
    private DmsDataPermissionDefinitionService dmsDataPermissionDefinitionService;

    @Autowired
    private DmsDataPermissionQueryService dmsDataPermissionQueryService;

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.jpa.criteria.QueryInterface#getQueryName()
     */
    @Override
    public String getQueryName() {
        return "数据权限查询";
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.jpa.criteria.QueryInterface#initCriteriaMetadata(com.wellsoft.pt.jpa.criteria.QueryContext)
     */
    @Override
    public CriteriaMetadata initCriteriaMetadata(QueryContext queryContext) {
        CriteriaMetadata criteriaMetadata = CriteriaMetadata.createMetadata();
        String dpDefId = this.getDmsDataPermissionDefId(queryContext);
        if (StringUtils.isNotBlank(dpDefId)) {
            DmsDataPermissionDefinitionEntity entity = dmsDataPermissionDefinitionService.getById(dpDefId);
            if (entity != null) {
                Criteria criteria = queryContext.getNativeDao().createTableCriteria(entity.getDataName());
                CriteriaMetadata metadata = criteria.getCriteriaMetadata();
                for (int index = 0; index < metadata.length(); index++) {
                    String columnIndex = metadata.getColumnIndex(index);
                    // 使用驼峰风格列索引
                    String camelColumnIndex = CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, columnIndex);
                    criteriaMetadata.add(camelColumnIndex, metadata.getMapColumnIndex(columnIndex),
                            metadata.getComment(index), metadata.getDataType(index), metadata.getColumnType(index));
                }
            }
        }
        return criteriaMetadata;
    }

    private String getDmsDataPermissionDefId(QueryContext context) {
        DmsDataPermissionInterfaceParam param = context.interfaceParam(DmsDataPermissionInterfaceParam.class);
        if (param == null) {
            return context.getInterfaceParam();//兼容旧的参数文本模式
        }
        return param.getDmsDataPermissionId();
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.datastore.support.AbstractDataStoreQueryInterface#getInterfaceDesc()
     */
    @Override
    public String getInterfaceDesc() {
        return "请在数据接口参数填写数据权限定义ID";
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.datastore.support.AbstractDataStoreQueryInterface#query(com.wellsoft.pt.jpa.criteria.QueryContext)
     */
    @Override
    public List<QueryItem> query(QueryContext queryContext) {
        queryContext.getQueryParams().put("joinProjectionSql", getJoinProjectionSql());
        queryContext.getQueryParams().put("joinTableSql", getJoinTableSql());
        return dmsDataPermissionQueryService.query(this.getDmsDataPermissionDefId(queryContext), queryContext);
    }

    /**
     * @return
     */
    protected String getJoinProjectionSql() {
        return StringUtils.EMPTY;
    }

    /**
     * @return
     */
    protected String getJoinTableSql() {
        return StringUtils.EMPTY;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.jpa.criteria.QueryInterface#count(com.wellsoft.pt.jpa.criteria.QueryContext)
     */
    @Override
    public long count(QueryContext queryContext) {
        queryContext.getQueryParams().put("joinTableSql", getJoinTableSql());
        queryContext.getQueryParams().put("joinProjectionSql", getJoinProjectionSql());
        long totalCount = queryContext.getPagingInfo().getTotalCount();
        if (totalCount < 0) {
            totalCount = dmsDataPermissionQueryService
                    .count(this.getDmsDataPermissionDefId(queryContext), queryContext);
        }
        return totalCount;
    }

    @Override
    public Class<? extends InterfaceParam> interfaceParamsClass() {
        return DmsDataPermissionInterfaceParam.class;
    }

}
