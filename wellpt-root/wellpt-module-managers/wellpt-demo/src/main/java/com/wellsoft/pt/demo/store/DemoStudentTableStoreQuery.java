package com.wellsoft.pt.demo.store;

import com.google.common.base.CaseFormat;
import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.pt.basicdata.datastore.support.AbstractDataStoreQueryInterface;
import com.wellsoft.pt.jpa.criteria.Criteria;
import com.wellsoft.pt.jpa.criteria.CriteriaMetadata;
import com.wellsoft.pt.jpa.criteria.QueryContext;
import com.wellsoft.pt.jpa.dao.NativeDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;


/**
 * Description: 平台二开示例_培训demo_学生列表数据仓库
 *
 * @author zenghw
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	        修改人		修改日期			修改内容
 * 2021/7/28.1	    zenghw		2021/7/28		    Create
 * </pre>
 * @date 2021/7/28
 */
@Component
public class DemoStudentTableStoreQuery extends AbstractDataStoreQueryInterface {

    @Autowired
    private NativeDao nativeDao;
    private String nameQuery = "queryDemoStudentTable";

    @Override
    public CriteriaMetadata initCriteriaMetadata(QueryContext queryContext) {
        CriteriaMetadata criteriaMetadata = CriteriaMetadata.createMetadata();

        //单表方式
        Criteria criteria = this.nativeDao.createTableCriteria("UF_TEST_STUDENT_TABLE");
        CriteriaMetadata metadata = criteria.getCriteriaMetadata();
        for (int index = 0; index < metadata.length(); index++) {
            String columnIndex = metadata.getColumnIndex(index);
            // 使用驼峰风格列索引
            String camelColumnIndex = CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, columnIndex);
            criteriaMetadata.add(camelColumnIndex, metadata.getMapColumnIndex(columnIndex),
                    metadata.getComment(index), metadata.getDataType(index), metadata.getColumnType(index));
        }
        return criteriaMetadata;
    }

    @Override
    public List<QueryItem> query(QueryContext queryContext) {
        Map<String, Object> params = convertQueryParams(queryContext);
        List<QueryItem> items = queryContext.getNativeDao().namedQuery(nameQuery, params, QueryItem.class,
                queryContext.getPagingInfo());
        return items;
    }

    @Override
    public String getQueryName() {
        return "平台二开示例_培训demo_学生列表数据仓库";
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
