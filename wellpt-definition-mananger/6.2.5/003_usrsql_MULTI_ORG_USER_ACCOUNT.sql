Alter   Table    MULTI_ORG_USER_ACCOUNT    Add    PWD_ERROR_NUM   INTEGER;

Alter   Table    MULTI_ORG_USER_ACCOUNT    Add    LAST_UN_LOCKED_TIME   TIMESTAMP;

Alter   Table    MULTI_ORG_USER_ACCOUNT    Add    LAST_LOCKED_TIME   TIMESTAMP;

Alter   Table    MULTI_ORG_USER_ACCOUNT    Add    PWD_CREATE_TIME   TIMESTAMP;

Alter   Table    MULTI_ORG_USER_ACCOUNT    Add    PWD_ERROR_LOCK   NUMBER(1);
Alter   Table    MULTI_ORG_USER_ACCOUNT    Add    IS_USER_SETTING_PWD   NUMBER(1);


comment on column MULTI_ORG_USER_ACCOUNT.PWD_ERROR_NUM is
'输入正确后清零-重置次数为0条件：
登录时，锁定时间结束，重置
登录时，输入正确后清零';

comment on column MULTI_ORG_USER_ACCOUNT.LAST_UN_LOCKED_TIME is
'最后一次账号解锁时间';

comment on column MULTI_ORG_USER_ACCOUNT.LAST_LOCKED_TIME is
'最后一次账号锁定时间';

comment on column MULTI_ORG_USER_ACCOUNT.PWD_CREATE_TIME is
'密码创建重置时间';

comment on column MULTI_ORG_USER_ACCOUNT.PWD_ERROR_LOCK is
'密码错误锁定';

comment on column MULTI_ORG_USER_ACCOUNT.IS_USER_SETTING_PWD is
'是否用户自己设置的密码';
