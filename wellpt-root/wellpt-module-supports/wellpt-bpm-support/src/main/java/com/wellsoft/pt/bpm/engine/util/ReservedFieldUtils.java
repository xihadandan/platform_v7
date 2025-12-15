/*
 * @(#)2014-4-11 V1.0
 *
 * Copyright 2014 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.util;

import com.wellsoft.context.util.date.DateUtils;
import com.wellsoft.pt.bpm.engine.entity.FlowInstance;
import com.wellsoft.pt.bpm.engine.exception.WorkFlowException;
import com.wellsoft.pt.bpm.engine.support.TaskData;
import com.wellsoft.pt.dyform.facade.dto.DyFormData;
import com.wellsoft.pt.workflow.enums.WorkFlowFieldMapping;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2014-4-11.1	zhulh		2014-4-11		Create
 * </pre>
 * @date 2014-4-11
 */
public class ReservedFieldUtils {

    private static Logger LOG = LoggerFactory.getLogger(ReservedFieldUtils.class);

    /**
     * 设置预留字段
     *
     * @param flowInstance
     * @param taskData
     */
    public static void setReservedFields(FlowInstance flowInstance, TaskData taskData) {
        if (taskData.isUpdateReservedText1()) {
            flowInstance.setReservedText1(taskData.getReservedText1());
        }
        if (taskData.isUpdateReservedText2()) {
            flowInstance.setReservedText2(taskData.getReservedText2());
        }
        if (taskData.isUpdateReservedText3()) {
            flowInstance.setReservedText3(taskData.getReservedText3());
        }
        if (taskData.isUpdateReservedText4()) {
            flowInstance.setReservedText4(taskData.getReservedText4());
        }
        if (taskData.isUpdateReservedText5()) {
            flowInstance.setReservedText5(taskData.getReservedText5());
        }
        if (taskData.isUpdateReservedText6()) {
            flowInstance.setReservedText6(taskData.getReservedText6());
        }
        if (taskData.isUpdateReservedText7()) {
            flowInstance.setReservedText7(taskData.getReservedText7());
        }
        if (taskData.isUpdateReservedText8()) {
            flowInstance.setReservedText8(taskData.getReservedText8());
        }
        if (taskData.isUpdateReservedText9()) {
            flowInstance.setReservedText9(taskData.getReservedText9());
        }
        if (taskData.isUpdateReservedText10()) {
            flowInstance.setReservedText10(taskData.getReservedText10());
        }
        if (taskData.isUpdateReservedText11()) {
            flowInstance.setReservedText11(taskData.getReservedText11());
        }
        if (taskData.isUpdateReservedText12()) {
            flowInstance.setReservedText12(taskData.getReservedText12());
        }
        if (taskData.isUpdateReservedNumber1()) {
            flowInstance.setReservedNumber1(taskData.getReservedNumber1());
        }
        if (taskData.isUpdateReservedNumber2()) {
            flowInstance.setReservedNumber2(taskData.getReservedNumber2());
        }
        if (taskData.isUpdateReservedNumber3()) {
            flowInstance.setReservedNumber3(taskData.getReservedNumber3());
        }
        if (taskData.isUpdateReservedDate1()) {
            flowInstance.setReservedDate1(taskData.getReservedDate1());
        }
        if (taskData.isUpdateReservedDate2()) {
            flowInstance.setReservedDate2(taskData.getReservedDate2());
        }
    }

    /**
     * @param dyFormData
     * @param taskData
     */
    public static void setReservedFields(DyFormData dyFormData, TaskData taskData) {
        if (dyFormData.hasFieldMappingName(WorkFlowFieldMapping.RESERVED_TEXT_1.getValue())) {
            Object reservedText1 = dyFormData.getFieldValueByMappingName(WorkFlowFieldMapping.RESERVED_TEXT_1
                    .getValue());
            taskData.setReservedText1(objectToString(reservedText1));
        }
        if (dyFormData.hasFieldMappingName(WorkFlowFieldMapping.RESERVED_TEXT_2.getValue())) {
            Object reservedText2 = dyFormData.getFieldValueByMappingName(WorkFlowFieldMapping.RESERVED_TEXT_2
                    .getValue());
            taskData.setReservedText2(objectToString(reservedText2));
        }
        if (dyFormData.hasFieldMappingName(WorkFlowFieldMapping.RESERVED_TEXT_3.getValue())) {
            Object reservedText3 = dyFormData.getFieldValueByMappingName(WorkFlowFieldMapping.RESERVED_TEXT_3
                    .getValue());
            taskData.setReservedText3(objectToString(reservedText3));
        }
        if (dyFormData.hasFieldMappingName(WorkFlowFieldMapping.RESERVED_TEXT_4.getValue())) {
            Object reservedText4 = dyFormData.getFieldValueByMappingName(WorkFlowFieldMapping.RESERVED_TEXT_4
                    .getValue());
            taskData.setReservedText4(objectToString(reservedText4));
        }
        if (dyFormData.hasFieldMappingName(WorkFlowFieldMapping.RESERVED_TEXT_5.getValue())) {
            Object reservedText5 = dyFormData.getFieldValueByMappingName(WorkFlowFieldMapping.RESERVED_TEXT_5
                    .getValue());
            taskData.setReservedText5(objectToString(reservedText5));
        }
        if (dyFormData.hasFieldMappingName(WorkFlowFieldMapping.RESERVED_TEXT_6.getValue())) {
            Object reservedText6 = dyFormData.getFieldValueByMappingName(WorkFlowFieldMapping.RESERVED_TEXT_6
                    .getValue());
            taskData.setReservedText6(objectToString(reservedText6));
        }
        if (dyFormData.hasFieldMappingName(WorkFlowFieldMapping.RESERVED_TEXT_7.getValue())) {
            Object reservedText7 = dyFormData.getFieldValueByMappingName(WorkFlowFieldMapping.RESERVED_TEXT_7
                    .getValue());
            taskData.setReservedText7(objectToString(reservedText7));
        }
        if (dyFormData.hasFieldMappingName(WorkFlowFieldMapping.RESERVED_TEXT_8.getValue())) {
            Object reservedText8 = dyFormData.getFieldValueByMappingName(WorkFlowFieldMapping.RESERVED_TEXT_8
                    .getValue());
            taskData.setReservedText8(objectToString(reservedText8));
        }
        if (dyFormData.hasFieldMappingName(WorkFlowFieldMapping.RESERVED_TEXT_9.getValue())) {
            Object reservedText9 = dyFormData.getFieldValueByMappingName(WorkFlowFieldMapping.RESERVED_TEXT_9
                    .getValue());
            taskData.setReservedText9(objectToString(reservedText9));
        }
        if (dyFormData.hasFieldMappingName(WorkFlowFieldMapping.RESERVED_TEXT_10.getValue())) {
            Object reservedText10 = dyFormData.getFieldValueByMappingName(WorkFlowFieldMapping.RESERVED_TEXT_10
                    .getValue());
            taskData.setReservedText10(objectToString(reservedText10));
        }
        if (dyFormData.hasFieldMappingName(WorkFlowFieldMapping.RESERVED_TEXT_11.getValue())) {
            Object reservedText11 = dyFormData.getFieldValueByMappingName(WorkFlowFieldMapping.RESERVED_TEXT_11
                    .getValue());
            taskData.setReservedText11(objectToString(reservedText11));
        }
        if (dyFormData.hasFieldMappingName(WorkFlowFieldMapping.RESERVED_TEXT_12.getValue())) {
            Object reservedText12 = dyFormData.getFieldValueByMappingName(WorkFlowFieldMapping.RESERVED_TEXT_12
                    .getValue());
            taskData.setReservedText12(objectToString(reservedText12));
        }
        if (dyFormData.hasFieldMappingName(WorkFlowFieldMapping.RESERVED_NUMBER_1.getValue())) {
            Object reservedNumber1 = dyFormData.getFieldValueByMappingName(WorkFlowFieldMapping.RESERVED_NUMBER_1
                    .getValue());
            taskData.setReservedNumber1(reservedNumber1 == null ? null : Integer.valueOf(reservedNumber1.toString()));
        }
        if (dyFormData.hasFieldMappingName(WorkFlowFieldMapping.RESERVED_NUMBER_2.getValue())) {
            Object reservedNumber2 = dyFormData.getFieldValueByMappingName(WorkFlowFieldMapping.RESERVED_NUMBER_2
                    .getValue());
            taskData.setReservedNumber2(reservedNumber2 == null ? null : Double.valueOf(reservedNumber2.toString()));
        }
        if (dyFormData.hasFieldMappingName(WorkFlowFieldMapping.RESERVED_NUMBER_3.getValue())) {
            Object reservedNumber3 = dyFormData.getFieldValueByMappingName(WorkFlowFieldMapping.RESERVED_NUMBER_3
                    .getValue());
            taskData.setReservedNumber3(reservedNumber3 == null ? null : Double.valueOf(reservedNumber3.toString()));
        }
        try {
            if (dyFormData.hasFieldMappingName(WorkFlowFieldMapping.RESERVED_DATE_1.getValue())) {
                Object reservedDate1 = dyFormData.getFieldValueByMappingName(WorkFlowFieldMapping.RESERVED_DATE_1
                        .getValue());
                if (reservedDate1 instanceof Date) {
                    taskData.setReservedDate1((Date) reservedDate1);
                } else {
                    taskData.setReservedDate1(reservedDate1 == null ? null : DateUtils.parse(reservedDate1.toString()));
                }
            }
            if (dyFormData.hasFieldMappingName(WorkFlowFieldMapping.RESERVED_DATE_2.getValue())) {
                Object reservedDate2 = dyFormData.getFieldValueByMappingName(WorkFlowFieldMapping.RESERVED_DATE_2
                        .getValue());
                if (reservedDate2 instanceof Date) {
                    taskData.setReservedDate2((Date) reservedDate2);
                } else {
                    taskData.setReservedDate2(reservedDate2 == null ? null : DateUtils.parse(reservedDate2.toString()));
                }
            }
        } catch (Exception e) {
            LOG.error(ExceptionUtils.getStackTrace(e));

            throw new WorkFlowException("提交失败，日期类型数据解析错误!");
        }
    }

    private static final String objectToString(Object object) {
        return object == null ? null : object.toString();
    }

}
