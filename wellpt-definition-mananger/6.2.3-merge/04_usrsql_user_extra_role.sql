-- 互联网用户额外角色类型表
create table USER_EXTRA_ROLE_TYPE
(
    uuid                 VARCHAR2(64) primary key,
    create_time          TIMESTAMP(6),
    creator              VARCHAR2(255 CHAR),
    modifier             VARCHAR2(255 CHAR),
    modify_time          TIMESTAMP(6),
    rec_ver              NUMBER(10),
    login_name           VARCHAR2(64) not null,
    user_extra_role_type NUMBER(1)    not null
);

COMMENT ON COLUMN "USER_EXTRA_ROLE_TYPE"."UUID" IS 'uuid';
COMMENT ON COLUMN "USER_EXTRA_ROLE_TYPE"."CREATE_TIME" IS '创建时间';
COMMENT ON COLUMN "USER_EXTRA_ROLE_TYPE"."CREATOR" IS '创建人';
COMMENT ON COLUMN "USER_EXTRA_ROLE_TYPE"."MODIFIER" IS '修改人';
COMMENT ON COLUMN "USER_EXTRA_ROLE_TYPE"."MODIFY_TIME" IS '修改时间';
COMMENT ON COLUMN "USER_EXTRA_ROLE_TYPE"."REC_VER" IS '记录版本号';
COMMENT ON COLUMN "USER_EXTRA_ROLE_TYPE"."LOGIN_NAME" IS '登录名';
COMMENT ON COLUMN "USER_EXTRA_ROLE_TYPE"."USER_EXTRA_ROLE_TYPE" IS '用户额外角色类型';
