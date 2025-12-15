/*
 * @(#)2014-10-28 V1.0
 *
 * Copyright 2014 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.parser.activity;

import com.google.common.collect.Maps;
import com.wellsoft.context.util.json.JsonUtils;
import com.wellsoft.context.util.reflection.ConvertUtils;
import com.wellsoft.pt.bpm.engine.entity.TaskIdentity;
import com.wellsoft.pt.bpm.engine.entity.TaskOperation;
import com.wellsoft.pt.bpm.engine.enums.ActionCode;
import com.wellsoft.pt.bpm.engine.enums.TransferCode;
import com.wellsoft.pt.bpm.engine.query.TaskActivityQueryItem;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang.StringUtils;

import java.util.*;

/**
 * Description: 如何描述该类
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
public class TaskActivityStackFactary {

    /**
     * 构建任务活动堆栈
     *
     * @param topTaskInstUuid
     * @param taskActivities
     * @return
     */
    public static TaskActivityStack build(String topTaskInstUuid, List<TaskActivityQueryItem> taskActivities) {
        return build(topTaskInstUuid, false, taskActivities);
    }

    /**
     * 构建任务活动堆栈
     *
     * @param topTaskInstUuid
     * @param addSkipTask
     * @param taskActivities
     * @return
     */
    public static TaskActivityStack build(String topTaskInstUuid, boolean addSkipTask, List<TaskActivityQueryItem> taskActivities) {
        List<TaskActivityQueryItem> activities = null;
        if (StringUtils.isNotBlank(topTaskInstUuid)) {
            // 过滤掉不在与任务topTaskInstUuid不在同级的任务活动
            activities = filterTaskActivity(topTaskInstUuid, taskActivities);
        } else {
            activities = taskActivities;
        }
        List<TaskActivityItem> items = new ArrayList<TaskActivityItem>();
        for (TaskActivityQueryItem taskActivitiy : activities) {
            TaskActivityItem item = new TaskActivityItem();
            item.setTaskId(taskActivitiy.getTaskId());
            item.setTaskName(taskActivitiy.getTaskName());
            item.setTaskInstUuid(taskActivitiy.getTaskInstUuid());
            item.setPreTaskId(taskActivitiy.getPreTaskId());
            item.setPreTaskInstUuid(taskActivitiy.getPreTaskInstUuid());
            item.setPreGatewayIds(taskActivitiy.getPreGatewayIds());
            item.setTransferCode(taskActivitiy.getTransferCode());
            item.setFlowInstUuid(taskActivitiy.getFlowInstUuid());
            item.setIsParallel(taskActivitiy.getIsParallel());
            item.setParallelTaskInstUuid(taskActivitiy.getParallelTaskInstUuid());
            item.setCreator(taskActivitiy.getCreator());
            item.setCreateTime(taskActivitiy.getCreateTime());
            item.setEndTime(taskActivitiy.getEndTime());

            items.add(item);
        }
        // 任务活动项按创建时间升序排序
        Collections.sort(items);

        // 创建任务活动堆栈，当前任务在最上面
        TaskActivityStack stack = new TaskActivityStack();
        Iterator<TaskActivityItem> it = items.iterator();
        Map<String, List<TaskActivityItem>> rollbackItemMap = Maps.newHashMap();
        while (it.hasNext()) {
            TaskActivityItem item = it.next();
            Integer transferCode = item.getTransferCode();
            transferCode = transferCode == null ? 1 : transferCode;
            TaskActivityItem top = null;
            switch (transferCode) {
                case 1:
                    // 提交流转，直接加入活动堆栈
                    stack.push(item);
                    break;
                case 2:
                    // 退回流转处理
                    top = stack.pop();
                    while (!top.getTaskId().equals(item.getTaskId()) && !stack.isEmpty()) {
                        top = stack.pop();
                        // 记录中间跳过的环节
                        if (!top.getTaskId().equals(item.getTaskId())) {
                            List<TaskActivityItem> rollbackItems = rollbackItemMap.get(item.getTaskInstUuid());
                            if (CollectionUtils.isEmpty(rollbackItems)) {
                                rollbackItems = Lists.newArrayList();
                            }
                            rollbackItems.add(top);
                            rollbackItemMap.put(item.getTaskInstUuid(), rollbackItems);
                        }
                    }
                    stack.push(item);
                    break;
                case 3:
                    // 撤回流转处理
                    String preTaskInstUuid = item.getPreTaskInstUuid();
                    TaskActivityItem preItem = getTaskActivityItem(preTaskInstUuid, items);
                    Integer preTransferCode = preItem.getTransferCode();
                    if (TransferCode.Submit.getCode().equals(preTransferCode)) {
                        top = stack.pop();
                        while (!top.getTaskId().equals(item.getTaskId()) && !stack.isEmpty()) {
                            top = stack.pop();
                        }
                        stack.push(item);
                    } else if (TransferCode.RollBack.getCode().equals(preTransferCode)) {
                        // stack.pop();
                        // 还原中间退回的环节
                        List<TaskActivityItem> rollbackItems = rollbackItemMap.get(item.getPreTaskInstUuid());
                        if (CollectionUtils.isNotEmpty(rollbackItems)) {
                            Collections.sort(rollbackItems);
                            for (TaskActivityItem taskActivityItem : rollbackItems) {
                                stack.push(taskActivityItem);
                            }
                        }
                        stack.push(item);
                    } else {
                        top = stack.pop();
                        while (!top.getTaskId().equals(item.getTaskId()) && !stack.isEmpty()) {
                            top = stack.pop();
                        }
                        stack.push(item);
                    }
                    break;
                case 4:
                    // 移交环节流转处理
                    stack.push(item);
                    break;
                case 5:
                    // 办理人为空自动跳过，当前环节为自动跳转过来的加入stack
                    if ((StringUtils.isNotBlank(topTaskInstUuid) && topTaskInstUuid.equals(item.getTaskInstUuid()))
                            || addSkipTask) {
                        stack.push(item);
                    }
                    break;
                case 6:
                    // 转办提交流转
                    stack.push(item);
                    break;
                case 7:
                    // 委托提交流转
                    stack.push(item);
                    break;
                default:
                    break;
            }
        }
        return stack;
    }

    /**
     * 过滤掉不在与任务topTaskInstUuid不在同级的任务活动
     *
     * @param taskActivities
     * @return
     */
    private static List<TaskActivityQueryItem> filterTaskActivity(String topTaskInstUuid,
                                                                  List<TaskActivityQueryItem> taskActivities) {
        List<TaskActivityQueryItem> activities = new ArrayList<TaskActivityQueryItem>();
        String taskInstUuid = topTaskInstUuid;
        Map<String, TaskActivityQueryItem> activityMap = ConvertUtils.convertElementToMap(taskActivities,
                "taskInstUuid");
        while (taskInstUuid != null && activityMap.containsKey(taskInstUuid)) {
            TaskActivityQueryItem activity = activityMap.get(taskInstUuid);
            activities.add(activity);

            taskInstUuid = activity.getPreTaskInstUuid();
        }
        return activities;
    }

    /**
     * @param preTaskInstUuid
     * @return
     */
    private static TaskActivityItem getTaskActivityItem(String preTaskInstUuid, List<TaskActivityItem> items) {
        for (TaskActivityItem taskActivityItem : items) {
            if (taskActivityItem.getTaskInstUuid().equals(preTaskInstUuid)) {
                return taskActivityItem;
            }
        }
        return null;
    }

    /**
     * @param allTaskActivities
     * @param allTaskOperations
     * @return
     */
    public static TaskActivityStack build(String topTaskInstUuid, List<TaskActivityQueryItem> allTaskActivities,
                                          List<TaskOperation> allTaskOperations) {
        // 构建任务活动堆栈
        TaskActivityStack stack = build(topTaskInstUuid, allTaskActivities);

        // 任务活动堆栈加入操作记录
        Iterator<TaskActivityItem> it = stack.iterator();
        while (it.hasNext()) {
            TaskActivityItem item = it.next();
            List<TaskOperationItem> operationItems = new ArrayList<TaskOperationItem>(0);
            String taskInstUuid = item.getTaskInstUuid();
            for (TaskOperation taskOperation : allTaskOperations) {
                if (!taskInstUuid.equals(taskOperation.getTaskInstUuid())) {
                    continue;
                }
                Integer actionCode = taskOperation.getActionCode();
                if (ActionCode.DELEGATION.getCode().equals(actionCode) || ActionCode.SKIP_TASK.getCode().equals(actionCode)
                        || ActionCode.AUTO_SUBMIT.getCode().equals(actionCode) || ActionCode.SKIP_SUBMIT.getCode().equals(actionCode)) {
                    continue;
                }
                TaskOperationItem operationItem = operation2Item(taskOperation);
                operationItems.add(operationItem);
            }
            // 任务操作项按创建时间升序排序
            Collections.sort(operationItems);

            // 任务操作堆栈
            item.setOperationStack(buildTaskOperation(operationItems));
        }
        return stack;
    }

    /**
     * 如何描述该方法
     *
     * @param taskOperation
     * @return
     */
    private static TaskOperationItem operation2Item(TaskOperation taskOperation) {
        // 环节操作项
        TaskOperationItem operationItem = new TaskOperationItem();
        operationItem.setUuid(taskOperation.getUuid());
        operationItem.setTaskId(taskOperation.getTaskId());
        operationItem.setTaskName(taskOperation.getTaskName());
        operationItem.setTaskInstUuid(taskOperation.getTaskInstUuid());
        operationItem.setOperator(taskOperation.getAssignee());
        operationItem.setOperatorIdentityId(taskOperation.getOperatorIdentityId());
        operationItem.setActionCode(taskOperation.getActionCode());
        operationItem.setCreateTime(taskOperation.getCreateTime());
        operationItem.setExtraInfo(taskOperation.getExtraInfo());
        return operationItem;
    }

    /**
     * 如何描述该方法
     *
     * @param operationItems
     * @return
     */
    private static TaskOperationStack buildTaskOperation(List<TaskOperationItem> operationItems) {
        // 创建任务活动堆栈，当前任务在最上面
        TaskOperationStack stack = new TaskOperationStack();
        Iterator<TaskOperationItem> it = operationItems.iterator();
        while (it.hasNext()) {
            TaskOperationItem item = it.next();
            Integer actionCode = item.getActionCode();
            switch (actionCode) {
                case 1:
                    // 提交 SUBMIT(1)
                    stack.push(item);
                    break;
                case 2:
                    // 会签提交 COUNTER_SIGN_SUBMIT(2)
                    stack.push(item);
                    break;
                case 3:
                    // 转办提交 TRANSFER_SUBMIT(3)
                    stack.push(item);
                    break;
                case 4:
                    // 退回 ROLLBACK(4) 节点流转操作结束
                    stack.push(item);
                    break;
                case 5:
                    // 直接退回 DIRECT_ROLLBACK(5) 节点流转操作结束
                    stack.push(item);
                    break;
                case 6:
                    // 撤回 CANCEL(6)
                    String itemExtraInfo = item.getExtraInfo();
                    Iterator<TaskOperationItem> oit = stack.iterator();
                    List<TaskOperationItem> delItems = new ArrayList<TaskOperationItem>();
                    while (oit.hasNext()) {
                        TaskOperationItem oitem = oit.next();
                        String oitemExtraInfo = oitem.getExtraInfo();
                        if (StringUtils.isNotBlank(itemExtraInfo) && StringUtils.isNotBlank(oitemExtraInfo)) {
                            if (itemExtraInfo.equals(oitemExtraInfo)) {
                                delItems.add(oitem);
                            } else {
                                TaskIdentity itemTi = JsonUtils.json2Object(itemExtraInfo, TaskIdentity.class);
                                TaskIdentity oitemTi = JsonUtils.json2Object(oitemExtraInfo, TaskIdentity.class);
                                if (itemTi != null && oitemTi != null
                                        && itemTi.getUuid().equals(oitemTi.getSourceTaskIdentityUuid())) {
                                    delItems.add(oitem);
                                }
                            }
                        }
                    }
                    stack.removeAll(delItems);
                    // stack.push(item);
                    break;
                case 7:
                    // 转办 TRANSFER(7)
                    stack.push(item);
                    break;
                case 8:
                    // 会签 COUNTER_SIGN(8)
                    stack.push(item);
                    break;
                case 9:
                    // 关注 ATTENTION(9)
                    // stack.push(item);
                    break;
                case 10:
                    // 取消关注 UNFOLLOW(10)
                    // stack.push(item);
                    break;
                case 11:
                    // 抄送 COPY_TO(11)
                    break;
                case 12:
                    // 催办 REMIND(12)
                    break;
                case 13:
                    // 标记已阅 MARK_READ(13)
                    break;
                case 14:
                    // 标记未阅 MARK_UNREAD(14)
                    break;
                case 15:
                    // 移交人员 HAND_OVER(15)
                    break;
                case 16:
                    // 移交环节 GOTO_TASK(16)
                    stack.push(item);
                    break;
                case 17:
                    // 签署意见 SIGN_OPINION(17)
                    break;
                case 18:
                    // 挂起 SUSPEND(18)
                    break;
                case 19:
                    // 恢复 RESUME(19)
                    break;
                case 20:
                    // 计时器挂起 SUSPEND_TIMER(20)
                    break;
                case 21:
                    // 计时器恢复 RESUME_TIMER(21)
                    break;
                case 22:
                    // 拒绝，直接结束 REJECT(22)
                    break;
                case 23:
                    // 委托
                    break;
                case 27:
                    // 委托提交 DELEGATION_SUBMIT(27)
                    stack.push(item);
                    break;
                default:
                    break;
            }
        }
        return stack;
    }

    /**
     * 如何描述该方法
     *
     * @param allTaskOperations
     * @param preTaskInstUuid
     * @return
     */
    public static List<TaskOperationItem> convert2OperationItem(List<TaskOperation> allTaskOperations,
                                                                String taskInstUuid) {
        List<TaskOperationItem> operationItems = new ArrayList<TaskOperationItem>(0);
        for (TaskOperation taskOperation : allTaskOperations) {
            TaskOperationItem operationItem = operation2Item(taskOperation);
            operationItems.add(operationItem);
        }

        if (StringUtils.isNotBlank(taskInstUuid)) {
            List<TaskOperationItem> items = new ArrayList<TaskOperationItem>(0);
            for (TaskOperationItem taskOperationItem : operationItems) {
                if (taskInstUuid.equals(taskOperationItem.getTaskInstUuid())) {
                    items.add(taskOperationItem);
                }
            }
            return items;
        }

        return operationItems;
    }

    /**
     * 如何描述该方法
     *
     * @param stack
     */
    public static void print(TaskActivityStack stack) {
        System.out.println(stack.toString());
        Iterator<TaskActivityItem> e = stack.iterator();
        while (e.hasNext()) {
            System.out.println(e.next());
        }
    }

    public static List<TaskActivityItem> sortTaskActivityItem(List<TaskActivityItem> taskActivityItems, boolean asc) {
        if (asc) {
            Collections.sort(taskActivityItems, new TaskActivityItemAscComparator());
        } else {
            Collections.sort(taskActivityItems, new TaskActivityItemDescComparator());
        }
        return taskActivityItems;
    }

    public static List<TaskOperationItem> sortTaskOperationItem(List<TaskOperationItem> taskOperationItems, boolean asc) {
        if (asc) {
            Collections.sort(taskOperationItems, new TaskOperationItemAscComparator());
        } else {
            Collections.sort(taskOperationItems, new TaskOperationItemDescComparator());
        }
        return taskOperationItems;
    }

    static class TaskActivityItemAscComparator implements Comparator<TaskActivityItem> {

        /**
         * (non-Javadoc)
         *
         * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
         */
        @Override
        public int compare(TaskActivityItem o1, TaskActivityItem o2) {
            return o1.getCreateTime().compareTo(o2.getCreateTime());
        }

    }

    static class TaskActivityItemDescComparator implements Comparator<TaskActivityItem> {

        /**
         * (non-Javadoc)
         *
         * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
         */
        @Override
        public int compare(TaskActivityItem o1, TaskActivityItem o2) {
            return -o1.getCreateTime().compareTo(o2.getCreateTime());
        }

    }

    static class TaskOperationItemAscComparator implements Comparator<TaskOperationItem> {

        /**
         * (non-Javadoc)
         *
         * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
         */
        @Override
        public int compare(TaskOperationItem o1, TaskOperationItem o2) {
            return o1.getCreateTime().compareTo(o2.getCreateTime());
        }

    }

    static class TaskOperationItemDescComparator implements Comparator<TaskOperationItem> {

        /**
         * (non-Javadoc)
         *
         * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
         */
        @Override
        public int compare(TaskOperationItem o1, TaskOperationItem o2) {
            return -o1.getCreateTime().compareTo(o2.getCreateTime());
        }

    }

}
