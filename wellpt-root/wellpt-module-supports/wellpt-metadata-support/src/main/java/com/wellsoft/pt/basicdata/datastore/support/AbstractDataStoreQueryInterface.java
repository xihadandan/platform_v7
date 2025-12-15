package com.wellsoft.pt.basicdata.datastore.support;

import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.pt.jpa.criteria.AbstractQueryInterface;
import com.wellsoft.pt.jpa.criteria.InterfaceParam;
import com.wellsoft.pt.jpa.criteria.QueryContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.List;

/**
 * Description: 如何描述该类
 *
 * @author Xiem
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 26 Dec 2016.1	Xiem		26 Dec 2016		Create
 * </pre>
 * @date 26 Dec 2016
 */
public abstract class AbstractDataStoreQueryInterface extends
        AbstractQueryInterface {
    protected final static Logger LOGGER = LoggerFactory.getLogger(AbstractQueryInterface.class);

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.core.criteria.QueryInterface#list(com.wellsoft.pt.core.criteria.QueryContext)
     */
    @SuppressWarnings("unchecked")
    @Override
    public final <ITEM extends Serializable> List<ITEM> list(QueryContext context) {
        return (List<ITEM>) this.query(context);
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.core.criteria.QueryInterface#list(com.wellsoft.pt.core.criteria.QueryContext, java.lang.Class)
     */
    @SuppressWarnings("unchecked")
    @Override
    public final <ITEM extends Serializable> List<ITEM> list(QueryContext context,
                                                             Class<ITEM> itemClass) {
        return (List<ITEM>) this.query(context);
    }

    /**
     * 如何描述该方法
     *
     * @param context
     * @return
     */
    public abstract List<QueryItem> query(QueryContext context);

    @Override
    public String getInterfaceDesc() {
        return "";
    }


    /**
     * 接口参数定义类
     *
     * @return
     */
    public Class<? extends InterfaceParam> interfaceParamsClass() {
        return null;
    }


}
