-- 全文检索分类
CREATE TABLE FULLTEXT_CATEGORY
(
    uuid        NUMBER(20, 0) PRIMARY KEY,
    create_time TIMESTAMP,
    modify_time TIMESTAMP,
    creator     VARCHAR2(64),
    modifier    VARCHAR2(64),
    rec_ver     number(10, 0),
    system      VARCHAR2(64),
    tenant      VARCHAR2(64),
    name        VARCHAR2(100),
    sort_order  NUMBER(10, 0),
    parent_uuid NUMBER(20, 0),
    module_id   VARCHAR2(100)
);
-- 添加字段说明
COMMENT
    ON TABLE fulltext_category IS '全文检索分类';
COMMENT
    ON COLUMN fulltext_category.uuid IS '主键';
COMMENT
    ON COLUMN fulltext_category.create_time IS '创建时间';
COMMENT
    ON COLUMN fulltext_category.modify_time IS '更新时间';
COMMENT
    ON COLUMN fulltext_category.creator IS '创建用户';
COMMENT
    ON COLUMN fulltext_category.modifier IS '更新用户';
COMMENT
    ON COLUMN fulltext_category.rec_ver IS '版本号';
COMMENT
    ON COLUMN fulltext_category.system IS '系统标识';
COMMENT
    ON COLUMN fulltext_category.tenant IS '租户标识';
COMMENT
    ON COLUMN fulltext_category.name IS '分类名称';
COMMENT
    ON COLUMN fulltext_category.sort_order IS '排序号';
COMMENT
    ON COLUMN fulltext_category.parent_uuid IS '上级分类UUID';
COMMENT
    ON COLUMN fulltext_category.module_id IS '归属模块';


-- 全文检索数据模型
CREATE TABLE FULLTEXT_MODEL
(
    uuid                  NUMBER(20, 0) PRIMARY KEY,
    create_time           TIMESTAMP,
    modify_time           TIMESTAMP,
    creator               VARCHAR2(64),
    modifier              VARCHAR2(64),
    rec_ver               number(10, 0),
    system                VARCHAR2(64),
    tenant                VARCHAR2(64),
    category_uuid         NUMBER(20, 0),
    data_model_uuid       NUMBER(20, 0),
    match_json            CLOB,
    view_data_form_source VARCHAR2(10),
    view_data_form_uuid   VARCHAR2(50)
);
-- 添加字段说明
COMMENT
    ON TABLE fulltext_model IS '全文检索数据模型';
COMMENT
    ON COLUMN fulltext_model.uuid IS '主键';
COMMENT
    ON COLUMN fulltext_model.create_time IS '创建时间';
COMMENT
    ON COLUMN fulltext_model.modify_time IS '更新时间';
COMMENT
    ON COLUMN fulltext_model.creator IS '创建用户';
COMMENT
    ON COLUMN fulltext_model.modifier IS '更新用户';
COMMENT
    ON COLUMN fulltext_model.rec_ver IS '版本号';
COMMENT
    ON COLUMN fulltext_model.system IS '系统标识';
COMMENT
    ON COLUMN fulltext_model.tenant IS '租户标识';
COMMENT
    ON COLUMN fulltext_model.category_uuid IS '索引分类UUID';
COMMENT
    ON COLUMN fulltext_model.data_model_uuid IS '数据模型UUID';
COMMENT
    ON COLUMN fulltext_model.match_json IS '数据模型索引条件定义';
COMMENT
    ON COLUMN fulltext_model.view_data_form_source IS '查看数据的表单来源，auto数据创建表单，custom指定表单';
COMMENT
    ON COLUMN fulltext_model.view_data_form_uuid IS '查看数据的表单定义UUID';


-- 全文检索设置
CREATE TABLE FULLTEXT_SETTING
(
    uuid            NUMBER(20, 0) PRIMARY KEY,
    create_time     TIMESTAMP,
    modify_time     TIMESTAMP,
    creator         VARCHAR2(64),
    modifier        VARCHAR2(64),
    rec_ver         number(10, 0),
    system          VARCHAR2(64),
    tenant          VARCHAR2(64),
    type            VARCHAR2(10),
    definition_json CLOB
);
-- 添加字段说明
COMMENT
    ON TABLE fulltext_setting IS '全文检索设置';
COMMENT
    ON COLUMN fulltext_setting.uuid IS '主键';
COMMENT
    ON COLUMN fulltext_setting.create_time IS '创建时间';
COMMENT
    ON COLUMN fulltext_setting.modify_time IS '更新时间';
COMMENT
    ON COLUMN fulltext_setting.creator IS '创建用户';
COMMENT
    ON COLUMN fulltext_setting.modifier IS '更新用户';
COMMENT
    ON COLUMN fulltext_setting.rec_ver IS '版本号';
COMMENT
    ON COLUMN fulltext_setting.system IS '系统标识';
COMMENT
    ON COLUMN fulltext_setting.tenant IS '租户标识';
COMMENT
    ON COLUMN fulltext_setting.type IS '全文检索设置类型，search搜索设置，index索引设置';
COMMENT
    ON COLUMN fulltext_setting.definition_json IS '全文检索设置JSON信息';


-- 全文检索索引重建日志
CREATE TABLE FULLTEXT_REBUILD_LOG
(
    uuid                   NUMBER(20, 0) PRIMARY KEY,
    create_time            TIMESTAMP,
    modify_time            TIMESTAMP,
    creator                VARCHAR2(64),
    modifier               VARCHAR2(64),
    rec_ver                number(10, 0),
    system                 VARCHAR2(64),
    tenant                 VARCHAR2(64),
    setting_uuid           NUMBER(20, 0),
    rule_id                VARCHAR2(64),
    start_time             TIMESTAMP,
    end_time               TIMESTAMP,
    elapsed_time_in_second NUMBER(20, 0),
    original_index_count   NUMBER(20, 0),
    rebuild_index_count    NUMBER(20, 0),
    execute_state          VARCHAR2(2)
);
-- 添加字段说明
COMMENT
    ON TABLE fulltext_rebuild_log IS '全文检索索引重建日志';
COMMENT
    ON COLUMN fulltext_rebuild_log.uuid IS '主键';
COMMENT
    ON COLUMN fulltext_rebuild_log.create_time IS '创建时间';
COMMENT
    ON COLUMN fulltext_rebuild_log.modify_time IS '更新时间';
COMMENT
    ON COLUMN fulltext_rebuild_log.creator IS '创建用户';
COMMENT
    ON COLUMN fulltext_rebuild_log.modifier IS '更新用户';
COMMENT
    ON COLUMN fulltext_rebuild_log.rec_ver IS '版本号';
COMMENT
    ON COLUMN fulltext_rebuild_log.system IS '系统标识';
COMMENT
    ON COLUMN fulltext_rebuild_log.tenant IS '租户标识';
COMMENT
    ON COLUMN fulltext_rebuild_log.setting_uuid IS '索引设置UUID';
COMMENT
    ON COLUMN fulltext_rebuild_log.rule_id IS '规则ID';
COMMENT
    ON COLUMN fulltext_rebuild_log.start_time IS '开始时间';
COMMENT
    ON COLUMN fulltext_rebuild_log.end_time IS '结束时间';
COMMENT
    ON COLUMN fulltext_rebuild_log.elapsed_time_in_second IS '耗时，单位秒';
COMMENT
    ON COLUMN fulltext_rebuild_log.original_index_count IS '原索引数';
COMMENT
    ON COLUMN fulltext_rebuild_log.rebuild_index_count IS '重建索引数';
COMMENT
    ON COLUMN fulltext_rebuild_log.execute_state IS '执行状态，1执行中，2执行，3失败';

create index frl_setting_uuid_idx on fulltext_rebuild_log (setting_uuid);
