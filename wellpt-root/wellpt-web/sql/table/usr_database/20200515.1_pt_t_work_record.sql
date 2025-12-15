/*==============================================================*/
/* Table: PT_T_WORK_RECORD                                      */
/*==============================================================*/
create table PT_T_WORK_RECORD 
(
   UUID                 VARCHAR2(255 BYTE)   not null,
   CREATE_TIME          TIMESTAMP,
   CREATOR              VARCHAR2(255 BYTE),
   MODIFIER             VARCHAR2(255 BYTE),
   MODIFY_TIME          TIMESTAMP,
   REC_VER              NUMBER(10,2),
   BIZ_ID               VARCHAR2(255 BYTE),
   DING_RECORD_ID       VARCHAR2(255 BYTE),
   DING_USER_ID         VARCHAR2(255 BYTE),
   OPERATE_TIME         TIMESTAMP,
   RECORD_STATUS        NUMBER,
   TITLE                VARCHAR2(255 BYTE),
   USER_NAME            VARCHAR2(255 BYTE),
   ERR_CODE             VARCHAR2(255 BYTE),
   ERR_MSG              VARCHAR2(255 BYTE),
   FORM_ITEM_LIST       VARCHAR2(255 BYTE),
   URL                  VARCHAR2(255 BYTE),
   constraint PK_PT_T_WORK_RECORD primary key (UUID)
);

comment on table PT_T_WORK_RECORD is
'钉钉待办任务表';

comment on column PT_T_WORK_RECORD.UUID is
'唯一主键';

comment on column PT_T_WORK_RECORD.CREATE_TIME is
'创建时间';

comment on column PT_T_WORK_RECORD.CREATOR is
'创建人';

comment on column PT_T_WORK_RECORD.MODIFIER is
'更新人';

comment on column PT_T_WORK_RECORD.MODIFY_TIME is
'更新时间';

comment on column PT_T_WORK_RECORD.REC_VER is
'数据版本';

comment on column PT_T_WORK_RECORD.RECORD_STATUS is
'0：未更新；1：已更新；2：平台调取失败
';

comment on column PT_T_WORK_RECORD.TITLE is
'待办事项的标题';

comment on column PT_T_WORK_RECORD.USER_NAME is
'待办人名称';

comment on column PT_T_WORK_RECORD.ERR_CODE is
'钉钉错误码';

comment on column PT_T_WORK_RECORD.ERR_MSG is
'钉钉错误信息';

comment on column PT_T_WORK_RECORD.FORM_ITEM_LIST is
'推送内容';

comment on column PT_T_WORK_RECORD.URL is
'待办事项的跳转链接';
