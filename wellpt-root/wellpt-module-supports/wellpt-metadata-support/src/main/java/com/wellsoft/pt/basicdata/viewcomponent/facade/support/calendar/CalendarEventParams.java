/*
 * @(#)2016年11月1日 V1.0
 *
 * Copyright 2016 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.viewcomponent.facade.support.calendar;

import com.wellsoft.pt.basicdata.datastore.support.Condition;

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
public class CalendarEventParams {
    private String dataProviderId;
    private String belongObjId;
    private Date startTime;
    private Date endTime;
    // 查询类型
    private List<Condition> criterions = new ArrayList<Condition>(0);
    private Map<String, Object> params = new HashMap<String, Object>(0);

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

    public Map<String, Object> getParams() {
        return params;
    }

    public void setParams(Map<String, Object> params) {
        this.params = params;
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

    /**
     * @return the dataProviderId
     */
    public String getDataProviderId() {
        return dataProviderId;
    }

    /**
     * @param dataProviderId 要设置的dataProviderId
     */
    public void setDataProviderId(String dataProviderId) {
        this.dataProviderId = dataProviderId;
    }

    /**
     * @return the belongObjId
     */
    public String getBelongObjId() {
        return belongObjId;
    }

    /**
     * @param belongObjId 要设置的belongObjId
     */
    public void setBelongObjId(String belongObjId) {
        this.belongObjId = belongObjId;
    }

    /**
     * @return the startTime
     */
    public Date getStartTime() {
        return startTime;
    }

    /**
     * @param startTime 要设置的startTime
     */
    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    /**
     * @return the endTime
     */
    public Date getEndTime() {
        return endTime;
    }

    /**
     * @param endTime 要设置的endTime
     */
    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }
}
