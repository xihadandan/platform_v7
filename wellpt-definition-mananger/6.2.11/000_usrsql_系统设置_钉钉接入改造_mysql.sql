-- 组织同步设置，部门同步开关，默认 1 开启
insert into SYS_PARAM_ITEM
values ('183ce481-cb14-451b-8555-874db420c809', str_to_date('2021-08-05 11:48:06','%Y-%m-%d %T'), 'U0000000059', 'U0000000059', str_to_date('2021-08-05 11:48:06','%Y-%m-%d %T'), 0, 'pt.app.dingtalk.org.sync.dept', '1', 'pt.app.dingtalk.org.sync.dept', 2, 'pt.app.dingtalk.org.sync.dept', '009');

-- 组织同步设置，人员头像同步开关，默认 1 开启
insert into SYS_PARAM_ITEM
values ('183ce481-cb14-451b-8555-874db420c810', str_to_date('2021-08-05 11:48:06','%Y-%m-%d %T'), 'U0000000059', 'U0000000059', str_to_date('2021-08-05 11:48:06','%Y-%m-%d %T'), 0, 'pt.app.dingtalk.org.sync.user.photo', '1', 'pt.app.dingtalk.org.sync.user.photo', 2, 'pt.app.dingtalk.org.sync.user.photo', '009');

-- 组织同步设置，人员手机同步开关，默认 1 开启
insert into SYS_PARAM_ITEM
values ('183ce481-cb14-451b-8555-874db420c811', str_to_date('2021-08-05 11:48:06','%Y-%m-%d %T'), 'U0000000059', 'U0000000059', str_to_date('2021-08-05 11:48:06','%Y-%m-%d %T'), 0, 'pt.app.dingtalk.org.sync.user.mobile', '1', 'pt.app.dingtalk.org.sync.user.mobile', 2, 'pt.app.dingtalk.org.sync.user.mobile', '009');

-- 组织同步设置，人员办公电话同步开关，默认 1 开启
insert into SYS_PARAM_ITEM
values ('183ce481-cb14-451b-8555-874db420c812', str_to_date('2021-08-05 11:48:06','%Y-%m-%d %T'), 'U0000000059', 'U0000000059', str_to_date('2021-08-05 11:48:06','%Y-%m-%d %T'), 0, 'pt.app.dingtalk.org.sync.user.telephone', '1', 'pt.app.dingtalk.org.sync.user.telephone', 2, 'pt.app.dingtalk.org.sync.user.telephone', '009');

-- 组织同步设置，人员邮箱同步开关，默认 1 开启
insert into SYS_PARAM_ITEM
values ('183ce481-cb14-451b-8555-874db420c813', str_to_date('2021-08-05 11:48:06','%Y-%m-%d %T'), 'U0000000059', 'U0000000059', str_to_date('2021-08-05 11:48:06','%Y-%m-%d %T'), 0, 'pt.app.dingtalk.org.sync.user.email', '1', 'pt.app.dingtalk.org.sync.user.email', 2, 'pt.app.dingtalk.org.sync.user.email', '009');

-- 组织同步设置，员工编号同步开关，默认 1 开启
insert into SYS_PARAM_ITEM
values ('183ce481-cb14-451b-8555-874db420c814', str_to_date('2021-08-05 11:48:06','%Y-%m-%d %T'), 'U0000000059', 'U0000000059', str_to_date('2021-08-05 11:48:06','%Y-%m-%d %T'), 0, 'pt.app.dingtalk.org.sync.user.jobno', '1', 'pt.app.dingtalk.org.sync.user.jobno', 2, 'pt.app.dingtalk.org.sync.user.jobno', '009');

-- 组织同步设置，人员备注同步开关，默认 1 开启
insert into SYS_PARAM_ITEM
values ('183ce481-cb14-451b-8555-874db420c815', str_to_date('2021-08-05 11:48:06','%Y-%m-%d %T'), 'U0000000059', 'U0000000059', str_to_date('2021-08-05 11:48:06','%Y-%m-%d %T'), 0, 'pt.app.dingtalk.org.sync.user.remark', '1', 'pt.app.dingtalk.org.sync.user.remark', 2, 'pt.app.dingtalk.org.sync.user.remark', '009');

-- 组织同步设置，人员和职位的关系同步开关，默认 1 开启
insert into SYS_PARAM_ITEM
values ('183ce481-cb14-451b-8555-874db420c816', str_to_date('2021-08-05 11:48:06','%Y-%m-%d %T'), 'U0000000059', 'U0000000059', str_to_date('2021-08-05 11:48:06','%Y-%m-%d %T'), 0, 'pt.app.dingtalk.org.sync.workinfo', '1', 'pt.app.dingtalk.org.sync.workinfo', 2, 'pt.app.dingtalk.org.sync.workinfo', '009');


-- Add/modify columns
alter table MULTI_ORG_DING_USER_INFO add OFFICE_PHONE VARCHAR(255);
alter table MULTI_ORG_DING_USER_INFO add EMPLOYEE_NUMBER VARCHAR(255);
alter table MULTI_ORG_DING_USER_INFO add REMARK VARCHAR(500);
-- Add comments to the columns
ALTER table MULTI_ORG_DING_USER_INFO modify column MOBILE varchar(255) COMMENT '手机号';
ALTER table MULTI_ORG_DING_USER_INFO modify column OFFICE_PHONE varchar(255) COMMENT '办公电话';
ALTER table MULTI_ORG_DING_USER_INFO modify column AVATAR varchar(255) COMMENT '头像';
ALTER table MULTI_ORG_DING_USER_INFO modify column NAME varchar(255) COMMENT '姓名';
ALTER table MULTI_ORG_DING_USER_INFO modify column EMAIL varchar(255) COMMENT '邮箱';
ALTER table MULTI_ORG_DING_USER_INFO modify column EMPLOYEE_NUMBER varchar(255) COMMENT '员工编号';
ALTER table MULTI_ORG_DING_USER_INFO modify column REMARK varchar(500) COMMENT '备注';


-- Create table
create table MULTI_ORG_SYNC_LOG
(
  UUID              varchar(255) COMMENT '主键uuid',
  REC_VER           int COMMENT '数据版本',
  SYNC_TIME         timestamp COMMENT '同步时间',
  SYNC_CONTENT      varchar(255) COMMENT '同步内容：部门、人员、人员和职位的关系',
  SYNC_STATUS       int COMMENT '同步状态，1：同步成功 2：同步异常',
  OPERATOR          varchar(255) COMMENT '操作人',
  OPERATOR_NAME     varchar(255) COMMENT '操作人姓名',
  CREATE_TIME    TIMESTAMP NULL,
  CREATOR        VARCHAR(255),
  MODIFIER       VARCHAR(255),
  MODIFY_TIME    TIMESTAMP NULL,
  PRIMARY KEY(UUID)
) COMMENT '组织同步日志表';


-- Create table
create table MULTI_ORG_SYNC_DEPT_LOG
(
  UUID             VARCHAR(255) not null COMMENT '主键uuid',
  REC_VER          int COMMENT '数据版本',
  LOG_ID           VARCHAR(255) COMMENT 'MULTI_ORG_SYNC_LOG表的UUID',
  DEPT_ID          VARCHAR(255) COMMENT '部门ID',
  DEPT_NAME        VARCHAR(255) COMMENT '部门名称',
  OPERATION_NAME   VARCHAR(255) COMMENT '部门操作',
  DEPT_PARENT_ID   VARCHAR(255) COMMENT '上级部门ID',
  DEPT_PARENT_NAME VARCHAR(255) COMMENT '上级部门名称',
  SYNC_STATUS      int COMMENT '同步状态，1：同步成功 2：同步异常',
  REMARK           VARCHAR(255) COMMENT '当同步异常时，填写异常原因',
  CREATE_TIME      TIMESTAMP(6) COMMENT '创建时间',
  CREATOR          VARCHAR(255) COMMENT '创建人',
  MODIFIER         VARCHAR(255) COMMENT '更新人',
  MODIFY_TIME      TIMESTAMP NULL COMMENT '更新时间',
  primary key (UUID)
) COMMENT '组织同步日志部门同步详细日志表';


-- Create table
create table MULTI_ORG_SYNC_USER_LOG
(
  UUID           VARCHAR(255) COMMENT '主键uuid',
  REC_VER        int COMMENT '数据版本',
  LOG_ID         VARCHAR(255) COMMENT 'MULTI_ORG_SYNC_LOG表的UUID',
  USER_ID        VARCHAR(255) COMMENT '人员ID',
  NAME           VARCHAR(255) COMMENT '人员姓名',
  OPERATION_NAME VARCHAR(255) COMMENT '用户操作',
  MOBILE         VARCHAR(255) COMMENT '手机',
  SYNC_STATUS    int COMMENT '同步状态，1：同步成功 2：同步异常',
  REMARK         VARCHAR(255) COMMENT '当同步异常时，填写异常原因',
  CREATE_TIME    TIMESTAMP NULL COMMENT '创建时间',
  CREATOR        VARCHAR(255) COMMENT '创建人',
  MODIFIER       VARCHAR(255) COMMENT '更新人',
  MODIFY_TIME    TIMESTAMP NULL COMMENT '更新时间',
  IS_MULTI_DEPTS int default 0 COMMENT '是否多部门人员，0：否，1：是',
  primary key (UUID)
) COMMENT '组织同步日志人员同步详细日志表';


-- Create table
create table MULTI_ORG_SYNC_USER_WORK_LOG
(
  UUID        VARCHAR(255) COMMENT '主键uuid',
  REC_VER     int COMMENT '数据版本',
  LOG_ID      VARCHAR(255) COMMENT 'MULTI_ORG_SYNC_LOG表的UUID',
  USER_ID     VARCHAR(255) COMMENT '人员ID',
  NAME        VARCHAR(255) COMMENT '人员姓名',
  MOBILE      VARCHAR(255) COMMENT '手机',
  DEPT_ID     VARCHAR(255) COMMENT '部门ID',
  DEPT_NAME   VARCHAR(255) COMMENT '部门名称',
  JOB_ID      VARCHAR(255) COMMENT '职位ID',
  JOB_NAME    VARCHAR(255) COMMENT '职位名称',
  SYNC_STATUS int COMMENT '同步状态，1：同步成功 2：同步异常',
  REMARK      VARCHAR(255) COMMENT '当同步异常时，填写异常原因',
  CREATE_TIME TIMESTAMP NULL COMMENT '创建时间',
  CREATOR     VARCHAR(255) COMMENT '创建人',
  MODIFIER    VARCHAR(255) COMMENT '更新人',
  MODIFY_TIME TIMESTAMP NULL COMMENT '更新时间',
  primary key (UUID)
) comment '组织同步日志人员和职位关系同步详细日志表';


-- Create table
create table MULTI_DEPT_USER_AUDIT
(
  UUID                  VARCHAR(255) not null comment '主键uuid',
  CREATE_TIME           TIMESTAMP NULL comment '创建时间',
  CREATOR               VARCHAR(255) comment '创建人',
  MODIFIER              VARCHAR(255) comment '更新人',
  MODIFY_TIME           TIMESTAMP NULL comment '更新时间',
  REC_VER               int comment '数据版本',
  USER_ID               VARCHAR(255) comment '平台USER_ID',
  DING_USER_ID          VARCHAR(255) comment '钉钉USER_ID',
  UNION_ID              VARCHAR(255) comment '钉钉用户唯一标识',
  NAME                  VARCHAR(255) comment '姓名',
  LOGIN_NAME            VARCHAR(255) comment 'OA账号名',
  EMPLOYEE_NUMBER       VARCHAR(255) comment 'OA员工编号',
  DEPT_IDS              text comment '部门ID，多个用,分隔',
  DEPT_NAMES            text comment '部门名称，多个用,分隔',
  JOB_NAME              text comment '钉钉职位',
  BEFORE_AUDIT_MAIN_JOB text comment '审核前的OA主职位',
  AFTER_AUDIT_MAIN_JOB  text comment '审核后的OA主职位',
  SYNC_TIME             TIMESTAMP NULL comment '同步时间',
  AUDIT_TIME            TIMESTAMP NULL comment '审核时间',
  AUDIT_USER            VARCHAR(255) comment '审核人',
  AUDIT_STATUS          int comment '审核状态1：已审核 0：待审核',
  AUDIT_USER_NAME       VARCHAR(255) comment '审核人姓名',
  SYSTEM_UNIT_ID        VARCHAR(255) comment '系统单位ID',
  primary key (UUID)
) comment '多部门人员审核表';


-- Create table
create table MULTI_DEPT_USER_AUDIT_DETAIL
(
  UUID          VARCHAR(255) not null comment '主键uuid',
  CREATE_TIME   TIMESTAMP(6) NULL comment '创建时间',
  CREATOR       VARCHAR(255) comment '创建人',
  MODIFY_TIME   TIMESTAMP(6) NULL comment '更新时间',
  MODIFIER      VARCHAR(255) comment '更新人',
  REC_VER       int comment '数据版本',
  AUDIT_UUID    VARCHAR(255) comment '多部门人员审核uuid',
  JOB_TYPE      int comment '职位类型，1：钉钉职位；2：OA职位',
  IS_MAIN       int comment '是否主职，1：是；0：否',
  DEPT_ID       VARCHAR(255) comment '部门id',
  DEPT_NAME     VARCHAR(255) comment '部门名称',
  JOB_ID        VARCHAR(255) comment '职位id',
  JOB_NAME      VARCHAR(255) comment '职位名称',
  JOB_ID_PATH   text comment '职位ID路径',
  JOB_NAME_PATH text comment '职位名称路径',
  primary key (UUID)
) comment '多部门人员审核详情表';


-- Add/modify columns
alter table PT_T_EVENT_CALL_BACK add SYNC_CONTENT VARCHAR(255) comment '同步内容：部门、人员、人员和职位的关系';
-- Add/modify columns
alter table PT_T_EVENT_CALL_BACK add IS_MULTI_DEPTS int default 0 comment '是否多部门人员，0：否，1：是';

