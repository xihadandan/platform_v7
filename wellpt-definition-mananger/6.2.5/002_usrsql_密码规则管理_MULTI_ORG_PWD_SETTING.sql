-- 如果表存在，则执行删除操作，表不存在，drop不需要执行
drop table MULTI_ORG_PWD_SETTING ;


create table MULTI_ORG_PWD_SETTING 
(
   UUID                 VARCHAR2(255 CHAR)   not null,
   CREATE_TIME          TIMESTAMP,
   CREATOR              VARCHAR2(255 BYTE),
   MODIFIER             VARCHAR2(255 BYTE),
   MODIFY_TIME          TIMESTAMP,
   REC_VER              NUMBER(10,2),
   LETTER_ASK           VARCHAR(4),
   LETTER_LIMITED       VARCHAR(4),
   MIN_LENGTH           NUMBER(10),
   MAX_LENGTH           NUMBER(10),
   IS_PWD_VALIDITY      NUMBER(1),
   PWD_VALIDITY         NUMBER(10),
   ADVANCE_PWD_DAY      NUMBER(10),
   IS_ENFORCE_UPDATE_PWD NUMBER(1),
   IS_PWD_ERROR_LOCK    NUMBER(1),
   MORE_THEAN_ERROR_NUMBER NUMBER(10),
   ACCOUNT_LOCK_MINUTE  NUMBER(10),
   IS_PWD_ERROR_LOCK_AUTO_UNLOCK NUMBER(1),
   IS_NOT_USER_SET_PWD_UPDATE NUMBER(1),
   ADMIN_SET_PWD_TYPE   VARCHAR(6),
   constraint PK_MULTI_ORG_PWD_SETTING primary key (UUID)
);

comment on column MULTI_ORG_PWD_SETTING.UUID is
'唯一主键';

comment on column MULTI_ORG_PWD_SETTING.CREATE_TIME is
'创建时间';

comment on column MULTI_ORG_PWD_SETTING.CREATOR is
'创建人';

comment on column MULTI_ORG_PWD_SETTING.MODIFIER is
'更新人';

comment on column MULTI_ORG_PWD_SETTING.MODIFY_TIME is
'更新时间';

comment on column MULTI_ORG_PWD_SETTING.REC_VER is
'数据版本';

comment on column MULTI_ORG_PWD_SETTING.LETTER_ASK is
'字母、数字、特殊字符 至少包含 枚举：LetterAskEnum
LA01 :至少1种
LA02: 至少2种
LA02:3种';

comment on column MULTI_ORG_PWD_SETTING.LETTER_LIMITED is
'是否必须要有大写、小写 枚举：LetterLimitedEnum
LL01 : 是
LL02:否';

comment on column MULTI_ORG_PWD_SETTING.IS_PWD_VALIDITY is
'枚举 IsPwdValidityEnum
0：否，1：是';

comment on column MULTI_ORG_PWD_SETTING.IS_ENFORCE_UPDATE_PWD is
'枚举 IsEnforceUpdatePwdEnum

0：否，1：是';

comment on column MULTI_ORG_PWD_SETTING.IS_PWD_ERROR_LOCK is
'枚举 IsPwdErrorLockEnum
0：否，1：是';

comment on column MULTI_ORG_PWD_SETTING.MORE_THEAN_ERROR_NUMBER is
'超过输错次数触发';

comment on column MULTI_ORG_PWD_SETTING.IS_PWD_ERROR_LOCK_AUTO_UNLOCK is
'关闭账号锁定时，自动解锁【已锁定账号】
枚举IsPwdErrorLockAutoUnlockEnum
0：否
1：是';

comment on column MULTI_ORG_PWD_SETTING.IS_NOT_USER_SET_PWD_UPDATE is
'非用户设置的密码：通过系统初始化用户/管理员重置密码/管理员创建用户的操作，生成的密码
枚举：IsNotUserSetPwdUpdateEnum
0：否
1：是';

comment on column MULTI_ORG_PWD_SETTING.ADMIN_SET_PWD_TYPE is
'管理员设置的密码类型 枚举 AdminSetPwdTypeEnum
ASPT01: 随机密码
ASPT02:自定义密码';

