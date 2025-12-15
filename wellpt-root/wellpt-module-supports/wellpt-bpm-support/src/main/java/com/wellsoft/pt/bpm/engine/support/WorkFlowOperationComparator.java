/*
 * @(#)2017-02-14 V1.0
 *
 * Copyright 2017 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.support;

import com.wellsoft.pt.bpm.engine.form.CustomDynamicButton;
import com.wellsoft.pt.workflow.enums.WorkFlowPrivilege;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

/**
 * Description: 流程操作按钮排序比较器
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2017-02-14.1	zhulh		2017-02-14		Create
 * </pre>
 * @date 2017-02-14
 */
public class WorkFlowOperationComparator implements Comparator<CustomDynamicButton> {

    private static Map<String, Integer> orderMap = new HashMap<String, Integer>();

    static {
        // 点击
        orderMap.put(WorkFlowPrivilege.Click.getCode(), 0);
        // 关闭
        orderMap.put(WorkFlowPrivilege.Close.getCode(), 29 * 10);
        // 保存
        orderMap.put(WorkFlowPrivilege.Save.getCode(), 5 * 10);
        // 提交
        orderMap.put(WorkFlowPrivilege.Submit.getCode(), 1 * 10);
        // 退回
        orderMap.put(WorkFlowPrivilege.Rollback.getCode(), 2 * 10);
        // 直接退回
        orderMap.put(WorkFlowPrivilege.DirectRollback.getCode(), 3 * 10);
        // 退回主流程
        orderMap.put(WorkFlowPrivilege.RollbackToMainFlow.getCode(), 28 * 10);
        // 查看主流程
        orderMap.put(WorkFlowPrivilege.ViewTheMainFlow.getCode(), 29 * 10);
        // 撤回
        orderMap.put(WorkFlowPrivilege.Cancel.getCode(), 4 * 10);
        // 转办
        orderMap.put(WorkFlowPrivilege.Transfer.getCode(), 6 * 10);
        // 会签
        orderMap.put(WorkFlowPrivilege.CounterSign.getCode(), 7 * 10);
        // 关注
        orderMap.put(WorkFlowPrivilege.Attention.getCode(), 9 * 10);
        // 打印
        orderMap.put(WorkFlowPrivilege.Print.getCode(), 11 * 10);
        // 抄送
        orderMap.put(WorkFlowPrivilege.CopyTo.getCode(), 8 * 10);
        // 签署意见
        orderMap.put(WorkFlowPrivilege.SignOpinion.getCode(), 12 * 10);
        // 取消关注
        orderMap.put(WorkFlowPrivilege.Unfollow.getCode(), 10 * 10);
        // 挂起
        orderMap.put(WorkFlowPrivilege.Suspend.getCode(), 13 * 10);
        // 恢复
        orderMap.put(WorkFlowPrivilege.Resume.getCode(), 14 * 10);
        // 催办
        orderMap.put(WorkFlowPrivilege.Remind.getCode(), 15 * 10);
        // 特送个人
        orderMap.put(WorkFlowPrivilege.HandOver.getCode(), 16 * 10);
        // 特送环节
        orderMap.put(WorkFlowPrivilege.GotoTask.getCode(), 17 * 10);
        // 删除
        orderMap.put(WorkFlowPrivilege.Delete.getCode(), 18 * 10);
        // 管理员删除
        orderMap.put(WorkFlowPrivilege.AdminDelete.getCode(), 19 * 10);
        // 办理过程
        orderMap.put(WorkFlowPrivilege.ViewProcess.getCode(), 20 * 10);
        // 不可编辑文档
        orderMap.put(WorkFlowPrivilege.Edit.getCode(), 21 * 10);
        // 必须签署意见
        orderMap.put(WorkFlowPrivilege.RequiredSignOpinion.getCode(), 22 * 10);
        // 转办必填意见
        orderMap.put(WorkFlowPrivilege.RequiredTransferOpinion.getCode(), 23 * 10);
        // 会签必填意见
        orderMap.put(WorkFlowPrivilege.RequiredCounterSignOpinion.getCode(), 24 * 10);
        // 退回必填意见
        orderMap.put(WorkFlowPrivilege.RequiredRollbackOpinion.getCode(), 25 * 10);
        // 特送个人必填意见
        orderMap.put(WorkFlowPrivilege.RequiredHandOverOpinion.getCode(), 26 * 10);
        // 特送环节必填意见
        orderMap.put(WorkFlowPrivilege.RequiredGotoTaskOpinion.getCode(), 27 * 10);

        // 查看阅读记录
        orderMap.put(WorkFlowPrivilege.ViewReadLog.getCode(), 9 * 10);
        // 查看流程数据快照
        orderMap.put(WorkFlowPrivilege.ViewFlowDataSnapshot.getCode(), 28 * 10);
        // 打印表单
        orderMap.put(WorkFlowPrivilege.PrintForm.getCode(), 115);
        // 版式文档处理
        orderMap.put(WorkFlowPrivilege.LayoutDocumentProcess.getCode(), 116);
    }

    /**
     * 获取流程按钮的排序号
     *
     * @param code
     * @return
     */
    public static int getOrder(String code) {
        Integer order = orderMap.get(code);
        if (order == null) {
            order = 0;
        }
        return order;
    }

    /**
     * (non-Javadoc)
     *
     * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
     */
    @Override
    public int compare(CustomDynamicButton o1, CustomDynamicButton o2) {
        Integer order1 = 0;
        Integer order2 = 0;
        String key1 = o1.getCode();
        String key2 = o2.getCode();
        if (orderMap.containsKey(key1)) {
            order1 = orderMap.get(key1);
        }
        if (orderMap.containsKey(key2)) {
            order2 = orderMap.get(key2);
        }
        return order1.compareTo(order2);
    }

}
