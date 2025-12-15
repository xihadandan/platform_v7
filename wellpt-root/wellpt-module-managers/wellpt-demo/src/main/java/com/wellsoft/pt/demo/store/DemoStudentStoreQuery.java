package com.wellsoft.pt.demo.store;

import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.pt.basicdata.datastore.support.AbstractDataStoreQueryInterface;
import com.wellsoft.pt.jpa.criteria.CriteriaMetadata;
import com.wellsoft.pt.jpa.criteria.QueryContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Description: 学生数据查询
 *
 * @author hongjz
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2020年3月28日.1	hongjz		2020年3月28日		Create
 * </pre>
 * @date 2020年3月28日
 */
@Component
public class DemoStudentStoreQuery extends AbstractDataStoreQueryInterface {
    private static final String CRITERIA_KEY = "DemoStudentStoreQuery";
    private Logger logger = LoggerFactory.getLogger(DemoStudentStoreQuery.class);


    @Override
    public String getQueryName() {
        return "平台二开示例--学生列表";
    }


    @Override
    public CriteriaMetadata initCriteriaMetadata(QueryContext arg0) {
        CriteriaMetadata criteriaMetadata = CriteriaMetadata.createMetadata();
        criteriaMetadata.add("uuid", "t.uuid", "UUID", String.class);
        criteriaMetadata.add("createTime", "t.create_time", "创建时间", String.class);
        criteriaMetadata.add("creator", "t.creator", "创建人", String.class);
        criteriaMetadata.add("modifier", "t.modifier", "修改人", String.class);
        criteriaMetadata.add("modifyTime", "t.modify_time", "修改时间", Date.class);
        criteriaMetadata.add("recVer", "t.rec_ver", "recVer", Integer.class);
        // criteriaMetadata.add("name", "t.name", "名称", String.class);
        //  criteriaMetadata.add("id", "t.id", "ID", String.class);
        criteriaMetadata.add("age", "t.student_age", "年龄", String.class);
        criteriaMetadata.add("sex", "t.student_sex", "性别", String.class);
        criteriaMetadata.add("name", "t.student_name", "名称", String.class);
        return criteriaMetadata;
    }

    @Override
    public List<QueryItem> query(QueryContext queryContext) {
        List<QueryItem> queryItems = queryContext.getNativeDao().namedQuery("demoStudentStoreQuery",
                getQueryParams(queryContext), QueryItem.class, queryContext.getPagingInfo());
        return queryItems;

    }

    @Override
    public long count(QueryContext queryContext) {
        return queryContext.getNativeDao()
                .countByNamedQuery("demoStudentStoreQuery", getQueryParams(queryContext));
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
        return queryParams;
    }

}
