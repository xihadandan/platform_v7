/*
 * @(#)2016年3月17日 V1.0
 *
 * Copyright 2016 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.common.fdext.support;

/**
 * Description: 如何描述该类
 *
 * @author zhongzh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2016年3月17日.1	zhongzh		2016年3月17日		Create
 * </pre>
 * @date 2016年3月17日
 */
public enum ValueFieldEnumAbs {
    uuid("UUID") {
        @Override
        public Object getValue(ICdFieldValue fieldValue) {
            return fieldValue.getUuid();
        }
    },
    recVer("REC_VER") {
        @Override
        public Object getValue(ICdFieldValue fieldValue) {
            return fieldValue.getRecVer();
        }
    },
    creator("CREATOR") {
        @Override
        public Object getValue(ICdFieldValue fieldValue) {
            return fieldValue.getCreator();
        }
    },
    createTime("CREATE_TIME") {
        @Override
        public Object getValue(ICdFieldValue fieldValue) {
            return fieldValue.getCreateTime();
        }
    },
    modifier("MODIFIER") {
        @Override
        public Object getValue(ICdFieldValue fieldValue) {
            return fieldValue.getModifier();
        }
    },
    modifyTime("MODIFY_TIME") {
        @Override
        public Object getValue(ICdFieldValue fieldValue) {
            return fieldValue.getModifyTime();
        }
    },
    fieldExtDefUuid("FIELD_EXT_DEF_UUID") {
        @Override
        public Object getValue(ICdFieldValue fieldValue) {
            return fieldValue.getFieldExtDefUuid();
        }
    },
    groupCode("GROUP_CODE") {
        @Override
        public Object getValue(ICdFieldValue fieldValue) {
            return fieldValue.getGroupCode();
        }
    },
    dataUuid("DATA_UUID") {
        @Override
        public Object getValue(ICdFieldValue fieldValue) {
            return fieldValue.getDataUuid();
        }
    },
    fieldExtValue1("FIELD_EXT_VALUE_1") {
        @Override
        public Object getValue(ICdFieldValue fieldValue) {
            return fieldValue.getFieldExtValue1();
        }
    },
    fieldExtValue2("FIELD_EXT_VALUE_2") {
        @Override
        public Object getValue(ICdFieldValue fieldValue) {
            return fieldValue.getFieldExtValue2();
        }
    },
    fieldExtValue3("FIELD_EXT_VALUE_3") {
        @Override
        public Object getValue(ICdFieldValue fieldValue) {
            return fieldValue.getFieldExtValue3();
        }
    },
    fieldExtValue4("FIELD_EXT_VALUE_4") {
        @Override
        public Object getValue(ICdFieldValue fieldValue) {
            return fieldValue.getFieldExtValue4();
        }
    },
    fieldExtValue5("FIELD_EXT_VALUE_5") {
        @Override
        public Object getValue(ICdFieldValue fieldValue) {
            return fieldValue.getFieldExtValue5();
        }
    },
    fieldExtValue6("FIELD_EXT_VALUE_6") {
        @Override
        public Object getValue(ICdFieldValue fieldValue) {
            return fieldValue.getFieldExtValue6();
        }
    },
    fieldExtValue7("FIELD_EXT_VALUE_7") {
        @Override
        public Object getValue(ICdFieldValue fieldValue) {
            return fieldValue.getFieldExtValue7();
        }
    },
    fieldExtValue8("FIELD_EXT_VALUE_8") {
        @Override
        public Object getValue(ICdFieldValue fieldValue) {
            return fieldValue.getFieldExtValue8();
        }
    },
    fieldExtValue9("FIELD_EXT_VALUE_9") {
        @Override
        public Object getValue(ICdFieldValue fieldValue) {
            return fieldValue.getFieldExtValue9();
        }
    },
    fieldExtValue10("FIELD_EXT_VALUE_10") {
        @Override
        public Object getValue(ICdFieldValue fieldValue) {
            return fieldValue.getFieldExtValue10();
        }
    },
    fieldExtValue11("FIELD_EXT_VALUE_11") {
        @Override
        public Object getValue(ICdFieldValue fieldValue) {
            return fieldValue.getFieldExtValue11();
        }
    },
    fieldExtValue12("FIELD_EXT_VALUE_12") {
        @Override
        public Object getValue(ICdFieldValue fieldValue) {
            return fieldValue.getFieldExtValue12();
        }
    },
    fieldExtValue13("FIELD_EXT_VALUE_13") {
        @Override
        public Object getValue(ICdFieldValue fieldValue) {
            return fieldValue.getFieldExtValue13();
        }
    },
    fieldExtValue14("FIELD_EXT_VALUE_14") {
        @Override
        public Object getValue(ICdFieldValue fieldValue) {
            return fieldValue.getFieldExtValue14();
        }
    },
    fieldExtValue15("FIELD_EXT_VALUE_15") {
        @Override
        public Object getValue(ICdFieldValue fieldValue) {
            return fieldValue.getFieldExtValue15();
        }
    },
    fieldExtValue16("FIELD_EXT_VALUE_16") {
        @Override
        public Object getValue(ICdFieldValue fieldValue) {
            return fieldValue.getFieldExtValue16();
        }
    },
    fieldExtValue17("FIELD_EXT_VALUE_17") {
        @Override
        public Object getValue(ICdFieldValue fieldValue) {
            return fieldValue.getFieldExtValue17();
        }
    },
    fieldExtValue18("FIELD_EXT_VALUE_18") {
        @Override
        public Object getValue(ICdFieldValue fieldValue) {
            return fieldValue.getFieldExtValue18();
        }
    },
    fieldExtValue19("FIELD_EXT_VALUE_19") {
        @Override
        public Object getValue(ICdFieldValue fieldValue) {
            return fieldValue.getFieldExtValue19();
        }
    },
    fieldExtValue20("FIELD_EXT_VALUE_20") {
        @Override
        public Object getValue(ICdFieldValue fieldValue) {
            return fieldValue.getFieldExtValue20();
        }
    },
    fieldExtValue21("FIELD_EXT_VALUE_21") {
        @Override
        public Object getValue(ICdFieldValue fieldValue) {
            return fieldValue.getFieldExtValue21();
        }
    },
    fieldExtValue22("FIELD_EXT_VALUE_22") {
        @Override
        public Object getValue(ICdFieldValue fieldValue) {
            return fieldValue.getFieldExtValue22();
        }
    },
    fieldExtValue23("FIELD_EXT_VALUE_23") {
        @Override
        public Object getValue(ICdFieldValue fieldValue) {
            return fieldValue.getFieldExtValue23();
        }
    },
    fieldExtValue24("FIELD_EXT_VALUE_24") {
        @Override
        public Object getValue(ICdFieldValue fieldValue) {
            return fieldValue.getFieldExtValue24();
        }
    },
    fieldExtValue25("FIELD_EXT_VALUE_25") {
        @Override
        public Object getValue(ICdFieldValue fieldValue) {
            return fieldValue.getFieldExtValue25();
        }
    },
    fieldExtValue26("FIELD_EXT_VALUE_26") {
        @Override
        public Object getValue(ICdFieldValue fieldValue) {
            return fieldValue.getFieldExtValue26();
        }
    },
    fieldExtValue27("FIELD_EXT_VALUE_27") {
        @Override
        public Object getValue(ICdFieldValue fieldValue) {
            return fieldValue.getFieldExtValue27();
        }
    },
    fieldExtValue28("FIELD_EXT_VALUE_28") {
        @Override
        public Object getValue(ICdFieldValue fieldValue) {
            return fieldValue.getFieldExtValue28();
        }
    },
    fieldExtValue29("FIELD_EXT_VALUE_29") {
        @Override
        public Object getValue(ICdFieldValue fieldValue) {
            return fieldValue.getFieldExtValue29();
        }
    },
    fieldExtValue30("FIELD_EXT_VALUE_30") {
        @Override
        public Object getValue(ICdFieldValue fieldValue) {
            return fieldValue.getFieldExtValue30();
        }
    };

    private String name;

    // 构造方法
    private ValueFieldEnumAbs(final String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public abstract Object getValue(ICdFieldValue fieldValue);

}
