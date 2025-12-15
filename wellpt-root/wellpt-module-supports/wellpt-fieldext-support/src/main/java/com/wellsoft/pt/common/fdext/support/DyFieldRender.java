/*
 * @(#)2016年3月21日 V1.0
 *
 * Copyright 2016 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.common.fdext.support;

import java.util.List;
import java.util.Map;

/**
 * Description: 如何描述该类
 *
 * @author zhongzh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2016年3月21日.1	zhongzh		2016年3月21日		Create
 * </pre>
 * @date 2016年3月21日
 */
public class DyFieldRender implements IDyFieldRender {

    protected boolean renderData;

    protected ICdDataValue<?> idata;

    protected List<ICdFieldRender> renders;

    /**
     * 如何描述该构造方法
     *
     * @param renders
     */
    public DyFieldRender(List<ICdFieldRender> renders) {
        this.renders = renders;
    }

    /**
     * 如何描述该构造方法
     *
     * @param renders
     * @param data
     */
    public DyFieldRender(List<ICdFieldRender> renders, ICdDataValue<?> data) {
        this.idata = data;
        this.renders = renders;
    }

    /**
     * 如何描述该构造方法
     *
     * @param renders
     * @param data
     */
    public DyFieldRender(List<ICdFieldRender> renders, Map<String, Object> data) {
        if (data != null) {
            this.idata = new MapDataValue(data);
        }
        this.renders = renders;
    }

    /**
     * 如何描述该构造方法
     *
     * @param renders
     * @param data
     */
    public DyFieldRender(List<ICdFieldRender> renders, ICdFieldValue data) {
        if (data != null) {
            this.idata = new BeanDataValue(data);
        }
        this.renders = renders;
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.common.fdext.support.IDyFieldRender#getRenders()
     */
    @Override
    public List<ICdFieldRender> getRenders() {
        return renders;
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.common.fdext.support.IDyFieldRender#isRenderData()
     */
    @Override
    public boolean isRenderData() {
        return renderData;
    }

    public void setRenderData(boolean renderData) {
        this.renderData = renderData;
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.common.fdext.support.IDyFieldRender#getData()
     */
    @SuppressWarnings("unchecked")
    @Override
    public <T> T getData() {
        return idata == null ? null : ((ICdDataValue<T>) idata).getData();
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.common.fdext.support.IDyFieldRender#getValue(java.lang.String, java.lang.String)
     */
    @Override
    public Object getValue(String fieldName, String defaultValue) {
        if (isRenderData() == false) {
            return "";
        } else if (idata == null) {
            return defaultValue;
        }
        return idata.getValue(fieldName);
    }

}
