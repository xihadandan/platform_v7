create table WF_OPINION_CHECK_SET 
(
   UUID                 VARCHAR2(255 char)   not null,
   CREATE_TIME          TIMESTAMP,
   CREATOR              VARCHAR2(255 char),
   MODIFIER             VARCHAR2(255 char),
   MODIFY_TIME          TIMESTAMP,
   REC_VER              NUMBER(10),
   FLOW_DEF_ID          VARCHAR2(255),
   SCENE                VARCHAR2(4),
   OPINION_RULE_UUID    VARCHAR2(255 char),
   TASK_IDS             VARCHAR2(255 char),
   constraint PK_WF_OPINION_CHECK_SET primary key (UUID)
);

comment on column WF_OPINION_CHECK_SET.UUID is
'UUID，系统字段';

comment on column WF_OPINION_CHECK_SET.CREATE_TIME is
'创建时间';

comment on column WF_OPINION_CHECK_SET.CREATOR is
'创建人';

comment on column WF_OPINION_CHECK_SET.MODIFIER is
'修改人';

comment on column WF_OPINION_CHECK_SET.MODIFY_TIME is
'修改时间';

comment on column WF_OPINION_CHECK_SET.REC_VER is
'版本号';

comment on column WF_OPINION_CHECK_SET.FLOW_DEF_ID is
'流程定义ID:对应WF_FLOW_DEFINITION表的ID字段';

comment on column WF_OPINION_CHECK_SET.SCENE is
'枚举：SceneEnum
S001:提交
S002:退回
S003:转办
S004:会签';

comment on column WF_OPINION_CHECK_SET.TASK_IDS is
'固定备选项：全部(值用all) 所在环节ID集合，多个用;号隔开例如：T003;T005';
