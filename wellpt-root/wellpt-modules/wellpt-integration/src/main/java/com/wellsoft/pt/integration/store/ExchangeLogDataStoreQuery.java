package com.wellsoft.pt.integration.store;

import com.wellsoft.context.config.Config;
import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.pt.basicdata.datastore.support.AbstractDataStoreQueryInterface;
import com.wellsoft.pt.jpa.criteria.CriteriaMetadata;
import com.wellsoft.pt.jpa.criteria.QueryContext;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Description: 如何描述该类
 *
 * @author wangrf
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2020年3月5日.1	wangrf		2020年3月5日		Create
 * </pre>
 * @date 2020年3月5日
 */
@Component
public class ExchangeLogDataStoreQuery extends AbstractDataStoreQueryInterface {

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.jpa.criteria.QueryInterface#getQueryName()
     */
    @Override
    public String getQueryName() {
        return "数据交换管理_数据交换日志";
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.jpa.criteria.QueryInterface#initCriteriaMetadata(QueryContext)
     */
    @Override
    public CriteriaMetadata initCriteriaMetadata(QueryContext queryContext) {
        CriteriaMetadata metadata = CriteriaMetadata.createMetadata();
        // system field
        metadata.add("uuid", "uuid", "UUID", String.class);
        metadata.add("recVer", "rec_ver", "版本号", Integer.class);
        metadata.add("creator", "creator", "创建人", String.class);
        metadata.add("createTime", "create_time", "创建时间", Date.class);
        metadata.add("modifier", "modifier", "修改人", String.class);
        metadata.add("modifyTime", "modify_time", "修改时间", Date.class);
        metadata.add("attach", "attach", "附件属性", String.class);
        // "batchId": "BATCHID_201406091459_6fab276a-90e5-486d-8b51-7b9afe0cf80c",
        // "node": 1,
        // "fromUnitId": "004140203",
        // "msg": "访问公共webservice发送数据接口dxSend"
        metadata.add("batchId", "batch_id", "批次号", String.class);
        metadata.add("node", "node", "节点", String.class);
        metadata.add("fromUnitId", "from_unit_id", "原单位", String.class);
        metadata.add("msg", "msg", "msg", String.class);
        return metadata;
    }

    /**
     * (non-Javadoc)
     *
     * @see AbstractDataStoreQueryInterface#query(QueryContext)
     */
    @Override
    public List<QueryItem> query(QueryContext queryContext) {
        List<QueryItem> queryItems = queryContext.getNativeDao().namedQuery("exchangeLogDataQuery",
                getQueryParams(queryContext), QueryItem.class, queryContext.getPagingInfo());
        return queryItems;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.jpa.criteria.QueryInterface#count(QueryContext)
     */
    @Override
    public long count(QueryContext queryContext) {
        Long total = queryContext.getPagingInfo().getTotalCount();
        return total != -1 ? total : queryContext.getNativeDao().countByNamedQuery("exchangeLogDataQuery",
                getQueryParams(queryContext));
    }

    /**
     * @param queryContext
     * @return
     */
    private Map<String, Object> getQueryParams(QueryContext queryContext) {
        Map<String, Object> queryParams = queryContext.getQueryParams();
        String username = Config.getValue("multi.tenancy.common.username");
        queryParams.put("username", username);
        queryParams.put("keyword", queryContext.getKeyword());
        queryParams.put("whereSql", queryContext.getWhereSqlString());
        queryParams.put("orderBy", queryContext.getOrderString());
        return queryParams;
    }

}
