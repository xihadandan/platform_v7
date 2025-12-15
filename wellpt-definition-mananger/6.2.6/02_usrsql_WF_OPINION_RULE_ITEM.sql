create table WF_OPINION_RULE_ITEM 
(
   UUID                 VARCHAR2(255 char)   not null,
   CREATE_TIME          TIMESTAMP,
   CREATOR              VARCHAR2(255 char),
   MODIFIER             VARCHAR2(255 char),
   MODIFY_TIME          TIMESTAMP,
   REC_VER              NUMBER(10),
   OPINION_RULE_UUID    VARCHAR2(255 char),
   ITEM_NAME            VARCHAR2(4 char),
   ITEM_CONDITION       VARCHAR2(4 char),
   ITEM_VALUE           VARCHAR2(10 char),
   constraint PK_WF_OPINION_RULE_ITEM primary key (UUID)
);

comment on column WF_OPINION_RULE_ITEM.UUID is
'UUID，系统字段';

comment on column WF_OPINION_RULE_ITEM.CREATE_TIME is
'创建时间';

comment on column WF_OPINION_RULE_ITEM.CREATOR is
'创建人';

comment on column WF_OPINION_RULE_ITEM.MODIFIER is
'修改人';

comment on column WF_OPINION_RULE_ITEM.MODIFY_TIME is
'修改时间';

comment on column WF_OPINION_RULE_ITEM.REC_VER is
'版本号';

comment on column WF_OPINION_RULE_ITEM.ITEM_NAME is
'固定值：意见内容或意见长度';

comment on column WF_OPINION_RULE_ITEM.ITEM_CONDITION is
'枚举：ItemConditionEnum
IC01:等于
IC02:不等于
IC03:大于
IC04:大于等于
IC05:小于
IC06:小于等于
IC07:包含
IC08:不包含';