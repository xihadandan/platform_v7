/*
 * @(#)2013-4-26 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.expression;

import com.wellsoft.context.enums.Separator;
import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.pt.bpm.engine.core.Token;
import com.wellsoft.pt.bpm.engine.entity.TaskOperation;
import com.wellsoft.pt.bpm.engine.node.Node;
import com.wellsoft.pt.bpm.engine.service.TaskOperationService;
import com.wellsoft.pt.bpm.engine.support.TaskData;
import com.wellsoft.pt.dyform.facade.dto.DyFormData;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-4-26.1	zhulh		2013-4-26		Create
 * </pre>
 * @date 2013-4-26
 */
public class VoteCondition extends AbstractCondition {
    private static final String VOTE_PATTERN = "\\[VOTE.*?(?= .*)";

    private static final String VOTE_PREFIX = "[VOTE=";

    private static Comparator<TaskOperation> optDescComparator = new Comparator<TaskOperation>() {
        @Override
        public int compare(TaskOperation o1, TaskOperation o2) {
            return -o1.getCreateTime().compareTo(o2.getCreateTime());
        }
    };

    /**
     * @param expression
     */
    public VoteCondition(String expression) {
        super(expression);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.expression.Condition#evaluate(com.wellsoft.pt.bpm.engine.core.Token)
     */
    @Override
    public String evaluate(Token token, Node to) {
        TaskData taskData = token.getTaskData();
        DyFormData dyFormData = taskData.getDyFormData(taskData.getDataUuid());
        // [VOTE=1] > 30 | [VOTE=2] < 20
        String expression = getExpression().trim();
        Pattern pattern = Pattern.compile(VOTE_PATTERN);
        Matcher matcher = pattern.matcher(expression);
        StringBuilder sb = new StringBuilder();
        while (matcher.find()) {
            String group = matcher.group();
            String opinionValue = group.substring(VOTE_PREFIX.length(), group.length() - 1);
            long opinionCount = getOpinionCount(token, opinionValue);
            long allCount = getAllOpinionCount(token, opinionValue);
            sb.append(expression.replaceAll(VOTE_PATTERN, (opinionCount / Double.valueOf(allCount)) * 100 + ""));
        }
        return updateFormValueIfRequired(sb.toString(), dyFormData.getFormDataOfMainform());
    }

    /**
     * @param expression
     * @return
     */
    private String updateFormValueIfRequired(String expression, Map<String, Object> root) {
        String tmpExpression = expression;
        String rightBracket = StringUtils.EMPTY;
        if (StringUtils.endsWith(tmpExpression, ")")) {
            rightBracket = StringUtils.substring(tmpExpression, tmpExpression.length() - 1);
            tmpExpression = StringUtils.trim(StringUtils.substring(tmpExpression, 0, tmpExpression.length() - 1));
        }
        if (StringUtils.endsWith(tmpExpression, ":1") || StringUtils.endsWith(tmpExpression, ":2")) {
            String[] parts = StringUtils.split(tmpExpression, Separator.SPACE.getValue());
            int formValueIndex = parts.length - 1;
            String formValueType = StringUtils.substring(parts[formValueIndex], parts[formValueIndex].length() - 1);
            // 常量值或表单字段值
            String formValue = StringUtils.substring(parts[formValueIndex], 0, parts[formValueIndex].length() - 2);
            // 表单字段值
            if (StringUtils.equals(formValueType, VALUE_TYPE_FORM_FIELD)) {
                formValue = StringUtils.trim(ObjectUtils.toString(root.get(formValue)));
                // 去掉%
                if (StringUtils.endsWith(formValue, "%")) {
                    formValue = StringUtils.substring(formValue, 0, formValue.length() - 1);
                }
            }
            // 非数字代表最大值
            if (!NumberUtils.isNumber(formValue)) {
                formValue = Integer.MAX_VALUE + StringUtils.EMPTY;
            }
            parts[parts.length - 1] = formValue;
            tmpExpression = StringUtils.join(parts, Separator.SPACE.getValue());
        }
        return tmpExpression + rightBracket;
    }

    /**
     * @param token
     * @param opinionValue
     * @return
     */
    private long getOpinionCount(Token token, String opinionValue) {
        TaskOperationService taskOperationService = ApplicationContextHolder.getBean(TaskOperationService.class);
        List<TaskOperation> taskOperations = taskOperationService.getAllTaskOperationByTaskInstUuid(token.getTask()
                .getUuid());
        Collections.sort(taskOperations, optDescComparator);
        long count = 0;
        Map<String, String> operatorMap = new HashMap<String, String>();
        for (TaskOperation taskOperation : taskOperations) {
            String opValue = taskOperation.getOpinionValue();
            if (StringUtils.isBlank(opValue)) {
                continue;
            }
            // 操作人ID
            String operatorId = taskOperation.getAssignee();
            if (operatorMap.containsKey(operatorId)) {
                continue;
            }
            operatorMap.put(operatorId, operatorId);
            if (StringUtils.equals(opinionValue, opValue)) {
                count++;
            }
        }
        return count;
    }

    /**
     * @param token
     * @param opinionValue
     * @return
     */
    private long getAllOpinionCount(Token token, String opinionValue) {
        TaskOperationService taskOperationService = ApplicationContextHolder.getBean(TaskOperationService.class);
        List<TaskOperation> taskOperations = taskOperationService.getAllTaskOperationByTaskInstUuid(token.getTask()
                .getUuid());
        Collections.sort(taskOperations, optDescComparator);
        Map<String, String> operatorMap = new HashMap<String, String>();
        for (TaskOperation taskOperation : taskOperations) {
            String opValue = taskOperation.getOpinionValue();
            if (StringUtils.isBlank(opValue)) {
                continue;
            }
            // 操作人ID
            String operatorId = taskOperation.getAssignee();
            if (operatorMap.containsKey(operatorId)) {
                continue;
            }
            operatorMap.put(operatorId, operatorId);
        }
        return operatorMap.size();
    }

}
