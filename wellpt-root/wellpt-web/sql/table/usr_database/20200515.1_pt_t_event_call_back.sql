/*==============================================================*/
/* Table: PT_T_EVENT_CALL_BACK                                  */
/*==============================================================*/
create table PT_T_EVENT_CALL_BACK
(
   UUID                 VARCHAR2(255 BYTE)   not null,
   CREATE_TIME          TIMESTAMP,
   CREATOR              VARCHAR2(255 BYTE),
   MODIFIER             VARCHAR2(255 BYTE),
   MODIFY_TIME          TIMESTAMP,
   REC_VER              NUMBER(10,2),
   EVENT_TYPE           VARCHAR2(255 BYTE),
   DING_TIME_STAMP      VARCHAR2(255 BYTE),
   DING_USER_ID         VARCHAR2(255 BYTE),
   DING_CORP_ID         VARCHAR2(255 BYTE),
   STATUS               NUMBER,
   OPT_TIME             TIMESTAMP,
   DING_DEPT_ID         VARCHAR2(255 BYTE),
   REMARK         VARCHAR2(255 BYTE),
   constraint PK_PT_T_EVENT_CALL_BACK primary key (UUID)
);

comment on table PT_T_EVENT_CALL_BACK is
'通讯录事件回调数据表';

comment on column PT_T_EVENT_CALL_BACK.UUID is
'唯一主键';

comment on column PT_T_EVENT_CALL_BACK.CREATE_TIME is
'创建时间';

comment on column PT_T_EVENT_CALL_BACK.CREATOR is
'创建人';

comment on column PT_T_EVENT_CALL_BACK.MODIFIER is
'更新人';

comment on column PT_T_EVENT_CALL_BACK.MODIFY_TIME is
'更新时间';

comment on column PT_T_EVENT_CALL_BACK.REC_VER is
'数据版本';

comment on column PT_T_EVENT_CALL_BACK.DING_USER_ID is
'用户发生变更的userid列表';

comment on column PT_T_EVENT_CALL_BACK.DING_CORP_ID is
'发生通讯录变更的企业';

comment on column PT_T_EVENT_CALL_BACK.STATUS is
'0：未处理 1：已处理 2：处理失败


';

comment on column PT_T_EVENT_CALL_BACK.OPT_TIME is
'待办事项的标题';

comment on column PT_T_EVENT_CALL_BACK.DING_DEPT_ID is
'部门发生变更的deptId列表';

comment on column PT_T_EVENT_CALL_BACK.REMARK is
'备注';
