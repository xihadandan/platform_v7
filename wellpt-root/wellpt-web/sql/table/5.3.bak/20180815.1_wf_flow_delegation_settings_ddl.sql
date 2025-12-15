drop table WF_TASK_DELEGATION;
-- Create table
create table WF_TASK_DELEGATION
(
  uuid                     VARCHAR2(255 CHAR) not null,
  create_time              TIMESTAMP(6),
  creator                  VARCHAR2(255 CHAR),
  modifier                 VARCHAR2(255 CHAR),
  modify_time              TIMESTAMP(6),
  rec_ver                  NUMBER(10),
  flow_inst_uuid           VARCHAR2(255 CHAR),
  task_inst_uuid           VARCHAR2(255 CHAR),
  delegation_settings_uuid VARCHAR2(255 CHAR),
  consignor                VARCHAR2(255 CHAR),
  consignor_name           VARCHAR2(255 CHAR),
  trustee                  VARCHAR2(255 CHAR),
  trustee_name             VARCHAR2(255 CHAR),
  task_identity_uuid       VARCHAR2(255 CHAR),
  due_to_take_back_work    NUMBER(1),
  deactive_to_take_back_work    NUMBER(1),
  completion_state         NUMBER(10),
  from_time                TIMESTAMP(6),
  to_time                  TIMESTAMP(6)
);
-- Add comments to the columns 
comment on column WF_TASK_DELEGATION.uuid
  is 'UUID，系统字段';
comment on column WF_TASK_DELEGATION.create_time
  is '创建时间';
comment on column WF_TASK_DELEGATION.creator
  is '创建人';
comment on column WF_TASK_DELEGATION.modifier
  is '修改人';
comment on column WF_TASK_DELEGATION.modify_time
  is '修改时间';
comment on column WF_TASK_DELEGATION.rec_ver
  is '版本号';
comment on column WF_TASK_DELEGATION.flow_inst_uuid
  is '流程实例UUID';
comment on column WF_TASK_DELEGATION.task_inst_uuid
  is '任务实例UUID';
comment on column WF_TASK_DELEGATION.delegation_settings_uuid
  is '委托设置UUID';
comment on column WF_TASK_DELEGATION.consignor
  is '委托人ID';
comment on column WF_TASK_DELEGATION.consignor_name
  is '委托人名称';
comment on column WF_TASK_DELEGATION.trustee
  is '受托人ID';
comment on column WF_TASK_DELEGATION.trustee_name
  is '受托人名称';
comment on column WF_TASK_DELEGATION.task_identity_uuid
  is '受托人待办信息，多个待办信息以分号隔开';
comment on column WF_TASK_DELEGATION.due_to_take_back_work
  is '到期自动收回受委托人在委托期间还未处理的待办工作';
comment on column WF_TASK_DELEGATION.deactive_to_take_back_work
  is '手动终止时自动收回受委托人在委托期间还未处理的待办工作';
comment on column WF_TASK_DELEGATION.completion_state
  is '完成状态 0运行中、1等待回收中、2已结束';
comment on column WF_TASK_DELEGATION.from_time
  is '开始时间';
comment on column WF_TASK_DELEGATION.to_time
  is '结束时间';
  
  
  
  
CREATE TABLE "WF_FLOW_DELEGATION_SETTINGS" 
   (	"UUID" VARCHAR2(255 CHAR) NOT NULL ENABLE, 
	"CREATE_TIME" TIMESTAMP (6), 
	"CREATOR" VARCHAR2(255 CHAR), 
	"MODIFIER" VARCHAR2(255 CHAR), 
	"MODIFY_TIME" TIMESTAMP (6), 
	"REC_VER" NUMBER(10,0), 
	"CONSIGNOR" VARCHAR2(255 CHAR), 
	"CONSIGNOR_NAME" VARCHAR2(255 CHAR), 
	"TRUSTEE" VARCHAR2(255 CHAR), 
	"TRUSTEE_NAME" VARCHAR2(255 CHAR), 
	"CONTENT" CLOB, 
	"CONTENT_NAME" CLOB, 
	"INCLUDE_CURRENT_WORK" NUMBER(1,0), 
	"DUE_TO_TAKE_BACK_WORK" NUMBER(1,0), 
	"DEACTIVE_TO_TAKE_BACK_WORK" NUMBER(1,0), 
	"STATUS" NUMBER(10,0), 
	"FROM_TIME" TIMESTAMP (6), 
	"TO_TIME" TIMESTAMP (6), 
	"SYSTEM_UNIT_ID" VARCHAR2(255 CHAR)
   );
   COMMENT ON COLUMN "WF_FLOW_DELEGATION_SETTINGS"."UUID" IS 'UUID，系统字段';
   COMMENT ON COLUMN "WF_FLOW_DELEGATION_SETTINGS"."CREATE_TIME" IS '创建时间';
   COMMENT ON COLUMN "WF_FLOW_DELEGATION_SETTINGS"."CREATOR" IS '创建人';
   COMMENT ON COLUMN "WF_FLOW_DELEGATION_SETTINGS"."MODIFIER" IS '修改人';
   COMMENT ON COLUMN "WF_FLOW_DELEGATION_SETTINGS"."MODIFY_TIME" IS '修改时间';
   COMMENT ON COLUMN "WF_FLOW_DELEGATION_SETTINGS"."REC_VER" IS '版本号';
   COMMENT ON COLUMN "WF_FLOW_DELEGATION_SETTINGS"."CONSIGNOR" IS '委托人ID，只能是一个用户ID';
   COMMENT ON COLUMN "WF_FLOW_DELEGATION_SETTINGS"."CONSIGNOR_NAME" IS '委托人';
   COMMENT ON COLUMN "WF_FLOW_DELEGATION_SETTINGS"."TRUSTEE" IS '受托人ID，只能是一个用户ID';
   COMMENT ON COLUMN "WF_FLOW_DELEGATION_SETTINGS"."TRUSTEE_NAME" IS '受托人';
   COMMENT ON COLUMN "WF_FLOW_DELEGATION_SETTINGS"."CONTENT" IS '委托内容';
   COMMENT ON COLUMN "WF_FLOW_DELEGATION_SETTINGS"."CONTENT_NAME" IS '委托内容显示值';
   COMMENT ON COLUMN "WF_FLOW_DELEGATION_SETTINGS"."INCLUDE_CURRENT_WORK" IS '包括当前待办工作';
   COMMENT ON COLUMN "WF_FLOW_DELEGATION_SETTINGS"."DUE_TO_TAKE_BACK_WORK" IS '到期自动收回受委托人在委托期间还未处理的待办工作';
   COMMENT ON COLUMN "WF_FLOW_DELEGATION_SETTINGS"."DEACTIVE_TO_TAKE_BACK_WORK" IS '手动终止时自动收回受委托人在委托期间还未处理的待办工作';
   COMMENT ON COLUMN "WF_FLOW_DELEGATION_SETTINGS"."STATUS" IS '状态0终止，1激活，2征求受托人意见';
   COMMENT ON COLUMN "WF_FLOW_DELEGATION_SETTINGS"."FROM_TIME" IS '开始时间';
   COMMENT ON COLUMN "WF_FLOW_DELEGATION_SETTINGS"."TO_TIME" IS '结束时间';
   COMMENT ON COLUMN "WF_FLOW_DELEGATION_SETTINGS"."SYSTEM_UNIT_ID" IS '系统单位ID';
   
   
   -- Add/modify columns 
alter table WF_FLOW_DELEGATION_SETTINGS add consult_message_id VARCHAR2(255 CHAR);
-- Add comments to the columns 
comment on column WF_FLOW_DELEGATION_SETTINGS.consult_message_id
  is '征求意见发送的消息ID';
