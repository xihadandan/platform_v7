/*
 * @(#)2016年5月10日 V1.0
 *
 * Copyright 2016 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.design.component;

import com.wellsoft.pt.app.ui.ComponentCategory;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2016年5月10日.1	zhulh		2016年5月10日		Create
 * </pre>
 * @date 2016年5月10日
 */
public class SimpleComponentCategory implements ComponentCategory {

    // 布局组件
    public static final SimpleComponentCategory LAYOUT = new SimpleComponentCategory("layout", "布局组件", 1);
    // 基本组件
    public static final SimpleComponentCategory BASIC = new SimpleComponentCategory("basic", "基本组件", 2);
    // 应用组件
    public static final SimpleComponentCategory APP = new SimpleComponentCategory("app", "应用组件", 3);
    // 报表组件
    public static final SimpleComponentCategory REPORT = new SimpleComponentCategory("report", "报表组件", 4);
    // 图表组件
    //public static final SimpleComponentCategory CHART = new SimpleComponentCategory("chart", "图表组件", 5);

    // 分类ID
    private String id;
    // 分类名称
    private String name;
    // 排序
    private int order;

    /**
     * @param id
     * @param name
     */
    public SimpleComponentCategory(String id, String name, int order) {
        super();
        this.id = id;
        this.name = name;
        this.order = order;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.ui.ComponentCategory#getId()
     */
    @Override
    public String getId() {
        return this.id;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.ui.ComponentCategory#getName()
     */
    @Override
    public String getName() {
        return this.name;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.ui.ComponentCategory#getOrder()
     */
    @Override
    public int getOrder() {
        return this.order;
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }

    /**
     * (non-Javadoc)
     *
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        SimpleComponentCategory other = (SimpleComponentCategory) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }

}
