
-- Create table
create table CD_SERIAL_NUMBER_RELATION
(
  UUID              VARCHAR2(36) not null,
  CREATE_TIME       TIMESTAMP(6),
  CREATOR           VARCHAR2(255),
  MODIFIER          VARCHAR2(255),
  MODIFY_TIME       TIMESTAMP(6),
  REC_VER           NUMBER(10),
  SYSTEM_UNIT_ID    VARCHAR2(64),

  SN_ID             VARCHAR2(255) NOT NULL,
  OBJECT_TYPE       NUMBER(1) NOT NULL,
  OBJECT_NAME       VARCHAR2(255) NOT NULL,
  FIELD_NAME       VARCHAR2(255) NOT NULL,
  constraint PK_CD_SERIAL_NUMBER_RELATION primary key (UUID)
);
create unique index INDEX_C_S_N_R_SN_ID ON CD_SERIAL_NUMBER_RELATION(SN_ID,OBJECT_TYPE,OBJECT_NAME,FIELD_NAME);

-- Add comments to the table
comment on table CD_SERIAL_NUMBER_RELATION is '流水号关联表字段记录';
-- Add comments to the columns
comment on column CD_SERIAL_NUMBER_RELATION.UUID is '主键uuid';
comment on column CD_SERIAL_NUMBER_RELATION.CREATE_TIME is '创建时间';
comment on column CD_SERIAL_NUMBER_RELATION.CREATOR is '创建人';
comment on column CD_SERIAL_NUMBER_RELATION.MODIFIER is '更新人';
comment on column CD_SERIAL_NUMBER_RELATION.MODIFY_TIME is '更新时间';
comment on column CD_SERIAL_NUMBER_RELATION.REC_VER is '数据版本';
comment on column CD_SERIAL_NUMBER_RELATION.SYSTEM_UNIT_ID is '系统单位ID';

comment on column CD_SERIAL_NUMBER_RELATION.SN_ID is '流水号定义Id';
comment on column CD_SERIAL_NUMBER_RELATION.OBJECT_TYPE is '对象类型：1：数据库表';
comment on column CD_SERIAL_NUMBER_RELATION.OBJECT_NAME is '对象名';
comment on column CD_SERIAL_NUMBER_RELATION.FIELD_NAME is '字段名';


-- Create table
create table CD_SERIAL_NUMBER_RECORD
(
  UUID              VARCHAR2(36) not null,
  CREATE_TIME       TIMESTAMP(6),
  CREATOR           VARCHAR2(255),
  MODIFIER          VARCHAR2(255),
  MODIFY_TIME       TIMESTAMP(6),
  REC_VER           NUMBER(10),
  SYSTEM_UNIT_ID    VARCHAR2(64),

  RELATION_UUID     VARCHAR2(36) NOT NULL,
  DATA_UUID         VARCHAR2(36) NOT NULL,
  KEY_PART          VARCHAR2(255) NOT NULL,
  HEAD_PART         VARCHAR2(255),
  LAST_PART         VARCHAR2(255),
  POINTER           NUMBER(10) NOT NULL,
  SERIAL_NO         VARCHAR2(255) NOT NULL,
  constraint PK_CD_SERIAL_NUMBER_RECORD primary key (UUID)
);
create unique index uq_idx_C_S_N_RE_RELATION_UUID ON CD_SERIAL_NUMBER_RECORD(RELATION_UUID,KEY_PART,POINTER);
create index uq_idx_C_S_N_RE_DATA_UUID ON CD_SERIAL_NUMBER_RECORD(DATA_UUID);
create index uq_idx_C_S_N_RE_SERIAL_NO ON CD_SERIAL_NUMBER_RECORD(SERIAL_NO);

-- Add comments to the table
comment on table CD_SERIAL_NUMBER_RECORD is '流水号记录';
-- Add comments to the columns
comment on column CD_SERIAL_NUMBER_RECORD.UUID is '主键uuid';
comment on column CD_SERIAL_NUMBER_RECORD.CREATE_TIME is '创建时间';
comment on column CD_SERIAL_NUMBER_RECORD.CREATOR is '创建人';
comment on column CD_SERIAL_NUMBER_RECORD.MODIFIER is '更新人';
comment on column CD_SERIAL_NUMBER_RECORD.MODIFY_TIME is '更新时间';
comment on column CD_SERIAL_NUMBER_RECORD.REC_VER is '数据版本';
comment on column CD_SERIAL_NUMBER_RECORD.SYSTEM_UNIT_ID is '系统单位ID';

comment on column CD_SERIAL_NUMBER_RECORD.RELATION_UUID is '流水号关联表字段记录UUID';
comment on column CD_SERIAL_NUMBER_RECORD.DATA_UUID is '使用流水号的数据UUID';
comment on column CD_SERIAL_NUMBER_RECORD.KEY_PART is '关键部分';
comment on column CD_SERIAL_NUMBER_RECORD.HEAD_PART is '头部';
comment on column CD_SERIAL_NUMBER_RECORD.LAST_PART is '尾部';
comment on column CD_SERIAL_NUMBER_RECORD.POINTER is '指针';
comment on column CD_SERIAL_NUMBER_RECORD.SERIAL_NO is '流水号';



-- Create table
create table CD_SERIAL_NUMBER_OLD_DEF
(
  UUID              VARCHAR2(36) not null,
  CREATE_TIME       TIMESTAMP(6),
  CREATOR           VARCHAR2(255),
  MODIFIER          VARCHAR2(255),
  MODIFY_TIME       TIMESTAMP(6),
  REC_VER           NUMBER(10),
  SYSTEM_UNIT_ID    VARCHAR2(64),

  DEFINITION_TYPE   NUMBER(1) NOT NULL,
  DEFINITION_UUID   VARCHAR2(36) NOT NULL,
  DEFINITION_NAME   VARCHAR2(255) NOT NULL,
  TABLE_NAME        VARCHAR2(255),
  DATA_STATE        NUMBER(1) DEFAULT 0 NOT NULL,
  SN_DATA           CLOB,
  constraint PK_C_S_N_O_D primary key (UUID)
);
create unique index INDEX_C_S_N_O_D_DEF_UNIQUE ON CD_SERIAL_NUMBER_OLD_DEF(DEFINITION_TYPE,DEFINITION_UUID);
create index INDEX_C_S_N_O_D_DEF_DATA_STATE ON CD_SERIAL_NUMBER_OLD_DEF(DATA_STATE);


-- Add comments to the table
comment on table CD_SERIAL_NUMBER_OLD_DEF is '流水号定义关联记录处理';
-- Add comments to the columns
comment on column CD_SERIAL_NUMBER_OLD_DEF.UUID is '主键uuid';
comment on column CD_SERIAL_NUMBER_OLD_DEF.CREATE_TIME is '创建时间';
comment on column CD_SERIAL_NUMBER_OLD_DEF.CREATOR is '创建人';
comment on column CD_SERIAL_NUMBER_OLD_DEF.MODIFIER is '更新人';
comment on column CD_SERIAL_NUMBER_OLD_DEF.MODIFY_TIME is '更新时间';
comment on column CD_SERIAL_NUMBER_OLD_DEF.REC_VER is '数据版本';

comment on column CD_SERIAL_NUMBER_OLD_DEF.DEFINITION_TYPE is '定义类型：1，表单，2，流程';
comment on column CD_SERIAL_NUMBER_OLD_DEF.DEFINITION_UUID is '定义uuid';
comment on column CD_SERIAL_NUMBER_OLD_DEF.DEFINITION_NAME is '定义名称';
comment on column CD_SERIAL_NUMBER_OLD_DEF.TABLE_NAME is '数据库表名称';
comment on column CD_SERIAL_NUMBER_OLD_DEF.DATA_STATE is '数据处理状态：0：待处理，1：有流水号字段，2：无流水号字段，3，数据已处理';
comment on column CD_SERIAL_NUMBER_OLD_DEF.SN_DATA is '流水号字段相关信息json';


-- Create table
create table CD_SERIAL_NUMBER_OLD_DATA
(
  UUID              VARCHAR2(36) not null,
  CREATE_TIME       TIMESTAMP(6),
  CREATOR           VARCHAR2(255),
  MODIFIER          VARCHAR2(255),
  MODIFY_TIME       TIMESTAMP(6),
  REC_VER           NUMBER(10),
  SYSTEM_UNIT_ID    VARCHAR2(64),

  SN_TYPE            VARCHAR2(255),
  SN_ID             VARCHAR2(255),
  SERIAL_NO         VARCHAR2(255) NOT NULL,
  OBJECT_NAME       VARCHAR2(255) NOT NULL,
  FIELD_NAME        VARCHAR2(255) NOT NULL,
  DATA_UUID         VARCHAR2(36) NOT NULL,
  DATA_STATE        NUMBER(1) DEFAULT 0 NOT NULL,
  MAINTAIN_UUID     VARCHAR2(36),
  RECORD_DATA_UUID  VARCHAR2(36),
  constraint PK_C_S_N_O_DATA primary key (UUID)
);
create index INDEX_C_S_N_O_D_D_S_ID ON CD_SERIAL_NUMBER_OLD_DATA(SN_ID);
create index INDEX_C_S_N_O_D_D_O_NAME ON CD_SERIAL_NUMBER_OLD_DATA(OBJECT_NAME);
create index INDEX_C_S_N_O_D_D_D_UUID ON CD_SERIAL_NUMBER_OLD_DATA(DATA_UUID);

-- Add comments to the table
comment on table CD_SERIAL_NUMBER_OLD_DATA is '流水号旧数据记录表';
-- Add comments to the columns
comment on column CD_SERIAL_NUMBER_OLD_DATA.UUID is '主键uuid';
comment on column CD_SERIAL_NUMBER_OLD_DATA.CREATE_TIME is '创建时间';
comment on column CD_SERIAL_NUMBER_OLD_DATA.CREATOR is '创建人';
comment on column CD_SERIAL_NUMBER_OLD_DATA.MODIFIER is '更新人';
comment on column CD_SERIAL_NUMBER_OLD_DATA.MODIFY_TIME is '更新时间';
comment on column CD_SERIAL_NUMBER_OLD_DATA.REC_VER is '数据版本';
comment on column CD_SERIAL_NUMBER_OLD_DATA.SYSTEM_UNIT_ID is '系统单位ID';

comment on column CD_SERIAL_NUMBER_OLD_DATA.SN_TYPE is '流水号分类';
comment on column CD_SERIAL_NUMBER_OLD_DATA.SN_ID is '流水号定义ID';
comment on column CD_SERIAL_NUMBER_OLD_DATA.SERIAL_NO is '流水号';
comment on column CD_SERIAL_NUMBER_OLD_DATA.OBJECT_NAME is '表名';
comment on column CD_SERIAL_NUMBER_OLD_DATA.FIELD_NAME is '字段名';
comment on column CD_SERIAL_NUMBER_OLD_DATA.DATA_UUID is '数据UUID';
comment on column CD_SERIAL_NUMBER_OLD_DATA.DATA_STATE is '数据状态：1：已占用，2：匹配有重复（重复记录只记录一条RECORD_DATA_UUID）3：不匹配';
comment on column CD_SERIAL_NUMBER_OLD_DATA.MAINTAIN_UUID is '流水号维护记录uuid';
comment on column CD_SERIAL_NUMBER_OLD_DATA.RECORD_DATA_UUID is '已记录数据UUID';



-- 初始化数据
-- 表单
INSERT INTO CD_SERIAL_NUMBER_OLD_DEF (UUID,CREATE_TIME,CREATOR,REC_VER,DEFINITION_TYPE,DEFINITION_UUID,DEFINITION_NAME,TABLE_NAME,DATA_STATE)
SELECT sys_guid(),SYSDATE,'admin',1,1,UUID,NAME,TABLE_NAME,0 FROM DYFORM_FORM_DEFINITION WHERE FORM_TYPE='P' OR FORM_TYPE='p';
-- 流程
INSERT INTO CD_SERIAL_NUMBER_OLD_DEF (UUID,CREATE_TIME,CREATOR,REC_VER,DEFINITION_TYPE,DEFINITION_UUID,DEFINITION_NAME,TABLE_NAME,DATA_STATE)
SELECT sys_guid(),SYSDATE,'admin',1,2,UUID,NAME,'wf_task_instance',0 FROM WF_FLOW_DEFINITION;

















