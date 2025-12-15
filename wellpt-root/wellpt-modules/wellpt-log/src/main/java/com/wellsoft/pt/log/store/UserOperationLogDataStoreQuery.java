package com.wellsoft.pt.log.store;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.pt.basicdata.datastore.support.AbstractDataStoreQueryInterface;
import com.wellsoft.pt.jpa.criteria.CriteriaMetadata;
import com.wellsoft.pt.jpa.criteria.QueryContext;
import com.wellsoft.pt.jpa.dao.NativeDao;
import com.wellsoft.pt.log.dto.BusinessDetailsLogItem;
import com.wellsoft.pt.log.service.BusinessDetailsLogService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * Description: 平台管理_日志管理_用户操作日志数据仓库
 *
 * @author zenghw
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	        修改人		修改日期			修改内容
 * 2021/10/10.1	    zenghw		2021/10/10		    Create
 * </pre>
 * @date 2021/10/10
 */
@Component
public class UserOperationLogDataStoreQuery extends AbstractDataStoreQueryInterface {

    @Autowired
    private NativeDao nativeDao;
    @Autowired
    private BusinessDetailsLogService businessDetailsLogService;
    private String nameQuery = "queryUserOperationLogList";


    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.jpa.criteria.QueryInterface#initCriteriaMetadata(QueryContext)
     */
    @Override
    public CriteriaMetadata initCriteriaMetadata(QueryContext queryContext) {
        CriteriaMetadata criteriaMetadata = CriteriaMetadata.createMetadata();
        criteriaMetadata.add("uuid", "UUID", "UUID", String.class);
        criteriaMetadata.add("moduleName", "MODULE_NAME", "模块名称", String.class);
        criteriaMetadata.add("dataDefType", "DATA_DEF_TYPE", "数据类型", String.class);
        criteriaMetadata.add("operation", "OPERATION", "操作类型", String.class);
        criteriaMetadata.add("dataName", "DATA_NAME", "数据名称", String.class);
        criteriaMetadata.add("dataDefName", "DATA_DEF_NAME", "数据定义名称", String.class);
        criteriaMetadata.add("userName", "USER_NAME", "操作人", String.class);
        criteriaMetadata.add("createTime", "CREATE_TIME", "操作时间", String.class);
        criteriaMetadata.add("systemUnitId", "SYSTEM_UNIT_ID", "系统单位ID", String.class);
        criteriaMetadata.add("DETAILS_CNT", "DETAILS_CNT", "DETAILS_CNT", String.class);
        return criteriaMetadata;
    }

    @Override
    public List<QueryItem> query(QueryContext queryContext) {
        Map<String, Object> params = convertQueryParams(queryContext);
        List<QueryItem> items = queryContext.getNativeDao().namedQuery(nameQuery, params, QueryItem.class,
                queryContext.getPagingInfo());
        List<String> logIds = Lists.newArrayList();
        for (QueryItem item : items) {
            logIds.add(item.getString("uuid"));
        }
        if (logIds.size() > 0) {
            List<BusinessDetailsLogItem> businessDetailsLogItems = businessDetailsLogService.getBusinessDetailsLogItemByLogIds(logIds);
            Map<String, BusinessDetailsLogItem> businessDetailsLogItemMap = Maps.newHashMap();
            for (BusinessDetailsLogItem businessDetailsLogItem : businessDetailsLogItems) {
                businessDetailsLogItemMap.put(businessDetailsLogItem.getLogId(), businessDetailsLogItem);
            }
            for (QueryItem item : items) {
                BusinessDetailsLogItem businessDetailsLogItem = businessDetailsLogItemMap.get(item.getString("uuid"));
                if (businessDetailsLogItem != null) {
                    item.put("DETAILS_CNT", businessDetailsLogItem.getNum());
                }
            }
            for (QueryItem item : items) {
                if (StringUtils.isBlank(item.getString("DETAILS_CNT"))) {
                    item.put("DETAILS_CNT", "0");
                }
            }
        }
        return items;
    }

    @Override
    public String getQueryName() {
        return "数据平台管理_日志管理_用户操作日志";
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
        params.put("orderStr", orderStr);
        params.put("whereSql", wheresql);
        return params;
    }

}
