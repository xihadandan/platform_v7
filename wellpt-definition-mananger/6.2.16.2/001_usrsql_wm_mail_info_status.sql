
--  Add/modify columns
ALTER TABLE WM_MAILBOX_INFO ADD IS_PUBLIC_EMAIL number(1);
-- Add comments to the columns
comment on column WM_MAILBOX_INFO.IS_PUBLIC_EMAIL is '是否公网邮箱 0：否，1：是';
--  Add/modify columns
ALTER TABLE WM_MAILBOX_INFO ADD KEEP_ON_SERVER number(1);
-- Add comments to the columns
comment on column WM_MAILBOX_INFO.KEEP_ON_SERVER is '在服务器保留备份 0：否，1：是';
--  Add/modify columns
ALTER TABLE WM_MAILBOX_INFO ADD SEND_RECEIPT number(1);
-- Add comments to the columns
comment on column WM_MAILBOX_INFO.SEND_RECEIPT is '是否自动发送回执 0：否，1：是';


-- Create table
create table WM_MAILBOX_INFO_STATUS
(
  UUID              VARCHAR2(255) not null,
  CREATE_TIME       TIMESTAMP(6),
  CREATOR           VARCHAR2(255),
  MODIFIER          VARCHAR2(255),
  MODIFY_TIME       TIMESTAMP(6),
  REC_VER           NUMBER(10),
  MAIL_INFO_UUID    VARCHAR2(255),
  MAIL_INFO_USER_UUID    VARCHAR2(255),
  RECIPIENT_TYPE    NUMBER(1),
  USER_ID    VARCHAR2(255),
  MAIL_NAME    VARCHAR2(255),
  MAIL_ADDRESS    VARCHAR2(255),
  STATUS NUMBER(1),
  SYSTEM_UNIT_ID VARCHAR2(64),
  constraint PK_WM_MAILBOX_INFO_STATUS primary key (UUID)
);
create index INDEX_W_M_I_S_I_UUID ON WM_MAILBOX_INFO_STATUS(MAIL_INFO_UUID);
create index INDEX_W_M_I_S_I_U_UUID   ON WM_MAILBOX_INFO_STATUS(MAIL_INFO_USER_UUID);

-- Add comments to the table
comment on table WM_MAILBOX_INFO_STATUS is '邮件状态信息表';
-- Add comments to the columns
comment on column WM_MAILBOX_INFO_STATUS.UUID is '主键uuid';
comment on column WM_MAILBOX_INFO_STATUS.CREATE_TIME is '创建时间';
comment on column WM_MAILBOX_INFO_STATUS.CREATOR is '创建人';
comment on column WM_MAILBOX_INFO_STATUS.MODIFIER is '更新人';
comment on column WM_MAILBOX_INFO_STATUS.MODIFY_TIME is '更新时间';

comment on column WM_MAILBOX_INFO_STATUS.MAIL_INFO_UUID is '邮件信息UUID';
comment on column WM_MAILBOX_INFO_STATUS.MAIL_INFO_USER_UUID is '发件人关联信息UUID';
comment on column WM_MAILBOX_INFO_STATUS.RECIPIENT_TYPE is '收件人类型（1，收件，2：抄送，3：密送）';
comment on column WM_MAILBOX_INFO_STATUS.USER_ID is '用户Id';
comment on column WM_MAILBOX_INFO_STATUS.MAIL_NAME is '邮件用户名';
comment on column WM_MAILBOX_INFO_STATUS.MAIL_ADDRESS is '邮件地址';
comment on column WM_MAILBOX_INFO_STATUS.STATUS is '状态（1：已发送，2：已投递到邮箱服务，3：地址不存在，4：未开启公网邮箱，5：无效邮件地址，6：邮件服务异常）';
comment on column WM_MAILBOX_INFO_STATUS.SYSTEM_UNIT_ID is '系统单位Id';