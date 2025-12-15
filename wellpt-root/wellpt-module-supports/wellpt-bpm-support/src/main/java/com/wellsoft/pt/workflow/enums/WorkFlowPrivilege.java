/*
 * @(#)2014-9-26 V1.0
 *
 * Copyright 2014 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.workflow.enums;

import org.apache.commons.lang.StringUtils;

/**
 * Description: 如何描述该类
 *
 * @author Asus
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2014-9-26.1	zhulh		2014-9-26		Create
 * </pre>
 * @date 2014-9-26
 */
public enum WorkFlowPrivilege {
    // 点击
    Click("点击", "B004000"),
    // 关闭
    Close("保存", "B004019"),
    // 保存
    Save("保存", "B004001"),
    // 提交
    Submit("提交", "B004002"),
    // 完成
    Complete("完成", "B004044"),
    // 退回
    Rollback("退回", "B004003"),
    // 直接退回
    DirectRollback("直接退回", "B004004"),
    // 撤回
    Cancel("撤回", "B004005"),
    // 转办
    Transfer("转办", "B004006"),
    // 会签
    CounterSign("会签", "B004007"),
    // 加签
    AddSign("加签", "B004042"),
    // 关注
    Attention("关注", "B004008"),
    // 套打
    Print("套打", "B004009"),
    // 抄送
    CopyTo("抄送", "B004010"),
    // 签署意见
    SignOpinion("签署意见", "B004011"),
    // 取消关注
    Unfollow("取消关注", "B004012"),
    // 办理过程
    ViewProcess("办理过程", "B004013"),
    // 催办
    Remind("催办", "B004014"),
    // 特送个人
    HandOver("移交办理人", "B004015"),
    // 特送环节
    GotoTask("跳转环节", "B004016"),
    // 挂起
    Suspend("挂起", "B004017"),
    // 恢复
    Resume("恢复", "B004018"),
    // 删除
    Delete("删除", "B004023"),
    // 恢复
    Recover("恢复", "B004046"),
    // 管理员删除
    AdminDelete("管理员删除", "B004024"),
    // 不可编辑文档
    Edit("不可编辑文档", "B004025"),
    // 必须签署意见
    RequiredSignOpinion("必须签署意见", "B004026"),
    // 撤回必填意见
    RequiredCancelOpinion("撤回必填意见", "B004039"),
    // 转办必填意见
    RequiredTransferOpinion("转办必填意见", "B004029"),
    // 会签必填意见
    RequiredCounterSignOpinion("会签必填意见", "B004030"),
    // 加签必填意见
    RequiredAddSignOpinion("加签必填意见", "B004043"),
    // 退回必填意见
    RequiredRollbackOpinion("退回必填意见", "B004031"),
    // 特送个人必填意见
    RequiredHandOverOpinion("特送个人必填意见", "B004032"),
    // 特送环节必填意见
    RequiredGotoTaskOpinion("特送环节必填意见", "B004033"),
    // 催办环节必填意见
    RequiredRemindOpinion("催办环节必填意见", "B004034"),
    // 退回主流程
    RollbackToMainFlow("退回主流程", "B004035"),

    ViewTheMainFlow("查看主流程", "B004095"),
    // 查看阅读记录
    ViewReadLog("查看阅读记录", "B004037"),

    // 查看流程数据快照
    ViewFlowDataSnapshot("查看流程数据快照", "B004038"),
    // 发起群聊
    StartGroupChat("发起群聊", "B004050"),

    PrintForm("打印表单", "B004096"),

    LayoutDocumentProcess("版式文档处理", "B004097");

    private String name;

    private String code;

    private WorkFlowPrivilege(String name, String code) {
        this.name = name;
        this.code = code;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name 要设置的name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the code
     */
    public String getCode() {
        return code;
    }

    /**
     * @param code 要设置的code
     */
    public void setCode(String code) {
        this.code = code;
    }

    /**
     * @param code
     * @return
     */
    public static WorkFlowPrivilege getByCode(String code) {
        WorkFlowPrivilege[] values = values();
        for (WorkFlowPrivilege privilege : values) {
            if (StringUtils.equals(privilege.getCode(), code)) {
                return privilege;
            }
        }
        return null;
    }
}
