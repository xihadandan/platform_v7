/*==============================================================*/
/* Table: PT_T_DINGTALK_CONFIG                                  */
/*==============================================================*/
create table PT_T_DINGTALK_CONFIG 
(
   UUID                 VARCHAR2(255 BYTE)   not null,
   CREATE_TIME          TIMESTAMP,
   CREATOR              VARCHAR2(255 BYTE),
   MODIFIER             VARCHAR2(255 BYTE),
   MODIFY_TIME          TIMESTAMP,
   REC_VER              NUMBER(10,2),
   AGENT_ID             VARCHAR2(255 BYTE),
   APP_KEY              VARCHAR2(255 BYTE),
   APP_SECRET           VARCHAR2(255 BYTE),
   CORP_ID              VARCHAR2(255 BYTE),
   CORP_DOMAIN_URI      VARCHAR2(255 BYTE),
   SYSTEM_UNIT_ID       VARCHAR2(255 BYTE),
   SYSTEM_UNIT_NAME     VARCHAR2(255 BYTE),
   ORG_VERSION_ID       VARCHAR2(255 BYTE),
   ORG_VERSION_NAME     VARCHAR2(255 BYTE),
   SELF_BUSINESS_UNIT_ID VARCHAR2(255 BYTE),
   SNS_APP_ID           VARCHAR2(255 BYTE),
   SNS_APP_SECRET       VARCHAR2(255 BYTE)
);

comment on column PT_T_DINGTALK_CONFIG.UUID is
'唯一主键';

comment on column PT_T_DINGTALK_CONFIG.CREATE_TIME is
'创建时间';

comment on column PT_T_DINGTALK_CONFIG.CREATOR is
'创建人';

comment on column PT_T_DINGTALK_CONFIG.MODIFIER is
'更新人';

comment on column PT_T_DINGTALK_CONFIG.MODIFY_TIME is
'更新时间';

comment on column PT_T_DINGTALK_CONFIG.REC_VER is
'数据版本';

comment on column PT_T_DINGTALK_CONFIG.AGENT_ID is
'企业代理id';

comment on column PT_T_DINGTALK_CONFIG.APP_KEY is
'微应用key';

comment on column PT_T_DINGTALK_CONFIG.APP_SECRET is
'微应用secret';

comment on column PT_T_DINGTALK_CONFIG.CORP_ID is
'企业ID';

comment on column PT_T_DINGTALK_CONFIG.CORP_DOMAIN_URI is
'企业回调域名';

comment on column PT_T_DINGTALK_CONFIG.SYSTEM_UNIT_ID is
'系统单位ID';

comment on column PT_T_DINGTALK_CONFIG.SYSTEM_UNIT_NAME is
'系统单位名称';

comment on column PT_T_DINGTALK_CONFIG.ORG_VERSION_ID is
'组织版本ID';

comment on column PT_T_DINGTALK_CONFIG.ORG_VERSION_NAME is
'组织版本名称';

comment on column PT_T_DINGTALK_CONFIG.SELF_BUSINESS_UNIT_ID is
'组织版本对应的自业务ID';

comment on column PT_T_DINGTALK_CONFIG.SNS_APP_ID is
'免密登录应用授权ID';

comment on column PT_T_DINGTALK_CONFIG.SNS_APP_SECRET is
'免密登录应用授权秘钥';
