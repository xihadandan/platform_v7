package com.wellsoft.pt.log.store;

import com.google.common.base.CaseFormat;
import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.pt.basicdata.datastore.support.AbstractDataStoreQueryInterface;
import com.wellsoft.pt.jpa.criteria.Criteria;
import com.wellsoft.pt.jpa.criteria.CriteriaMetadata;
import com.wellsoft.pt.jpa.criteria.QueryContext;
import com.wellsoft.pt.jpa.dao.NativeDao;
import com.wellsoft.pt.log.entity.LogManageDetailsEntity;
import com.wellsoft.pt.log.enums.DataParseTypeEnum;
import com.wellsoft.pt.log.service.LogManageDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * Description:
 *
 * @author zenghw
 * @date 2021-06-28
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2021-06-28.1	zenghw		2021-06-28		Create
 * </pre>
 */
@Component
public class LogManageOperationDataStoreQuery extends AbstractDataStoreQueryInterface {

    @Autowired
    private LogManageDetailsService logManageDetailsService;

    @Autowired
    private NativeDao nativeDao;
    private String nameQuery = "queryLogManageOperationList";

    @Override
    public CriteriaMetadata initCriteriaMetadata(QueryContext queryContext) {
        CriteriaMetadata criteriaMetadata = CriteriaMetadata.createMetadata();

        Criteria criteria = this.nativeDao.createTableCriteria("LOG_MANAGE_OPERATION");
        CriteriaMetadata metadata = criteria.getCriteriaMetadata();
        for (int index = 0; index < metadata.length(); index++) {
            String columnIndex = metadata.getColumnIndex(index);
            // 使用驼峰风格列索引
            String camelColumnIndex = CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, columnIndex);
            criteriaMetadata.add(camelColumnIndex, metadata.getMapColumnIndex(columnIndex), metadata.getComment(index),
                    metadata.getDataType(index), metadata.getColumnType(index));
        }
        criteriaMetadata.add("isShowDetailToEntityButton", "isShowDetailToEntityButton",
                "解析方式是entity时，详情按钮是否显示 0:不显示，；1：显示； 2：跳过此逻辑", Integer.class);
        return criteriaMetadata;
    }

    @Override
    public List<QueryItem> query(QueryContext queryContext) {
        Map<String, Object> params = convertQueryParams(queryContext);
        List<QueryItem> items = queryContext.getNativeDao().namedQuery(nameQuery, params, QueryItem.class,
                queryContext.getPagingInfo());

        for (QueryItem item : items) {
            String dataParseType = item.getString("dataParseType");
            if (DataParseTypeEnum.Entity.getValue().equals(dataParseType)) {
                // 解析方式是entity时，详情按钮是否显示 0:不显示，；1：显示； 2：跳过此逻辑
                List<LogManageDetailsEntity> list = logManageDetailsService.getListByLogId(item.getString("uuid"));
                if (list.size() == 0) {
                    item.put("isShowDetailToEntityButton", 0);
                } else {
                    item.put("isShowDetailToEntityButton", 1);
                }
            } else {
                item.put("isShowDetailToEntityButton", 2);
            }
        }
        return items;
    }

    @Override
    public String getQueryName() {
        return "平台管理_日志管理_管理日志";
    }

    @Override
    public long count(QueryContext queryContext) {
        Map<String, Object> params = convertQueryParams(queryContext);
        return this.nativeDao.countByNamedQuery(this.nameQuery, params);
    }

    /**
     * 将queryContext转化查询查询参数
     *
     * @param queryContext
     * @return java.util.HashMap<java.lang.String, java.lang.Object>
     **/
    private Map<String, Object> convertQueryParams(QueryContext queryContext) {
        Map<String, Object> params = queryContext.getQueryParams();

        String wheresql = queryContext.getWhereSqlString();
        String orderStr = queryContext.getOrderString();

        wheresql = wheresql.replace("BEFORE_DATA_NAME like :beforeDataName",
                "(BEFORE_DATA_NAME like :beforeDataName or AFTER_DATA_NAME like :beforeDataName)");

        params.put("orderStr", orderStr);
        params.put("whereSql", wheresql);

        return params;
    }
}
