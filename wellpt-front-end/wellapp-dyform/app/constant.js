// 常量定义
exports._constant = {
    dyformControlInputModeJs: { 45: 'wTableView', 26: 'wDialog', 28: 'wUnit2', 29: 'wSerialNumber', 191: 'wComboSelect', 130: 'wPlaceholder', 199: 'wSelect', 133: 'wFormFileLibrary', 51: 'wUnit2', 30: 'wDatePicker', 52: 'wUnit2', 31: 'wNumberInput', 53: 'wUnit2', 10: 'wUnit2', 54: 'wUnit2', 32: 'wViewDisplay', 11: 'wUnit2', 55: 'wUnit2', 33: 'wFileUpload4Image', 56: 'wUnit2', 12: 'wTimeEmploy', 57: 'wUnit2', 13: 'wTimeEmploy', 14: 'wTimeEmploy', 15: 'wTimeEmploy', 16: 'wComboTree', 17: 'wRadio', 18: 'wCheckBox', 19: 'wComboBox', 1: 'wTextInput', 2: 'wCkeditor', 4: 'wFileUpload4Icon,wFileUpload4Body', 5: 'wFileUpload4Body', 126: 'wTagGroup', 6: 'wFileUpload', 127: 'wColor', 7: 'wSerialNumber', 128: 'wSwitchs', 8: 'wUnit2', 129: 'wProgress', 9: 'wUnit2', 61: 'wChained', 40: 'wEmbedded', 41: 'wJobSelect', 20: 'wTextArea', 43: 'wUnit2' },

    //系统预留字段
    SystemPreservedField: {
        uuid: {
            elementType: 'id', name: 'uuid', column: 'uuid', dataType: 'string', length: 255
        },
        creator: {
            elementType: 'property', name: 'creator', column: 'creator', dataType: 'string', length: 255
        },
        create_time: {
            elementType: 'property', name: 'create_time', column: 'create_time', dataType: 'java.sql.Timestamp', length: null
        },
        modifier: {
            elementType: 'property', name: 'creator', column: 'creator', dataType: 'string', length: 255
        },
        modify_time: {
            elementType: 'property', name: 'modify_time', column: 'modify_time', dataType: 'java.sql.Timestamp', length: null
        },
        rec_ver: {
            elementType: 'property', name: 'rec_ver', column: 'rec_ver', dataType: 'integer', length: null
        },
        form_uuid: {
            elementType: 'property', name: 'form_uuid', column: 'form_uuid', dataType: 'string', length: 255
        },
        status: {
            elementType: 'property', name: 'status', column: 'status', dataType: 'string', length: 10
        },
        signature_: {
            elementType: 'property', name: 'signature_', column: 'signature_', dataType: 'string', length: 50
        },
        version: {
            elementType: 'property', name: 'version', column: 'version', dataType: 'string', length: 255
        },
        system_unit_id: {
            elementType: 'property', name: 'system_unit_id', column: 'system_unit_id', dataType: 'string', length: 32
        }
    },

    //系统关系表预留字段
    SystemRelaTblField:{
        uuid: {
            elementType: 'id', name: 'uuid', column: 'uuid', dataType: 'string', length: 255
        },
        creator: {
            elementType: 'property', name: 'creator', column: 'creator', dataType: 'string', length: 255
        },
        create_time: {
            elementType: 'property', name: 'create_time', column: 'create_time', dataType: 'java.sql.Timestamp', length: null
        },
        modifier: {
            elementType: 'property', name: 'creator', column: 'creator', dataType: 'string', length: 255
        },
        modify_time: {
            elementType: 'property', name: 'modify_time', column: 'modify_time', dataType: 'java.sql.Timestamp', length: null
        },
        rec_ver: {
            elementType: 'property', name: 'rec_ver', column: 'rec_ver', dataType: 'integer', length: null
        },

        data_uuid: {
            elementType: 'property', name: 'data_uuid', column: 'data_uuid', dataType: 'string', length: 255
        },
        mainform_data_uuid: {
            elementType: 'property', name: 'mainform_data_uuid', column: 'mainform_data_uuid', dataType: 'string', length: 255
        },
        sort_order: {
            elementType: 'property', name: 'sort_order', column: 'sort_order', dataType: 'string', length: 255
        },
        parent_uuid: {
            elementType: 'property', name: 'parent_uuid', column: 'parent_uuid', dataType: 'string', length: 255
        },
    }
}