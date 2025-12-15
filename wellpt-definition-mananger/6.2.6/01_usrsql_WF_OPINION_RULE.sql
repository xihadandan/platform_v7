create table WF_OPINION_RULE 
(
   UUID                 VARCHAR2(255 char)   not null,
   CREATE_TIME          TIMESTAMP,
   CREATOR              VARCHAR2(255 char),
   MODIFIER             VARCHAR2(255 char),
   MODIFY_TIME          TIMESTAMP,
   REC_VER              NUMBER(10),
   SYSTEM_UNIT_ID       VARCHAR2(12),
   OPINION_RULE_NAME    VARCHAR2(50 char),
   SATISFY_CONDITION    VARCHAR2(4 char),
   CUE_WORDS            VARCHAR2(50 char),
   IS_ALERT_AUTO_CLOSE  VARCHAR2(1),
   OPINION_RULE_ITEM    VARCHAR2(1000),
   constraint PK_WF_OPINION_RULE primary key (UUID)
);

comment on table WF_OPINION_RULE is
'所有流程共用';

comment on column WF_OPINION_RULE.UUID is
'UUID，系统字段';

comment on column WF_OPINION_RULE.CREATE_TIME is
'创建时间';

comment on column WF_OPINION_RULE.CREATOR is
'创建人';

comment on column WF_OPINION_RULE.MODIFIER is
'修改人';

comment on column WF_OPINION_RULE.MODIFY_TIME is
'修改时间';

comment on column WF_OPINION_RULE.REC_VER is
'版本号';

comment on column WF_OPINION_RULE.SYSTEM_UNIT_ID is
'归属系统单位ID';

comment on column WF_OPINION_RULE.SATISFY_CONDITION is
'枚举：SatisfyConditionEnum
SC01:全部
SC02:任何';

comment on column WF_OPINION_RULE.IS_ALERT_AUTO_CLOSE is
'1是选中，0是没选中，默认选中';

comment on column WF_OPINION_RULE.OPINION_RULE_ITEM is
'校验项查询值，只用列表显示和关键字模糊搜索';
