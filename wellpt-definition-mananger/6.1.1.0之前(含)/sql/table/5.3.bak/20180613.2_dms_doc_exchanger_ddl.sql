
create table DMS_DOC_EXCHANGE_LOG
(
  uuid                     VARCHAR2(64) not null,
  create_time              TIMESTAMP(6) not null,
  creator                  VARCHAR2(64) not null,
  modify_time              TIMESTAMP(6),
  modifier                 VARCHAR2(64),
  rec_ver                  NUMBER(10),
  operation_name           VARCHAR2(64) not null,
  operator                 VARCHAR2(64) not null,
  content                  VARCHAR2(1500),
  file_uuids               VARCHAR2(1200),
  doc_exchange_record_uuid VARCHAR2(64) not null,
  target                   CLOB,
  system_unit_id           VARCHAR2(64) not null,
  file_names               VARCHAR2(1200)
)
;
comment on table DMS_DOC_EXCHANGE_LOG
  is '文档交换操作日志';
comment on column DMS_DOC_EXCHANGE_LOG.operation_name
  is '操作名称';
comment on column DMS_DOC_EXCHANGE_LOG.operator
  is '操作人员';
comment on column DMS_DOC_EXCHANGE_LOG.content
  is '内容';
comment on column DMS_DOC_EXCHANGE_LOG.file_uuids
  is '附件';
comment on column DMS_DOC_EXCHANGE_LOG.target
  is '操作对象';
alter table DMS_DOC_EXCHANGE_LOG
  add primary key (UUID);


create table DMS_DOC_EXCHANGE_RECORD
(
  uuid                    VARCHAR2(64) not null,
  create_time             TIMESTAMP(6) not null,
  creator                 VARCHAR2(64) not null,
  modify_time             TIMESTAMP(6),
  modifier                VARCHAR2(64),
  rec_ver                 NUMBER(10),
  user_id                 VARCHAR2(64),
  exchange_type           NUMBER(1) default 0 not null,
  system_unit_id          VARCHAR2(64) not null,
  dyform_uuid             VARCHAR2(64),
  data_uuid               VARCHAR2(64),
  feedback_time_limit     TIMESTAMP(6),
  sign_time_limit         TIMESTAMP(6),
  doc_encryption_level    NUMBER(1),
  doc_urge_level          NUMBER(1),
  is_sms_notify           NUMBER(1) default 0,
  is_im_notify            NUMBER(1) default 0,
  is_mail_notify          NUMBER(1) default 0,
  record_status           NUMBER(1) default 0 not null,
  from_record_detail_uuid VARCHAR2(64),
  to_user_ids             CLOB,
  to_user_names           CLOB,
  is_need_sign            NUMBER(1) default 0,
  is_need_feedback        NUMBER(1) default 0,
  file_uuids              VARCHAR2(1200),
  file_names              VARCHAR2(1200),
  from_user_id            VARCHAR2(64),
  flow_uuid               VARCHAR2(64),
  doc_title               VARCHAR2(450),
  configuration_json      CLOB
)
;
comment on table DMS_DOC_EXCHANGE_RECORD
  is '文档交换记录';
comment on column DMS_DOC_EXCHANGE_RECORD.exchange_type
  is '文档交换类型：0 动态表单   1 文件';
comment on column DMS_DOC_EXCHANGE_RECORD.dyform_uuid
  is '表单定义UUID';
comment on column DMS_DOC_EXCHANGE_RECORD.data_uuid
  is '表单保存数据的UUID';
comment on column DMS_DOC_EXCHANGE_RECORD.feedback_time_limit
  is '反馈时限';
comment on column DMS_DOC_EXCHANGE_RECORD.sign_time_limit
  is '签收时限';
comment on column DMS_DOC_EXCHANGE_RECORD.doc_encryption_level
  is '文档密级';
comment on column DMS_DOC_EXCHANGE_RECORD.doc_urge_level
  is '文档缓急程度';
comment on column DMS_DOC_EXCHANGE_RECORD.is_sms_notify
  is '是否短信通知';
comment on column DMS_DOC_EXCHANGE_RECORD.is_im_notify
  is '是否消息通知';
comment on column DMS_DOC_EXCHANGE_RECORD.is_mail_notify
  is '是否邮件通知';
comment on column DMS_DOC_EXCHANGE_RECORD.record_status
  is '文档交换记录状态位：
发件方状态：0 草稿  1 已发送 2 已办结 8 待审批 9 审批拒绝
收件方状态：3 已退回  4 已签收 5 已反馈 6 待签收 7 待反馈';
comment on column DMS_DOC_EXCHANGE_RECORD.from_record_detail_uuid
  is '来源文档交换明细UUID';
comment on column DMS_DOC_EXCHANGE_RECORD.to_user_ids
  is '接收用户ID';
comment on column DMS_DOC_EXCHANGE_RECORD.to_user_names
  is '接收用户名称';
comment on column DMS_DOC_EXCHANGE_RECORD.is_need_sign
  is '是否需要签收';
comment on column DMS_DOC_EXCHANGE_RECORD.is_need_feedback
  is '是否需要反馈';
comment on column DMS_DOC_EXCHANGE_RECORD.file_uuids
  is '文档交换类型为文件时候，存储的是文件上传后的fileId';
comment on column DMS_DOC_EXCHANGE_RECORD.flow_uuid
  is '流程定义UUID';
comment on column DMS_DOC_EXCHANGE_RECORD.doc_title
  is '文档标题';
create index SYS_IDX_C00195210 on DMS_DOC_EXCHANGE_RECORD (EXCHANGE_TYPE, DATA_UUID);
alter table DMS_DOC_EXCHANGE_RECORD
  add primary key (UUID);


create table DMS_DOC_EXCHANGE_RECORD_DETAIL
(
  uuid                     VARCHAR2(64) not null,
  create_time              TIMESTAMP(6) not null,
  creator                  VARCHAR2(64) not null,
  modify_time              TIMESTAMP(6),
  modifier                 VARCHAR2(64),
  rec_ver                  NUMBER(10),
  to_user_id               VARCHAR2(64) not null,
  doc_exchange_record_uuid VARCHAR2(64) not null,
  system_unit_id           VARCHAR2(64) not null,
  sign_status              NUMBER(1) default 6 not null,
  is_feedback              NUMBER(1) default 0,
  is_revoked               NUMBER(1) default 0,
  revoke_reason            VARCHAR2(512),
  type                     NUMBER(1) default 0,
  sign_time                TIMESTAMP(6),
  extra_send_uuid          VARCHAR2(64),
  return_reason            VARCHAR2(450)
)
;
comment on table DMS_DOC_EXCHANGE_RECORD_DETAIL
  is '文档交换接收人员明细';
comment on column DMS_DOC_EXCHANGE_RECORD_DETAIL.to_user_id
  is '接收用户ID';
comment on column DMS_DOC_EXCHANGE_RECORD_DETAIL.doc_exchange_record_uuid
  is '文档交换记录UUID';
comment on column DMS_DOC_EXCHANGE_RECORD_DETAIL.sign_status
  is '6 待签收 4 已签收 3 已退回';
comment on column DMS_DOC_EXCHANGE_RECORD_DETAIL.is_feedback
  is '是否已经反馈';
comment on column DMS_DOC_EXCHANGE_RECORD_DETAIL.is_revoked
  is '是否已经撤回';
comment on column DMS_DOC_EXCHANGE_RECORD_DETAIL.revoke_reason
  is '撤回理由';
comment on column DMS_DOC_EXCHANGE_RECORD_DETAIL.type
  is '0 正常发送的人员  1 补充发送的人员 2 转发';
comment on column DMS_DOC_EXCHANGE_RECORD_DETAIL.sign_time
  is '签收或退回时间';
comment on column DMS_DOC_EXCHANGE_RECORD_DETAIL.extra_send_uuid
  is '补发的UUID';
comment on column DMS_DOC_EXCHANGE_RECORD_DETAIL.return_reason
  is '退回理由';
create index SYS_IDX_C00195218 on DMS_DOC_EXCHANGE_RECORD_DETAIL (DOC_EXCHANGE_RECORD_UUID);
alter table DMS_DOC_EXCHANGE_RECORD_DETAIL
  add primary key (UUID);


create table DMS_DOC_EXC_CONTACT_BOOK
(
  uuid              VARCHAR2(64) not null,
  create_time       TIMESTAMP(6) not null,
  creator           VARCHAR2(64) not null,
  modify_time       TIMESTAMP(6),
  modifier          VARCHAR2(64),
  rec_ver           NUMBER(10),
  system_unit_id    VARCHAR2(64) not null,
  contact_name      VARCHAR2(64) not null,
  personal_email    VARCHAR2(64),
  cellphone_number  VARCHAR2(32),
  remark            VARCHAR2(450),
  contact_id        VARCHAR2(32) not null,
  contact_unit_uuid VARCHAR2(64),
  rela_user_id      VARCHAR2(64)
)
;
comment on table DMS_DOC_EXC_CONTACT_BOOK
  is '文档交换通讯录';
comment on column DMS_DOC_EXC_CONTACT_BOOK.system_unit_id
  is '系统组织ID';
comment on column DMS_DOC_EXC_CONTACT_BOOK.contact_name
  is '联系人名称';
comment on column DMS_DOC_EXC_CONTACT_BOOK.personal_email
  is '个人邮件';
comment on column DMS_DOC_EXC_CONTACT_BOOK.cellphone_number
  is '手机号码';
comment on column DMS_DOC_EXC_CONTACT_BOOK.remark
  is '备注';
comment on column DMS_DOC_EXC_CONTACT_BOOK.contact_id
  is '联系人ID';
comment on column DMS_DOC_EXC_CONTACT_BOOK.contact_unit_uuid
  is '联系人单位UUID';
comment on column DMS_DOC_EXC_CONTACT_BOOK.rela_user_id
  is '关联系统用户ID';
alter table DMS_DOC_EXC_CONTACT_BOOK
  add primary key (UUID);
alter table DMS_DOC_EXC_CONTACT_BOOK
  add constraint SYSUNQ_C00196463 unique (CONTACT_ID, SYSTEM_UNIT_ID);


create table DMS_DOC_EXC_CONTACT_BOOK_UNIT
(
  uuid           VARCHAR2(64) not null,
  create_time    TIMESTAMP(6) not null,
  creator        VARCHAR2(64) not null,
  modify_time    TIMESTAMP(6),
  modifier       VARCHAR2(64),
  rec_ver        NUMBER(10),
  system_unit_id VARCHAR2(64) not null,
  unit_name      VARCHAR2(450) not null,
  unit_code      VARCHAR2(64),
  unit_id        VARCHAR2(64)
)
;
comment on table DMS_DOC_EXC_CONTACT_BOOK_UNIT
  is '文档交换通讯录单位';
comment on column DMS_DOC_EXC_CONTACT_BOOK_UNIT.unit_name
  is '单位名称';
comment on column DMS_DOC_EXC_CONTACT_BOOK_UNIT.unit_code
  is '单位编码';
comment on column DMS_DOC_EXC_CONTACT_BOOK_UNIT.unit_id
  is '单位ID';
alter table DMS_DOC_EXC_CONTACT_BOOK_UNIT
  add primary key (UUID);
alter table DMS_DOC_EXC_CONTACT_BOOK_UNIT
  add constraint SYSUQ_C00196530 unique (SYSTEM_UNIT_ID, UNIT_ID);


create table DMS_DOC_EXC_EXTRA_SEND_DETAIL
(
  uuid                     VARCHAR2(64) not null,
  create_time              TIMESTAMP(6) not null,
  creator                  VARCHAR2(64) not null,
  modify_time              TIMESTAMP(6),
  modifier                 VARCHAR2(64),
  rec_ver                  NUMBER(10),
  to_user_ids              CLOB not null,
  to_user_names            CLOB not null,
  doc_exchange_record_uuid VARCHAR2(64) not null,
  system_unit_id           VARCHAR2(64) not null,
  is_im_notify             NUMBER(1) default 0,
  is_sms_notify            NUMBER(1) default 0,
  is_mail_notify           NUMBER(1) default 0,
  sign_time_limit          TIMESTAMP(6),
  feedback_time_limit      TIMESTAMP(6)
)
;
comment on column DMS_DOC_EXC_EXTRA_SEND_DETAIL.to_user_ids
  is '补充发送接收者ID';
comment on column DMS_DOC_EXC_EXTRA_SEND_DETAIL.to_user_names
  is '补充发送接收者名称';
comment on column DMS_DOC_EXC_EXTRA_SEND_DETAIL.doc_exchange_record_uuid
  is '文档交换记录UUID';
comment on column DMS_DOC_EXC_EXTRA_SEND_DETAIL.is_im_notify
  is '是否消息发送';
comment on column DMS_DOC_EXC_EXTRA_SEND_DETAIL.is_sms_notify
  is '是否短信发送';
comment on column DMS_DOC_EXC_EXTRA_SEND_DETAIL.is_mail_notify
  is '是否邮件发送';
comment on column DMS_DOC_EXC_EXTRA_SEND_DETAIL.sign_time_limit
  is '签收时限';
comment on column DMS_DOC_EXC_EXTRA_SEND_DETAIL.feedback_time_limit
  is '反馈时限';
alter table DMS_DOC_EXC_EXTRA_SEND_DETAIL
  add primary key (UUID);


create table DMS_DOC_EXC_FEEDBACK_DETAIL
(
  uuid                      VARCHAR2(64) not null,
  create_time               TIMESTAMP(6) not null,
  creator                   VARCHAR2(64) not null,
  modify_time               TIMESTAMP(6),
  modifier                  VARCHAR2(64),
  rec_ver                   NUMBER(10),
  to_user_id                VARCHAR2(64) not null,
  from_user_id              VARCHAR2(64) not null,
  doc_exchange_record_uuid  VARCHAR2(64) not null,
  to_feedback_detail_uuid   VARCHAR2(64),
  system_unit_id            VARCHAR2(64) not null,
  content                   VARCHAR2(1500) not null,
  feedback_time             TIMESTAMP(6) not null,
  file_uuids                VARCHAR2(450),
  file_names                VARCHAR2(450),
  feedback_type             NUMBER(1) default 0,
  from_feedback_detail_uuid VARCHAR2(64)
)
;
comment on table DMS_DOC_EXC_FEEDBACK_DETAIL
  is '文档交换反馈详情';
comment on column DMS_DOC_EXC_FEEDBACK_DETAIL.to_user_id
  is '反馈目标用户';
comment on column DMS_DOC_EXC_FEEDBACK_DETAIL.from_user_id
  is '反馈来源用户';
comment on column DMS_DOC_EXC_FEEDBACK_DETAIL.doc_exchange_record_uuid
  is '文档交换记录UUID';
comment on column DMS_DOC_EXC_FEEDBACK_DETAIL.to_feedback_detail_uuid
  is '回执的反馈详情UUID';
comment on column DMS_DOC_EXC_FEEDBACK_DETAIL.content
  is '反馈内容';
comment on column DMS_DOC_EXC_FEEDBACK_DETAIL.feedback_time
  is '反馈时间';
comment on column DMS_DOC_EXC_FEEDBACK_DETAIL.file_uuids
  is '反馈附件UUID，如果多个文件已/符号分割';
comment on column DMS_DOC_EXC_FEEDBACK_DETAIL.feedback_type
  is '0 收文人反馈  1 发文人回执 2 发文人要求再次反馈';
comment on column DMS_DOC_EXC_FEEDBACK_DETAIL.from_feedback_detail_uuid
  is '来自反馈详情UUID';
alter table DMS_DOC_EXC_FEEDBACK_DETAIL
  add primary key (UUID);


create table DMS_DOC_EXC_RECORD_FORWARD
(
  uuid                     VARCHAR2(64) not null,
  create_time              TIMESTAMP(6) not null,
  creator                  VARCHAR2(64) not null,
  modify_time              TIMESTAMP(6),
  modifier                 VARCHAR2(64),
  rec_ver                  NUMBER(10),
  to_user_ids              CLOB not null,
  to_user_names            CLOB not null,
  doc_exchange_record_uuid VARCHAR2(64) not null,
  system_unit_id           VARCHAR2(64) not null,
  is_im_notify             NUMBER(1) default 0,
  is_sms_notify            NUMBER(1) default 0,
  is_mail_notify           NUMBER(1) default 0,
  sign_time_limit          TIMESTAMP(6),
  feedback_time_limit      TIMESTAMP(6),
  file_uuids               VARCHAR2(1200),
  remark                   VARCHAR2(450),
  forward_status           NUMBER(1) default 1 not null,
  file_names               VARCHAR2(1200),
  from_user_id             VARCHAR2(64)
)
;
comment on table DMS_DOC_EXC_RECORD_FORWARD
  is '转发详情';
comment on column DMS_DOC_EXC_RECORD_FORWARD.forward_status
  is '转发状态：1 已发送 4 转发已签收';
alter table DMS_DOC_EXC_RECORD_FORWARD
  add primary key (UUID);


create table DMS_DOC_EXC_SIGN_DETAIL
(
  uuid                     VARCHAR2(64) not null,
  create_time              TIMESTAMP(6) not null,
  creator                  VARCHAR2(64) not null,
  modify_time              TIMESTAMP(6),
  modifier                 VARCHAR2(64),
  rec_ver                  NUMBER(10),
  doc_exchange_record_uuid VARCHAR2(64) not null,
  operate_user_id          VARCHAR2(64) not null,
  operate_time             TIMESTAMP(6) not null,
  system_unit_id           VARCHAR2(64) not null,
  return_reason            VARCHAR2(1500) not null,
  sign_status              NUMBER(1) not null
)
;
comment on table DMS_DOC_EXC_SIGN_DETAIL
  is '文档交换签收详情';
comment on column DMS_DOC_EXC_SIGN_DETAIL.operate_user_id
  is '操作用户ID';
comment on column DMS_DOC_EXC_SIGN_DETAIL.operate_time
  is '操作时间';
comment on column DMS_DOC_EXC_SIGN_DETAIL.return_reason
  is '退回原因';
comment on column DMS_DOC_EXC_SIGN_DETAIL.sign_status
  is '签收类型：0 已签收 1 已退回';
alter table DMS_DOC_EXC_SIGN_DETAIL
  add primary key (UUID);


create table DMS_DOC_EXC_URGE_DETAIL
(
  uuid                     VARCHAR2(64) not null,
  create_time              TIMESTAMP(6) not null,
  creator                  VARCHAR2(64) not null,
  modify_time              TIMESTAMP(6),
  modifier                 VARCHAR2(64),
  rec_ver                  NUMBER(10),
  to_user_id               VARCHAR2(64) not null,
  urge_way                 VARCHAR2(64) not null,
  system_unit_id           VARCHAR2(64) not null,
  content                  VARCHAR2(1500) not null,
  doc_exchange_record_uuid VARCHAR2(64) not null
)
;
comment on table DMS_DOC_EXC_URGE_DETAIL
  is '文档交换催办详情';
comment on column DMS_DOC_EXC_URGE_DETAIL.to_user_id
  is '催办目标用户ID';
comment on column DMS_DOC_EXC_URGE_DETAIL.urge_way
  is '催办方式';
comment on column DMS_DOC_EXC_URGE_DETAIL.content
  is '催办意见';
alter table DMS_DOC_EXC_URGE_DETAIL
  add primary key (UUID);


  
-- Add/modify columns 
alter table DMS_DOC_EXC_CONTACT_BOOK_UNIT modify unit_name VARCHAR2(64 char);
alter table DMS_DOC_EXC_CONTACT_BOOK_UNIT add full_unit_name VARCHAR2(120 char);
-- Add comments to the columns 
comment on column DMS_DOC_EXC_CONTACT_BOOK_UNIT.unit_name
  is '单位简称';
comment on column DMS_DOC_EXC_CONTACT_BOOK_UNIT.full_unit_name
  is '单位全称';

-- Add/modify columns 
alter table DMS_DOC_EXC_CONTACT_BOOK_UNIT add module_id VARCHAR2(120);
-- Add comments to the columns 
comment on column DMS_DOC_EXC_CONTACT_BOOK_UNIT.module_id
  is '业务模块ID';
-- Add/modify columns 
alter table DMS_DOC_EXC_CONTACT_BOOK add module_id varchar2(120);
-- Add comments to the columns 
comment on column DMS_DOC_EXC_CONTACT_BOOK.module_id
  is '业务模块ID';
-- Add/modify columns 
alter table DMS_DOC_EXCHANGE_RECORD_DETAIL add sign_user_id varchar2(32);
-- Add comments to the columns 
comment on column DMS_DOC_EXCHANGE_RECORD_DETAIL.sign_user_id
  is '签收人员';

-- Add/modify columns
alter table DMS_DOC_EXCHANGE_RECORD add overtime_level NUMBER(1) default 0;
-- Add comments to the columns
comment on column DMS_DOC_EXCHANGE_RECORD.overtime_level
  is '紧要性，根据反馈/签收时限与工作日的差距判断，0 无 1 一般 2 重要 3 紧急';
