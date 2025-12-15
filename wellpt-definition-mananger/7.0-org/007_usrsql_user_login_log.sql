


/*==============================================================*/
/* Table: USER_LOGIN_LOG                                        */
/*==============================================================*/
create table USER_LOGIN_LOG 
(
   UUID                 NUMBER(20)           not null,
   USER_UUID            NUMBER(20)           not null,
   USER_NAME            VARCHAR(120 char),
   LOGIN_IP             VARCHAR(120),
   LOGIN_SOURCE         VARCHAR(64),
   USER_OS              VARCHAR(64),
   BROWSER              VARCHAR(120),
   LOGIN_TIME           TIMESTAMP,
   LOGIN_LOCALE         VARCHAR(64),
   LOGOUT_ITME          TIMESTAMP,
   CREATE_TIME          TIMESTAMP,
   CREATOR              VARCHAR2(64),
   MODIFIER             VARCHAR2(64),
   MODIFY_TIME          TIMESTAMP,
   REC_VER              FLOAT(3)             default 1,
   SYSTEM               VARCHAR2(64),
   TENANT               VARCHAR2(64),
   constraint PK_USER_LOGIN_LOG primary key (UUID)
);

comment on table USER_LOGIN_LOG is
'用户登录日志';

comment on column USER_LOGIN_LOG.USER_NAME is
'用户姓名';

comment on column USER_LOGIN_LOG.LOGIN_IP is
'登录IP';

comment on column USER_LOGIN_LOG.LOGIN_SOURCE is
'登录来源';

comment on column USER_LOGIN_LOG.USER_OS is
'用户操作系统';

comment on column USER_LOGIN_LOG.BROWSER is
'浏览器';

comment on column USER_LOGIN_LOG.LOGIN_LOCALE is
'登录语言环境';
