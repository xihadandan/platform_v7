package com.wellsoft.pt.dm.store;

import com.google.common.collect.Maps;
import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.context.util.json.JsonUtils;
import com.wellsoft.pt.basicdata.datastore.support.AbstractDataStoreQueryInterface;
import com.wellsoft.pt.dm.dto.ColumnDto;
import com.wellsoft.pt.dm.dto.DataModelDto;
import com.wellsoft.pt.dm.entity.DataModelEntity;
import com.wellsoft.pt.dm.service.DataModelService;
import com.wellsoft.pt.jpa.criteria.CriteriaMetadata;
import com.wellsoft.pt.jpa.criteria.QueryContext;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * Description:
 *
 * @author chenq
 * @date 2019/6/3
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2019/6/3    chenq		2019/6/3		Create
 * </pre>
 */
@Component
public class DataModelTableDataStoreQuery extends AbstractDataStoreQueryInterface {

    @Resource
    DataModelService dataModelService;


    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.jpa.criteria.QueryInterface#getQueryName()
     */
    @Override
    public String getQueryName() {
        return "平台管理_数据模型_存储对象表数据";
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.jpa.criteria.QueryInterface#initCriteriaMetadata(QueryContext)
     */
    @Override
    public CriteriaMetadata initCriteriaMetadata(QueryContext queryContext) {
        CriteriaMetadata criteriaMetadata = CriteriaMetadata.createMetadata();
        criteriaMetadata.add("uuid", "uuid", "UUID", Long.class);
        criteriaMetadata.add("creator", "creator", "创建人", String.class);
        criteriaMetadata.add("modifier", "modifier", "修改人", String.class);
        criteriaMetadata.add("tenant", "tenant", "归属租户", String.class);
        criteriaMetadata.add("system", "system", "归属系统", String.class);
        criteriaMetadata.add("createTime", "createTime", "创建时间", String.class);
        criteriaMetadata.add("modifyTime", "modifyTime", "修改时间", String.class);
        String id = null;
        if (queryContext.getQueryParams().containsKey("id")) {
            id = queryContext.getQueryParams().get("id").toString();
        }
        if (id == null && StringUtils.isNotBlank(queryContext.getInterfaceParam())) {
            id = queryContext.interfaceParam(Map.class).get("id").toString();
        }
        if (id != null) {
            DataModelEntity entity = dataModelService.getById(id);
            DataModelDto dto = dataModelService.getDataModelDto(entity.getUuid());
            if (StringUtils.isNotBlank(dto.getColumnJson())) {
                List<ColumnDto> columnDtos = (List<ColumnDto>) JsonUtils.toCollection(dto.getColumnJson(), ColumnDto.class);
                for (ColumnDto col : columnDtos) {
                    Class clz = String.class;
                    if ("number".equalsIgnoreCase(col.getDataType())) {
                        clz = Integer.class;
                        if (col.getScale() != 0) {
                            clz = Double.class;
                        }
                    }
                    criteriaMetadata.add(col.getColumn(), col.getColumn(), col.getTitle(), clz);
                }
            }
        }


        return criteriaMetadata;
    }

    /**
     * (non-Javadoc)
     *
     * @see AbstractDataStoreQueryInterface#query(QueryContext)
     */
    @Override
    public List<QueryItem> query(QueryContext queryContext) {

//        TableCriteria tableCriteria = new TableCriteria(this.dao, this.table);
//        List<CdDataStoreColumnBean> dataStoreColumnBeans = cdDataStoreDefinitionService.loadTableColumns(this.table);
//        Map<String, DataStoreColumn> columnMap = Maps.newHashMap();//TODO: 查询列定义
//        for (CdDataStoreColumnBean bean : dataStoreColumnBeans) {
//            DataStoreColumn column = new DataStoreColumn();
//            BeanUtils.copyProperties(bean, column);
//            columnMap.put(column.getColumnIndex(), column);
//        }
//        for (Condition condition : queryInfo.getConditions()) {
//            tableCriteria.addCriterion(DataStoreConditionConverter.covertCriterion(condition, columnMap));
//        }
//        tableCriteria.addCriterion(queryContext.getCriterion());
//        tableCriteria.setPagingInfo(queryContext.getPagingInfo());
//        List list = tableCriteria.list(QueryItem.class);
//        return list;
        return null;

    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.jpa.criteria.QueryInterface#count(QueryContext)
     */
    @Override
    public long count(QueryContext queryContext) {

        Map<String, Object> queryParams = getQueryParams(queryContext);

        return queryParams.containsKey("tableName") ? dataModelService.countByNamedHQLQuery("dataModelTableDataQuery", queryParams) : 0L;
    }

    /**
     * @param queryContext
     * @return
     */
    private Map<String, Object> getQueryParams(QueryContext queryContext) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("keyword", queryContext.getKeyword());
        params.put("whereSql", queryContext.getWhereSqlString());
        params.put("orderBy", queryContext.getOrderString());
        if (queryContext.getQueryParams().containsKey("id")) {
            DataModelEntity entity = dataModelService.getById(queryContext.getQueryParams().get("id").toString());
            params.put("tableName", "UF_" + entity.getId().toUpperCase());
        }
        return params;
    }

}
