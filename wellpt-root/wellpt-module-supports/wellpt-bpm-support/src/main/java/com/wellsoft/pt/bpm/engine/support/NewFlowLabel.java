/*
 * @(#)2021年1月22日 V1.0
 *
 * Copyright 2021 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.support;

import com.wellsoft.context.base.BaseObject;

import java.util.List;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2021年1月22日.1	zhulh		2021年1月22日		Create
 * </pre>
 * @date 2021年1月22日
 */
public class NewFlowLabel extends BaseObject implements Comparable<NewFlowLabel> {
    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = -5439457609790786902L;

    // 流程标签或子流程名称
    private String label;
    // 子流程ID
    private List<String> ids;
    // 是否流程标签
    private Boolean isLabel;
    // 新流程是否主办
    private Boolean isMajor;
    // 分发粒度
    private String granularity;

    /**
     * @param label
     * @param ids
     * @param isLabel
     * @param isMajor
     */
    public NewFlowLabel(String label, List<String> ids, boolean isLabel, boolean isMajor) {
        super();
        this.label = label;
        this.ids = ids;
        this.isLabel = isLabel;
        this.isMajor = isMajor;
    }

    /**
     * @return the label
     */
    public String getLabel() {
        return label;
    }

    /**
     * @param label 要设置的label
     */
    public void setLabel(String label) {
        this.label = label;
    }

    /**
     * @return the ids
     */
    public List<String> getIds() {
        return ids;
    }

    /**
     * @param ids 要设置的ids
     */
    public void setIds(List<String> ids) {
        this.ids = ids;
    }

    /**
     * @return the isLabel
     */
    public Boolean getIsLabel() {
        return isLabel;
    }

    /**
     * @param isLabel 要设置的isLabel
     */
    public void setIsLabel(Boolean isLabel) {
        this.isLabel = isLabel;
    }

    /**
     * @return the isMajor
     */
    public Boolean getIsMajor() {
        return isMajor;
    }

    /**
     * @param isMajor 要设置的isMajor
     */
    public void setIsMajor(Boolean isMajor) {
        this.isMajor = isMajor;
    }

    /**
     * @return the granularity
     */
    public String getGranularity() {
        return granularity;
    }

    /**
     * @param granularity 要设置的granularity
     */
    public void setGranularity(String granularity) {
        this.granularity = granularity;
    }

    /**
     * (non-Javadoc)
     *
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((isLabel == null) ? 0 : isLabel.hashCode());
        result = prime * result + ((label == null) ? 0 : label.hashCode());
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
        NewFlowLabel other = (NewFlowLabel) obj;
        if (isLabel == null) {
            if (other.isLabel != null)
                return false;
        } else if (!isLabel.equals(other.isLabel))
            return false;
        if (label == null) {
            if (other.label != null)
                return false;
        } else if (!label.equals(other.label))
            return false;
        return true;
    }

    /**
     * (non-Javadoc)
     *
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    @Override
    public int compareTo(NewFlowLabel o) {
        return Boolean.valueOf(this.isLabel).compareTo(o.isLabel);
    }

}
