/*
 * @(#)2016-03-11 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.common.fdext.entity;

import com.wellsoft.context.jdbc.entity.IdEntity;
import com.wellsoft.pt.common.fdext.support.ICdFieldValue;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Description: 如何描述该类
 *
 * @author zhongzh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2016-03-11.1	zhongzh		2016-03-11		Create
 * </pre>
 * @date 2016-03-11
 */
@Entity
@Table(name = "CD_FIELD_EXT_VALUE")
@DynamicUpdate
@DynamicInsert
public class CdFieldExtValue extends IdEntity implements ICdFieldValue {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 1457678530637L;

    // 字段扩展定义UUID
    private String fieldExtDefUuid;
    // 组别代码，冗余字段
    private String groupCode;
    // 数据UUID
    private String dataUuid;
    // 扩展字段1的值
    private String fieldExtValue1;
    // 扩展字段2的值
    private String fieldExtValue2;
    // 扩展字段3的值
    private String fieldExtValue3;
    // 扩展字段4的值
    private String fieldExtValue4;
    // 扩展字段5的值
    private String fieldExtValue5;
    // 扩展字段6的值
    private String fieldExtValue6;
    // 扩展字段7的值
    private String fieldExtValue7;
    // 扩展字段8的值
    private String fieldExtValue8;
    // 扩展字段9的值
    private String fieldExtValue9;
    // 扩展字段10的值
    private String fieldExtValue10;
    // 扩展字段11的值
    private String fieldExtValue11;
    // 扩展字段12的值
    private String fieldExtValue12;
    // 扩展字段13的值
    private String fieldExtValue13;
    // 扩展字段14的值
    private String fieldExtValue14;
    // 扩展字段15的值
    private String fieldExtValue15;
    // 扩展字段16的值
    private String fieldExtValue16;
    // 扩展字段17的值
    private String fieldExtValue17;
    // 扩展字段18的值
    private String fieldExtValue18;
    // 扩展字段19的值
    private String fieldExtValue19;
    // 扩展字段20的值
    private String fieldExtValue20;
    // 扩展字段21的值
    private String fieldExtValue21;
    // 扩展字段22的值
    private String fieldExtValue22;
    // 扩展字段23的值
    private String fieldExtValue23;
    // 扩展字段24的值
    private String fieldExtValue24;
    // 扩展字段25的值
    private String fieldExtValue25;
    // 扩展字段26的值
    private String fieldExtValue26;
    // 扩展字段27的值
    private String fieldExtValue27;
    // 扩展字段28的值
    private String fieldExtValue28;
    // 扩展字段29的值
    private String fieldExtValue29;
    // 扩展字段30的值
    private String fieldExtValue30;

    /**
     * @return the fieldExtDefUuid
     */
    @Column(name = "FIELD_EXT_DEF_UUID")
    public String getFieldExtDefUuid() {
        return this.fieldExtDefUuid;
    }

    /**
     * @param fieldExtDefUuid
     */
    public String setFieldExtDefUuid(String fieldExtDefUuid) {
        return this.fieldExtDefUuid = fieldExtDefUuid;
    }

    /**
     * @return the groupCode
     */
    @Column(name = "GROUP_CODE")
    public String getGroupCode() {
        return this.groupCode;
    }

    /**
     * @param groupCode
     */
    public String setGroupCode(String groupCode) {
        return this.groupCode = groupCode;
    }

    /**
     * @return the dataUuid
     */
    @Column(name = "DATA_UUID")
    public String getDataUuid() {
        return this.dataUuid;
    }

    /**
     * @param dataUuid
     */
    public String setDataUuid(String dataUuid) {
        return this.dataUuid = dataUuid;
    }

    /**
     * @return the fieldExtValue1
     */
    @Column(name = "FIELD_EXT_VALUE_1")
    public String getFieldExtValue1() {
        return this.fieldExtValue1;
    }

    /**
     * @param fieldExtValue1
     */
    public String setFieldExtValue1(String fieldExtValue1) {
        return this.fieldExtValue1 = fieldExtValue1;
    }

    /**
     * @return the fieldExtValue2
     */
    @Column(name = "FIELD_EXT_VALUE_2")
    public String getFieldExtValue2() {
        return this.fieldExtValue2;
    }

    /**
     * @param fieldExtValue2
     */
    public String setFieldExtValue2(String fieldExtValue2) {
        return this.fieldExtValue2 = fieldExtValue2;
    }

    /**
     * @return the fieldExtValue3
     */
    @Column(name = "FIELD_EXT_VALUE_3")
    public String getFieldExtValue3() {
        return this.fieldExtValue3;
    }

    /**
     * @param fieldExtValue3
     */
    public String setFieldExtValue3(String fieldExtValue3) {
        return this.fieldExtValue3 = fieldExtValue3;
    }

    /**
     * @return the fieldExtValue4
     */
    @Column(name = "FIELD_EXT_VALUE_4")
    public String getFieldExtValue4() {
        return this.fieldExtValue4;
    }

    /**
     * @param fieldExtValue4
     */
    public String setFieldExtValue4(String fieldExtValue4) {
        return this.fieldExtValue4 = fieldExtValue4;
    }

    /**
     * @return the fieldExtValue5
     */
    @Column(name = "FIELD_EXT_VALUE_5")
    public String getFieldExtValue5() {
        return this.fieldExtValue5;
    }

    /**
     * @param fieldExtValue5
     */
    public String setFieldExtValue5(String fieldExtValue5) {
        return this.fieldExtValue5 = fieldExtValue5;
    }

    /**
     * @return the fieldExtValue6
     */
    @Column(name = "FIELD_EXT_VALUE_6")
    public String getFieldExtValue6() {
        return this.fieldExtValue6;
    }

    /**
     * @param fieldExtValue6
     */
    public String setFieldExtValue6(String fieldExtValue6) {
        return this.fieldExtValue6 = fieldExtValue6;
    }

    /**
     * @return the fieldExtValue7
     */
    @Column(name = "FIELD_EXT_VALUE_7")
    public String getFieldExtValue7() {
        return this.fieldExtValue7;
    }

    /**
     * @param fieldExtValue7
     */
    public String setFieldExtValue7(String fieldExtValue7) {
        return this.fieldExtValue7 = fieldExtValue7;
    }

    /**
     * @return the fieldExtValue8
     */
    @Column(name = "FIELD_EXT_VALUE_8")
    public String getFieldExtValue8() {
        return this.fieldExtValue8;
    }

    /**
     * @param fieldExtValue8
     */
    public String setFieldExtValue8(String fieldExtValue8) {
        return this.fieldExtValue8 = fieldExtValue8;
    }

    /**
     * @return the fieldExtValue9
     */
    @Column(name = "FIELD_EXT_VALUE_9")
    public String getFieldExtValue9() {
        return this.fieldExtValue9;
    }

    /**
     * @param fieldExtValue9
     */
    public String setFieldExtValue9(String fieldExtValue9) {
        return this.fieldExtValue9 = fieldExtValue9;
    }

    /**
     * @return the fieldExtValue10
     */
    @Column(name = "FIELD_EXT_VALUE_10")
    public String getFieldExtValue10() {
        return this.fieldExtValue10;
    }

    /**
     * @param fieldExtValue10
     */
    public String setFieldExtValue10(String fieldExtValue10) {
        return this.fieldExtValue10 = fieldExtValue10;
    }

    /**
     * @return the fieldExtValue11
     */
    @Column(name = "FIELD_EXT_VALUE_11")
    public String getFieldExtValue11() {
        return this.fieldExtValue11;
    }

    /**
     * @param fieldExtValue11
     */
    public String setFieldExtValue11(String fieldExtValue11) {
        return this.fieldExtValue11 = fieldExtValue11;
    }

    /**
     * @return the fieldExtValue12
     */
    @Column(name = "FIELD_EXT_VALUE_12")
    public String getFieldExtValue12() {
        return this.fieldExtValue12;
    }

    /**
     * @param fieldExtValue12
     */
    public String setFieldExtValue12(String fieldExtValue12) {
        return this.fieldExtValue12 = fieldExtValue12;
    }

    /**
     * @return the fieldExtValue13
     */
    @Column(name = "FIELD_EXT_VALUE_13")
    public String getFieldExtValue13() {
        return this.fieldExtValue13;
    }

    /**
     * @param fieldExtValue13
     */
    public String setFieldExtValue13(String fieldExtValue13) {
        return this.fieldExtValue13 = fieldExtValue13;
    }

    /**
     * @return the fieldExtValue14
     */
    @Column(name = "FIELD_EXT_VALUE_14")
    public String getFieldExtValue14() {
        return this.fieldExtValue14;
    }

    /**
     * @param fieldExtValue14
     */
    public String setFieldExtValue14(String fieldExtValue14) {
        return this.fieldExtValue14 = fieldExtValue14;
    }

    /**
     * @return the fieldExtValue15
     */
    @Column(name = "FIELD_EXT_VALUE_15")
    public String getFieldExtValue15() {
        return this.fieldExtValue15;
    }

    /**
     * @param fieldExtValue15
     */
    public String setFieldExtValue15(String fieldExtValue15) {
        return this.fieldExtValue15 = fieldExtValue15;
    }

    /**
     * @return the fieldExtValue16
     */
    @Column(name = "FIELD_EXT_VALUE_16")
    public String getFieldExtValue16() {
        return this.fieldExtValue16;
    }

    /**
     * @param fieldExtValue16
     */
    public String setFieldExtValue16(String fieldExtValue16) {
        return this.fieldExtValue16 = fieldExtValue16;
    }

    /**
     * @return the fieldExtValue17
     */
    @Column(name = "FIELD_EXT_VALUE_17")
    public String getFieldExtValue17() {
        return this.fieldExtValue17;
    }

    /**
     * @param fieldExtValue17
     */
    public String setFieldExtValue17(String fieldExtValue17) {
        return this.fieldExtValue17 = fieldExtValue17;
    }

    /**
     * @return the fieldExtValue18
     */
    @Column(name = "FIELD_EXT_VALUE_18")
    public String getFieldExtValue18() {
        return this.fieldExtValue18;
    }

    /**
     * @param fieldExtValue18
     */
    public String setFieldExtValue18(String fieldExtValue18) {
        return this.fieldExtValue18 = fieldExtValue18;
    }

    /**
     * @return the fieldExtValue19
     */
    @Column(name = "FIELD_EXT_VALUE_19")
    public String getFieldExtValue19() {
        return this.fieldExtValue19;
    }

    /**
     * @param fieldExtValue19
     */
    public String setFieldExtValue19(String fieldExtValue19) {
        return this.fieldExtValue19 = fieldExtValue19;
    }

    /**
     * @return the fieldExtValue20
     */
    @Column(name = "FIELD_EXT_VALUE_20")
    public String getFieldExtValue20() {
        return this.fieldExtValue20;
    }

    /**
     * @param fieldExtValue20
     */
    public String setFieldExtValue20(String fieldExtValue20) {
        return this.fieldExtValue20 = fieldExtValue20;
    }

    /**
     * @return the fieldExtValue21
     */
    @Column(name = "FIELD_EXT_VALUE_21")
    public String getFieldExtValue21() {
        return this.fieldExtValue21;
    }

    /**
     * @param fieldExtValue21
     */
    public String setFieldExtValue21(String fieldExtValue21) {
        return this.fieldExtValue21 = fieldExtValue21;
    }

    /**
     * @return the fieldExtValue22
     */
    @Column(name = "FIELD_EXT_VALUE_22")
    public String getFieldExtValue22() {
        return this.fieldExtValue22;
    }

    /**
     * @param fieldExtValue22
     */
    public String setFieldExtValue22(String fieldExtValue22) {
        return this.fieldExtValue22 = fieldExtValue22;
    }

    /**
     * @return the fieldExtValue23
     */
    @Column(name = "FIELD_EXT_VALUE_23")
    public String getFieldExtValue23() {
        return this.fieldExtValue23;
    }

    /**
     * @param fieldExtValue23
     */
    public String setFieldExtValue23(String fieldExtValue23) {
        return this.fieldExtValue23 = fieldExtValue23;
    }

    /**
     * @return the fieldExtValue24
     */
    @Column(name = "FIELD_EXT_VALUE_24")
    public String getFieldExtValue24() {
        return this.fieldExtValue24;
    }

    /**
     * @param fieldExtValue24
     */
    public String setFieldExtValue24(String fieldExtValue24) {
        return this.fieldExtValue24 = fieldExtValue24;
    }

    /**
     * @return the fieldExtValue25
     */
    @Column(name = "FIELD_EXT_VALUE_25")
    public String getFieldExtValue25() {
        return this.fieldExtValue25;
    }

    /**
     * @param fieldExtValue25
     */
    public String setFieldExtValue25(String fieldExtValue25) {
        return this.fieldExtValue25 = fieldExtValue25;
    }

    /**
     * @return the fieldExtValue26
     */
    @Column(name = "FIELD_EXT_VALUE_26")
    public String getFieldExtValue26() {
        return this.fieldExtValue26;
    }

    /**
     * @param fieldExtValue26
     */
    public String setFieldExtValue26(String fieldExtValue26) {
        return this.fieldExtValue26 = fieldExtValue26;
    }

    /**
     * @return the fieldExtValue27
     */
    @Column(name = "FIELD_EXT_VALUE_27")
    public String getFieldExtValue27() {
        return this.fieldExtValue27;
    }

    /**
     * @param fieldExtValue27
     */
    public String setFieldExtValue27(String fieldExtValue27) {
        return this.fieldExtValue27 = fieldExtValue27;
    }

    /**
     * @return the fieldExtValue28
     */
    @Column(name = "FIELD_EXT_VALUE_28")
    public String getFieldExtValue28() {
        return this.fieldExtValue28;
    }

    /**
     * @param fieldExtValue28
     */
    public String setFieldExtValue28(String fieldExtValue28) {
        return this.fieldExtValue28 = fieldExtValue28;
    }

    /**
     * @return the fieldExtValue29
     */
    @Column(name = "FIELD_EXT_VALUE_29")
    public String getFieldExtValue29() {
        return this.fieldExtValue29;
    }

    /**
     * @param fieldExtValue29
     */
    public String setFieldExtValue29(String fieldExtValue29) {
        return this.fieldExtValue29 = fieldExtValue29;
    }

    /**
     * @return the fieldExtValue30
     */
    @Column(name = "FIELD_EXT_VALUE_30")
    public String getFieldExtValue30() {
        return this.fieldExtValue30;
    }

    /**
     * @param fieldExtValue30
     */
    public String setFieldExtValue30(String fieldExtValue30) {
        return this.fieldExtValue30 = fieldExtValue30;
    }

}
