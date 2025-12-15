package com.wellsoft.pt.jpa.criteria;

import java.io.Serializable;
import java.util.List;

/**
 * Description: 数据查询接口
 *
 * @author Xiem
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 23 Nov 2016.1	Xiem		23 Nov 2016		Create
 * </pre>
 * @date 23 Nov 2016
 */
public interface QueryInterface {

    /**
     * 返回查询名称
     *
     * @return
     */
    public String getQueryName();

    /**
     * 返回查询信息元数据
     *
     * @param context
     * @return
     */
    public CriteriaMetadata initCriteriaMetadata(QueryContext context);

    /**
     * 返回查询数据
     *
     * @param criteria
     * @return
     */
    public <ITEM extends Serializable> List<ITEM> list(QueryContext context);

    /**
     * 返回查询数据信息
     *
     * @param context
     * @param itemClass
     * @return
     */
    public <ITEM extends Serializable> List<ITEM> list(QueryContext context, Class<ITEM> itemClass);

    /**
     * 返回查询的条数
     *
     * @param context
     * @return
     */
    public long count(QueryContext context);

    // 返回接口的使用说明
    public String getInterfaceDesc();

}
