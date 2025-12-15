create table WF_FLOW_SIGN_OPINION_SAVE_TEMP 
(
   UUID                 VARCHAR2(255 char)   not null,
   CREATE_TIME          TIMESTAMP,
   CREATOR              VARCHAR2(255 char),
   MODIFIER             VARCHAR2(255 char),
   MODIFY_TIME          TIMESTAMP,
   REC_VER              NUMBER(10),
   FLOW_INST_UUID       VARCHAR2(64),
   OPINION_LABEL        VARCHAR2(255 char),
   OPINION_VALUE        VARCHAR2(255 char),
   OPINION_TEXT         VARCHAR2(4000 char),
   USER_ID              VARCHAR2(255 char),
   constraint PK_WF_FLOW_SIGN_OPINION_SAVE_T primary key (UUID)
);

comment on table WF_FLOW_SIGN_OPINION_SAVE_TEMP is
'环节提交时，把这笔流程实例的所有记录清空';

comment on column WF_FLOW_SIGN_OPINION_SAVE_TEMP.UUID is
'UUID，系统字段';

comment on column WF_FLOW_SIGN_OPINION_SAVE_TEMP.CREATE_TIME is
'创建时间';

comment on column WF_FLOW_SIGN_OPINION_SAVE_TEMP.CREATOR is
'创建人';

comment on column WF_FLOW_SIGN_OPINION_SAVE_TEMP.MODIFIER is
'修改人';

comment on column WF_FLOW_SIGN_OPINION_SAVE_TEMP.MODIFY_TIME is
'修改时间';

comment on column WF_FLOW_SIGN_OPINION_SAVE_TEMP.REC_VER is
'版本号';

comment on column WF_FLOW_SIGN_OPINION_SAVE_TEMP.FLOW_INST_UUID is
'流程实例UUID';

comment on column WF_FLOW_SIGN_OPINION_SAVE_TEMP.OPINION_LABEL is
'办理意见立场文本';

comment on column WF_FLOW_SIGN_OPINION_SAVE_TEMP.OPINION_VALUE is
'办理意见立场';

comment on column WF_FLOW_SIGN_OPINION_SAVE_TEMP.OPINION_TEXT is
'办理意见内容';

comment on column WF_FLOW_SIGN_OPINION_SAVE_TEMP.USER_ID is
'操作的用户ID';
