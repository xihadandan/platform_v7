/*
 * @(#)2016年11月1日 V1.0
 *
 * Copyright 2016 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.datastore.bean;

import com.google.common.collect.Maps;
import com.wellsoft.context.jdbc.support.PagingInfo;
import com.wellsoft.pt.basicdata.datastore.support.Condition;
import com.wellsoft.pt.basicdata.datastore.support.DataStoreProxy;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.time.DateUtils;

import java.util.*;

/**
 * Description: 如何描述该类
 *
 * @author xiem
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2016年11月1日.1	xiem		2016年11月1日		Create
 * </pre>
 * @date 2016年11月1日
 */
@ApiModel("数据仓库查询参数")
public class DataStoreParams {
    @ApiModelProperty("数据仓库ID")
    private String dataStoreId;
    // 查询类型
    @ApiModelProperty("查询类型")
    private DataStoreProxy proxy = null;
    @ApiModelProperty("查询条件列表")
    private List<Condition> criterions = new ArrayList<Condition>(0);
    @ApiModelProperty("查询参数")
    private Map<String, Object> params = new HashMap<String, Object>(0);
    @ApiModelProperty("渲染器列表")
    private List<DataStoreRendererBean> renderers = new ArrayList<DataStoreRendererBean>(0);
    @ApiModelProperty("排序信息列表")
    private List<DataStoreOrder> orders = new ArrayList<DataStoreOrder>(0);
    @ApiModelProperty("分页信息")
    private PagingInfo pagingInfo = new PagingInfo(1, Integer.MAX_VALUE);
    @ApiModelProperty("渲染器哈希表")
    private Map<String, DataStoreRendererBean> renderersMap;

    private List<String> selectColumNames = Lists.newArrayList();

    /**
     * @return the dataStoreId
     */
    public String getDataStoreId() {
        return dataStoreId;
    }

    /**
     * @param dataStoreId 要设置的dataStoreId
     */
    public void setDataStoreId(String dataStoreId) {
        this.dataStoreId = dataStoreId;
    }

    /**
     * @return the proxy
     */
    public DataStoreProxy getProxy() {
        return proxy;
    }

    /**
     * @param proxy 要设置的proxy
     */
    public void setProxy(DataStoreProxy proxy) {
        this.proxy = proxy;
    }

    /**
     * @return the criterions
     */
    public List<Condition> getCriterions() {
        return criterions;
    }

    /**
     * @param criterions 要设置的criterions
     */
    public void setCriterions(List<Condition> criterions) {
        this.criterions = criterions;
    }

    /**
     * @return the renderers
     */
    public List<DataStoreRendererBean> getRenderers() {
        return renderers;
    }

    /**
     * @param renderers 要设置的renderers
     */
    public void setRenderers(List<DataStoreRendererBean> renderers) {
        this.renderers = renderers;
    }

    /**
     * @return the pagingInfo
     */
    public PagingInfo getPagingInfo() {
        return pagingInfo;
    }

    /**
     * @param pagingInfo 要设置的pagingInfo
     */
    public void setPagingInfo(PagingInfo pagingInfo) {
        this.pagingInfo = pagingInfo;
    }

    /**
     * @return the orders
     */
    public List<DataStoreOrder> getOrders() {
        return orders;
    }

    /**
     * @param orders 要设置的orders
     */
    public void setOrders(List<DataStoreOrder> orders) {
        this.orders = orders;
    }

    public Map<String, Object> getParams() {
        return params;
    }

    public void setParams(Map<String, Object> params) {
        if (MapUtils.isNotEmpty(params)) {
            Map<String, Object> p = Maps.newHashMap();
            // 格式化处理参数对象
            Set<Map.Entry<String, Object>> entries = params.entrySet();
            for (Map.Entry<String, Object> ent : entries) {
                if (ent.getKey().indexOf("#DATE(") != -1) {
                    if (ent.getValue() != null) {
                        String[] parts = ent.getKey().split("#");
                        try {
                            String pattern = parts[1].substring(parts[1].indexOf("(") + 1, parts[1].lastIndexOf(")"));
                            pattern = pattern.replaceAll("D", "d");
                            p.put(parts[0], DateUtils.parseDate(ent.getValue().toString(), pattern));
                        } catch (Exception e) {
                        }
                    }
                } else if (ent.getKey().indexOf("#NUMBER") != -1) {
                    if (ent.getValue() != null) {
                        String[] parts = ent.getKey().split("#");
                        try {
                            p.put(parts[0], NumberUtils.createNumber(ent.getValue().toString()));
                        } catch (Exception e) {
                        }
                    }
                } else {
                    p.put(ent.getKey(), ent.getValue());
                }
            }
            this.params = p;
        }
    }

    public Object getParam(String key) {
        return params.get(key);
    }

    public Object getParam(String key, Object defaultValue) {
        if (params.containsKey(key)) {
            return params.get(key);
        }
        return defaultValue;
    }

    public Map<String, DataStoreRendererBean> getRenderersMap() {
        if (renderersMap == null) {
            renderersMap = new LinkedHashMap<String, DataStoreRendererBean>();
            for (DataStoreRendererBean renderer : renderers) {
                renderersMap.put(renderer.getColumnIndex(), renderer);
            }
        }
        return renderersMap;
    }

    public List<String> getSelectColumNames() {
        return selectColumNames;
    }

    public void setSelectColumNames(List<String> selectColumNames) {
        this.selectColumNames = selectColumNames;
    }
}
