/*
 * @(#)2014-10-28 V1.0
 *
 * Copyright 2014 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.enums;

/**
 * Description: 任务流转代码
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2014-10-28.1	zhulh		2014-10-28		Create
 * </pre>
 * @date 2014-10-28
 */
public enum TransferCode {
    // 提交流转
    Submit(1),
    // 退回流转
    RollBack(2),
    // 撤回流转
    Cancel(3),
    // 移交环节流转
    GotoTask(4),
    // 办理人为空自动跳过
    SkipTask(5),
    // 转办提交流转
    TransferSubmit(6),
    // 委托提交流转
    DelegationSubmit(7);

    private Integer code;

    private TransferCode(Integer code) {
        this.code = code;
    }

    /**
     * @return the code
     */
    public Integer getCode() {
        return code;
    }

    /**
     * @param code 要设置的code
     */
    public void setCode(Integer code) {
        this.code = code;
    }

}
