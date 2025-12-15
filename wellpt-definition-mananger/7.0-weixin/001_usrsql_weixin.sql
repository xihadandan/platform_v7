-- 微信配置信息
CREATE TABLE WEIXIN_CONFIG
(
    uuid            NUMBER(20, 0) PRIMARY KEY,
    create_time     TIMESTAMP,
    modify_time     TIMESTAMP,
    creator         VARCHAR2(64),
    modifier        VARCHAR2(64),
    rec_ver         number(10, 0),
    system          VARCHAR2(64),
    tenant          VARCHAR2(64),
    app_id          VARCHAR2(100),
    app_secret      VARCHAR2(200),
    corp_id         VARCHAR2(100),
    corp_domain_uri VARCHAR2(255),
    mobile_app_uri  VARCHAR2(255),
    enabled         NUMBER(1),
    definition_json CLOB
);
-- 添加字段说明
COMMENT
    ON TABLE weixin_config IS '微信配置信息';
COMMENT
    ON COLUMN weixin_config.uuid IS '主键';
COMMENT
    ON COLUMN weixin_config.create_time IS '创建时间';
COMMENT
    ON COLUMN weixin_config.modify_time IS '更新时间';
COMMENT
    ON COLUMN weixin_config.creator IS '创建用户';
COMMENT
    ON COLUMN weixin_config.modifier IS '更新用户';
COMMENT
    ON COLUMN weixin_config.rec_ver IS '版本号';
COMMENT
    ON COLUMN weixin_config.system IS '系统标识';
COMMENT
    ON COLUMN weixin_config.tenant IS '租户标识';
COMMENT
    ON COLUMN weixin_config.app_id IS '应用的ID';
COMMENT
    ON COLUMN weixin_config.app_secret IS '应用的凭证密钥';
COMMENT
    ON COLUMN weixin_config.corp_id IS '企业ID';
COMMENT
    ON COLUMN weixin_config.corp_domain_uri IS '企业回调域名';
COMMENT
    ON COLUMN weixin_config.mobile_app_uri IS '应用的移动端URL';
COMMENT
    ON COLUMN weixin_config.enabled IS '是否启用';
COMMENT
    ON COLUMN weixin_config.definition_json IS '配置json';
-- 添加索引以提高查询效率
CREATE INDEX idx_weixin_config_corp_id ON weixin_config (corp_id);
CREATE INDEX idx_weixin_config_system ON weixin_config (system);
CREATE INDEX idx_weixin_config_tenant ON weixin_config (tenant);


-- 微信部门信息
CREATE TABLE WEIXIN_DEPT
(
    uuid               NUMBER(20, 0) PRIMARY KEY,
    create_time        TIMESTAMP,
    modify_time        TIMESTAMP,
    creator            VARCHAR2(64),
    modifier           VARCHAR2(64),
    rec_ver            NUMBER(10, 0),
    config_uuid        NUMBER(20, 0),
    org_uuid           NUMBER(20, 0),
    org_version_uuid   NUMBER(20, 0),
    corp_id            VARCHAR2(100),
    org_element_uuid   NUMBER(20, 0),
    org_element_id     VARCHAR2(64),
    name               VARCHAR2(200),
    id                 NUMBER(20, 0),
    parent_id          NUMBER(20, 0),
    sort_order         NUMBER(20, 0),
    department_leaders VARCHAR2(500),
    status             NUMBER(4, 0)
);
-- 添加字段说明
COMMENT
    ON TABLE weixin_dept IS '微信部门信息';
COMMENT
    ON COLUMN weixin_dept.uuid IS '主键';
COMMENT
    ON COLUMN weixin_dept.create_time IS '创建时间';
COMMENT
    ON COLUMN weixin_dept.modify_time IS '更新时间';
COMMENT
    ON COLUMN weixin_dept.creator IS '创建用户';
COMMENT
    ON COLUMN weixin_dept.modifier IS '更新用户';
COMMENT
    ON COLUMN weixin_dept.rec_ver IS '版本号';
COMMENT
    ON COLUMN weixin_dept.config_uuid IS '微信配置信息Uuid';
COMMENT
    ON COLUMN weixin_dept.org_uuid IS '行政组织UUID';
COMMENT
    ON COLUMN weixin_dept.org_version_uuid IS '行政组织版本UUID';
COMMENT
    ON COLUMN weixin_dept.corp_id IS '企业ID';
COMMENT
    ON COLUMN weixin_dept.org_element_uuid IS 'oa系统节点UUID';
COMMENT
    ON COLUMN weixin_dept.org_element_id IS 'oa系统节点ID';
COMMENT
    ON COLUMN weixin_dept.name IS '部门名称';
COMMENT
    ON COLUMN weixin_dept.id IS '部门ID';
COMMENT
    ON COLUMN weixin_dept.parent_id IS '父部门的ID，在根部门下创建新部门，该参数值为 1';
COMMENT
    ON COLUMN weixin_dept.sort_order IS '在父部门中的次序值';
COMMENT
    ON COLUMN weixin_dept.department_leaders IS '部门负责人的UserID';
COMMENT
    ON COLUMN weixin_dept.status IS '部门状态，0正常，1删除';
-- 添加索引以提高查询效率
CREATE INDEX idx_weixin_dept_c_uuid ON weixin_dept (config_uuid);
CREATE INDEX idx_weixin_dept_org_v_uuid ON weixin_dept (org_version_uuid);
CREATE INDEX idx_weixin_dept_corp_id ON weixin_dept (corp_id);
CREATE INDEX idx_weixin_dept_id ON weixin_dept (id);
CREATE INDEX idx_weixin_dept_org_e_uuid ON weixin_dept (org_element_uuid);
CREATE INDEX idx_weixin_dept_org_e_id ON weixin_dept (org_element_id);


-- 微信用户信息
CREATE TABLE WEIXIN_USER
(
    uuid               NUMBER(20, 0) PRIMARY KEY,
    create_time        TIMESTAMP,
    modify_time        TIMESTAMP,
    creator            VARCHAR2(64),
    modifier           VARCHAR2(64),
    rec_ver            NUMBER(10, 0),
    config_uuid        NUMBER(20, 0),
    org_uuid           NUMBER(20, 0),
    org_version_uuid   NUMBER(20, 0),
    corp_id            VARCHAR2(100),
    oa_user_id         VARCHAR2(64),
    user_id            VARCHAR2(64),
    name               VARCHAR2(200),
    department_ids     VARCHAR2(500),
    position           VARCHAR2(100),
    status             NUMBER(4),
    enable             NUMBER(1),
    is_leader          NUMBER(1),
    ext_attr           VARCHAR2(4000),
    hide_mobile        NUMBER(1),
    telephone          VARCHAR2(20),
    orders             VARCHAR2(500),
    main_department    NUMBER(20, 0),
    alias              VARCHAR2(200),
    is_leader_in_depts VARCHAR2(100),
    direct_leaders     VARCHAR2(500),
    external_profile   VARCHAR2(4000)
);

-- 添加字段说明
COMMENT
    ON TABLE weixin_user IS '微信用户信息';
COMMENT
    ON COLUMN weixin_user.uuid IS '主键';
COMMENT
    ON COLUMN weixin_user.create_time IS '创建时间';
COMMENT
    ON COLUMN weixin_user.modify_time IS '更新时间';
COMMENT
    ON COLUMN weixin_user.creator IS '创建用户';
COMMENT
    ON COLUMN weixin_user.modifier IS '更新用户';
COMMENT
    ON COLUMN weixin_user.rec_ver IS '版本号';
COMMENT
    ON COLUMN weixin_user.config_uuid IS '微信配置信息Uuid';
COMMENT
    ON COLUMN weixin_user.org_uuid IS '行政组织UUID';
COMMENT
    ON COLUMN weixin_user.org_version_uuid IS '行政组织版本UUID';
COMMENT
    ON COLUMN weixin_user.corp_id IS '企业ID';
COMMENT
    ON COLUMN weixin_user.oa_user_id IS 'oa系统的用户ID';
COMMENT
    ON COLUMN weixin_user.user_id IS '成员UserID。对应管理端的账号';
COMMENT
    ON COLUMN weixin_user.name IS '成员名称';
COMMENT
    ON COLUMN weixin_user.department_ids IS '成员所属部门id列表';
COMMENT
    ON COLUMN weixin_user.position IS '职务信息';
COMMENT
    ON COLUMN weixin_user.status IS '激活状态: 1=已激活，2=已禁用，4=未激活，5=退出企业。';
COMMENT
    ON COLUMN weixin_user.enable IS '是否启用';
COMMENT
    ON COLUMN weixin_user.is_leader IS '是否为部门负责人：0-否；1-是。';
COMMENT
    ON COLUMN weixin_user.ext_attr IS '扩展属性';
COMMENT
    ON COLUMN weixin_user.hide_mobile IS '手机号码隐藏';
COMMENT
    ON COLUMN weixin_user.telephone IS '座机';
COMMENT
    ON COLUMN weixin_user.orders IS '部门内的排序值，默认为0。数量必须和department一致，数值越大排序越前面';
COMMENT
    ON COLUMN weixin_user.main_department IS '主部门，仅当应用对主部门有查看权限时返回';
COMMENT
    ON COLUMN weixin_user.alias IS '别名';
COMMENT
    ON COLUMN weixin_user.is_leader_in_depts IS '表示在所在的部门内是否为部门负责人。0-否；1-是。是一个列表，数量必须与department一致';
COMMENT
    ON COLUMN weixin_user.direct_leaders IS '直属上级UserID';
COMMENT
    ON COLUMN weixin_user.external_profile IS '成员对外属性';
-- 添加索引以提高查询效率
CREATE INDEX idx_weixin_user_config_uuid ON weixin_user (config_uuid);
CREATE INDEX idx_weixin_user_org_v_uuid ON weixin_user (org_version_uuid);
CREATE INDEX idx_weixin_user_corp_id ON weixin_user (corp_id);
CREATE INDEX idx_weixin_user_id ON weixin_user (user_id);
CREATE INDEX idx_weixin_user_oa_user_id ON weixin_user (oa_user_id);


-- 微信组织同步日志表
CREATE TABLE WEIXIN_SYNC_LOG
(
    uuid             NUMBER(20, 0) PRIMARY KEY,
    create_time      TIMESTAMP,
    modify_time      TIMESTAMP,
    creator          VARCHAR2(64),
    modifier         VARCHAR2(64),
    rec_ver          NUMBER(10, 0),
    system           VARCHAR2(64),
    tenant           VARCHAR2(64),
    config_uuid      NUMBER(20, 0),
    org_uuid         NUMBER(20, 0),
    org_name         VARCHAR2(255),
    org_version_uuid NUMBER(20, 0),
    sync_content     VARCHAR2(200),
    sync_type        VARCHAR2(10),
    sync_time        TIMESTAMP,
    sync_status      VARCHAR2(10),
    error_message    VARCHAR2(4000),
    remark           VARCHAR2(255)
);

-- 添加注释
COMMENT
    ON TABLE weixin_sync_log IS '微信同步日志表';
COMMENT
    ON COLUMN weixin_sync_log.uuid IS '主键';
COMMENT
    ON COLUMN weixin_sync_log.create_time IS '创建时间';
COMMENT
    ON COLUMN weixin_sync_log.modify_time IS '更新时间';
COMMENT
    ON COLUMN weixin_sync_log.creator IS '创建用户';
COMMENT
    ON COLUMN weixin_sync_log.modifier IS '更新用户';
COMMENT
    ON COLUMN weixin_sync_log.rec_ver IS '版本号';
COMMENT
    ON COLUMN weixin_sync_log.system IS '系统标识';
COMMENT
    ON COLUMN weixin_sync_log.tenant IS '租户标识';
COMMENT
    ON COLUMN weixin_sync_log.config_uuid IS '微信配置信息Uuid';
COMMENT
    ON COLUMN weixin_sync_log.org_uuid IS '同步的组织UUID';
COMMENT
    ON COLUMN weixin_sync_log.org_name IS '同步的组织名称';
COMMENT
    ON COLUMN weixin_sync_log.org_version_uuid IS '同步的组织版本UUID';
COMMENT
    ON COLUMN weixin_sync_log.sync_content IS '同步内容';
COMMENT
    ON COLUMN weixin_sync_log.sync_type IS '同步类型：1:手动触发、2:定时任务触发';
COMMENT
    ON COLUMN weixin_sync_log.sync_time IS '同步时间';
COMMENT
    ON COLUMN weixin_sync_log.sync_status IS '同步状态：1:成功、0:失败';
COMMENT
    ON COLUMN weixin_sync_log.error_message IS '错误信息';
COMMENT
    ON COLUMN weixin_sync_log.remark IS '微信同步配置简要说明';

-- 微信同步日志明细表
CREATE TABLE WEIXIN_SYNC_LOG_DETAIL
(
    uuid                  NUMBER(20, 0) PRIMARY KEY,
    create_time           TIMESTAMP,
    modify_time           TIMESTAMP,
    creator               VARCHAR2(64),
    modifier              VARCHAR2(64),
    rec_ver               NUMBER(10, 0),
    system                VARCHAR2(64),
    tenant                VARCHAR2(64),
    sync_log_uuid         NUMBER(20, 0),
    target_operation_type VARCHAR2(10),
    target_table          VARCHAR2(50),
    target_uuid           VARCHAR2(64),
    target_name           VARCHAR2(255),
    target_data           CLOB,
    sync_status           NUMBER(10),
    error_message         VARCHAR2(4000)
);

-- 添加字段说明
COMMENT
    ON TABLE weixin_sync_log_detail IS '微信同步日志明细表';
COMMENT
    ON COLUMN weixin_sync_log_detail.uuid IS '主键';
COMMENT
    ON COLUMN weixin_sync_log_detail.create_time IS '创建时间';
COMMENT
    ON COLUMN weixin_sync_log_detail.modify_time IS '更新时间';
COMMENT
    ON COLUMN weixin_sync_log_detail.creator IS '创建用户';
COMMENT
    ON COLUMN weixin_sync_log_detail.modifier IS '更新用户';
COMMENT
    ON COLUMN weixin_sync_log_detail.rec_ver IS '版本号';
COMMENT
    ON COLUMN weixin_sync_log_detail.system IS '系统标识';
COMMENT
    ON COLUMN weixin_sync_log_detail.tenant IS '租户标识';
COMMENT
    ON COLUMN weixin_sync_log_detail.sync_log_uuid IS '同步日志uuid';
COMMENT
    ON COLUMN weixin_sync_log_detail.target_operation_type IS '目标操作类型：新增、修改、删除';
COMMENT
    ON COLUMN weixin_sync_log_detail.target_table IS '目标表：部门表：weixin_dept、用户表：weixin_user';
COMMENT
    ON COLUMN weixin_sync_log_detail.target_uuid IS '目标表uuid';
COMMENT
    ON COLUMN weixin_sync_log_detail.target_name IS '目标表uuid对应名称';
COMMENT
    ON COLUMN weixin_sync_log_detail.target_data IS '目标表原数据';
COMMENT
    ON COLUMN weixin_sync_log_detail.sync_status IS '同步状态：1:成功、0:失败';
COMMENT
    ON COLUMN weixin_sync_log_detail.error_message IS '错误信息';
-- 添加索引以提高查询效率
CREATE INDEX idx_weixin_sld_sync_log_uuid ON weixin_sync_log_detail (sync_log_uuid);


-- 微信事件表
CREATE TABLE weixin_event
(
    uuid            NUMBER(20, 0) PRIMARY KEY,
    create_time     TIMESTAMP,
    modify_time     TIMESTAMP,
    creator         VARCHAR2(64),
    modifier        VARCHAR2(64),
    rec_ver         NUMBER(10, 0),
    system          VARCHAR2(64),
    tenant          VARCHAR2(64),
    corp_id         VARCHAR2(100),
    handle_status   NUMBER(4),
    handle_result   CLOB,
    event_type      VARCHAR2(64),
    event_type_name VARCHAR2(200),
    event_data      CLOB
);
-- 添加字段说明
COMMENT
    ON TABLE weixin_event IS '微信事件表';
COMMENT
    ON COLUMN weixin_event.uuid IS '主键';
COMMENT
    ON COLUMN weixin_event.create_time IS '创建时间';
COMMENT
    ON COLUMN weixin_event.modify_time IS '更新时间';
COMMENT
    ON COLUMN weixin_event.creator IS '创建用户';
COMMENT
    ON COLUMN weixin_event.modifier IS '更新用户';
COMMENT
    ON COLUMN weixin_event.rec_ver IS '版本号';
COMMENT
    ON COLUMN weixin_event.system IS '系统标识';
COMMENT
    ON COLUMN weixin_event.tenant IS '租户标识';
COMMENT
    ON COLUMN weixin_event.corp_id IS '企业ID';
COMMENT
    ON COLUMN weixin_event.handle_status IS '处理状态：0:待分发，1：已分发';
COMMENT
    ON COLUMN weixin_event.handle_result IS '处理结果';
COMMENT
    ON COLUMN weixin_event.event_type IS '事件类型';
COMMENT
    ON COLUMN weixin_event.event_type_name IS '事件类型名称';
COMMENT
    ON COLUMN weixin_event.event_data IS '事件数据';


-- 微信待办工作信息
CREATE TABLE WEIXIN_WORK_RECORD
(
    uuid           NUMBER(20, 0) PRIMARY KEY,
    create_time    TIMESTAMP,
    modify_time    TIMESTAMP,
    creator        VARCHAR2(64),
    modifier       VARCHAR2(64),
    rec_ver        NUMBER(10, 0),
    system         VARCHAR2(64),
    tenant         VARCHAR2(64),
    config_uuid    NUMBER(20, 0),
    corp_id        VARCHAR2(100),
    app_id         VARCHAR2(100),
    title          VARCHAR2(255),
    flow_inst_uuid VARCHAR2(50),
    task_inst_uuid VARCHAR2(50),
    url            VARCHAR2(200),
    content        CLOB,
    oa_user_id     CLOB,
    user_id        CLOB,
    owner_id       VARCHAR2(50),
    message_id     VARCHAR2(255),
    chat_id        VARCHAR2(255),
    is_group_chat  NUMBER(1),
    type           NUMBER(10, 0),
    state          NUMBER(10, 0),
    err_msg        CLOB
);
-- 添加字段说明
COMMENT
    ON TABLE weixin_work_record IS '微信待办工作信息';
COMMENT
    ON COLUMN weixin_work_record.uuid IS '主键';
COMMENT
    ON COLUMN weixin_work_record.create_time IS '创建时间';
COMMENT
    ON COLUMN weixin_work_record.modify_time IS '更新时间';
COMMENT
    ON COLUMN weixin_work_record.creator IS '创建用户';
COMMENT
    ON COLUMN weixin_work_record.modifier IS '更新用户';
COMMENT
    ON COLUMN weixin_work_record.rec_ver IS '版本号';
COMMENT
    ON COLUMN weixin_work_record.system IS '系统标识';
COMMENT
    ON COLUMN weixin_work_record.tenant IS '租户标识';
COMMENT
    ON COLUMN weixin_work_record.config_uuid IS '微信配置信息Uuid';
COMMENT
    ON COLUMN weixin_work_record.corp_id IS '企业ID';
COMMENT
    ON COLUMN weixin_work_record.app_id IS '微信应用的App ID';
COMMENT
    ON COLUMN weixin_work_record.title IS '流程标题';
COMMENT
    ON COLUMN weixin_work_record.flow_inst_uuid IS '流程实例UUID';
COMMENT
    ON COLUMN weixin_work_record.task_inst_uuid IS '环节实例UUID';
COMMENT
    ON COLUMN weixin_work_record.url IS '工作详情访问地址';
COMMENT
    ON COLUMN weixin_work_record.content IS '工作内容';
COMMENT
    ON COLUMN weixin_work_record.oa_user_id IS 'oa系统的用户ID';
COMMENT
    ON COLUMN weixin_work_record.user_id IS '微信用户的user_id';
COMMENT
    ON COLUMN weixin_work_record.owner_id IS '群主的user_id';
COMMENT
    ON COLUMN weixin_work_record.message_id IS '消息推送返回的消息ID';
COMMENT
    ON COLUMN weixin_work_record.chat_id IS '消息推送返回的会话ID';
COMMENT
    ON COLUMN weixin_work_record.is_group_chat IS '是否群聊';
COMMENT
    ON COLUMN weixin_work_record.type IS '0系统创建、1用户创建';
COMMENT
    ON COLUMN weixin_work_record.state IS '消息推送状态';
COMMENT
    ON COLUMN weixin_work_record.err_msg IS '消息推送错误信息';
-- 添加索引以提高查询效率
CREATE INDEX idx_weixin_wc_ti_uuid ON weixin_work_record (task_inst_uuid);
CREATE INDEX idx_weixin_wc_fi_uuid ON weixin_work_record (flow_inst_uuid);
CREATE INDEX idx_weixin_wc_chat_id ON weixin_work_record (chat_id);
