package com.wellsoft.pt.jpa.criteria;

import com.wellsoft.context.jdbc.support.PagingInfo;
import com.wellsoft.context.util.json.JsonUtils;
import com.wellsoft.pt.jpa.dao.NativeDao;
import org.apache.commons.lang.StringUtils;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class InterfaceCriteria extends AbstractNativeCriteria implements QueryContext {
    private static final String KEY_KEYWORD = "keyword";

    private final QueryInterface query;

    private final Map<Object, Object> context = new HashMap<Object, Object>();

    private String whereSqlString;

    private boolean fillParams = false;

    private String interfaceParam;

    public InterfaceCriteria(NativeDao nativeDao, QueryInterface query, String interfaceParams) {
        super(nativeDao);
        this.query = query;
        this.interfaceParam = interfaceParams;
    }

    @Override
    public synchronized <ITEM extends Serializable> List<ITEM> list() {
        fillParams();
        if (this.pagingInfo == null) {
            this.pagingInfo = new PagingInfo(1, Integer.MAX_VALUE, false);
        }
        List<ITEM> list = query.list(this);
        if (this.pagingInfo.isAutoCount() && this.pagingInfo.getTotalCount() <= 0) {
            this.pagingInfo.setTotalCount(count());
        }
        return list;
    }

    @Override
    public synchronized <ITEM extends Serializable> List<ITEM> list(Class<ITEM> itemClass) {
        fillParams();
        if (this.pagingInfo == null) {
            this.pagingInfo = new PagingInfo(1, Integer.MAX_VALUE, false);
        }
        List<ITEM> list = query.list(this, itemClass);
        if (this.pagingInfo.isAutoCount() && this.pagingInfo.getTotalCount() <= 0) {
            this.pagingInfo.setTotalCount(count());
        }
        return list;
    }

    @Override
    public synchronized long count() {
        fillParams();
        if (this.pagingInfo == null) {
            this.pagingInfo = new PagingInfo(1, Integer.MAX_VALUE, true);
        }
        return query.count(this);
    }

    /**
     * 填充查询参数
     */
    private void fillParams() {
        if (!fillParams) {
            this.getSqlString();
            fillParams = true;
        }
    }

    @Override
    protected synchronized CriteriaMetadata initCriteriaMetadata() {
        return query.initCriteriaMetadata(this);

    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.core.criteria.Criteria#getColumnName(java.lang.String)
     */
    @Override
    public String getColumnName(String columnIndex) {
        return getCriteriaMetadata().getMapColumnIndex(columnIndex);
    }

    @Override
    protected String getFromSql() {
        return "from dual";
    }

    public QueryInterface getQuery() {
        return query;
    }

    public NativeDao getNativeDao() {
        return nativeDao;
    }

    @Override
    public Object put(Object key, Object value) {
        return context.put(key, value);
    }

    @Override
    public Object get(Object key) {
        return context.get(key);
    }

    @Override
    public String getSqlString() {
        if (StringUtils.isBlank(this.whereSqlString)) {
            this.whereSqlString = this.getCriterion().toSqlString(this);
        }
        return this.whereSqlString;
    }

    @Override
    public String getWhereSqlString() {
        if (StringUtils.isBlank(this.whereSqlString)) {
            this.whereSqlString = this.getCriterion().toSqlString(this);
        }
        return this.whereSqlString;
    }

    @Override
    public Map<Object, Object> getContext() {
        return context;
    }

    @Override
    public boolean contains(Object key) {
        return context.containsKey(key);
    }

    @Override
    public Set<Object> keySet() {
        return context.keySet();
    }

    @Override
    public Object remove(Object key) {
        return context.remove(key);
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.core.criteria.QueryContext#getOrderString()
     */
    @Override
    public String getOrderString() {
        return this.getOrderSql();
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.core.criteria.QueryContext#getCriteria()
     */
    @Override
    public Criteria getCriteria() {
        return this;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.core.criteria.QueryContext#isKeywordQuery()
     */
    @Override
    public boolean isKeywordQuery() {
        fillParams();
        Object keyword = getQueryParams().get(KEY_KEYWORD);
        if (keyword == null) {
            return false;
        }
        return StringUtils.isNotBlank(keyword.toString());
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.core.criteria.QueryContext#getKeyword()
     */
    @Override
    public String getKeyword() {
        return (String) getQueryParams().get(KEY_KEYWORD);
    }

    /**
     * @return the interfaceParam
     */
    public String getInterfaceParam() {
        return this.interfaceParam;
    }

    @Override
    public <T> T interfaceParam(Class<T> i) {
        if (StringUtils.isNotBlank(this.interfaceParam)) {
            return JsonUtils.json2Object(this.interfaceParam, i);
//            try {
//                return new Gson().fromJson(this.interfaceParam, i);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
        }
        return null;
    }


}
