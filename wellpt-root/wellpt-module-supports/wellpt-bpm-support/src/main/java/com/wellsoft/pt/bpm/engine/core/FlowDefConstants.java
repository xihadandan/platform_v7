package com.wellsoft.pt.bpm.engine.core;

public interface FlowDefConstants {
    /* 分类 */
    // 节点路径
    public static final String CATEGORY_XPATH = "/diction/categorys/category";
    public static final String CATEGORY_NAME = "name";
    public static final String CATEGORY_CODE = "code";
    public static final String CATEGORY_PARENT = "parent";

    /* 环节权限 */
    public static final String RIGHT_XPATH = "/diction/rights/right";
    public static final String RIGHT_NAME = "name";
    public static final String RIGHT_CODE = "code";
    public static final String RIGHT_VALUE = "value";
    public static final String RIGHT_IS_DEFAULT = "isDefault";
    /* 操作按钮 */
    public static final String BUTTON_XPATH = "/diction/buttons/button";
    public static final String BUTTON_NAME = "name";
    public static final String BUTTON_CODE = "code";
    public static final String BUTTON_VALUE = "value";
    public static final String BUTTON_TITLE = "title";
    /* 信息格式 */
    public static final String FORMAT_XPATH = "/diction/formats/format";
    public static final String FORMAT_NAME = "name";
    public static final String FORMAT_CODE = "code";
    public static final String FORMAT_VALUE = "value";
    public static final String FORMAT_HTML = "html";
    public static final String FORMAT_IS_UP = "isUp";
    public static final String FORMAT_IS_UNIQUE = "isUnique";
    public static final String FORMAT_IS_NO_EMPTY = "isNoEmpty";
    public static final String FORMAT_IS_NO_READ = "isNoRead";
    /* 表单 */
    public static final String FORM_XPATH = "/diction/forms/form";
    public static final String FORM_NAME = "name";
    public static final String FORM_ID = "id";

    /* 多值单元 */
    public static final String UNIT_TPYE = "type";
    public static final String UNIT_VALUE = "value";
    public static final String UNIT_ARGVALUE = "argValue";

    public static final String EVENT_SCRIPT_TYPE = "type";
    public static final String EVENT_SCRIPT_CONTENT_TYPE = "contentType";
    public static final String EVENT_SCRIPT_POINTCUT = "pointcut";

    /* 过程操作 */
    public static final String BUTTON_BTN_SOURCE = "btnSource";
    public static final String BUTTON_BTN_ROLE = "btnRole";
    public static final String BUTTON_BTN_ID = "btnId";
    public static final String BUTTON_PI_UUID = "piUuid";
    public static final String BUTTON_PI_NAME = "piName";
    public static final String BUTTON_HASH_TYPE = "hashType";
    public static final String BUTTON_HASH = "hash";
    public static final String BUTTON_EVENT_PARAMS = "eventParams";
    public static final String BUTTON_TARGET_POSITION = "targetPosition";
    public static final String BUTTON_ID = "id";
    public static final String BUTTON_BTNVALUE = "btnValue";
    public static final String BUTTON_NEWNAME = "newName";
    public static final String BUTTON_NEWCODE = "newCode";
    public static final String BUTTON_USE_WAY = "useWay";
    public static final String BUTTON_BTNARGUMENT = "btnArgument";
    public static final String BUTTON_OWNERS = "btnOwners";
    public static final String BUTTON_OWNER_IDS = "btnOwnerIds";
    public static final String BUTTON_USERS = "btnUsers";
    public static final String BUTTON_USER_IDS = "btnUserIds";
    public static final String BUTTON_COPY_USERS = "btnCopyUsers";
    public static final String BUTTON_COPY_USER_IDS = "btnCopyUserIds";
    public static final String BUTTON_CLASS_NAME = "btnClassName";
    public static final String BUTTON_BTN_ICON = "btnIcon";
    public static final String BUTTON_BTN_STYLE = "btnStyle";
    public static final String BUTTON_BTN_REMARK = "btnRemark";
    public static final String BUTTON_SORT_ORDER = "sortOrder";
    /* 过程记录 */
    public static final String RECORD_NAME = "name";
    public static final String RECORD_FIELD = "field";
    public static final String RECORD_WAY = "way";
    public static final String RECORD_ASSEMBLER = "assembler";
    public static final String RECORD_IGNORE_EMPTY = "ignoreEmpty";
    public static final String RECORD_FIELD_NOT_VALIDATE = "fieldNotValidate";
    public static final String RECORD_ENABLE_WYSIWYG = "enableWysiwyg";
    public static final String RECORD_VALUE = "value";
    public static final String RECORD_TASK_IDS = "taskIds";
    public static final String CONTENT_ORIGIN = "contentOrigin";
    public static final String RECORD_ENABLE_PRE_CONDITION = "enablePreCondition";
    public static final String RECORD_CONDITIONS_XPATH = "conditions/unit";
    /* 流程定义 */
    public static final String FLOW_XPATH = "/flow";
    public static final String FLOW_PROPERTY_XPATH = FLOW_XPATH + "/property";
    public static final String FLOW_TIMERS_XPATH = FLOW_XPATH + "/timers/timer";
    public static final String TASK_XPATH = FLOW_XPATH + "/tasks/task";
    public static final String DIRECTION_XPATH = FLOW_XPATH + "/directions/direction";
    public static final String LABEL_XPATH = FLOW_XPATH + "/labels/label";
    public static final String DELETE_XPATH = FLOW_XPATH + "/deletes/*";
    public static final String FLOW_UUID = "uuid";
    public static final String FLOW_ID = "id";
    public static final String FLOW_NAME = "name";
    public static final String FLOW_CODE = "code";
    public static final String FLOW_VERSION = "version";
    public static final String FLOW_SYSTEM_UNIT_ID = "systemUnitId";
    public static final String FLOW_VERTYPE = "verType";
    public static final String FLOW_APPLY_ID = "applyId";
    public static final String FLOW_MODULE_ID = "moduleId";
    /* add by huanglinchuan2014.10.20 begin */
    public static final String FLOW_TITLE_EXPRESSION = "titleExpression";
    /* add by huanglinchuan2014.10.20 end */

    public static final String FLOW_LASTVER = "lastVer";
    public static final String FLOW_CATEGORY_SN = "categorySN";
    public static final String FLOW_EQUALFLOW_XPATH = FLOW_PROPERTY_XPATH + "/equalFlow";
    public static final String FLOW_EQUAL_FLOW_NAME = "name";
    public static final String FLOW_EQUAL_FLOWID = "id";
    public static final String FLOW_FORMID = "formID";
    public static final String FLOW_ORG_VERSION_ID = "orgVersionId";
    public static final String FLOW_USE_DEFAULT_ORG = "useDefaultOrg";
    public static final String FLOW_ORG_ID = "orgId";
    public static final String FLOW_ENABLE_MULTI_ORG = "enableMultiOrg";
    public static final String FLOW_MULTI_ORG_ID = "multiOrgId";
    public static final String MULTI_JOB_FLOW_TYPE = "multiJobFlowType";
    public static final String JOB_FIELD = "jobField";
    public static final String INDEX_TYPE = "indexType";
    public static final String INDEX_TITLE_EXPS = "indexTitleExps";
    public static final String INDEX_CONTENT_EXPS = "indexContentExps";
    public static final String IS_MOBILE_SHOW = "isMobileShow";
    public static final String PC_SHOW_FLAG = "pcShowFlag";
    public static final String FLOW_AUTO_UPGRADE_ORG_VERSION = "autoUpgradeOrgVersion";
    public static final String FLOW_IS_FREE = "isFree";
    public static final String FLOW_IS_ACTIVE = "isActive";
    public static final String AUTO_UPDATE_TITLE = "autoUpdateTitle";
    public static final String FLOW_REMARK = "remark";
    public static final String FLOW_KEEP_RUNTIME_PERMISSION = "keepRuntimePermission";
    public static final String FLOW_IS_GRANULARITY = "granularity";
    public static final String FLOW_IS_ONLY_ONE_GRANULARITY = "isOnlyOneGranularity";
    public static final String FLOW_ENABLE_ACCESS_PERMISSION_PROVIDER = "enableAccessPermissionProvider";
    public static final String FLOW_ACCESS_PERMISSION_PROVIDER = "accessPermissionProvider";
    public static final String FLOW_ONLY_USE_ACCESS_PERMISSION_PROVIDER = "onlyUseAccessPermissionProvider";

    public static final String FLOW_CREATORS_XPATH = FLOW_PROPERTY_XPATH + "/creators/unit";
    public static final String FLOW_USERS_XPATH = FLOW_PROPERTY_XPATH + "/users/unit";
    public static final String FLOW_MONITORS_XPATH = FLOW_PROPERTY_XPATH + "/monitors/unit";
    /* lmw 2015-4-23 11:12 begin */
    public static final String FLOW_RECORDS_XPATH = FLOW_PROPERTY_XPATH + "/records/record";
    /* lmw 2015-4-23 11:12 end */
    public static final String FLOW_ADMINS_XPATH = FLOW_PROPERTY_XPATH + "/admins/unit";
    /* add by huanglinchuan 2014.10.28 begin */
    public static final String FLOW_VIEWERS_XPATH = FLOW_PROPERTY_XPATH + "/viewers/unit";
    /* add by huanglinchuan 2014.10.28 end */

    public static final String FLOW_DUE_TIME = "dueTime";
    public static final String FLOW_TIME_UNIT = "timeUnit";
    public static final String FLOW_BEGINDIRECTIONS_XPATH = FLOW_PROPERTY_XPATH + "/beginDirections/unit";
    public static final String FLOW_ENDDIRECTIONS_XPATH = FLOW_PROPERTY_XPATH + "/endDirections/unit";

    public static final String FLOW_IS_SEND_FILE = "isSendFile";
    public static final String FLOW_FILE_RECIPIENTS_XPATH = FLOW_PROPERTY_XPATH + "/fileRecipients/unit";
    public static final String FLOW_IS_SENDMSG = "isSendMsg";
    public static final String FLOW_MSG_RECIPIENTS_XPATH = FLOW_PROPERTY_XPATH + "/msgRecipients/unit";
    public static final String FLOW_BAK_USERS_XPATH = FLOW_PROPERTY_XPATH + "/bakUsers/unit";
    public static final String FLOW_STATE_XPATH = FLOW_PROPERTY_XPATH + "/flowStates/flowState";
    public static final String FLOW_STATE_NAME = "name";
    public static final String FLOW_STATE_CODE = "code";
    public static final String FLOW_MESSAGE_TEMPLATES_XPATH = FLOW_PROPERTY_XPATH + "/messageTemplates/template";
    public static final String FLOW_MESSAGE_TEMPLATES_DISTRIBUTERS_XPATH = "distributers/distributer";
    public static final String FLOW_MESSAGE_TEMPLATES_COPYMSGRECIPIENTS_XPATH = "copyMsgRecipients/unit";
    public static final String FLOW_MESSAGE_TEMPLATES_DISTRIBUTER_DESIGNEES_XPATH = "/designees/designee";
    public static final String FLOW_MESSAGE_TEMPLATES_DISTRIBUTIONS_XPATH = "distributions/distribution";
    public static final String FLOW_MESSAGE_TEMPLATES_CONDITIONS_XPATH = "conditions/condition";
    public static final String FLOW_MESSAGE_TEMPLATES_DESIGNEE_UNIT_XPATH = "designees/unit";
    public static final String FLOW_MESSAGE_TEMPLATE_TYPE = "type";
    public static final String FLOW_MESSAGE_TEMPLATE_TYPE_NAME = "typeName";
    public static final String FLOW_MESSAGE_TEMPLATE_ID = "id";
    public static final String FLOW_MESSAGE_TEMPLATE_NAME = "name";
    public static final String FLOW_MESSAGE_TEMPLATE_CONDITION = "condition";
    public static final String FLOW_MESSAGE_TEMPLATE_DISTRIBUION_VALUE = "value";
    public static final String FLOW_MESSAGE_TEMPLATE_DISTRIBUION_TYPE = "dtype";
    public static final String FLOW_MESSAGE_TEMPLATE_DISTRIBUION_TYPE_NAME = "dtypeName";
    public static final String FLOW_MESSAGE_TEMPLATE_CONDITION_CODE = "code";
    public static final String FLOW_MESSAGE_TEMPLATE_CONDITION_SYMBOLS = "symbols";
    public static final String FLOW_MESSAGE_TEMPLATE_CONDITION_VALUE = "value";
    public static final String FLOW_MESSAGE_TEMPLATE_CONDITION_TYPE = "ctype";
    /* modified by huanglinchuan 2014.10.21 begin */
    public static final String FLOW_MESSAGE_TEMPLATE_IS_SEND_MSG = "isSendMsg";
    public static final String FLOW_MESSAGE_TEMPLATE_EXTRA_MSG_RECIPIENTS = "extraMsgRecipients";
    public static final String FLOW_MESSAGE_TEMPLATE_EXTRA_MSG_RECIPIENT_USER_IDS = "extraMsgRecipientUserIds";
    public static final String FLOW_MESSAGE_TEMPLATE_EXTRA_MSG_CUSTOM_RECIPIENTS = "extraMsgCustomRecipients";
    public static final String FLOW_MESSAGE_TEMPLATE_EXTRA_MSG_CUSTOM_RECIPIENT_USER_IDS = "extraMsgCustomRecipientUserIds";
    public static final String FLOW_MESSAGE_TEMPLATE_ENABLE = "enable";
    public static final String FLOW_MESSAGE_TEMPLATE_CONDITION_ENABLE = "conditionEnable";
    public static final String FLOW_MESSAGE_TEMPLATE_CON_EXPRESSION_SIGNAL = "condExpressionSignal";
    /* modified by huanglinchuan 2014.10.21 end */
    public static final String FLOW_FILE_TEMPLATE = "fileTemplate";
    public static final String FLOW_PRINT_TEMPLATE = "printTemplate";
    public static final String FLOW_PRINT_TEMPLATE_ID = "printTemplateId";
    public static final String FLOW_PRINT_TEMPLATE_UUID = "printTemplateUuid";
    public static final String FLOW_LISTENER = "listener";
    public static final String GLOBAL_TASK_FLOW_LISTENER = "globalTaskListener";
    public static final String TIMER_LISTENER = "timerListener";
    public static final String FLOW_CUSTOM_JS_MODULE = "customJsModule";
    public static final String FLOW_EVENT_SCRIPT_XPATH = "eventScripts/eventScript";
    public static final String Flow_OPINION_CHECK_SETS = "opinionCheckSets";
    public static final String Flow_OPINION_CHECK_SET = "opinionCheckSet";
    /* 过程定义 */

    public static final String TASK_ID = "id";
    public static final String TASK_NAME = "name";
    public static final String TASK_CODE = "code";
    public static final String TASK_TYPE = "type";
    public static final String TASK_EXPAND_LIST = "expandList";

    public static final String TASK_IS_SET_USER = "isSetUser";
    public static final String TASK_IS_SET_COPY_USER = "isSetCopyUser";
    public static final String TASK_IS_SET_TRANSFER_USER = "isSetTransferUser";
    public static final String TASK_IS_CONFIRM_COPY_USER = "isConfirmCopyUser";
    public static final String TASK_COPY_USER_CONDITION = "copyUserCondition";
    public static final String TASK_USERS_XPATH = "users/unit";
    public static final String TASK_COPYUSERS_XPATH = "copyUsers/unit";
    public static final String TASK_TRANSFERUSERS_XPATH = "transferUsers/unit";
    public static final String TASK_EMPTY_TO_USERS_XPATH = "emptyToUsers/unit";
    public static final String TASK_IS_SET_USER_EMPTY = "isSetUserEmpty";
    public static final String TASK_EMPTY_TOTASK = "emptyToTask";
    public static final String TASK_EMPTY_NOTE_DONE = "emptyNoteDone";
    public static final String TASK_IS_SELECT_AGAIN = "isSelectAgain";
    public static final String TASK_IS_ONLYONE = "isOnlyOne";
    public static final String TASK_IS_ANYONE = "isAnyone";
    public static final String TASK_IS_BYORDER = "isByOrder";
    public static final String TASK_SAME_USER_SUBMIT = "sameUserSubmit";
    public static final String TASK_IS_SET_MONITOR = "isSetMonitor";
    public static final String TASK_IS_ALLOW_APP = "isAllowApp";
    public static final String TASK_MONITORS_XPATH = "monitors/unit";
    public static final String TASK_IS_INHERIT_MONITOR = "isInheritMonitor";
    public static final String TASK_GRANULARITY = "granularity";
    public static final String TASK_IS_ONLY_ONE_GRANULARITY = "isOnlyOneGranularity";

    public static final String TASK_START_RIGHTS_XPATH = "startRights/unit";
    public static final String TASK_RIGHTS_XPATH = "rights/unit";
    public static final String TASK_DONE_RIGHTS_XPATH = "doneRights/unit";
    public static final String TASK_MONITOR_RIGHTS_XPATH = "monitorRights/unit";
    public static final String TASK_ADMIN_RIGHTS_XPATH = "adminRights/unit";
    public static final String TASK_COPY_TO_RIGHTS_XPATH = "copyToRights/unit";
    public static final String TASK_VIEWER_RIGHTS_XPATH = "viewerRights/unit";
    public static final String TASK_UNTREAD_TYPE = "untreadType";
    public static final String TASK_UNTREAD_TASKS_XPATH = "untreadTasks/unit";

    public static final String TASK_BUTTONS_XPATH = "buttons/button";
    public static final String TASK_BUTTON_BTNVALUE = "btnValue";
    public static final String TASK_BUTTON_NEWNAME = "newName";
    public static final String TASK_BUTTON_BTNARGUMENT = "btnArgument";

    public static final String TASK_FORK_MODE_XPATH = "parallelGateway/forkMode";
    public static final String TASK_FORK_MODE_VALUE = "value";
    public static final String TASK_FORK_MODE_CHOOSE_FORKING_DIRECTION = "chooseForkingDirection";
    public static final String TASK_FORK_MODE_BUSINESS_TYPE = "businessType";
    public static final String TASK_FORK_MODE_BUSINESS_TYPE_NAME = "businessTypeName";
    public static final String TASK_FORK_MODE_BUSINESS_ROLE = "businessRole";
    public static final String TASK_FORK_MODE_BUSINESS_ROLE_NAME = "businessRoleName";
    public static final String TASK_FORK_MODE_BRANCH_TASK_MONITORS_XPATH = TASK_FORK_MODE_XPATH
            + "/branchTaskMonitors/unit";
    public static final String TASK_FORK_MODE_UNDERTAKE_SITUATION_PLACE_HOLDER = "undertakeSituationPlaceHolder";
    public static final String TASK_FORK_MODE_UNDERTAKE_SITUATION_TITLE_EXPRESSION = "undertakeSituationTitleExpression";
    public static final String TASK_FORK_MODE_UNDERTAKE_SITUATION_BUTTONS_XPATH = TASK_FORK_MODE_XPATH
            + "/undertakeSituationButtons/button";
    // 子流程分发承办人列
    public static final String TASK_FORK_MODE_UNDERTAKE_SITUATION_COLUMNS_XPATH = TASK_FORK_MODE_XPATH
            + "/undertakeSituationColumns/column";
    public static final String TASK_FORK_MODE_SORT_RULE_XPATH = TASK_FORK_MODE_XPATH + "/sortRule";
    // 子流程分发承办人排序列
    public static final String TASK_FORK_MODE_UNDERTAKE_SITUATION_ORDER_XPATH = TASK_FORK_MODE_XPATH
            + "/undertakeSituationOrder/order";
    public static final String TASK_FORK_MODE_INFO_DISTRIBUTION_PLACE_HOLDER = "infoDistributionPlaceHolder";
    public static final String TASK_FORK_MODE_INFO_DISTRIBUTION_TITLE_EXPRESSION = "infoDistributionTitleExpression";
    public static final String TASK_FORK_MODE_OPERATION_RECORD_PLACE_HOLDER = "operationRecordPlaceHolder";
    public static final String TASK_FORK_MODE_OPERATION_RECORD_TITLE_EXPRESSION = "operationRecordTitleExpression";
    public static final String TASK_JOIN_MODE_XPATH = "parallelGateway/joinMode";
    public static final String TASK_JOIN_MODE_VALUE = "value";
    public static final String TASK_ALLOW_RETURN_AFTER_ROLLBACK = "allowReturnAfterRollback";
    public static final String TASK_ONLY_RETURN_AFTER_ROLLBACK = "onlyReturnAfterRollback";
    public static final String TASK_NOT_ROLLBACK = "notRollback";
    public static final String TASK_NOT_CANCEL = "notCancel";

    public static final String TASK_ENABLE_OPINION_POSITION = "enableOpinionPosition";
    public static final String TASK_OPTNAMES_XPATH = "optNames/unit";
    public static final String TASK_REQUIRED_OPINION_POSITION = "requiredOpinionPosition";
    public static final String TASK_SHOW_USER_OPINION_POSITION = "showUserOpinionPosition";
    public static final String TASK_SHOW_OPINION_POSITION_STATISTICS = "showOpinionPositionStatistics";

    public static final String TASK_IS_TIMELIMIT = "isTimeLimit";
    // public static final String TASK_TIMERS_XPATH = TASK_XPATH +
    // "/timers/timer";

    public static final String TASK_RECORDS_XPATH = "records/record";
    public static final String TASK_RECORD_NAME = "name";
    public static final String TASK_RECORD_FIELD = "field";
    public static final String TASK_RECORD_VALUE = "value";

    public static final String TASK_FORMID = "formID";
    public static final String TASK_READ_FIELDS_XPATH = "readFields/unit";
    public static final String TASK_EDIT_FIELDS_XPATH = "editFields/unit";
    public static final String TASK_HIDE_FIELDS_XPATH = "hideFields/unit";
    public static final String TASK_NOTNULL_FIELDS_XPATH = "notNullFields/unit";
    public static final String TASK_FILE_RIGHTS_FIELDS_XPATH = "fileRights/unit";
    public static final String ALL_FORM_FIELDS_XPATH = "allFormField/unit";
    public static final String TASK_ALL_FORM_FIELD_WIDGET_IDS_XPATH = "allFormFieldWidgetIds/unit";
    public static final String TASK_FORM_BTN_RIGHT_SETTINGS_XPATH = "formBtnRightSettings/unit";
    // add by wujx 20160728 begin
    public static final String TASK_FIELD_PROPERTY_XPATH = "fieldProperty";
    public static final String TASK_FIELD_PROPERTY_VALUE = "value";
    public static final String TASK_FIELD_PROPERTY_NAME = "name";
    // add by wujx 20160728 end
    public static final String TASK_HIDE_BLOCKS_XPATH = "hideBlocks/unit";

    public static final String TASK_HIDE_TABS_XPATH = "hideTabs/unit";

    public static final String TASK_SN_NAME = "snName";
    public static final String TASK_SERIAL_NO = "serialNo";
    public static final String TASK_PRINT_TEMPLATE = "printTemplate";
    public static final String TASK_PRINT_TEMPLATE_ID = "printTemplateId";
    public static final String TASK_PRINT_TEMPLATE_UUID = "printTemplateUuid";
    public static final String TASK_LISTENER = "listener";
    public static final String TASK_CUSTOM_JS_MODULE = "customJsModule";

    public static final String TASK_EVENT_SCRIPT_XPATH = "eventScripts/eventScript";

    public static final String TASK_X = "X";
    public static final String TASK_Y = "Y";
    public static final String TASK_CAN_EDIT_FORM = "canEditForm";
    public static final String TASK_CONDITION_NAME = "conditionName";
    public static final String TASK_CONDITION_BODY = "conditionBody";
    public static final String TASK_CONDITION_X = "conditionX";
    public static final String TASK_CONDITION_Y = "conditionY";
    public static final String TASK_CONDITION_LINE = "conditionLine";
    public static final String TASK_BUSINESS_TYPE = "businessType";
    public static final String TASK_BUSINESS_TYPE_NAME = "businessTypeName";
    public static final String TASK_BUSINESS_ROLE = "businessRole";
    public static final String TASK_BUSINESS_ROLE_NAME = "businessRoleName";
    public static final String TASK_NEW_FLOW = "newFlows/newFlow";
    public static final String TASK_RELATION = "relations/relation";
    public static final String TASK_UNDERTAKE_SITUATION_PLACE_HOLDER = "undertakeSituationPlaceHolder";
    public static final String TASK_UNDERTAKE_SITUATION_TITLE_EXPRESSION = "undertakeSituationTitleExpression";
    public static final String TASK_UNDERTAKE_SITUATION_BUTTONS_XPATH = TASK_XPATH
            + "/undertakeSituationButtons/button";
    public static final String TASK_UNDERTAKE_SITUATION_COLUMNS_XPATH = TASK_XPATH
            + "/undertakeSituationColumns/column";
    public static final String TASK_SORT_RULE_XPATH = TASK_XPATH + "/sortRule";
    public static final String TASK_UNDERTAKE_SITUATION_ORDERS_XPATH = TASK_XPATH + "/undertakeSituationOrders/order";
    public static final String TASK_INFO_DISTRIBUTION_PLACE_HOLDER = "infoDistributionPlaceHolder";
    public static final String TASK_INFO_DISTRIBUTION_TITLE_EXPRESSION = "infoDistributionTitleExpression";
    public static final String TASK_OPERATION_RECORD_PLACE_HOLDER = "operationRecordPlaceHolder";
    public static final String TASK_OPERATION_RECORD_TITLE_EXPRESSION = "operationRecordTitleExpression";
    public static final String TASK_IN_STAGES_CONDITION = "inStagesCondition";
    // 0:默认不可查看主流程 1:默认可查看主流程
    public static final String TASK_CHILD_LOOK_PARENT = "childLookParent";
    // 1:允许主流程更改查看权限
    public static final String TASK_PARENT_SET_CHILD = "parentSetChild";
    public static final String TASK_SUB_TASK_MONITORS_XPATH = TASK_XPATH + "/subTaskMonitors/unit";
    public static final String TASK_STAGE_DIVISION_FORM_ID = "stageDivisionFormId";
    public static final String TASK_STAGE_HANDLING_STATE = "stageHandlingState";
    public static final String TASK_STAGE_STATE = "stageState";
    public static final String TASK_TRACE_TASK = "traceTask";
    /* 子过程 */
    public static final String TASK_IS_SETFLOW = "isSetFlow";
    public static final String TASK_NEW_FLOWS_XPATH = TASK_XPATH + "/newFlows/newFlow";
    public static final String TASK_IS_AUTOSUBMIT = "isAutoSubmit";
    public static final String TASK_IS_COPYALL = "isCopyAll";
    public static final String TASK_COPY_FIELDS_XPATH = "copyFields/unit";
    public static final String TASK_IS_COPYBODY = "isCopyBody";
    public static final String TASK_IS_COPYATTACH = "isCopyAttach";
    /* 并联关系 */
    public static final String TASK_RELATIONS_XPATH = TASK_XPATH + "/relations/relation";
    public static final String TASK_RELATIONS_NEW_FLOW_NAME = "newFlowName";
    public static final String TASK_RELATIONS_NEW_FLOW_ID = "newFlowId";
    public static final String TASK_RELATIONS_TASK_NAME = "taskName";
    public static final String TASK_RELATIONS_TASK_ID = "taskId";
    public static final String TASK_RELATIONS_FRONT_NEW_FLOW_NAME = "frontNewFlowName";
    public static final String TASK_RELATIONS_FRONT_NEW_FLOW_ID = "frontNewFlowId";
    public static final String TASK_RELATIONS_FRONT_TASK_NAME = "frontTaskName";
    public static final String TASK_RELATIONS_FRONT_TASK_ID = "frontTaskId";
    public static final String TASK_STAGES_XPATH = TASK_XPATH + "/stages/stage";
    public static final String TASK_STAGES_NEW_FLOW_ID = "newFlowId";
    public static final String TASK_StageS_NEW_FLOW_NAME = "newFlowName";
    public static final String TASK_STAGES_TASK_USERS = "taskUsers";
    public static final String TASK_STAGES_TASK_USERS_NAME = "taskUsersName";
    public static final String TASK_STAGES_CREATE_INSTANCE_WAY = "createInstanceWay";
    public static final String TASK_STAGES_LIMIT_TIME = "limitTime";
    public static final String TASK_STAGES_LIMIT_TIME_NAME = "limitTimeName";

    /* 列定义 */
    public static final String COLUMN_TYPE = "type";
    public static final String COLUMN_TYPE_NAME = "typeName";
    public static final String COLUMN_INDEX = "index";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_SOURCES = "sources";
    public static final String COLUMN_SEARCH_FLAG = "searchFlag";
    public static final String COLUMN_EXTRA_COLUMN = "extraColumn";

    /* 排序列定义 */
    public static final String ORDER_NAME = "name";
    public static final String ORDER_DIRECTION = "direction";

    /* 流向定义 */
    public static final String DIRECTION_ID = "id";
    public static final String DIRECTION_NAME = "name";
    public static final String DIRECTION_USE_AS_BUTTON = "useAsButton";
    public static final String DIRECTION_BUTTON_NAME = "buttonName";
    public static final String DIRECTION_BUTTON_CLASS_NAME = "buttonClassName";
    public static final String DIRECTION_BTN_STYLE = "btnStyle";
    public static final String DIRECTION_BUTTON_ORDER = "buttonOrder";
    public static final String DIRECTION_CODE = "code";
    public static final String DIRECTION_FROMID = "fromID";
    public static final String DIRECTION_TOID = "toID";
    public static final String DIRECTION_TYPE = "type";
    public static final String DIRECTION_SORT_ORDER = "sortOrder";
    public static final String DIRECTION_REMARK = "remark";
    public static final String DIRECTION_SHOW_REMARK = "showRemark";

    public static final String DIRECTION_IS_DEFAULT = "isDefault";
    public static final String DIRECTION_IS_EVERYCHECK = "isEveryCheck";

    public static final String DIRECTION_CONDITIONS_XPATH = "conditions/unit";
    public static final String DIRECTION_CONDITION = "condition";

    public static final String DIRECTION_IS_SEND_FILE = "isSendFile";
    public static final String DIRECTION_FILE_RECIPIENTS_XPATH = "fileRecipients/unit";
    public static final String DIRECTION_IS_SEND_MSG = "isSendMsg";
    public static final String DIRECTION_LISTENER = "listener";
    public static final String DIRECTION_BRANCH_MODE = "branchMode";
    public static final String DIRECTION_BRANCH_INSTANCE_TYPE = "branchInstanceType";
    public static final String DIRECTION_BRANCH_CREATE_WAY = "branchCreateWay";
    public static final String DIRECTION_BRANCH_CREATE_INSTANCE_FORM_ID = "branchCreateInstanceFormId";
    public static final String DIRECTION_IS_MAIN_FORM_BRANCH_CREATE_WAY = "isMainFormBranchCreateWay";
    public static final String DIRECTION_BRANCH_TASK_USERS = "branchTaskUsers";
    public static final String DIRECTION_BRANCH_CREATE_INSTANCE_WAY = "branchCreateInstanceWay";
    public static final String DIRECTION_BRANCH_CREATE_INSTANCE_BATCH = "branchCreateInstanceBatch";
    public static final String DIRECTION_BRANCH_TIMER = "branchTimer";
    public static final String DIRECTION_BRANCH_INTERFACE = "branchInterface";
    public static final String DIRECTION_BRANCH_INTERFACE_NAME = "branchInterfaceName";
    public static final String DIRECTION_SHARE_BRANCH = "shareBranch";
    public static final String DIRECTION_IS_INDEPENDENT_BRANCH = "isIndependentBranch";
    public static final String DIRECTION_EVENT_SCRIPT = "eventScript";
    public static final String DIRECTION_ARCHIVE_XPATH = "archives/archive";
    public static final String DIRECTION_ARCHIVE_ARCHIVE_ID = "archiveId";
    public static final String DIRECTION_ARCHIVE_ARCHIVE_WAY = "archiveWay";
    public static final String DIRECTION_ARCHIVE_ARCHIVE_STRATEGY = "archiveStrategy";
    public static final String DIRECTION_ARCHIVE_BOT_RULE_NAME = "botRuleName";
    public static final String DIRECTION_ARCHIVE_BOT_RULE_ID = "botRuleId";
    public static final String DIRECTION_ARCHIVE_DEST_FOLDER_NAME = "destFolderName";
    public static final String DIRECTION_ARCHIVE_DEST_FOLDER_UUID = "destFolderUuid";
    public static final String DIRECTION_ARCHIVE_SUB_FOLDER_RULE = "subFolderRule";
    public static final String DIRECTION_ARCHIVE_FILL_DATETIME = "fillDateTime";
    public static final String DIRECTION_ARCHIVE_ARCHIVE_SCRIPT_TYPE = "archiveScriptType";
    public static final String DIRECTION_ARCHIVE_ARCHIVE_SCRIPT = "archiveScript";
    public static final String DIRECTION_EVENT_SCRIPT_ATTRIBUTE_TYPE = "type";
    public static final String DIRECTION_MSG_RECIPIENTS_XPATH = "msgRecipients/unit";
    public static final String DIRECTION_LINE = "line";
    public static final String DIRECTION_LINE_LABEL = "lineLabel";
    public static final String DIRECTION_IS_SHOW_NAME = "isShowName";

    public static final String DIRECTION_TERMINAL_NAME = "terminalName";
    public static final String DIRECTION_TERMINAL_TYPE = "terminalType";
    public static final String DIRECTION_TERMINAL_X = "terminalX";
    public static final String DIRECTION_TERMINAL_Y = "terminalY";
    public static final String DIRECTION_TERMINAL_BODY = "terminalBody";

    /* label */

    public static final String LABEL_X = "X";
    public static final String LABEL_Y = "Y";
    public static final String LABEL_W = "W";
    public static final String LABEL_H = "H";
    public static final String LABEL_HTML = "html";

    /* 分割符 */
    public static final String SPLIT = "|";
    public static final String SPLIT_ARGVALUE = "%@%";

    /* 字段定义 */
    public static final String FIELD_FLOW_USERS = "users";
    public static final String FIELD_FLOW_MONITORS = "monitors";
    public static final String FIELD_FLOW_CREATORS = "creators";
    public static final String FIELD_FLOW_ADMINS = "admins";

    public static final String FIELD_FLOW_FILERECIPIENTS = "fileRecipients";
    public static final String FIELD_FLOW_MSGRECIPIENTS = "msgRecipients";
    public static final String FIELD_FLOW_BEGINDIRECTIONS = "beginDirections";
    public static final String FIELD_FLOW_ENDDIRECTIONS = "endDirections";
    public static final String FIELD_FLOW_BAKUSERS = "bakUsers";
    public static final String FIELD_DIRECTION_FILERECIPIENTS = "fileRecipients";
    public static final String FIELD_DIRECTION_CONDITIONS = "conditions";
    public static final String FIELD_DIRECTION_MSGRECIPIENTS = "msgRecipients";
    public static final String FIELD_TASK_NEWFLOWS = "newFlows";
    public static final String FIELD_TASK_COPYFIELDS = "copyFields";
    public static final String FIELD_TASK_USERS = "users";
    public static final String FIELD_TASK_COPYUSERS = "copyUsers";
    public static final String FIELD_TASK_MONITORS = "monitors";
    public static final String FIELD_TASK_RIGHTS = "rights";
    public static final String FIELD_TASK_UNTREADTASKS = "untreadTasks";
    public static final String FIELD_TASK_BUTTONS = "buttons";
    public static final String FIELD_TASK_OPTNAMES = "optNames";
    public static final String FIELD_TASK_READFIELDS = "readFields";
    public static final String FIELD_TASK_EDITFIELDS = "editFields";
    public static final String FIELD_TASK_HIDEFIELDS = "hideFields";
    public static final String FIELD_TASK_NOTNULLFIELDS = "notNullFields";
    public static final String FIELD_TIMER_DUTYS = "dutys";
    public static final String FIELD_TIMER_TASKS = "tasks";
    public static final String FIELD_TIMER_ALARMOBJECTS = "alarmObjects";
    public static final String FIELD_TIMER_ALARMUSERS = "alarmUsers";
    public static final String FIELD_TIMER_ALARMFLOWDOINGS = "alarmFlowDoings";
    public static final String FIELD_TIMER_ALARMFLOWDOINGUSERS = "alarmFlowDoingUsers";
    public static final String FIELD_TIMER_DUEOBJECTS = "dueObjects";
    public static final String FIELD_TIMER_DUEUSERS = "dueUsers";
    public static final String FIELD_TIMER_DUETOUSERS = "dueToUsers";
    public static final String FIELD_TIMER_DUEFLOWDOINGS = "dueFlowDoings";
    public static final String FIELD_TIMER_DUEFLOWDOINGUSERS = "dueFlowDoingUsers";

    /* 节点定义 */
    public static final String NODE_FLOW = "flow";
    public static final String NODE_UNIT = "unit";
    public static final String NODE_FLOW_PROPERTY = "property";
    public static final String NODE_FLOW_EQUAL = "equalFlow";
    public static final String NODE_FLOW_CREATORS = "creators";
    public static final String NODE_FLOW_USERS = "users";
    public static final String NODE_FLOW_MONITORS = "monitors";
    public static final String NODE_FLOW_ADMINS = "admins";
    public static final String NODE_FLOW_BEGINDIRECTIONS = "beginDirections";
    public static final String NODE_FLOW_ENDDIRECTIONS = "endDirections";
    public static final String NODE_FLOW_FILERECIPIENTS = "fileRecipients";
    public static final String NODE_FLOW_MSGRECIPIENTS = "msgRecipients";
    public static final String NODE_FLOW_BAKUSERS = "bakUsers";
    public static final String NODE_TIMERS = "timers";
    public static final String NODE_TIMER = "timer";
    public static final String NODE_TIMER_DUTYS = "dutys";
    public static final String NODE_TIMER_TASKS = "tasks";
    public static final String NODE_TIMER_ALARMFLOW = "alarmFlow";
    public static final String NODE_TIMER_DUEFLOW = "dueFlow";
    public static final String NODE_TIMER_ALARMOBJECTS = "alarmObjects";
    public static final String NODE_TIMER_ALARMUSERS = "alarmUsers";
    public static final String NODE_TIMER_ALARMFLOWDOINGS = "alarmFlowDoings";
    public static final String NODE_TIMER_ALARMFLOWDOINGUSERS = "alarmFlowDoingUsers";
    public static final String NODE_TIMER_DUEOBJECTS = "dueObjects";
    public static final String NODE_TIMER_DUEUSERS = "dueUsers";
    public static final String NODE_TIMER_DUETOUSERS = "dueToUsers";
    public static final String NODE_TIMER_DUEFLOWDOINGS = "dueFlowDoings";
    public static final String NODE_TIMER_DUEFLOWDOINGUSERS = "dueFlowDoingUsers";
    public static final String NODE_TASKS = "tasks";
    public static final String NODE_TASK = "task";
    public static final String NODE_TASK_USERS = "users";
    public static final String NODE_TASK_COPYUSERS = "copyUsers";
    public static final String NODE_TASK_MONITORS = "monitors";
    public static final String NODE_TASK_RIGHTS = "rights";
    public static final String NODE_TASK_UNTREADTASKS = "untreadTasks";
    public static final String NODE_TASK_OPTNAMES = "optNames";
    public static final String NODE_TASK_READFIELDS = "readFields";
    public static final String NODE_TASK_EDITFIELDS = "editFields";
    public static final String NODE_TASK_HIDEFIELDS = "hideFields";
    public static final String NODE_TASK_NOTNULLFIELDS = "notNullFields";
    public static final String NODE_TASK_NEWFLOWS = "newFlows";
    public static final String NODE_TASK_COPYFIELDS = "copyFields";
    public static final String NODE_TASK_BUTTONS = "buttons";
    public static final String NODE_TASK_BUTTON = "button";
    public static final String NODE_TASK_RECORDS = "records";
    public static final String NODE_TASK_RECORD = "record";
    public static final String NODE_DIRECTIONS = "directions";
    public static final String NODE_DIRECTION = "direction";
    public static final String NODE_LABELS = "labels";
    public static final String NODE_LABEL = "label";
    public static final String NODE_DELETES = "deletes";
    public static final String NODE_DIRECTION_FILERECIPIENTS = "fileRecipients";
    public static final String NODE_DIRECTION_MSGRECIPIENTS = "msgRecipients";
    public static final String NODE_DIRECTION_CONDITIONS = "conditions";
    /* 常量定义 */
    public static final int BOOL_TRUE = 1;
    public static final int BOOL_FALSE = 0;
    public static final int TASK_NORMAL_TYPE = 1;
    public static final int TASK_CHILD_TYPE = 2;
    // type user 也包括部门和群组
    public static final Integer UNIT_TYPE_USER = 1;
    public static final Integer UNIT_TYPE_FORM = 2;
    public static final Integer UNIT_TYPE_TASK = 4;
    public static final Integer UNIT_TYPE_SPECIALVALUE = 8;
    public static final Integer UNIT_TYPE_MULTIVALUE = 16;
    public static final Integer UNIT_TYPE_SINGLEVALUE = 32;
    // /*计时系统*/
    // public static String TIMER_NAME = "name";
    // public static String TIMER_SN = "limit";
    // public static String TIMER_UNIT = "unit";
    // public static String TIMER_DUTY = "duty";
    // public static String TIMER_ALARM_TIME = "alarmTime";
    // public static String TIMER_ALARM_UNIT = "alarmUnit";
    // public static String TIMER_ALARM_FREQUENCY = "alarmFrequency";
    // public static String TIMER_ALARM_FLOW_NAME = "alarmFlowName";
    // public static String TIMER_ALARM_FLOWID = "alarmFlowID";
    // public static String TIMER_ALARM_FLOW_DOINGS = "alarmFlowDoings";
    // public static String TIMER_DUE_TIME = "dueTime";
    // public static String TIMER_DUE_UNIT = "dueUnit";
    // public static String TIMER_DUE_FREQUENCY = "dueFrequency";
    // public static String TIMER_DUE_ACTION = "dueAction";
    // public static String TIMER_DUE_TO_TASK = "dueToTask";
    // public static String TIMER_DUE_FLOW_NAME = "dueFlowName";
    // public static String TIMER_DUE_FLOWID = "dueFlowID";
    /* 计时系统 */
    public static final String TIMER_NAME = "name";
    public static final String TIMER_ID = "timerId";
    public static final String TIMER_CONFIG_UUID = "timerConfigUuid";
    public static final String TIMER_INTRODUCTION_TYPE = "introductionType";
    public static final String TIMER_INCLUDE_START_TIME_POINT = "includeStartTimePoint";
    public static final String TIMER_AUTO_DELAY = "autoDelay";
    public static final String TIMER_TIME_LIMIT_UNIT = "timeLimitUnit";
    public static final String TIMER_WORK_TIME_PLAN_UUID = "workTimePlanUuid";
    public static final String TIMER_WORK_TIME_PLAN_ID = "workTimePlanId";
    public static final String TIMER_WORK_TIME_PLAN_NAME = "workTimePlanName";
    public static final String TIMER_LIMIT_TYPE = "limitTimeType";
    public static final String TIMER_LIMIT_1 = "limitTime1";
    public static final String TIMER_LIMIT = "limitTime";
    public static final String TIMER_AUTO_UPDATE_LIMIT_TIME = "autoUpdateLimitTime";
    public static final String TIMER_IGNORE_EMPTY_LIMIT_TIME = "ignoreEmptyLimitTime";
    public static final String TIMER_UNIT = "limitUnit";
    public static final String TIMER_DUTYS_XPATH = "dutys/unit";
    public static final String TIMER_DUTY = "duty";
    public static final String TIMER_TASKS_XPATH = "tasks/unit";
    public static final String TIMER_SUBTASKS_XPATH = "subTasks/subTask";
    public static final String TIMER_SUBTASK_TASK_ID = "taskId";
    public static final String TIMER_SUBTASK_TIMING_MODE = "timingMode";
    public static final String TIMER_SUBTASK_TIMERS_XPATH = "timers/timer";
    public static final String TIMER_SUBTASK_TIMERS_NEW_FLOW_ID = "newFlowId";
    public static final String TIMER_SUBTASK_TIMERS_NEW_FLOW_TIMER_NAME = "newFlowTimerName";
    public static final String TIMER_SUBTASK_TIMERS_NEW_FLOW_TIMER_ID = "newFlowTimerId";
    public static final String TIMER_TIME_END_TYPE = "timeEndType";
    public static final String TIMER_OVER_DIRECTIONS = "overDirections";
    public static final String TIMER_AFFECT_MAIN_FLOW = "affectMainFlow";
    public static final String TIMER_ENABLE_ALARM = "enableAlarm";
    public static final String TIMER_ENABLE_DUE_DOING = "enableDueDoing";
    public static final String TIMER_ALARM_TIME = "alarmTime";
    public static final String TIMER_ALARM_UNIT = "alarmUnit";
    public static final String TIMER_ALARM_FREQUENCY = "alarmFrequency";
    public static final String TIMER_ALARM_OBJECTS_XPATH = "alarmObjects/unit";
    public static final String TIMER_ALARM_USERS_XPATH = "alarmUsers/unit";
    public static final String TIMER_ALARMS_XPATH = "alarms/alarm";
    public static final String TIMER_ALARM_FLOW_XPATH = "alarmFlow";
    public static final String TIMER_ALARM_FLOWNAME = "name";
    public static final String TIMER_ALARM_FLOWID = "id";
    public static final String TIMER_ALARM_FLOWDOINGS_XPATH = "alarmFlowDoings/unit";
    public static final String TIMER_ALARM_FLOWDOING_USERS_XPATH = "alarmFlowDoingUsers/unit";
    public static final String TIMER_DUE_OBJECTS_XPATH = "dueObjects/unit";
    public static final String TIMER_DUE_USERS_XPATH = "dueUsers/unit";
    public static final String TIMER_DUE_TIME = "dueTime";
    public static final String TIMER_DUE_UNIT = "dueUnit";
    public static final String TIMER_DUE_FREQUENCY = "dueFrequency";
    public static final String TIMER_DUE_ACTION = "dueAction";
    public static final String TIMER_DUE_TOUSERS_XPATH = "dueToUsers/unit";
    public static final String TIMER_DUE_TOTASK = "dueToTask";
    public static final String TIMER_DUE_FLOW_XPATH = "dueFlow";
    public static final String TIMER_DUE_FLOWNAME = "name";
    public static final String TIMER_DUE_FLOWID = "id";
    public static final String TIMER_DUE_FLOWDOINGS_XPATH = "dueFlowDoings/unit";
    public static final String TIMER_DUE_FLOWDOINGUSERS_XPATH = "dueFlowDoingUsers/unit";

    public static String FLOW_ATTR_NAME = "name";
    public static String FLOW_ATTR_VERSION = "version";
    public static String FLOW_ATTR_VERTYPE = "verType";
    public static String FLOW_CHILD_DESC = "description";
    public static String FLOW_CHILD_LIMITTIME = "limitTime";
    public static String FLOW_ATTR_CHNAME = "chname";

    public static String ACT_START_ID = "act_start";
    public static String ACT_END_ID = "act_end";

    public static String ACT_ATTR_TYPE = "type";
    public static String ACT_ATTR_ID = "id";
    public static String ACT_ATTR_NAME = "name";

    public static String ACT_CHILD_DESC = "description";
    public static String ACT_CHILD_SPLIT = "splitMode";
    public static String ACT_CHILD_JOIN = "joinMode";
    public static String ACT_CHILD_LIMITTIME = "limitTime";
    public static String ACT_CHILD_SUBPROCESS = "subProcess";
    public static String ACT_CHILD_SP_INVOKE_PATTERN = "invokePattern";
    public static String ACT_CHILD_PARTICI_MODE = "participantType";
    public static String ACT_CHILD_PARTICI_IS_ALLOW_APPOINT = "isAllowAppointParticipants";
    public static String ACT_CHILD_PARTICIPANT = "Participants/participant";
    public static String ACT_CHILD_PARTICIPANT_ID = "id";
    public static String ACT_CHILD_PARTICIPANT_NAME = "name";
    public static String ACT_CHILD_PARTICIPANT_TYPE = "type";
    public static String ACT_CHILD_PARTICI_ACTID = "particiSpecActID";
    public static String ACT_CHILD_PARTICI_LOGIC = "particiLogic";
    // 工作项
    public static String ACT_CHILD_WI_MODE = "wiMode";
    public static String ACT_CHILD_WI_IS_SEQ_EXEC = "isSequentialExecute";
    public static String ACT_CHILD_WI_FINISHRULE = "finishRule";
    public static String ACT_CHILD_WI_WORKITEMNUMSTRATEGY = "workitemNumStrategy";
    public static String ACT_CHILD_WI_FINISHREQUIREDPERCENT = "finishRequiredPercent";
    public static String ACT_CHILD_WI_FINISHRQUIREDNUM = "finishRquiredNum";
    public static String ACT_CHILD_WI_IS_AUTO_CANCEL = "isAutoCancel";

    // 流程启动策略
    public static String ACT_CHILD_ACTIVATE_RULE_TYPE = "activateRuleType";
    public static String ACT_CHILD_STARTSTRATEGYBYAPPACTION = "startStrategybyAppAction";
    public static String ACT_CHILD_RESET_PARTICIPANT = "resetParticipant";

    // 环节操作
    public static String ACT_CHILD_ACTION = "action";
    public static String ACT_OPERATION = "Operations/operation";
    public static String ACT_OPERATION_ID = "id";
    public static String ACT_OPERATION_CODE = "code";
    public static String ACT_OPERATION_NAME = "name";
    public static String ACT_OPERATION_ACTION = "action";

    // 自由流相关子节点
    public static String ACT_FREE_ISFREEACT = "isFreeActivity";
    public static String ACT_FREE_ISONLYLIMITEDMANUALACT = "isOnlyLimitedManualActivity";
    public static String ACT_FREE_RANGESTRATEGY = "freeRangeStrategy";
    public static String ACT_FREE_ACT = "FreeActivities/freeActivity";
    public static String ACT_FREE_ACT_ID = "id";
    public static String ACT_FREE_ACT_NAME = "name";
    public static String ACT_FREE_ACT_TYPE = "type";

    // 自动环节
    public static String ACT_AUTO_FINSISH_TYPE = "finishType";
    public static String ACT_AUTO_INVOKE_PATTERN = "invokePattern";
    public static String ACT_AUTO_TRANSACTION_TYPE = "transactionType";
    public static String ACT_AUTO_EXEC_ACTION = "executeAction";
    public static String ACT_EXCEPTION_STRATEGY = "exceptionStrategy";
    public static String ACT_EXCEPTION_ACTION = "exceptionAction";

    // 扩展属性
    public static String FLOW_EXT_PROPERTY = "ExtendNodes/extendNode";

    public static String TRAN_ATTR_ID = "id";
    public static String TRAN_ATTR_NAME = "name";
    public static String TRAN_ATTR_TO = "to";
    public static String TRAN_ATTR_FROM = "from";
    public static String TRAN_CHILD_ISSIMPLEEXPRESSION = "isSimpleExpression";
    public static String TRAN_CHILD_LEFTVALUE = "leftValue";
    public static String TRAN_CHILD_COMPTYPE = "compType";
    public static String TRAN_CHILD_RIGHTVALUE = "rightValue";
    public static String TRAN_CHILD_COMPLEXEXPRESSIONVALUE = "complexExpressionValue";
    public static String TRAN_CHILD_ISDEFAULT = "isDefault";
    public static String TRAN_CHILD_PRIORITY = "priority";

    // 一人多职流转设置选项
    public static final String FLOW_BY_USER_MAIN_JOB = "flow_by_user_main_job";
    public static final String FLOW_BY_USER_ALL_JOBS = "flow_by_user_all_jobs";
    public static final String FLOW_BY_USER_SELECT_JOB = "flow_by_user_select_job";

}
