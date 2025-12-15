package com.wellsoft.pt.webmail.store;

import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.pt.basicdata.datastore.support.AbstractDataStoreQueryInterface;
import com.wellsoft.pt.jpa.criteria.CriteriaMetadata;
import com.wellsoft.pt.jpa.criteria.QueryContext;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Description: 文件夹数据存储查询
 *
 * @author chenq
 * @date 2019/7/19
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2019/7/19    chenq		2019/7/19		Create
 * </pre>
 */
@Component
public class WmWebmailFolderDataStoreQuery extends AbstractDataStoreQueryInterface {

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.jpa.criteria.QueryInterface#getQueryName()
     */
    @Override
    public String getQueryName() {
        return "平台邮件_邮件文件夹以及空间使用";
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.jpa.criteria.QueryInterface#initCriteriaMetadata(QueryContext)
     */
    @Override
    public CriteriaMetadata initCriteriaMetadata(QueryContext queryContext) {
        CriteriaMetadata criteriaMetadata = CriteriaMetadata.createMetadata();
        criteriaMetadata.add("uuid", "uuid", "UUID", String.class);
        criteriaMetadata.add("create_time", " create_time", "创建时间", Date.class);
        criteriaMetadata.add("modify_time", " modify_time", "修改时间", Date.class);
        criteriaMetadata.add("name", "name", "名称", String.class);
        criteriaMetadata.add("code", "code", "编号", String.class);
        criteriaMetadata.add("type_name", "type_name", "类型", String.class);
        criteriaMetadata.add("type_code", "type_code", "类型编码", String.class);
        criteriaMetadata.add("unread_total_number", "uuid", "未读/总邮件数", String.class);//渲染器加载
        criteriaMetadata.add("capacity_used", "capacity_used", "占用空间", Integer.class);
        return criteriaMetadata;
    }

    /**
     * (non-Javadoc)
     *
     * @see AbstractDataStoreQueryInterface#query(QueryContext)
     */
    @Override
    public List<QueryItem> query(QueryContext queryContext) {
        List<QueryItem> queryItems = queryContext.getNativeDao().namedQuery(
                "wmWebmailFolderCapacityQuery",
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
        return total != -1 ? total : queryContext.getNativeDao().countByNamedQuery(
                "wmWebmailFolderCapacityQuery", getQueryParams(queryContext));
    }

    /**
     * @param queryContext
     * @return
     */
    private Map<String, Object> getQueryParams(QueryContext queryContext) {
        Map<String, Object> queryParams = queryContext.getQueryParams();
        queryParams.put("keyword", queryContext.getKeyword());
        queryParams.put("whereSql", queryContext.getWhereSqlString());
        queryParams.put("orderBy", queryContext.getOrderString());
        queryParams.put("userId", queryContext.getQueryParams().get("userId"));
        return queryParams;
    }
}
