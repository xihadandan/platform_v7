-- 文档交换-配置
create table dms_doc_exchange_config
(
   UUID                                     VARCHAR2(64)   not null,
   CREATE_TIME                              TIMESTAMP,
   CREATOR                                  VARCHAR2(64),
   MODIFIER                                 VARCHAR2(64),
   MODIFY_TIME                              TIMESTAMP,
   REC_VER                                  NUMBER(10),
   SYSTEM_UNIT_ID                           VARCHAR2(64),
   sequence                                 NUMBER(6),
   name                                     VARCHAR2(255),
   descriptor                               VARCHAR2(255),
   exchange_type                            NUMBER(1) DEFAULT 0 NOT NULL,
   dyform_uuid                              VARCHAR2(64),
   dyform_name                              VARCHAR2(64),
   doc_encryption_level                     NUMBER(1),
   doc_urge_level                           NUMBER(1),
   business_category_uuid                   VARCHAR2(64),
   send_role_uuid                           VARCHAR2(64),
   recipient_role_uuid                      VARCHAR2(64),
   approve                                  NUMBER(1),
   flow_uuid                                VARCHAR2(64),
   is_need_sign                             NUMBER(1),
   doc_sign                                 NUMBER(1),
   default_sign                             NUMBER(1),
   sign_time_limit                          NUMBER(1),
   sign_event                               VARCHAR2(2000),
   is_need_feedback                         NUMBER(1),
   doc_feedback                             NUMBER(1),
   default_feedback                         NUMBER(1),
   feedback_time_limit                      NUMBER(1),
   feedback_event                           VARCHAR2(2000),
   auto_finish                              NUMBER(1),
   is_forward                               NUMBER(1),
   process_view                             NUMBER(1),
   refuse_to_view                           NUMBER(1),
   dms_doc_exchange_dyform_uuid             VARCHAR2(64),
   notify_types                            VARCHAR2(255),
   default_notify_types                    VARCHAR2(255),
   notify_msg_uuid                          VARCHAR2(64),
   sign_before_num                          NUMBER(8,2),
   sign_before_unit                         NUMBER(6),
   sign_before_msg_uuid                     VARCHAR2(64),
   sign_after_num                           NUMBER(8,2),
   sign_after_unit                          NUMBER(6),
   sign_after_frequency                     NUMBER(6),
   sign_after_msg_uuid                      VARCHAR2(64),
   feedback_before_num                      NUMBER(8,2),
   feedback_before_unit                     NUMBER(6),
   feedback_before_msg_uuid                 VARCHAR2(64),
   feedback_after_num                       NUMBER(8,2),
   feedback_after_unit                      NUMBER(6),
   feedback_after_frequency                 NUMBER(6),
   feedback_after_msg_uuid                  VARCHAR2(64),
   constraint pk_dms_doc_exchange_config primary key (UUID)
);
comment on table  dms_doc_exchange_config is '文档交换-配置';
comment on column dms_doc_exchange_config.UUID is 'UUID';
comment on column dms_doc_exchange_config.CREATE_TIME is '创建时间';
comment on column dms_doc_exchange_config.CREATOR is '创建人';
comment on column dms_doc_exchange_config.MODIFIER is '修改人';
comment on column dms_doc_exchange_config.MODIFY_TIME is '修改时间';
comment on column dms_doc_exchange_config.REC_VER is '版本号';
comment on column dms_doc_exchange_config.SYSTEM_UNIT_ID is '系统单位ID';

comment on column dms_doc_exchange_config.sequence is '排序';
comment on column dms_doc_exchange_config.name is '业务名称';
comment on column dms_doc_exchange_config.descriptor is '业务描述';

comment on column dms_doc_exchange_config.exchange_type is '文档交换类型：0 动态表单   1 文件';
comment on column dms_doc_exchange_config.dyform_uuid is '动态表单uuid';
comment on column dms_doc_exchange_config.dyform_name is '动态表单名称';
comment on column dms_doc_exchange_config.doc_encryption_level is '文档密级';
comment on column dms_doc_exchange_config.doc_urge_level is '文档缓急程度';

comment on column dms_doc_exchange_config.business_category_uuid is '业务类别uuid';
comment on column dms_doc_exchange_config.send_role_uuid is '发件角色uuid(取业务类别定义的角色)';
comment on column dms_doc_exchange_config.recipient_role_uuid is '收件角色uuid(取业务类别定义的角色)';

comment on column dms_doc_exchange_config.approve is '是否发件审批';
comment on column dms_doc_exchange_config.flow_uuid is '发件审批流程uuid';

comment on column dms_doc_exchange_config.is_need_sign is '启用签收功能';
comment on column dms_doc_exchange_config.doc_sign is '按文档设置签收';
comment on column dms_doc_exchange_config.default_sign is '默认需要签收';
comment on column dms_doc_exchange_config.sign_time_limit is '启用签收时限';
comment on column dms_doc_exchange_config.sign_event is '签收事件监听';

comment on column dms_doc_exchange_config.is_need_feedback is '启用反馈功能';
comment on column dms_doc_exchange_config.doc_feedback is '按文档设置反馈';
comment on column dms_doc_exchange_config.default_feedback is '默认需要反馈';
comment on column dms_doc_exchange_config.feedback_time_limit is '启用反馈时限';
comment on column dms_doc_exchange_config.feedback_event is '反馈事件监听';

comment on column dms_doc_exchange_config.auto_finish is '自动办结设置（发件被全部签收或反馈完成后自动办结）';
comment on column dms_doc_exchange_config.is_forward is '转发设置（收件单位可转发收件）';

comment on column dms_doc_exchange_config.process_view is '办理过程查看设置（发件单位查看收件单位办理过程相关文档）';
comment on column dms_doc_exchange_config.refuse_to_view is '收件单位可拒绝查看';

comment on column dms_doc_exchange_config.dms_doc_exchange_dyform_uuid is '编辑文档展示单据UUID';

comment on column dms_doc_exchange_config.notify_types is '发件可选提醒方式 在线消息，短信，邮件';
comment on column dms_doc_exchange_config.default_notify_types is '默认提醒方式  在线消息，短信，邮件';
comment on column dms_doc_exchange_config.notify_msg_uuid is '发件消息格式UUID';

comment on column dms_doc_exchange_config.sign_before_num is '签收提醒 到期前 提前 数量';
comment on column dms_doc_exchange_config.sign_before_unit is '签收提醒 到期前 提前 单位 （工作日，时分秒等）';
comment on column dms_doc_exchange_config.sign_before_msg_uuid is '签收提醒 到期前 消息格式UUID';
comment on column dms_doc_exchange_config.sign_after_num is '签收提醒 逾期后 每隔 数量';
comment on column dms_doc_exchange_config.sign_after_unit is '签收提醒 逾期后 每隔 单位 （工作日，时分秒等）';
comment on column dms_doc_exchange_config.sign_after_frequency is '签收提醒 逾期后 提醒次数';
comment on column dms_doc_exchange_config.sign_after_msg_uuid is '签收提醒 逾期后 消息格式UUID';

comment on column dms_doc_exchange_config.feedback_before_num is '反馈提醒 到期前 提前 数量';
comment on column dms_doc_exchange_config.feedback_before_unit is '反馈提醒 到期前 提前 单位 （工作日，时分秒等）';
comment on column dms_doc_exchange_config.feedback_before_msg_uuid is '反馈提醒 到期前 消息格式UUID';
comment on column dms_doc_exchange_config.feedback_after_num is '反馈提醒 逾期后 每隔 数量';
comment on column dms_doc_exchange_config.feedback_after_unit is '反馈提醒 逾期后 每隔 单位 （工作日，时分秒等）';
comment on column dms_doc_exchange_config.feedback_after_frequency is '反馈提醒 逾期后 提醒次数';
comment on column dms_doc_exchange_config.feedback_after_msg_uuid is '反馈提醒 逾期后 消息格式UUID';


-- 文档交换-展示单据
create table dms_doc_exchange_dyform
(
   UUID                                     VARCHAR2(64)   not null,
   CREATE_TIME                              TIMESTAMP,
   CREATOR                                  VARCHAR2(64),
   MODIFIER                                 VARCHAR2(64),
   MODIFY_TIME                              TIMESTAMP,
   REC_VER                                  NUMBER(10),
   SYSTEM_UNIT_ID                           VARCHAR2(64),
   dms_doc_exchange_config_uuid             VARCHAR2(64),
   definition_json                          CLOB,
   dyform_uuid                              VARCHAR2(64),
   constraint pk_dms_doc_exchange_dyform primary key (UUID)
);
comment on table  dms_doc_exchange_dyform is '文档交换-展示单据';
comment on column dms_doc_exchange_dyform.UUID is 'UUID';
comment on column dms_doc_exchange_dyform.CREATE_TIME is '创建时间';
comment on column dms_doc_exchange_dyform.CREATOR is '创建人';
comment on column dms_doc_exchange_dyform.MODIFIER is '修改人';
comment on column dms_doc_exchange_dyform.MODIFY_TIME is '修改时间';
comment on column dms_doc_exchange_dyform.REC_VER is '版本号';
comment on column dms_doc_exchange_dyform.SYSTEM_UNIT_ID is '系统单位ID';

comment on column dms_doc_exchange_dyform.dms_doc_exchange_config_uuid is '文档交换-配置UUID';
comment on column dms_doc_exchange_dyform.definition_json is '表单定义JSON';
comment on column dms_doc_exchange_dyform.dyform_uuid is '动态表单定义uuid';


-- 文档交换-到期提醒
create table dms_doc_exchange_expire
(
   UUID                                     VARCHAR2(64)   not null,
   CREATE_TIME                              TIMESTAMP,
   CREATOR                                  VARCHAR2(64),
   MODIFIER                                 VARCHAR2(64),
   MODIFY_TIME                              TIMESTAMP,
   REC_VER                                  NUMBER(10),
   SYSTEM_UNIT_ID                           VARCHAR2(64),
   doc_exchange_record_uuid                 VARCHAR2(64),
   type                                     NUMBER(1),
   send_time                                TIMESTAMP,
   msg_template_uuid                        VARCHAR2(64),
   constraint pk_dms_doc_exchange_expire primary key (UUID)
);
comment on table  dms_doc_exchange_expire is '文档交换-到期提醒';
comment on column dms_doc_exchange_expire.UUID is 'UUID';
comment on column dms_doc_exchange_expire.CREATE_TIME is '创建时间';
comment on column dms_doc_exchange_expire.CREATOR is '创建人';
comment on column dms_doc_exchange_expire.MODIFIER is '修改人';
comment on column dms_doc_exchange_expire.MODIFY_TIME is '修改时间';
comment on column dms_doc_exchange_expire.REC_VER is '版本号';
comment on column dms_doc_exchange_expire.SYSTEM_UNIT_ID is '系统单位ID';

comment on column dms_doc_exchange_expire.doc_exchange_record_uuid is '文档交换-记录uuid';
comment on column dms_doc_exchange_expire.type is '提醒类型 1：签收，2：反馈';
comment on column dms_doc_exchange_expire.send_time is '发送时间（小于等于当前时间的，根据 文档交换记录的相关状态为来确定是发送，或删除该条数据）';
comment on column dms_doc_exchange_expire.msg_template_uuid is '消息格式uuid';

-- 文档交换-交换记录字段版本记录
create table dms_doc_exchange_field_ver
(
   UUID                                     VARCHAR2(64)   not null,
   CREATE_TIME                              TIMESTAMP,
   CREATOR                                  VARCHAR2(64),
   MODIFIER                                 VARCHAR2(64),
   MODIFY_TIME                              TIMESTAMP,
   REC_VER                                  NUMBER(10),
   SYSTEM_UNIT_ID                           VARCHAR2(64),
   version                                  VARCHAR2(64),
   user_id                                  VARCHAR2(64),
   constraint pk_dms_doc_exchange_field_ver primary key (UUID)
);
comment on table  dms_doc_exchange_field_ver is '文档交换-交换记录字段版本记录';
comment on column dms_doc_exchange_field_ver.UUID is 'UUID';
comment on column dms_doc_exchange_field_ver.CREATE_TIME is '创建时间';
comment on column dms_doc_exchange_field_ver.CREATOR is '创建人';
comment on column dms_doc_exchange_field_ver.MODIFIER is '修改人';
comment on column dms_doc_exchange_field_ver.MODIFY_TIME is '修改时间';
comment on column dms_doc_exchange_field_ver.REC_VER is '版本号';
comment on column dms_doc_exchange_field_ver.SYSTEM_UNIT_ID is '系统单位ID';

comment on column dms_doc_exchange_field_ver.version is '交换记录-字段版本';
comment on column dms_doc_exchange_field_ver.user_id is '已读用户Id';



-- 文档交换-相关文档
create table dms_doc_exchange_related_doc
(
   UUID                                     VARCHAR2(64)   not null,
   CREATE_TIME                              TIMESTAMP,
   CREATOR                                  VARCHAR2(64),
   MODIFIER                                 VARCHAR2(64),
   MODIFY_TIME                              TIMESTAMP,
   REC_VER                                  NUMBER(10),
   SYSTEM_UNIT_ID                           VARCHAR2(64),
   doc_exchange_record_uuid                 VARCHAR2(64),
   from_record_detail_uuid                  VARCHAR2(64),
   processing_method                        VARCHAR2(255),
   doc_title                                VARCHAR2(255),
   doc_link                                 VARCHAR2(255),
   constraint pk_dms_doc_e_related_doc primary key (UUID)
);
comment on table  dms_doc_exchange_related_doc is '文档交换-相关文档';
comment on column dms_doc_exchange_related_doc.UUID is 'UUID';
comment on column dms_doc_exchange_related_doc.CREATE_TIME is '创建时间';
comment on column dms_doc_exchange_related_doc.CREATOR is '创建人';
comment on column dms_doc_exchange_related_doc.MODIFIER is '修改人';
comment on column dms_doc_exchange_related_doc.MODIFY_TIME is '修改时间';
comment on column dms_doc_exchange_related_doc.REC_VER is '版本号';
comment on column dms_doc_exchange_related_doc.SYSTEM_UNIT_ID is '系统单位ID';

comment on column dms_doc_exchange_related_doc.doc_exchange_record_uuid is '文档交换-记录uuid';
comment on column dms_doc_exchange_related_doc.from_record_detail_uuid is '来源文档交换-记录明细uuid';
comment on column dms_doc_exchange_related_doc.processing_method is '处理方式';
comment on column dms_doc_exchange_related_doc.doc_title is '文档标题';
comment on column dms_doc_exchange_related_doc.doc_link is '文档链接';



-- Add/modify columns
alter table dms_doc_exchange_record add(
    REFUSE_TO_VIEW  NUMBER(1),
    NO_REMINDERS  NUMBER(1),
    from_unit_id VARCHAR2(64),
    config_uuid VARCHAR2(64),
    send_time  TIMESTAMP
);

comment on column dms_doc_exchange_record.refuse_to_view is '拒绝查看';
comment on column dms_doc_exchange_record.no_reminders is '不再提醒';
comment on column dms_doc_exchange_record.from_unit_id is '发件单位Id';
comment on column dms_doc_exchange_record.config_uuid is '文档交换-配置uuid';
comment on column dms_doc_exchange_record.send_time is '发件时间';


-- Add/modify columns
alter table dms_doc_exc_record_forward add(
    from_unit_id VARCHAR2(64)
);

comment on column dms_doc_exc_record_forward.from_unit_id is '来源单位Id';


-- Add/modify columns
alter table dms_doc_exc_feedback_detail add(
    from_unit_id VARCHAR2(64)
);

comment on column dms_doc_exc_feedback_detail.from_unit_id is '来源单位Id';



