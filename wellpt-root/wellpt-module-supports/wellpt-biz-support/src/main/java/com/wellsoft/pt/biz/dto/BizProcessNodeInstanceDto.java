/*
 * @(#)10/18/22 V1.0
 *
 * Copyright 2022 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.biz.dto;

import com.wellsoft.pt.biz.entity.BizProcessNodeInstanceEntity;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 10/18/22.1	zhulh		10/18/22		Create
 * </pre>
 * @date 10/18/22
 */
public class BizProcessNodeInstanceDto extends BizProcessNodeInstanceEntity {

    // 状态名称
    private String stateName;

    // 业务事项实例数量
    private int processItemInstCount;

    /**
     * @return the stateName
     */
    public String getStateName() {
        return stateName;
    }

    /**
     * @param stateName 要设置的stateName
     */
    public void setStateName(String stateName) {
        this.stateName = stateName;
    }

    /**
     * @return the processItemInstCount
     */
    public int getProcessItemInstCount() {
        return processItemInstCount;
    }

    /**
     * @param processItemInstCount 要设置的processItemInstCount
     */
    public void setProcessItemInstCount(int processItemInstCount) {
        this.processItemInstCount = processItemInstCount;
    }
}
