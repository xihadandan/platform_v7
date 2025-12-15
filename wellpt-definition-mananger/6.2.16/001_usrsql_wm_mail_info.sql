
-- Create table
create table WM_MAILBOX_INFO
(
  UUID              VARCHAR2(255) not null,
  CREATE_TIME       TIMESTAMP(6),
  CREATOR           VARCHAR2(255),
  MODIFIER          VARCHAR2(255),
  MODIFY_TIME       TIMESTAMP(6),
  REC_VER           NUMBER(10),
  FROM_USER_NAME         VARCHAR2(255),
  FROM_MAIL_ADDRESS    VARCHAR2(255),
  TO_USER_NAME    CLOB,
  TO_MAIL_ADDRESS    CLOB,
  CC_USER_NAME    CLOB,
  CC_MAIL_ADDRESS    CLOB,
  BCC_USER_NAME    CLOB,
  BCC_MAIL_ADDRESS    CLOB,
  ACTUAL_TO_MAIL_ADDRESS CLOB,
  ACTUAL_CC_MAIL_ADDRESS CLOB,
  ACTUAL_BCC_MAIL_ADDRESS CLOB,
  SUBJECT    VARCHAR2(1000),
  CONTENT    CLOB,
  REPO_FILE_NAMES    VARCHAR2(2000),
  REPO_FILE_UUIDS    VARCHAR2(2000),
  MAIL_SIZE    NUMBER(19),
  READ_RECEIPT_STATUS    NUMBER DEFAULT 0 NOT NULL,
  PRIORITY    NUMBER DEFAULT 3 NOT NULL,
  SEND_TIME TIMESTAMP(6),
  constraint PK_WM_MAILBOX_INFO primary key (UUID)
);

-- Add comments to the table
comment on table WM_MAILBOX_INFO is '邮件信息表';
-- Add comments to the columns
comment on column WM_MAILBOX_INFO.UUID is '主键uuid';
comment on column WM_MAILBOX_INFO.CREATE_TIME is '创建时间';
comment on column WM_MAILBOX_INFO.CREATOR is '创建人';
comment on column WM_MAILBOX_INFO.MODIFIER is '更新人';
comment on column WM_MAILBOX_INFO.MODIFY_TIME is '更新时间';
comment on column WM_MAILBOX_INFO.REC_VER is '数据版本';

comment on column WM_MAILBOX_INFO.FROM_USER_NAME is '发送人名称';
comment on column WM_MAILBOX_INFO.FROM_MAIL_ADDRESS is '发送人邮箱地址';
comment on column WM_MAILBOX_INFO.TO_USER_NAME is '接收人名称，多个以分号隔开';
comment on column WM_MAILBOX_INFO.TO_MAIL_ADDRESS is '接收人邮箱地址/部门、职位、用户、群组id';
comment on column WM_MAILBOX_INFO.CC_USER_NAME is '抄送人名称，多个以分号隔开';
comment on column WM_MAILBOX_INFO.CC_MAIL_ADDRESS is '抄送人邮箱地址/部门、职位、用户、群组id';
comment on column WM_MAILBOX_INFO.BCC_USER_NAME is '密送人名称，多个以分号隔开';
comment on column WM_MAILBOX_INFO.BCC_MAIL_ADDRESS is '密送人邮箱地址/部门、职位、用户、群组id';
comment on column WM_MAILBOX_INFO.ACTUAL_TO_MAIL_ADDRESS is '真实接收人';
comment on column WM_MAILBOX_INFO.ACTUAL_CC_MAIL_ADDRESS is '真实抄送人';
comment on column WM_MAILBOX_INFO.ACTUAL_BCC_MAIL_ADDRESS is '真实密送人';

comment on column WM_MAILBOX_INFO.SUBJECT is '主题';
comment on column WM_MAILBOX_INFO.CONTENT is '邮件文本内容';
comment on column WM_MAILBOX_INFO.REPO_FILE_NAMES is 'mogodb附件名称，多个以分隔开';
comment on column WM_MAILBOX_INFO.REPO_FILE_UUIDS is 'mogodb附件UUID，多个以分隔开';
comment on column WM_MAILBOX_INFO.MAIL_SIZE is '邮件大小';
comment on column WM_MAILBOX_INFO.READ_RECEIPT_STATUS is '阅读回执状态（0：不需要 默认值，1:需要，2，已发送回执，3：取消发送回执）';
comment on column WM_MAILBOX_INFO.PRIORITY is '优先级（1：最高，2：高，3: 正常 默认值，4：低，5：最低）';
comment on column WM_MAILBOX_INFO.SEND_TIME is '发送时间';

-- Create table
create table WM_MAILBOX_INFO_USER
(
  UUID              VARCHAR2(255) not null,
  CREATE_TIME       TIMESTAMP(6),
  CREATOR           VARCHAR2(255),
  MODIFIER          VARCHAR2(255),
  MODIFY_TIME       TIMESTAMP(6),
  REC_VER           NUMBER(10),
  USER_ID         VARCHAR2(255),
  USER_NAME     VARCHAR2(255),
  MAIL_ADDRESS    VARCHAR2(255),
  READ_RECEIPT_STATUS    NUMBER DEFAULT 0 NOT NULL,
  SEND_STATUS    NUMBER,
  SEND_COUNT    NUMBER(2,0) DEFAULT 0 NOT NULL,
  FAIL_MSG    VARCHAR2(2000),
  NEXT_SEND_TIME TIMESTAMP(6),
  MID NUMBER,
  MAILBOX_NAME VARCHAR2(255),
  IS_READ NUMBER,
  STATUS NUMBER,
  REVOKE_STATUS NUMBER,
  SYSTEM_UNIT_ID VARCHAR2(64),
  MAIL_INFO_UUID VARCHAR2(255),
  constraint PK_WM_MAILBOX_INFO_USER primary key (UUID)
);
create index INDEX_W_M_I_U_USER_ID ON WM_MAILBOX_INFO_USER(USER_ID);
create index INDEX_W_M_I_U_MID ON WM_MAILBOX_INFO_USER(MID);
create index INDEX_W_M_I_U_MAIL_ADDRESS ON WM_MAILBOX_INFO_USER(MAIL_ADDRESS);
create index INDEX_W_M_I_U_MAILBOX_NAME ON WM_MAILBOX_INFO_USER(MAILBOX_NAME);
create index INDEX_W_M_I_U_MAIL_INFO_UUID ON WM_MAILBOX_INFO_USER(MAIL_INFO_UUID);

-- Add comments to the table
comment on table WM_MAILBOX_INFO_USER is '邮件信息关联用户表';
-- Add comments to the columns
comment on column WM_MAILBOX_INFO_USER.UUID is '主键uuid';
comment on column WM_MAILBOX_INFO_USER.CREATE_TIME is '创建时间';
comment on column WM_MAILBOX_INFO_USER.CREATOR is '创建人';
comment on column WM_MAILBOX_INFO_USER.MODIFIER is '更新人';
comment on column WM_MAILBOX_INFO_USER.MODIFY_TIME is '更新时间';
comment on column WM_MAILBOX_INFO_USER.REC_VER is '数据版本';

comment on column WM_MAILBOX_INFO_USER.USER_ID is '用户Id';
comment on column WM_MAILBOX_INFO_USER.USER_NAME is '用户名称';
comment on column WM_MAILBOX_INFO_USER.MAIL_ADDRESS is '邮件地址';
comment on column WM_MAILBOX_INFO_USER.READ_RECEIPT_STATUS is '阅读回执状态（0：不需要 默认值，1:需要，2，已发送回执，3：取消发送回执）';
comment on column WM_MAILBOX_INFO_USER.SEND_STATUS is '发送状态（0：待发送，1：已发送，2：发送失败）';
comment on column WM_MAILBOX_INFO_USER.SEND_COUNT is '发送次数';
comment on column WM_MAILBOX_INFO_USER.FAIL_MSG is '失败原因';
comment on column WM_MAILBOX_INFO_USER.NEXT_SEND_TIME is '下次执行发送时间';
comment on column WM_MAILBOX_INFO_USER.MID is '邮件UID，从邮件服务器上取到的邮件唯一标识';
comment on column WM_MAILBOX_INFO_USER.MAILBOX_NAME is '邮件文件夹 系统文件夹：（INBOX：收件箱，OUTBOX：发件箱，DRAFT：草稿箱，RECYCLE：回收站 ），其他值代表 其他文件夹';
comment on column WM_MAILBOX_INFO_USER.IS_READ is '是否已读 0=未读 1=已读';
comment on column WM_MAILBOX_INFO_USER.STATUS is '邮件状态 0草稿 1发送成功 2接收成功 3接收失败（空间不足） -1删除 -2彻底删除';
comment on column WM_MAILBOX_INFO_USER.REVOKE_STATUS is '撤回状态：0 撤回失败 1 撤回成功 2 部分撤回成功';
comment on column WM_MAILBOX_INFO_USER.SYSTEM_UNIT_ID is '系统单位Id';
comment on column WM_MAILBOX_INFO_USER.MAIL_INFO_UUID is '邮件信息uuid';



