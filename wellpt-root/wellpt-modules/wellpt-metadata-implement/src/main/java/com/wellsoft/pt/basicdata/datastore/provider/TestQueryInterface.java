package com.wellsoft.pt.basicdata.datastore.provider;

import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.pt.basicdata.datastore.support.AbstractDataStoreQueryInterface;
import com.wellsoft.pt.jpa.criteria.Criteria;
import com.wellsoft.pt.jpa.criteria.CriteriaMetadata;
import com.wellsoft.pt.jpa.criteria.QueryContext;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TestQueryInterface extends AbstractDataStoreQueryInterface {
    private static final String CRITERIA_KEY = "tableCriteria";

    private Criteria convert(QueryContext context) {
        if (!context.contains(CRITERIA_KEY)) {
            Criteria tableCriteria = context.getNativeDao().createTableCriteria("org_user");
            tableCriteria.addCriterion(context.getCriterion());
            tableCriteria.addOrders(context.getOrders());
            tableCriteria.setPagingInfo(context.getPagingInfo());
            context.put(CRITERIA_KEY, tableCriteria);
        }
        return (Criteria) context.get(CRITERIA_KEY);
    }

    @Override
    public List<QueryItem> query(QueryContext context) {
        return convert(context).list(QueryItem.class);
    }

    @Override
    public long count(QueryContext context) {
        return convert(context).count();
    }

    @Override
    public CriteriaMetadata initCriteriaMetadata(QueryContext context) {
        return convert(context).getCriteriaMetadata();
    }

    @Override
    public String getQueryName() {
        return "测试查询";
    }

}
