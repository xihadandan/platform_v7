package com.wellsoft.pt.dm.dto;

import com.wellsoft.context.jdbc.support.PagingInfo;
import com.wellsoft.pt.basicdata.datastore.bean.DataStoreOrder;
import com.wellsoft.pt.basicdata.datastore.support.Condition;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Description: 如何描述该类
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2023年04月12日   chenq	 Create
 * </pre>
 */
public class DmQueryInfo implements Serializable {
    private static final long serialVersionUID = 6430702502824844458L;

    private List<Condition> conditions = new ArrayList<Condition>(0);

    private Map<String, Object> queryParams = new HashMap<String, Object>();

    private List<DataStoreOrder> orders = new ArrayList<DataStoreOrder>(0);

    private PagingInfo pagingInfo;

    public List<Condition> getConditions() {
        return this.conditions;
    }

    public void setConditions(final List<Condition> conditions) {
        this.conditions = conditions;
    }

    public Map<String, Object> getQueryParams() {
        return this.queryParams;
    }

    public void setQueryParams(final Map<String, Object> queryParams) {
        this.queryParams = queryParams;
    }

    public List<DataStoreOrder> getOrders() {
        return this.orders;
    }

    public void setOrders(final List<DataStoreOrder> orders) {
        this.orders = orders;
    }

    public PagingInfo getPagingInfo() {
        return this.pagingInfo;
    }

    public void setPagingInfo(final PagingInfo pagingInfo) {
        this.pagingInfo = pagingInfo;
    }
}
