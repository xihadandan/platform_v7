/*==============================================================*/
/* Table: MULTI_ORG_DING_USER_INFO                              */
/*==============================================================*/
create table MULTI_ORG_DING_USER_INFO 
(
   UUID                 VARCHAR2(255 BYTE)   not null,
   CREATE_TIME          TIMESTAMP,
   CREATOR              VARCHAR2(255 BYTE),
   MODIFIER             VARCHAR2(255 BYTE),
   MODIFY_TIME          TIMESTAMP,
   REC_VER              NUMBER(10,2),
   USER_ID              VARCHAR2(255 BYTE),
   DING_USER_ID         VARCHAR2(255 BYTE),
   UNION_ID             VARCHAR2(255 BYTE),
   PT_IS_ACTIVE         NUMBER(1),
   ORDER_IN_DEPTS       VARCHAR2(255 BYTE),
   OPEN_ID              VARCHAR2(255 BYTE),
   ROLES                VARCHAR2(255 BYTE),
   MOBILE               VARCHAR2(255 BYTE),
   ACTIVE               NUMBER(1),
   AVATAR               VARCHAR2(255 BYTE),
   IS_ADMIN             NUMBER(1),
   TAGS                 VARCHAR2(255 BYTE),
   IS_HIDE              NUMBER(1),
   IS_LEADER_IN_DEPTS   VARCHAR2(255 BYTE),
   IS_BOSS              NUMBER(1),
   IS_SENIOR            NUMBER(1),
   NAME                 VARCHAR2(255 BYTE),
   STATE_CODE           VARCHAR2(255 BYTE),
   DEPARTMENT           VARCHAR2(255 BYTE),
   EMAIL                VARCHAR2(255 BYTE),
   POSITION             VARCHAR2(255 BYTE),
   PT_ORIGIN_PWD        VARCHAR2(255 BYTE),
   constraint PK_MULTI_ORG_DING_USER_INFO primary key (UUID)
);

comment on table MULTI_ORG_DING_USER_INFO is
'钉钉用户信息表';

comment on column MULTI_ORG_DING_USER_INFO.UUID is
'唯一主键';

comment on column MULTI_ORG_DING_USER_INFO.CREATE_TIME is
'创建时间';

comment on column MULTI_ORG_DING_USER_INFO.CREATOR is
'创建人';

comment on column MULTI_ORG_DING_USER_INFO.MODIFIER is
'更新人';

comment on column MULTI_ORG_DING_USER_INFO.MODIFY_TIME is
'更新时间';

comment on column MULTI_ORG_DING_USER_INFO.REC_VER is
'数据版本';

comment on column MULTI_ORG_DING_USER_INFO.USER_ID is
'对应的USER_ID';

comment on column MULTI_ORG_DING_USER_INFO.DING_USER_ID is
'钉钉用户id';

comment on column MULTI_ORG_DING_USER_INFO.UNION_ID is
'钉钉用户唯一标识';

comment on column MULTI_ORG_DING_USER_INFO.ACTIVE is
'true表示已激活，false表示未激活';

comment on column MULTI_ORG_DING_USER_INFO.IS_ADMIN is
'true表示是，false表示不是';

comment on column MULTI_ORG_DING_USER_INFO.IS_HIDE is
'true表示隐藏，false表示不隐藏';

comment on column MULTI_ORG_DING_USER_INFO.IS_LEADER_IN_DEPTS is
'Map结构的json字符串，key是部门的id，value是人员在这个部门中是否为主管，true表示是，false表示不是';

comment on column MULTI_ORG_DING_USER_INFO.IS_BOSS is
'true表示是，false表示不是';

comment on column MULTI_ORG_DING_USER_INFO.POSITION is
'职位信息';

comment on column MULTI_ORG_DING_USER_INFO.PT_ORIGIN_PWD is
'平台原始登录密码';