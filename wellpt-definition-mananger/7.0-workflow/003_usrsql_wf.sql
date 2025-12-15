alter table wf_flow_definition
    add system varchar2(64);
alter table wf_flow_definition
    add tenant varchar2(64);

comment
    on column wf_flow_definition.system is
    '归属系统';
comment
    on column wf_flow_definition.tenant is
    '归属租户';

update wf_flow_definition t1
set t1.tenant = 'T001'
where t1.tenant is null;
commit;


alter table wf_def_format
    add system varchar2(64);
alter table wf_def_format
    add tenant varchar2(64);

comment
    on column wf_def_format.system is
    '归属系统';
comment
    on column wf_def_format.tenant is
    '归属租户';

update wf_def_format t1
set t1.tenant = 'T001'
where t1.tenant is null;
commit;


alter table wf_task_operation
    add opinion_file_ids varchar2(4000);
comment
    on column wf_task_operation.opinion_file_ids is
    '办理意见附件ID，多个以分号隔开';

alter table wf_def_format
    modify value varchar2(4000);


CREATE TABLE WF_FLOW_SETTING
(
    "UUID"        NUMBER(20, 0) NOT NULL,
    "CREATE_TIME" TIMESTAMP(6),
    "CREATOR"     VARCHAR2(50 CHAR),
    "MODIFIER"    VARCHAR2(50 CHAR),
    "MODIFY_TIME" TIMESTAMP(6),
    "REC_VER"     NUMBER(10, 0),
    "SYSTEM"      VARCHAR2(64),
    "TENANT"      VARCHAR2(64),
    "ATTR_KEY"    VARCHAR2(64),
    "ATTR_VAL"    VARCHAR2(2000),
    "IS_ENABLED"  NUMBER(1, 0),
    "CATEGORY"    VARCHAR2(64),
    "REMARK"      VARCHAR2(300 CHAR),
    PRIMARY KEY ("UUID")
);

COMMENT
    ON TABLE WF_FLOW_SETTING IS '流程设置';
COMMENT
    ON COLUMN WF_FLOW_SETTING.UUID IS 'UUID，系统字段';
COMMENT
    ON COLUMN WF_FLOW_SETTING.CREATE_TIME IS '创建时间';
COMMENT
    ON COLUMN WF_FLOW_SETTING.CREATOR IS '创建人';
COMMENT
    ON COLUMN WF_FLOW_SETTING.MODIFIER IS '修改人';
COMMENT
    ON COLUMN WF_FLOW_SETTING.MODIFY_TIME IS '修改时间';
COMMENT
    ON COLUMN WF_FLOW_SETTING.REC_VER IS '版本号';
COMMENT
    ON COLUMN WF_FLOW_SETTING.SYSTEM IS '归属系统';
COMMENT
    ON COLUMN WF_FLOW_SETTING.TENANT IS '归属租户';
COMMENT
    ON COLUMN WF_FLOW_SETTING.ATTR_KEY IS '属性键';
COMMENT
    ON COLUMN WF_FLOW_SETTING.ATTR_VAL IS '属性值';
COMMENT
    ON COLUMN WF_FLOW_SETTING.IS_ENABLED IS '是否启用';
COMMENT
    ON COLUMN WF_FLOW_SETTING.CATEGORY IS '分类';
COMMENT
    ON COLUMN WF_FLOW_SETTING.REMARK IS '描述';
