-- Create table
create table DYFORM_FILE_LIST_SOURCE_CONFIG
(
    uuid         VARCHAR2(50),
    create_time  TIMESTAMP(6),
    creator      VARCHAR2(50),
    modifier     VARCHAR2(50),
    modify_time  TIMESTAMP(6),
    rec_ver      NUMBER(10),
    source_name  VARCHAR2(1000 CHAR),
    code         VARCHAR2(1000 CHAR),
    icon         VARCHAR2(255 CHAR),
    js_module    VARCHAR2(255 CHAR),
    default_flag NUMBER(1, 0),
    order_index  NUMBER(3, 0)
);
-- Add comments to the columns
comment on column DYFORM_FILE_LIST_SOURCE_CONFIG.source_name
    is '名称';
comment on column DYFORM_FILE_LIST_SOURCE_CONFIG.code
    is '编码';
comment on column DYFORM_FILE_LIST_SOURCE_CONFIG.icon
    is '图标';
comment on column DYFORM_FILE_LIST_SOURCE_CONFIG.js_module
    is 'JS模块';
comment on column DYFORM_FILE_LIST_SOURCE_CONFIG.default_flag
    is '默认选中';
comment on column DYFORM_FILE_LIST_SOURCE_CONFIG.order_index
    is '排序字段';
-- Create/Recreate indexes
create unique index DYFORM_FILE_LIST_SOURCE_CONFIG on DYFORM_FILE_LIST_SOURCE_CONFIG (UUID);


-- Create table
create table DYFORM_FILE_LIST_BUTTON_CONFIG
(
    uuid            VARCHAR2(50),
    create_time     TIMESTAMP(6),
    creator         VARCHAR2(50),
    modifier        VARCHAR2(50),
    modify_time     TIMESTAMP(6),
    rec_ver         NUMBER(10),
    button_name     VARCHAR2(1000 CHAR),
    code            VARCHAR2(1000 CHAR),
    btn_lib         VARCHAR2(1000 CHAR),
    event_manger    VARCHAR2(1000 CHAR),
    file_extensions VARCHAR2(1000 CHAR),
    default_flag    NUMBER(1, 0),
    order_index     NUMBER(3, 0)
);
-- INSERT INTO DYFORM_FILE_LIST_SOURCE_CONFIG
INSERT INTO "DYFORM_FILE_LIST_SOURCE_CONFIG" ("UUID", "CREATE_TIME", "CREATOR", "MODIFIER", "MODIFY_TIME", "REC_VER",
                                              "SOURCE_NAME", "CODE", "ICON", "JS_MODULE", "DEFAULT_FLAG", "ORDER_INDEX")
VALUES ('83719f31-5c17-43ea-8fbe-206799cbc04a', NULL, NULL, NULL, NULL, '20', '本地上传', 'local_upload',
        '', NULL, '1', '0');

-- Add comments to the columns
comment on column DYFORM_FILE_LIST_BUTTON_CONFIG.button_name
    is '名称';
comment on column DYFORM_FILE_LIST_BUTTON_CONFIG.code
    is '编码';
comment on column DYFORM_FILE_LIST_BUTTON_CONFIG.btn_lib
    is '按钮库';
comment on column DYFORM_FILE_LIST_BUTTON_CONFIG.event_manger
    is '事件管理';
comment on column DYFORM_FILE_LIST_BUTTON_CONFIG.file_extensions
    is '支持的文件扩展名';
comment on column DYFORM_FILE_LIST_BUTTON_CONFIG.default_flag
    is '默认选中';
comment on column DYFORM_FILE_LIST_BUTTON_CONFIG.order_index
    is '排序字段';
-- Create/Recreate indexes
create unique index DYFORM_FILE_LIST_BUTTON_CONFIG on DYFORM_FILE_LIST_BUTTON_CONFIG (UUID);

-- alert table add column
ALTER TABLE REPO_FILE
    ADD ( "SOURCE_ICON" VARCHAR2(255) NULL );

COMMENT ON COLUMN "REPO_FILE"."SOURCE_ICON" IS '来源图标';



