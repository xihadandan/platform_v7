CREATE TABLE WF_FLOW_DEFINITION_USER
(
    "UUID"                NUMBER(20, 0) NOT NULL,
    "CREATE_TIME"         TIMESTAMP(6),
    "CREATOR"             VARCHAR2(50 CHAR),
    "MODIFIER"            VARCHAR2(50 CHAR),
    "MODIFY_TIME"         TIMESTAMP(6),
    "REC_VER"             NUMBER(10, 0),
    "FLOW_DEF_UUID"       VARCHAR2(50 CHAR),
    "NODE_TYPE"           VARCHAR2(10 CHAR),
    "NODE_NAME"           VARCHAR2(150),
    "NODE_ID"             VARCHAR2(50 CHAR),
    "NODE_USER_ATTRIBUTE" VARCHAR2(50 CHAR),
    "USER_TYPE"           VARCHAR2(10 CHAR),
    "USER_VALUE"          VARCHAR2(200),
    "USER_ARG_VALUE"      VARCHAR2(200),
    "USER_ORG_ID"         VARCHAR2(32 CHAR),
    "SORT_ORDER"          NUMBER(10, 0),
    PRIMARY KEY ("UUID")
);

COMMENT
    ON TABLE WF_FLOW_DEFINITION_USER IS '流程定义用户信息';
COMMENT
    ON COLUMN WF_FLOW_DEFINITION_USER.UUID IS 'UUID，系统字段';
COMMENT
    ON COLUMN WF_FLOW_DEFINITION_USER.CREATE_TIME IS '创建时间';
COMMENT
    ON COLUMN WF_FLOW_DEFINITION_USER.CREATOR IS '创建人';
COMMENT
    ON COLUMN WF_FLOW_DEFINITION_USER.MODIFIER IS '修改人';
COMMENT
    ON COLUMN WF_FLOW_DEFINITION_USER.MODIFY_TIME IS '修改时间';
COMMENT
    ON COLUMN WF_FLOW_DEFINITION_USER.REC_VER IS '版本号';
COMMENT
    ON COLUMN WF_FLOW_DEFINITION_USER.FLOW_DEF_UUID IS '流程定义UUID';
COMMENT
    ON COLUMN WF_FLOW_DEFINITION_USER.NODE_TYPE IS '节点类型，flow流程属性、timer计时器、task环节属性';
COMMENT
    ON COLUMN WF_FLOW_DEFINITION_USER.NODE_NAME IS '流程名称、计时器名称、环节名称';
COMMENT
    ON COLUMN WF_FLOW_DEFINITION_USER.NODE_ID IS '流程ID、计时器ID、环节ID';
COMMENT
    ON COLUMN WF_FLOW_DEFINITION_USER.NODE_USER_ATTRIBUTE IS '节点用户属性，如环节办理人users、抄送人copyUsers';
COMMENT
    ON COLUMN WF_FLOW_DEFINITION_USER.USER_TYPE IS '用户类型';
COMMENT
    ON COLUMN WF_FLOW_DEFINITION_USER.USER_VALUE IS '用户ID值';
COMMENT
    ON COLUMN WF_FLOW_DEFINITION_USER.USER_ARG_VALUE IS '用户ID值参数';
COMMENT
    ON COLUMN WF_FLOW_DEFINITION_USER.USER_ORG_ID IS '用户组织ID';
COMMENT
    ON COLUMN WF_FLOW_DEFINITION_USER.SORT_ORDER IS '排序号';

CREATE INDEX WF_FDU_FLOW_DEF_UUID_IDX ON WF_FLOW_DEFINITION_USER (FLOW_DEF_UUID);


alter table log_user_operation
    add details_clob clob;
update log_user_operation t
set t.details_clob = t.details;
commit;
alter table log_user_operation rename column details to details_bak;
alter table log_user_operation rename column details_clob to details;

alter table log_user_operation
    add system varchar2(64);
alter table log_user_operation
    add tenant varchar2(64);
comment
    on column log_user_operation.system is '归属系统';
comment
    on column log_user_operation.tenant is '归属租户';


alter table wf_task_form_attachment
    add content_clob clob;
alter table wf_task_form_attachment_log
    add content_clob clob;
update wf_task_form_attachment t
set t.content_clob = t.content;
update wf_task_form_attachment_log t
set t.content_clob = t.content;
commit;
alter table wf_task_form_attachment rename column content to content_bak;
alter table wf_task_form_attachment rename column content_clob to content;
alter table wf_task_form_attachment_log rename column content to content_bak;
alter table wf_task_form_attachment_log rename column content_clob to content;

alter table WF_FLOW_DEFINITION_USER
    modify USER_VALUE varchar(4000);
alter table WF_FLOW_DEFINITION_USER
    modify USER_ARG_VALUE varchar(4000);
alter table WF_FLOW_DEFINITION_USER
    modify USER_ORG_ID varchar(500);