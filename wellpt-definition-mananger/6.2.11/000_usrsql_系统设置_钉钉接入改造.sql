-- 组织同步设置，部门同步开关，默认 1 开启
insert into SYS_PARAM_ITEM (UUID, CREATE_TIME, CREATOR, MODIFIER, MODIFY_TIME, REC_VER, KEY, VALUE, NAME, SOURCETYPE, CODE, TYPE)
values ('183ce481-cb14-451b-8555-874db420c809', to_timestamp('05-08-2021 11:48:06.332000', 'dd-mm-yyyy hh24:mi:ss.ff'), 'U0000000059', 'U0000000059', to_timestamp('05-08-2021 11:48:06.332000', 'dd-mm-yyyy hh24:mi:ss.ff'), 0, 'pt.app.dingtalk.org.sync.dept', '1', 'pt.app.dingtalk.org.sync.dept', 2, 'pt.app.dingtalk.org.sync.dept', '009');

-- 组织同步设置，人员头像同步开关，默认 1 开启
insert into SYS_PARAM_ITEM (UUID, CREATE_TIME, CREATOR, MODIFIER, MODIFY_TIME, REC_VER, KEY, VALUE, NAME, SOURCETYPE, CODE, TYPE)
values ('183ce481-cb14-451b-8555-874db420c810', to_timestamp('05-08-2021 11:48:06.332000', 'dd-mm-yyyy hh24:mi:ss.ff'), 'U0000000059', 'U0000000059', to_timestamp('05-08-2021 11:48:06.332000', 'dd-mm-yyyy hh24:mi:ss.ff'), 0, 'pt.app.dingtalk.org.sync.user.photo', '1', 'pt.app.dingtalk.org.sync.user.photo', 2, 'pt.app.dingtalk.org.sync.user.photo', '009');

-- 组织同步设置，人员手机同步开关，默认 1 开启
insert into SYS_PARAM_ITEM (UUID, CREATE_TIME, CREATOR, MODIFIER, MODIFY_TIME, REC_VER, KEY, VALUE, NAME, SOURCETYPE, CODE, TYPE)
values ('183ce481-cb14-451b-8555-874db420c811', to_timestamp('05-08-2021 11:48:06.332000', 'dd-mm-yyyy hh24:mi:ss.ff'), 'U0000000059', 'U0000000059', to_timestamp('05-08-2021 11:48:06.332000', 'dd-mm-yyyy hh24:mi:ss.ff'), 0, 'pt.app.dingtalk.org.sync.user.mobile', '1', 'pt.app.dingtalk.org.sync.user.mobile', 2, 'pt.app.dingtalk.org.sync.user.mobile', '009');

-- 组织同步设置，人员办公电话同步开关，默认 1 开启
insert into SYS_PARAM_ITEM (UUID, CREATE_TIME, CREATOR, MODIFIER, MODIFY_TIME, REC_VER, KEY, VALUE, NAME, SOURCETYPE, CODE, TYPE)
values ('183ce481-cb14-451b-8555-874db420c812', to_timestamp('05-08-2021 11:48:06.332000', 'dd-mm-yyyy hh24:mi:ss.ff'), 'U0000000059', 'U0000000059', to_timestamp('05-08-2021 11:48:06.332000', 'dd-mm-yyyy hh24:mi:ss.ff'), 0, 'pt.app.dingtalk.org.sync.user.telephone', '1', 'pt.app.dingtalk.org.sync.user.telephone', 2, 'pt.app.dingtalk.org.sync.user.telephone', '009');

-- 组织同步设置，人员邮箱同步开关，默认 1 开启
insert into SYS_PARAM_ITEM (UUID, CREATE_TIME, CREATOR, MODIFIER, MODIFY_TIME, REC_VER, KEY, VALUE, NAME, SOURCETYPE, CODE, TYPE)
values ('183ce481-cb14-451b-8555-874db420c813', to_timestamp('05-08-2021 11:48:06.332000', 'dd-mm-yyyy hh24:mi:ss.ff'), 'U0000000059', 'U0000000059', to_timestamp('05-08-2021 11:48:06.332000', 'dd-mm-yyyy hh24:mi:ss.ff'), 0, 'pt.app.dingtalk.org.sync.user.email', '1', 'pt.app.dingtalk.org.sync.user.email', 2, 'pt.app.dingtalk.org.sync.user.email', '009');

-- 组织同步设置，员工编号同步开关，默认 1 开启
insert into SYS_PARAM_ITEM (UUID, CREATE_TIME, CREATOR, MODIFIER, MODIFY_TIME, REC_VER, KEY, VALUE, NAME, SOURCETYPE, CODE, TYPE)
values ('183ce481-cb14-451b-8555-874db420c814', to_timestamp('05-08-2021 11:48:06.332000', 'dd-mm-yyyy hh24:mi:ss.ff'), 'U0000000059', 'U0000000059', to_timestamp('05-08-2021 11:48:06.332000', 'dd-mm-yyyy hh24:mi:ss.ff'), 0, 'pt.app.dingtalk.org.sync.user.jobno', '1', 'pt.app.dingtalk.org.sync.user.jobno', 2, 'pt.app.dingtalk.org.sync.user.jobno', '009');

-- 组织同步设置，人员备注同步开关，默认 1 开启
insert into SYS_PARAM_ITEM (UUID, CREATE_TIME, CREATOR, MODIFIER, MODIFY_TIME, REC_VER, KEY, VALUE, NAME, SOURCETYPE, CODE, TYPE)
values ('183ce481-cb14-451b-8555-874db420c815', to_timestamp('05-08-2021 11:48:06.332000', 'dd-mm-yyyy hh24:mi:ss.ff'), 'U0000000059', 'U0000000059', to_timestamp('05-08-2021 11:48:06.332000', 'dd-mm-yyyy hh24:mi:ss.ff'), 0, 'pt.app.dingtalk.org.sync.user.remark', '1', 'pt.app.dingtalk.org.sync.user.remark', 2, 'pt.app.dingtalk.org.sync.user.remark', '009');

-- 组织同步设置，人员和职位的关系同步开关，默认 1 开启
insert into SYS_PARAM_ITEM (UUID, CREATE_TIME, CREATOR, MODIFIER, MODIFY_TIME, REC_VER, KEY, VALUE, NAME, SOURCETYPE, CODE, TYPE)
values ('183ce481-cb14-451b-8555-874db420c816', to_timestamp('05-08-2021 11:48:06.332000', 'dd-mm-yyyy hh24:mi:ss.ff'), 'U0000000059', 'U0000000059', to_timestamp('05-08-2021 11:48:06.332000', 'dd-mm-yyyy hh24:mi:ss.ff'), 0, 'pt.app.dingtalk.org.sync.workinfo', '1', 'pt.app.dingtalk.org.sync.workinfo', 2, 'pt.app.dingtalk.org.sync.workinfo', '009');

commit;


-- Add/modify columns
alter table MULTI_ORG_DING_USER_INFO add OFFICE_PHONE VARCHAR2(255);
alter table MULTI_ORG_DING_USER_INFO add EMPLOYEE_NUMBER VARCHAR2(255);
alter table MULTI_ORG_DING_USER_INFO add REMARK VARCHAR2(500);
-- Add comments to the columns
comment on column MULTI_ORG_DING_USER_INFO.MOBILE
  is '手机号';
comment on column MULTI_ORG_DING_USER_INFO.OFFICE_PHONE
  is '办公电话';
comment on column MULTI_ORG_DING_USER_INFO.AVATAR
  is '头像';
comment on column MULTI_ORG_DING_USER_INFO.NAME
  is '姓名';
comment on column MULTI_ORG_DING_USER_INFO.EMAIL
  is '邮箱';
comment on column MULTI_ORG_DING_USER_INFO.EMPLOYEE_NUMBER
  is '员工编号';
comment on column MULTI_ORG_DING_USER_INFO.REMARK
  is '备注';



-- Create table
create table MULTI_ORG_SYNC_LOG
(
  UUID              varchar2(255),
  REC_VER           NUMBER(10,2),
  SYNC_TIME         timestamp(6),
  SYNC_CONTENT      varchar2(255),
  SYNC_STATUS       number,
  OPERATOR          varchar2(255),
  OPERATOR_NAME     varchar2(255),
  CREATE_TIME    TIMESTAMP(6),
  CREATOR        VARCHAR2(255),
  MODIFIER       VARCHAR2(255),
  MODIFY_TIME    TIMESTAMP(6)
);
-- Add comments to the table
comment on table MULTI_ORG_SYNC_LOG
  is '组织同步日志表';
-- Add comments to the columns
comment on column MULTI_ORG_SYNC_LOG.UUID
  is '主键uuid';
comment on column MULTI_ORG_SYNC_LOG.REC_VER
  is '数据版本';
comment on column MULTI_ORG_SYNC_LOG.SYNC_TIME
  is '同步时间';
comment on column MULTI_ORG_SYNC_LOG.SYNC_CONTENT
  is '同步内容：部门、人员、人员和职位的关系';
comment on column MULTI_ORG_SYNC_LOG.SYNC_STATUS
  is '同步状态，1：同步成功 2：同步异常';
comment on column MULTI_ORG_SYNC_LOG.OPERATOR
  is '操作人';
comment on column MULTI_ORG_SYNC_LOG.OPERATOR_NAME
  is '操作人姓名';
-- Create/Recreate primary, unique and foreign key constraints
alter table MULTI_ORG_SYNC_LOG
  add constraint PK_MULTI_ORG_SYNC_LOG primary key (UUID);


-- Create table
create table MULTI_ORG_SYNC_DEPT_LOG
(
  UUID             VARCHAR2(255) not null,
  REC_VER          NUMBER(10,2),
  LOG_ID           VARCHAR2(255),
  DEPT_ID          VARCHAR2(255),
  DEPT_NAME        VARCHAR2(255),
  OPERATION_NAME   VARCHAR2(255),
  DEPT_PARENT_ID   VARCHAR2(255),
  DEPT_PARENT_NAME VARCHAR2(255),
  SYNC_STATUS      number,
  REMARK           VARCHAR2(255),
  CREATE_TIME      TIMESTAMP(6),
  CREATOR          VARCHAR2(255),
  MODIFIER         VARCHAR2(255),
  MODIFY_TIME      TIMESTAMP(6)
);
-- Add comments to the table
comment on table MULTI_ORG_SYNC_DEPT_LOG
  is '组织同步日志部门同步详细日志表';
-- Add comments to the columns
comment on column MULTI_ORG_SYNC_DEPT_LOG.UUID
  is '主键uuid';
comment on column MULTI_ORG_SYNC_DEPT_LOG.REC_VER
  is '数据版本';
comment on column MULTI_ORG_SYNC_DEPT_LOG.LOG_ID
  is 'MULTI_ORG_SYNC_LOG表的UUID';
comment on column MULTI_ORG_SYNC_DEPT_LOG.DEPT_ID
  is '部门ID';
comment on column MULTI_ORG_SYNC_DEPT_LOG.DEPT_NAME
  is '部门名称';
comment on column MULTI_ORG_SYNC_DEPT_LOG.OPERATION_NAME
  is '部门操作';
comment on column MULTI_ORG_SYNC_DEPT_LOG.DEPT_PARENT_ID
  is '上级部门ID';
comment on column MULTI_ORG_SYNC_DEPT_LOG.DEPT_PARENT_NAME
  is '上级部门名称';
comment on column MULTI_ORG_SYNC_DEPT_LOG.SYNC_STATUS
  is '同步状态，1：同步成功 2：同步异常';
comment on column MULTI_ORG_SYNC_DEPT_LOG.REMARK
  is '当同步异常时，填写异常原因';
comment on column MULTI_ORG_SYNC_DEPT_LOG.CREATE_TIME
  is '创建时间';
comment on column MULTI_ORG_SYNC_DEPT_LOG.CREATOR
  is '创建人';
comment on column MULTI_ORG_SYNC_DEPT_LOG.MODIFIER
  is '更新人';
comment on column MULTI_ORG_SYNC_DEPT_LOG.MODIFY_TIME
  is '更新时间';
-- Create/Recreate primary, unique and foreign key constraints
alter table MULTI_ORG_SYNC_DEPT_LOG
  add constraint PK_MULTI_ORG_SYNC_DEPT_LOG primary key (UUID);


-- Create table
create table MULTI_ORG_SYNC_USER_LOG
(
  UUID           VARCHAR2(255),
  REC_VER        NUMBER(10,2),
  LOG_ID         VARCHAR2(255),
  USER_ID        VARCHAR2(255),
  NAME           VARCHAR2(255),
  OPERATION_NAME VARCHAR2(255),
  MOBILE         VARCHAR2(255),
  SYNC_STATUS    number,
  REMARK         VARCHAR2(255),
  CREATE_TIME    TIMESTAMP(6),
  CREATOR        VARCHAR2(255),
  MODIFIER       VARCHAR2(255),
  MODIFY_TIME    TIMESTAMP(6),
  IS_MULTI_DEPTS NUMBER default 0
);
-- Add comments to the table
comment on table MULTI_ORG_SYNC_USER_LOG
  is '组织同步日志人员同步详细日志表';
-- Add comments to the columns
comment on column MULTI_ORG_SYNC_USER_LOG.UUID
  is '主键uuid';
comment on column MULTI_ORG_SYNC_USER_LOG.REC_VER
  is '数据版本';
comment on column MULTI_ORG_SYNC_USER_LOG.LOG_ID
  is 'MULTI_ORG_SYNC_LOG表的UUID';
comment on column MULTI_ORG_SYNC_USER_LOG.USER_ID
  is '人员ID';
comment on column MULTI_ORG_SYNC_USER_LOG.NAME
  is '人员姓名';
comment on column MULTI_ORG_SYNC_USER_LOG.OPERATION_NAME
  is '用户操作';
comment on column MULTI_ORG_SYNC_USER_LOG.MOBILE
  is '手机';
comment on column MULTI_ORG_SYNC_USER_LOG.SYNC_STATUS
  is '同步状态，1：同步成功 2：同步异常';
comment on column MULTI_ORG_SYNC_USER_LOG.REMARK
  is '当同步异常时，填写异常原因';
comment on column MULTI_ORG_SYNC_USER_LOG.CREATE_TIME
  is '创建时间';
comment on column MULTI_ORG_SYNC_USER_LOG.CREATOR
  is '创建人';
comment on column MULTI_ORG_SYNC_USER_LOG.MODIFIER
  is '更新人';
comment on column MULTI_ORG_SYNC_USER_LOG.MODIFY_TIME
  is '更新时间';
comment on column MULTI_ORG_SYNC_USER_LOG.IS_MULTI_DEPTS
  is '是否多部门人员，0：否，1：是';
-- Create/Recreate primary, unique and foreign key constraints
alter table MULTI_ORG_SYNC_USER_LOG
  add constraint PK_MULTI_ORG_SYNC_USER_LOG primary key (UUID);


-- Create table
create table MULTI_ORG_SYNC_USER_WORK_LOG
(
  UUID        VARCHAR2(255),
  REC_VER     NUMBER(10,2),
  LOG_ID      VARCHAR2(255),
  USER_ID     VARCHAR2(255),
  NAME        VARCHAR2(255),
  MOBILE      VARCHAR2(255),
  DEPT_ID     VARCHAR2(255),
  DEPT_NAME   VARCHAR2(255),
  JOB_ID      VARCHAR2(255),
  JOB_NAME    VARCHAR2(255),
  SYNC_STATUS number,
  REMARK      VARCHAR2(255),
  CREATE_TIME TIMESTAMP(6),
  CREATOR     VARCHAR2(255),
  MODIFIER    VARCHAR2(255),
  MODIFY_TIME TIMESTAMP(6)
);
-- Add comments to the table
comment on table MULTI_ORG_SYNC_USER_WORK_LOG
  is '组织同步日志人员和职位关系同步详细日志表';
-- Add comments to the columns
comment on column MULTI_ORG_SYNC_USER_WORK_LOG.UUID
  is '主键uuid';
comment on column MULTI_ORG_SYNC_USER_WORK_LOG.REC_VER
  is '数据版本';
comment on column MULTI_ORG_SYNC_USER_WORK_LOG.LOG_ID
  is 'MULTI_ORG_SYNC_LOG表的UUID';
comment on column MULTI_ORG_SYNC_USER_WORK_LOG.USER_ID
  is '人员ID';
comment on column MULTI_ORG_SYNC_USER_WORK_LOG.NAME
  is '人员姓名';
comment on column MULTI_ORG_SYNC_USER_WORK_LOG.MOBILE
  is '手机';
comment on column MULTI_ORG_SYNC_USER_WORK_LOG.DEPT_ID
  is '部门ID';
comment on column MULTI_ORG_SYNC_USER_WORK_LOG.DEPT_NAME
  is '部门名称';
comment on column MULTI_ORG_SYNC_USER_WORK_LOG.JOB_ID
  is '职位ID';
comment on column MULTI_ORG_SYNC_USER_WORK_LOG.JOB_NAME
  is '职位名称';
comment on column MULTI_ORG_SYNC_USER_WORK_LOG.SYNC_STATUS
  is '同步状态，1：同步成功 2：同步异常';
comment on column MULTI_ORG_SYNC_USER_WORK_LOG.REMARK
  is '当同步异常时，填写异常原因';
comment on column MULTI_ORG_SYNC_USER_WORK_LOG.CREATE_TIME
  is '创建时间';
comment on column MULTI_ORG_SYNC_USER_WORK_LOG.CREATOR
  is '创建人';
comment on column MULTI_ORG_SYNC_USER_WORK_LOG.MODIFIER
  is '更新人';
comment on column MULTI_ORG_SYNC_USER_WORK_LOG.MODIFY_TIME
  is '更新时间';
-- Create/Recreate primary, unique and foreign key constraints
alter table MULTI_ORG_SYNC_USER_WORK_LOG
  add primary key (UUID);



-- Create table
create table MULTI_DEPT_USER_AUDIT
(
  UUID                  VARCHAR2(255) not null,
  CREATE_TIME           TIMESTAMP(6),
  CREATOR               VARCHAR2(255),
  MODIFIER              VARCHAR2(255),
  MODIFY_TIME           TIMESTAMP(6),
  REC_VER               NUMBER(10,2),
  USER_ID               VARCHAR2(255),
  DING_USER_ID          VARCHAR2(255),
  UNION_ID              VARCHAR2(255),
  NAME                  VARCHAR2(255),
  LOGIN_NAME            VARCHAR2(255),
  EMPLOYEE_NUMBER       VARCHAR2(255),
  DEPT_IDS              VARCHAR2(4000),
  DEPT_NAMES            VARCHAR2(4000),
  JOB_NAME              VARCHAR2(4000),
  BEFORE_AUDIT_MAIN_JOB VARCHAR2(4000),
  AFTER_AUDIT_MAIN_JOB  VARCHAR2(4000),
  SYNC_TIME             TIMESTAMP(6),
  AUDIT_TIME            TIMESTAMP(6),
  AUDIT_USER            VARCHAR2(255),
  AUDIT_STATUS          NUMBER,
  AUDIT_USER_NAME       VARCHAR2(255),
  SYSTEM_UNIT_ID        VARCHAR2(255)
);
-- Add comments to the table
comment on table MULTI_DEPT_USER_AUDIT
  is '多部门人员审核表';
-- Add comments to the columns
comment on column MULTI_DEPT_USER_AUDIT.UUID
  is '主键uuid';
comment on column MULTI_DEPT_USER_AUDIT.CREATE_TIME
  is '创建时间';
comment on column MULTI_DEPT_USER_AUDIT.CREATOR
  is '创建人';
comment on column MULTI_DEPT_USER_AUDIT.MODIFIER
  is '更新人';
comment on column MULTI_DEPT_USER_AUDIT.MODIFY_TIME
  is '更新时间';
comment on column MULTI_DEPT_USER_AUDIT.REC_VER
  is '数据版本';
comment on column MULTI_DEPT_USER_AUDIT.USER_ID
  is '平台USER_ID';
comment on column MULTI_DEPT_USER_AUDIT.DING_USER_ID
  is '钉钉USER_ID';
comment on column MULTI_DEPT_USER_AUDIT.UNION_ID
  is '钉钉用户唯一标识';
comment on column MULTI_DEPT_USER_AUDIT.NAME
  is '姓名';
comment on column MULTI_DEPT_USER_AUDIT.LOGIN_NAME
  is 'OA账号名';
comment on column MULTI_DEPT_USER_AUDIT.EMPLOYEE_NUMBER
  is 'OA员工编号';
comment on column MULTI_DEPT_USER_AUDIT.DEPT_IDS
  is '部门ID，多个用,分隔';
comment on column MULTI_DEPT_USER_AUDIT.DEPT_NAMES
  is '部门名称，多个用,分隔';
comment on column MULTI_DEPT_USER_AUDIT.JOB_NAME
  is '钉钉职位';
comment on column MULTI_DEPT_USER_AUDIT.BEFORE_AUDIT_MAIN_JOB
  is '审核前的OA主职位';
comment on column MULTI_DEPT_USER_AUDIT.AFTER_AUDIT_MAIN_JOB
  is '审核后的OA主职位';
comment on column MULTI_DEPT_USER_AUDIT.SYNC_TIME
  is '同步时间';
comment on column MULTI_DEPT_USER_AUDIT.AUDIT_TIME
  is '审核时间';
comment on column MULTI_DEPT_USER_AUDIT.AUDIT_USER
  is '审核人';
comment on column MULTI_DEPT_USER_AUDIT.AUDIT_STATUS
  is '审核状态1：已审核 0：待审核';
comment on column MULTI_DEPT_USER_AUDIT.AUDIT_USER_NAME
  is '审核人姓名';
comment on column MULTI_DEPT_USER_AUDIT.SYSTEM_UNIT_ID
  is '系统单位ID';
-- Create/Recreate primary, unique and foreign key constraints
alter table MULTI_DEPT_USER_AUDIT
  add constraint PK_MULTI_ORG_DEPTS_USER_AUDIT primary key (UUID);


-- Create table
create table MULTI_DEPT_USER_AUDIT_DETAIL
(
  UUID          VARCHAR2(255) not null,
  CREATE_TIME   TIMESTAMP(6),
  CREATOR       VARCHAR2(255),
  MODIFY_TIME   TIMESTAMP(6),
  MODIFIER      VARCHAR2(255),
  REC_VER       NUMBER(10,2),
  AUDIT_UUID    VARCHAR2(255),
  JOB_TYPE      NUMBER,
  IS_MAIN       NUMBER,
  DEPT_ID       VARCHAR2(255),
  DEPT_NAME     VARCHAR2(255),
  JOB_ID        VARCHAR2(255),
  JOB_NAME      VARCHAR2(255),
  JOB_ID_PATH   VARCHAR2(4000),
  JOB_NAME_PATH VARCHAR2(4000)
);
-- Add comments to the table
comment on table MULTI_DEPT_USER_AUDIT_DETAIL
  is '多部门人员审核详情表';
-- Add comments to the columns
comment on column MULTI_DEPT_USER_AUDIT_DETAIL.UUID
  is '主键uuid';
comment on column MULTI_DEPT_USER_AUDIT_DETAIL.CREATE_TIME
  is '创建时间';
comment on column MULTI_DEPT_USER_AUDIT_DETAIL.CREATOR
  is '创建人';
comment on column MULTI_DEPT_USER_AUDIT_DETAIL.MODIFY_TIME
  is '更新时间';
comment on column MULTI_DEPT_USER_AUDIT_DETAIL.MODIFIER
  is '更新人';
comment on column MULTI_DEPT_USER_AUDIT_DETAIL.REC_VER
  is '数据版本';
comment on column MULTI_DEPT_USER_AUDIT_DETAIL.AUDIT_UUID
  is '多部门人员审核uuid';
comment on column MULTI_DEPT_USER_AUDIT_DETAIL.JOB_TYPE
  is '职位类型，1：钉钉职位；2：OA职位';
comment on column MULTI_DEPT_USER_AUDIT_DETAIL.IS_MAIN
  is '是否主职，1：是；0：否';
comment on column MULTI_DEPT_USER_AUDIT_DETAIL.DEPT_ID
  is '部门id';
comment on column MULTI_DEPT_USER_AUDIT_DETAIL.DEPT_NAME
  is '部门名称';
comment on column MULTI_DEPT_USER_AUDIT_DETAIL.JOB_ID
  is '职位id';
comment on column MULTI_DEPT_USER_AUDIT_DETAIL.JOB_NAME
  is '职位名称';
comment on column MULTI_DEPT_USER_AUDIT_DETAIL.JOB_ID_PATH
  is '职位ID路径';
comment on column MULTI_DEPT_USER_AUDIT_DETAIL.JOB_NAME_PATH
  is '职位名称路径';
-- Create/Recreate primary, unique and foreign key constraints
alter table MULTI_DEPT_USER_AUDIT_DETAIL
  add constraint PK_MULTI_ORG_DEPTS_USER_A primary key (UUID);


-- Add/modify columns
alter table PT_T_EVENT_CALL_BACK add SYNC_CONTENT VARCHAR2(255);
-- Add comments to the columns
comment on column PT_T_EVENT_CALL_BACK.SYNC_CONTENT
  is '同步内容：部门、人员、人员和职位的关系';


-- Add/modify columns
alter table PT_T_EVENT_CALL_BACK add IS_MULTI_DEPTS number default 0;
-- Add comments to the columns
comment on column PT_T_EVENT_CALL_BACK.IS_MULTI_DEPTS
  is '是否多部门人员，0：否，1：是';

