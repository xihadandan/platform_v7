-- 飞书配置信息
CREATE TABLE FEISHU_CONFIG
(
    uuid            NUMBER(20, 0) PRIMARY KEY,
    create_time     TIMESTAMP,
    modify_time     TIMESTAMP,
    creator         VARCHAR2(64),
    modifier        VARCHAR2(64),
    rec_ver         number(10, 0),
    system          VARCHAR2(64),
    tenant          VARCHAR2(64),
    app_id          VARCHAR2(255),
    app_secret      VARCHAR2(255),
    service_uri     VARCHAR2(255),
    redirect_uri    VARCHAR2(255),
    mobile_app_uri  VARCHAR2(255),
    enabled         NUMBER(1),
    definition_json CLOB
);
-- 添加字段说明
COMMENT
    ON TABLE feishu_config IS '飞书配置信息';
COMMENT
    ON COLUMN feishu_config.uuid IS '主键';
COMMENT
    ON COLUMN feishu_config.create_time IS '创建时间';
COMMENT
    ON COLUMN feishu_config.modify_time IS '更新时间';
COMMENT
    ON COLUMN feishu_config.creator IS '创建用户';
COMMENT
    ON COLUMN feishu_config.modifier IS '更新用户';
COMMENT
    ON COLUMN feishu_config.rec_ver IS '版本号';
COMMENT
    ON COLUMN feishu_config.system IS '系统标识';
COMMENT
    ON COLUMN feishu_config.tenant IS '租户标识';
COMMENT
    ON COLUMN feishu_config.app_id IS '飞书应用的App ID';
COMMENT
    ON COLUMN feishu_config.app_secret IS '飞书应用的App Secret';
COMMENT
    ON COLUMN feishu_config.service_uri IS '飞书应用的服务URL';
COMMENT
    ON COLUMN feishu_config.redirect_uri IS '飞书应用的重定向URL';
COMMENT
    ON COLUMN feishu_config.mobile_app_uri IS '飞书应用的移动端URL';
COMMENT
    ON COLUMN feishu_config.enabled IS '是否启用';
COMMENT
    ON COLUMN feishu_config.definition_json IS '配置json';
-- 添加索引以提高查询效率
CREATE INDEX idx_feishu_config_app_id ON feishu_config (app_id);
CREATE INDEX idx_feishu_config_system ON feishu_config (system);
CREATE INDEX idx_feishu_config_tenant ON feishu_config (tenant);
-- 添加唯一约束，防止重复添加
ALTER TABLE feishu_config
    ADD CONSTRAINT uk_feishu_config_app_id UNIQUE (system, tenant, app_id);


-- 飞书用户信息
CREATE TABLE FEISHU_USER
(
    uuid             NUMBER(20, 0) PRIMARY KEY,
    create_time      TIMESTAMP,
    modify_time      TIMESTAMP,
    creator          VARCHAR2(64),
    modifier         VARCHAR2(64),
    rec_ver          NUMBER(10, 0),
    config_uuid      NUMBER(20, 0),
    org_uuid         NUMBER(20, 0),
    org_version_uuid NUMBER(20, 0),
    app_id           VARCHAR2(255),
    oa_user_id       VARCHAR2(64),
    union_id         VARCHAR2(255),
    user_id          VARCHAR2(255),
    open_id          VARCHAR2(255),
    name             VARCHAR2(255),
    en_name          VARCHAR2(255),
    nickname         VARCHAR2(255),
    email            VARCHAR2(255),
    mobile           VARCHAR2(255),
    mobile_visible   NUMBER(1),
    gender           NUMBER(10, 0),
    avatar           VARCHAR2(4000),
    status           VARCHAR2(200),
    department_ids   CLOB,
    leader_user_id   VARCHAR2(255),
    join_time        TIMESTAMP,
    employee_no      VARCHAR2(255),
    job_title        VARCHAR2(255),
    department_path  CLOB
);

-- 添加字段说明
COMMENT
    ON TABLE feishu_user IS '飞书用户信息';
COMMENT
    ON COLUMN feishu_user.uuid IS '主键';
COMMENT
    ON COLUMN feishu_user.create_time IS '创建时间';
COMMENT
    ON COLUMN feishu_user.modify_time IS '更新时间';
COMMENT
    ON COLUMN feishu_user.creator IS '创建用户';
COMMENT
    ON COLUMN feishu_user.modifier IS '更新用户';
COMMENT
    ON COLUMN feishu_user.rec_ver IS '版本号';
COMMENT
    ON COLUMN feishu_user.config_uuid IS '飞书配置信息Uuid';
COMMENT
    ON COLUMN feishu_user.org_uuid IS '行政组织UUID';
COMMENT
    ON COLUMN feishu_user.org_version_uuid IS '行政组织版本UUID';
COMMENT
    ON COLUMN feishu_user.app_id IS '飞书应用的App ID';
COMMENT
    ON COLUMN feishu_user.oa_user_id IS 'oa系统的用户ID';
COMMENT
    ON COLUMN feishu_user.union_id IS '飞书用户的union_id，应用开发商发布的不同应用中同一用户的标识';
COMMENT
    ON COLUMN feishu_user.user_id IS '飞书用户的user_id，租户内用户的唯一标识';
COMMENT
    ON COLUMN feishu_user.open_id IS '飞书用户的open_id，应用内用户的唯一标识';
COMMENT
    ON COLUMN feishu_user.name IS '用户名';
COMMENT
    ON COLUMN feishu_user.en_name IS '英文名';
COMMENT
    ON COLUMN feishu_user.nickname IS '别名';
COMMENT
    ON COLUMN feishu_user.email IS '邮箱';
COMMENT
    ON COLUMN feishu_user.mobile IS '手机号';
COMMENT
    ON COLUMN feishu_user.mobile_visible IS '手机号码可见性';
COMMENT
    ON COLUMN feishu_user.gender IS '性别(0：未知1：男2：女3：其他)';
COMMENT
    ON COLUMN feishu_user.avatar IS '用户头像信息';
COMMENT
    ON COLUMN feishu_user.status IS '用户状态，0：未激活，1：正常，2：已禁用，3：已删除';
COMMENT
    ON COLUMN feishu_user.department_ids IS '用户所属部门的ID列表';
COMMENT
    ON COLUMN feishu_user.leader_user_id IS '用户的直接主管的用户ID';
COMMENT
    ON COLUMN feishu_user.join_time IS '入职时间，时间戳格式';
COMMENT
    ON COLUMN feishu_user.employee_no IS '工号';
COMMENT
    ON COLUMN feishu_user.job_title IS '职务';
COMMENT
    ON COLUMN feishu_user.department_path IS '部门路径';
-- 添加索引以提高查询效率
CREATE INDEX idx_feishu_user_config_uuid ON feishu_user (config_uuid);
CREATE INDEX idx_feishu_user_org_v_uuid ON feishu_user (org_version_uuid);
CREATE INDEX idx_feishu_user_app_id ON feishu_user (app_id);
CREATE INDEX idx_feishu_user_open_id ON feishu_user (open_id);
CREATE INDEX idx_feishu_user_oa_user_id ON feishu_user (oa_user_id);


-- 飞书部门信息
CREATE TABLE FEISHU_DEPT
(
    uuid                 NUMBER(20, 0) PRIMARY KEY,
    create_time          TIMESTAMP,
    modify_time          TIMESTAMP,
    creator              VARCHAR2(64),
    modifier             VARCHAR2(64),
    rec_ver              NUMBER(10, 0),
    config_uuid          NUMBER(20, 0),
    org_uuid             NUMBER(20, 0),
    org_version_uuid     NUMBER(20, 0),
    app_id               VARCHAR2(255),
    org_element_uuid     NUMBER(20, 0),
    org_element_id       VARCHAR2(255),
    name                 VARCHAR2(255),
    parent_department_id VARCHAR2(64),
    department_id        VARCHAR2(64),
    open_department_id   VARCHAR2(64),
    leader_user_id       VARCHAR2(64),
    department_order     VARCHAR2(50),
    status               VARCHAR2(50),
    leaders              CLOB
);
-- 添加字段说明
COMMENT
    ON TABLE feishu_dept IS '飞书部门信息';
COMMENT
    ON COLUMN feishu_dept.uuid IS '主键';
COMMENT
    ON COLUMN feishu_dept.create_time IS '创建时间';
COMMENT
    ON COLUMN feishu_dept.modify_time IS '更新时间';
COMMENT
    ON COLUMN feishu_dept.creator IS '创建用户';
COMMENT
    ON COLUMN feishu_dept.modifier IS '更新用户';
COMMENT
    ON COLUMN feishu_dept.rec_ver IS '版本号';
COMMENT
    ON COLUMN feishu_dept.config_uuid IS '飞书配置信息Uuid';
COMMENT
    ON COLUMN feishu_dept.org_uuid IS '行政组织UUID';
COMMENT
    ON COLUMN feishu_dept.org_version_uuid IS '行政组织版本UUID';
COMMENT
    ON COLUMN feishu_dept.app_id IS '飞书应用的App ID';
COMMENT
    ON COLUMN feishu_dept.org_element_uuid IS 'oa系统节点UUID';
COMMENT
    ON COLUMN feishu_dept.org_element_id IS 'oa系统节点ID';
COMMENT
    ON COLUMN feishu_dept.name IS '部门名称';
COMMENT
    ON COLUMN feishu_dept.parent_department_id IS '父部门的ID，在根部门下创建新部门，该参数值为 “0”';
COMMENT
    ON COLUMN feishu_dept.department_id IS '本部门的自定义部门ID，注意：除需要满足正则规则外，同时不能以`od-`开头';
COMMENT
    ON COLUMN feishu_dept.open_department_id IS '部门的open_id，类型与通过请求的查询参数传入的department_id_type相同';
COMMENT
    ON COLUMN feishu_dept.leader_user_id IS '部门主管用户ID';
COMMENT
    ON COLUMN feishu_dept.department_order IS '部门的排序，即部门在其同级部门的展示顺序';
COMMENT
    ON COLUMN feishu_dept.status IS '部门状态，1删除，2未删除';
COMMENT
    ON COLUMN feishu_dept.leaders IS '部门负责人';
-- 添加索引以提高查询效率
CREATE INDEX idx_feishu_dept_c_uuid ON feishu_dept (config_uuid);
CREATE INDEX idx_feishu_dept_org_v_uuid ON feishu_dept (org_version_uuid);
CREATE INDEX idx_feishu_dept_app_id ON feishu_dept (app_id);
CREATE INDEX idx_feishu_dept_open_d_id ON feishu_dept (open_department_id);
CREATE INDEX idx_feishu_dept_org_e_uuid ON feishu_dept (org_element_uuid);
CREATE INDEX idx_feishu_dept_org_e_id ON feishu_dept (org_element_id);


-- 飞书待办工作信息
CREATE TABLE FEISHU_WORK_RECORD
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
    app_id         VARCHAR2(255),
    title          VARCHAR2(255),
    flow_inst_uuid VARCHAR2(50),
    task_inst_uuid VARCHAR2(50),
    url            VARCHAR2(200),
    content        CLOB,
    oa_user_id     VARCHAR2(4000),
    open_id        VARCHAR2(4000),
    open_owner_id  VARCHAR2(50),
    message_id     VARCHAR2(255),
    chat_id        VARCHAR2(255),
    is_group_chat  NUMBER(1),
    type           NUMBER(10, 0),
    state          NUMBER(10, 0),
    err_msg        CLOB
);
-- 添加字段说明
COMMENT
    ON TABLE feishu_work_record IS '飞书待办工作信息';
COMMENT
    ON COLUMN feishu_work_record.uuid IS '主键';
COMMENT
    ON COLUMN feishu_work_record.create_time IS '创建时间';
COMMENT
    ON COLUMN feishu_work_record.modify_time IS '更新时间';
COMMENT
    ON COLUMN feishu_work_record.creator IS '创建用户';
COMMENT
    ON COLUMN feishu_work_record.modifier IS '更新用户';
COMMENT
    ON COLUMN feishu_work_record.rec_ver IS '版本号';
COMMENT
    ON COLUMN feishu_work_record.system IS '系统标识';
COMMENT
    ON COLUMN feishu_work_record.tenant IS '租户标识';
COMMENT
    ON COLUMN feishu_work_record.config_uuid IS '飞书配置信息Uuid';
COMMENT
    ON COLUMN feishu_work_record.app_id IS '飞书应用的App ID';
COMMENT
    ON COLUMN feishu_work_record.title IS '流程标题';
COMMENT
    ON COLUMN feishu_work_record.flow_inst_uuid IS '流程实例UUID';
COMMENT
    ON COLUMN feishu_work_record.task_inst_uuid IS '环节实例UUID';
COMMENT
    ON COLUMN feishu_work_record.url IS '工作详情访问地址';
COMMENT
    ON COLUMN feishu_work_record.content IS '工作内容';
COMMENT
    ON COLUMN feishu_work_record.oa_user_id IS 'oa系统的用户ID';
COMMENT
    ON COLUMN feishu_work_record.open_id IS '飞书用户的open_id，应用内用户的唯一标识';
COMMENT
    ON COLUMN feishu_work_record.open_owner_id IS '飞书用户的open_id，群主的open_id';
COMMENT
    ON COLUMN feishu_work_record.message_id IS '消息推送返回的消息ID';
COMMENT
    ON COLUMN feishu_work_record.chat_id IS '消息推送返回的会话ID';
COMMENT
    ON COLUMN feishu_work_record.is_group_chat IS '是否群聊';
COMMENT
    ON COLUMN feishu_work_record.type IS '0系统创建、1用户创建';
COMMENT
    ON COLUMN feishu_work_record.state IS '消息推送状态';
COMMENT
    ON COLUMN feishu_work_record.err_msg IS '消息推送错误信息';
-- 添加索引以提高查询效率
CREATE INDEX idx_feishu_wc_ti_uuid ON feishu_work_record (task_inst_uuid);
CREATE INDEX idx_feishu_wc_fi_uuid ON feishu_work_record (flow_inst_uuid);
CREATE INDEX idx_feishu_wc_chat_id ON feishu_work_record (chat_id);

-- 飞书同步日志表
CREATE TABLE feishu_sync_log
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
    ON TABLE feishu_sync_log IS '飞书同步日志表';
COMMENT
    ON COLUMN feishu_sync_log.uuid IS '主键';
COMMENT
    ON COLUMN feishu_sync_log.create_time IS '创建时间';
COMMENT
    ON COLUMN feishu_sync_log.modify_time IS '更新时间';
COMMENT
    ON COLUMN feishu_sync_log.creator IS '创建用户';
COMMENT
    ON COLUMN feishu_sync_log.modifier IS '更新用户';
COMMENT
    ON COLUMN feishu_sync_log.rec_ver IS '版本号';
COMMENT
    ON COLUMN feishu_sync_log.system IS '系统标识';
COMMENT
    ON COLUMN feishu_sync_log.tenant IS '租户标识';
COMMENT
    ON COLUMN feishu_sync_log.config_uuid IS '飞书配置信息Uuid';
COMMENT
    ON COLUMN feishu_sync_log.org_uuid IS '同步的组织UUID';
COMMENT
    ON COLUMN feishu_sync_log.org_name IS '同步的组织名称';
COMMENT
    ON COLUMN feishu_sync_log.org_version_uuid IS '同步的组织版本UUID';
COMMENT
    ON COLUMN feishu_sync_log.sync_content IS '同步内容';
COMMENT
    ON COLUMN feishu_sync_log.sync_type IS '同步类型：1:手动触发、2:定时任务触发';
COMMENT
    ON COLUMN feishu_sync_log.sync_time IS '同步时间';
COMMENT
    ON COLUMN feishu_sync_log.sync_status IS '同步状态：1:成功、0:失败';
COMMENT
    ON COLUMN feishu_sync_log.error_message IS '错误信息';
COMMENT
    ON COLUMN feishu_sync_log.remark IS '飞书同步配置简要说明';

-- 飞书同步日志明细表
CREATE TABLE feishu_sync_log_detail
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
    ON TABLE feishu_sync_log_detail IS '飞书同步日志明细表';
COMMENT
    ON COLUMN feishu_sync_log_detail.uuid IS '主键';
COMMENT
    ON COLUMN feishu_sync_log_detail.create_time IS '创建时间';
COMMENT
    ON COLUMN feishu_sync_log_detail.modify_time IS '更新时间';
COMMENT
    ON COLUMN feishu_sync_log_detail.creator IS '创建用户';
COMMENT
    ON COLUMN feishu_sync_log_detail.modifier IS '更新用户';
COMMENT
    ON COLUMN feishu_sync_log_detail.rec_ver IS '版本号';
COMMENT
    ON COLUMN feishu_sync_log_detail.system IS '系统标识';
COMMENT
    ON COLUMN feishu_sync_log_detail.tenant IS '租户标识';
COMMENT
    ON COLUMN feishu_sync_log_detail.sync_log_uuid IS '同步日志uuid';
COMMENT
    ON COLUMN feishu_sync_log_detail.target_operation_type IS '目标操作类型：新增、修改、删除';
COMMENT
    ON COLUMN feishu_sync_log_detail.target_table IS '目标表：部门表：feishu_dept、用户表：feishu_user';
COMMENT
    ON COLUMN feishu_sync_log_detail.target_uuid IS '目标表uuid';
COMMENT
    ON COLUMN feishu_sync_log_detail.target_name IS '目标表uuid对应名称';
COMMENT
    ON COLUMN feishu_sync_log_detail.target_data IS '目标表原数据';
COMMENT
    ON COLUMN feishu_sync_log_detail.sync_status IS '同步状态：1:成功、0:失败';
COMMENT
    ON COLUMN feishu_sync_log_detail.error_message IS '错误信息';

-- 飞书事件表
CREATE TABLE feishu_event
(
    uuid            NUMBER(20, 0) PRIMARY KEY,
    create_time     TIMESTAMP,
    modify_time     TIMESTAMP,
    creator         VARCHAR2(64),
    modifier        VARCHAR2(64),
    rec_ver         NUMBER(10, 0),
    system          VARCHAR2(64),
    tenant          VARCHAR2(64),
    app_id          VARCHAR2(255),
    handle_status   NUMBER(10),
    handle_result   CLOB,
    event_id        VARCHAR2(64),
    event_type      VARCHAR2(64),
    event_type_name VARCHAR2(200),
    event_data      CLOB
);
-- 添加字段说明
COMMENT
    ON TABLE feishu_event IS '飞书事件表';
COMMENT
    ON COLUMN feishu_event.uuid IS '主键';
COMMENT
    ON COLUMN feishu_event.create_time IS '创建时间';
COMMENT
    ON COLUMN feishu_event.modify_time IS '更新时间';
COMMENT
    ON COLUMN feishu_event.creator IS '创建用户';
COMMENT
    ON COLUMN feishu_event.modifier IS '更新用户';
COMMENT
    ON COLUMN feishu_event.rec_ver IS '版本号';
COMMENT
    ON COLUMN feishu_event.system IS '系统标识';
COMMENT
    ON COLUMN feishu_event.tenant IS '租户标识';
COMMENT
    ON COLUMN feishu_event.app_id IS '飞书appId';
COMMENT
    ON COLUMN feishu_event.handle_status IS '处理状态：0:待分发，1：已分发';
COMMENT
    ON COLUMN feishu_event.handle_result IS '处理结果';
COMMENT
    ON COLUMN feishu_event.event_id IS '事件id';
COMMENT
    ON COLUMN feishu_event.event_type IS '事件类型';
COMMENT
    ON COLUMN feishu_event.event_type_name IS '事件类型名称';
COMMENT
    ON COLUMN feishu_event.event_data IS '事件数据';


-- -- 飞书事件处理表
-- CREATE TABLE feishu_event_handle
-- (
--     uuid                  NUMBER        NOT NULL PRIMARY KEY,
--     create_time           TIMESTAMP,
--     modify_time           TIMESTAMP,
--     creator               VARCHAR2(64),
--     modifier              VARCHAR2(64),
--     rec_ver               float(3) default 1,
--     feishu_event_uuid     NUMBER        NOT NULL,
--     feishu_config_uuid    NUMBER        NOT NULL,
--     handle_status         VARCHAR2(10)  NOT NULL,
--     target_operation_type VARCHAR2(10)  NOT NULL,
--     target_table          VARCHAR2(50)  NOT NULL,
--     target_uuid           VARCHAR2(64)  NOT NULL,
--     target_name           VARCHAR2(255) NOT NULL,
--     target_data           CLOB,
--     error_message         VARCHAR2(255)
-- );
-- -- 添加字段说明
-- COMMENT
--     ON TABLE feishu_event_handle IS '飞书事件处理表';
-- COMMENT
--     ON COLUMN feishu_event_handle.uuid IS '主键';
-- COMMENT
--     ON COLUMN feishu_event_handle.create_time IS '创建时间';
-- COMMENT
--     ON COLUMN feishu_event_handle.modify_time IS '更新时间';
-- COMMENT
--     ON COLUMN feishu_event_handle.creator IS '创建用户';
-- COMMENT
--     ON COLUMN feishu_event_handle.modifier IS '更新用户';
-- COMMENT
--     ON COLUMN feishu_event_handle.rec_ver IS '版本号';
-- COMMENT
--     ON COLUMN feishu_event_handle.feishu_event_uuid IS '飞书事件表uuid';
-- COMMENT
--     ON COLUMN feishu_event_handle.feishu_config_uuid IS '飞书配置信息uuid';
-- COMMENT
--     ON COLUMN feishu_event_handle.handle_status IS '处理状态：0：待处理，1：处理完成，2：处理失败';
-- COMMENT
--     ON COLUMN feishu_event_handle.error_message IS '错误信息';
-- COMMENT
--     ON COLUMN feishu_event_handle.target_operation_type IS '目标操作类型：新增、修改、删除';
-- COMMENT
--     ON COLUMN feishu_event_handle.target_table IS '目标表：部门表：feishu_dept、用户表：feishu_user';
-- COMMENT
--     ON COLUMN feishu_event_handle.target_uuid IS '目标表uuid';
-- COMMENT
--     ON COLUMN feishu_event_handle.target_name IS '目标表uuid对应名称';
-- COMMENT
--     ON COLUMN feishu_event_handle.target_data IS '目标表原数据';
--
-- -- 飞书多部门人员审核表
-- CREATE TABLE feishu_dept_user_audit
-- (
--     uuid                  NUMBER         NOT NULL PRIMARY KEY,
--     create_time           TIMESTAMP,
--     modify_time           TIMESTAMP,
--     creator               VARCHAR2(64),
--     modifier              VARCHAR2(64),
--     rec_ver               float(3) default 1,
--     feishu_user_uuid      NUMBER         NOT NULL,
--     user_name             VARCHAR2(255)  NOT NULL,
--     login_name            VARCHAR2(255)  NOT NULL,
--     departments           VARCHAR2(1000) NOT NULL,
--     job_title             VARCHAR2(255)  NOT NULL,
--     before_audit_main_job VARCHAR2(1000),
--     after_audit_main_job  VARCHAR2(1000),
--     audit_time            TIMESTAMP,
--     audit_user_id         VARCHAR2(64),
--     audit_user_name       VARCHAR2(255),
--     audit_status          VARCHAR2(10),
--     job_json              CLOB,
--     oa_job_json           CLOB
-- );
-- -- 添加字段说明
-- COMMENT
--     ON TABLE feishu_dept_user_audit IS '飞书多部门人员审核表';
-- COMMENT
--     ON COLUMN feishu_dept_user_audit.uuid IS '主键';
-- COMMENT
--     ON COLUMN feishu_dept_user_audit.create_time IS '创建时间';
-- COMMENT
--     ON COLUMN feishu_dept_user_audit.modify_time IS '更新时间';
-- COMMENT
--     ON COLUMN feishu_dept_user_audit.creator IS '创建用户';
-- COMMENT
--     ON COLUMN feishu_dept_user_audit.modifier IS '更新用户';
-- COMMENT
--     ON COLUMN feishu_dept_user_audit.rec_ver IS '版本号';
-- COMMENT
--     ON COLUMN feishu_dept_user_audit.feishu_user_uuid IS '飞书用户表uuid';
-- COMMENT
--     ON COLUMN feishu_dept_user_audit.user_name IS '用户名称';
-- COMMENT
--     ON COLUMN feishu_dept_user_audit.login_name IS 'oa账号名';
-- COMMENT
--     ON COLUMN feishu_dept_user_audit.departments IS '飞书所属部门';
-- COMMENT
--     ON COLUMN feishu_dept_user_audit.job_title IS '飞书职位';
-- COMMENT
--     ON COLUMN feishu_dept_user_audit.before_audit_main_job IS '审核前的OA主职位';
-- COMMENT
--     ON COLUMN feishu_dept_user_audit.after_audit_main_job IS '审核后的OA主职位';
-- COMMENT
--     ON COLUMN feishu_dept_user_audit.audit_time IS '审核时间';
-- COMMENT
--     ON COLUMN feishu_dept_user_audit.audit_user_id IS '审核用户ID';
-- COMMENT
--     ON COLUMN feishu_dept_user_audit.audit_user_name IS '审核用户名称';
-- COMMENT
--     ON COLUMN feishu_dept_user_audit.audit_status IS '审核状态：0：待审核，1：已审核';
-- COMMENT
--     ON COLUMN feishu_dept_user_audit.job_json IS '飞书职位json';
-- COMMENT
--     ON COLUMN feishu_dept_user_audit.oa_job_json IS 'oa职位json';




