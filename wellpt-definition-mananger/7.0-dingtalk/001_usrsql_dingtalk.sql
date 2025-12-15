-- 钉钉配置信息
CREATE TABLE DINGTALK_CONFIG
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
    client_id       VARCHAR2(100),
    client_secret   VARCHAR2(200),
    service_uri     VARCHAR2(255),
    agent_id        VARCHAR2(100),
    corp_id         VARCHAR2(100),
    corp_domain_uri VARCHAR2(255),
    mobile_app_uri  VARCHAR2(255),
    enabled         NUMBER(1),
    definition_json CLOB
);
-- 添加字段说明
COMMENT
    ON TABLE dingtalk_config IS '钉钉配置信息';
COMMENT
    ON COLUMN dingtalk_config.uuid IS '主键';
COMMENT
    ON COLUMN dingtalk_config.create_time IS '创建时间';
COMMENT
    ON COLUMN dingtalk_config.modify_time IS '更新时间';
COMMENT
    ON COLUMN dingtalk_config.creator IS '创建用户';
COMMENT
    ON COLUMN dingtalk_config.modifier IS '更新用户';
COMMENT
    ON COLUMN dingtalk_config.rec_ver IS '版本号';
COMMENT
    ON COLUMN dingtalk_config.system IS '系统标识';
COMMENT
    ON COLUMN dingtalk_config.tenant IS '租户标识';
COMMENT
    ON COLUMN dingtalk_config.app_id IS '应用的APP ID';
COMMENT
    ON COLUMN dingtalk_config.client_id IS '应用的Client ID';
COMMENT
    ON COLUMN dingtalk_config.client_secret IS '应用的Client Secret';
COMMENT
    ON COLUMN dingtalk_config.service_uri IS '服务URL';
COMMENT
    ON COLUMN dingtalk_config.agent_id IS '企业代理ID';
COMMENT
    ON COLUMN dingtalk_config.corp_id IS '企业ID';
COMMENT
    ON COLUMN dingtalk_config.corp_domain_uri IS '企业回调域名';
COMMENT
    ON COLUMN dingtalk_config.mobile_app_uri IS '应用的移动端URL';
COMMENT
    ON COLUMN dingtalk_config.enabled IS '是否启用';
COMMENT
    ON COLUMN dingtalk_config.definition_json IS '配置json';
-- 添加索引以提高查询效率
CREATE INDEX idx_dingtalk_config_app_id ON dingtalk_config (app_id);
CREATE INDEX idx_dingtalk_config_system ON dingtalk_config (system);
CREATE INDEX idx_dingtalk_config_tenant ON dingtalk_config (tenant);


-- 钉钉部门信息
CREATE TABLE DINGTALK_DEPT
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
    app_id           VARCHAR2(100),
    org_element_uuid NUMBER(20, 0),
    org_element_id   VARCHAR2(64),
    name             VARCHAR2(200),
    dept_id          NUMBER(20, 0),
    parent_id        NUMBER(20, 0),
    status           NUMBER(4, 0),
    ext              VARCHAR2(4000)
);
-- 添加字段说明
COMMENT
    ON TABLE dingtalk_dept IS '钉钉部门信息';
COMMENT
    ON COLUMN dingtalk_dept.uuid IS '主键';
COMMENT
    ON COLUMN dingtalk_dept.create_time IS '创建时间';
COMMENT
    ON COLUMN dingtalk_dept.modify_time IS '更新时间';
COMMENT
    ON COLUMN dingtalk_dept.creator IS '创建用户';
COMMENT
    ON COLUMN dingtalk_dept.modifier IS '更新用户';
COMMENT
    ON COLUMN dingtalk_dept.rec_ver IS '版本号';
COMMENT
    ON COLUMN dingtalk_dept.config_uuid IS '钉钉配置信息Uuid';
COMMENT
    ON COLUMN dingtalk_dept.org_uuid IS '行政组织UUID';
COMMENT
    ON COLUMN dingtalk_dept.org_version_uuid IS '行政组织版本UUID';
COMMENT
    ON COLUMN dingtalk_dept.app_id IS '应用的App ID';
COMMENT
    ON COLUMN dingtalk_dept.org_element_uuid IS 'oa系统节点UUID';
COMMENT
    ON COLUMN dingtalk_dept.org_element_id IS 'oa系统节点ID';
COMMENT
    ON COLUMN dingtalk_dept.name IS '部门名称';
COMMENT
    ON COLUMN dingtalk_dept.dept_id IS '部门ID，由钉钉服务器生成，在创建部门时返回';
COMMENT
    ON COLUMN dingtalk_dept.parent_id IS '父部门的ID，在根部门下创建新部门，该参数值为 1';
COMMENT
    ON COLUMN dingtalk_dept.status IS '部门状态，0正常，1删除';
COMMENT
    ON COLUMN dingtalk_dept.ext IS '扩展信息';
-- 添加索引以提高查询效率
CREATE INDEX idx_dingtalk_dept_c_uuid ON dingtalk_dept (config_uuid);
CREATE INDEX idx_dingtalk_dept_org_v_uuid ON dingtalk_dept (org_version_uuid);
CREATE INDEX idx_dingtalk_dept_app_id ON dingtalk_dept (app_id);
CREATE INDEX idx_dingtalk_dept_id ON dingtalk_dept (dept_id);
CREATE INDEX idx_dingtalk_dept_org_e_uuid ON dingtalk_dept (org_element_uuid);
CREATE INDEX idx_dingtalk_dept_org_e_id ON dingtalk_dept (org_element_id);


-- 钉钉职位信息
CREATE TABLE DINGTALK_JOB
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
    app_id           VARCHAR2(100),
    org_element_uuid NUMBER(20, 0),
    org_element_id   VARCHAR2(64),
    title            VARCHAR2(200),
    dept_id          NUMBER(20, 0)
);
-- 添加字段说明
COMMENT
    ON TABLE dingtalk_job IS '钉钉职位信息';
COMMENT
    ON COLUMN dingtalk_job.uuid IS '主键';
COMMENT
    ON COLUMN dingtalk_job.create_time IS '创建时间';
COMMENT
    ON COLUMN dingtalk_job.modify_time IS '更新时间';
COMMENT
    ON COLUMN dingtalk_job.creator IS '创建用户';
COMMENT
    ON COLUMN dingtalk_job.modifier IS '更新用户';
COMMENT
    ON COLUMN dingtalk_job.rec_ver IS '版本号';
COMMENT
    ON COLUMN dingtalk_job.config_uuid IS '钉钉配置信息Uuid';
COMMENT
    ON COLUMN dingtalk_job.org_uuid IS '行政组织UUID';
COMMENT
    ON COLUMN dingtalk_job.org_version_uuid IS '行政组织版本UUID';
COMMENT
    ON COLUMN dingtalk_job.app_id IS '应用的App ID';
COMMENT
    ON COLUMN dingtalk_job.org_element_uuid IS 'oa系统节点UUID';
COMMENT
    ON COLUMN dingtalk_job.org_element_id IS 'oa系统节点ID';
COMMENT
    ON COLUMN dingtalk_job.title IS '职位名称';
COMMENT
    ON COLUMN dingtalk_job.dept_id IS '职位所在部门ID';
-- 添加索引以提高查询效率
CREATE INDEX idx_dingtalk_job_c_uuid ON dingtalk_job (config_uuid);
CREATE INDEX idx_dingtalk_job_org_v_uuid ON dingtalk_job (org_version_uuid);
CREATE INDEX idx_dingtalk_job_app_id ON dingtalk_job (app_id);
CREATE INDEX idx_dingtalk_job_id ON dingtalk_job (dept_id);
CREATE INDEX idx_dingtalk_job_org_e_uuid ON dingtalk_job (org_element_uuid);
CREATE INDEX idx_dingtalk_job_org_e_id ON dingtalk_job (org_element_id);

-- 钉钉用户信息
CREATE TABLE DINGTALK_USER
(
    uuid                    NUMBER(20, 0) PRIMARY KEY,
    create_time             TIMESTAMP,
    modify_time             TIMESTAMP,
    creator                 VARCHAR2(64),
    modifier                VARCHAR2(64),
    rec_ver                 NUMBER(10, 0),
    config_uuid             NUMBER(20, 0),
    org_uuid                NUMBER(20, 0),
    org_version_uuid        NUMBER(20, 0),
    app_id                  VARCHAR2(100),
    oa_user_id              VARCHAR2(64),
    union_id                VARCHAR2(64),
    user_id                 VARCHAR2(64),
    name                    VARCHAR2(200),
    avatar                  VARCHAR2(500),
    mobile                  VARCHAR2(20),
    hide_mobile             NUMBER(1),
    telephone               VARCHAR2(30),
    job_number              VARCHAR2(50),
    title                   VARCHAR2(200),
    email                   VARCHAR2(200),
    leader                  NUMBER(1),
    leader_dept_id          NUMBER(20, 0),
    leader_org_element_uuid NUMBER(20, 0),
    dept_id_list            VARCHAR2(1000),
    dept_order              NUMBER(20, 0),
    hired_date              TIMESTAMP,
    active                  NUMBER(1),
    remark                  VARCHAR2(200),
    extension               VARCHAR2(4000)
);

-- 添加字段说明
COMMENT
    ON TABLE dingtalk_user IS '钉钉用户信息';
COMMENT
    ON COLUMN dingtalk_user.uuid IS '主键';
COMMENT
    ON COLUMN dingtalk_user.create_time IS '创建时间';
COMMENT
    ON COLUMN dingtalk_user.modify_time IS '更新时间';
COMMENT
    ON COLUMN dingtalk_user.creator IS '创建用户';
COMMENT
    ON COLUMN dingtalk_user.modifier IS '更新用户';
COMMENT
    ON COLUMN dingtalk_user.rec_ver IS '版本号';
COMMENT
    ON COLUMN dingtalk_user.config_uuid IS '钉钉配置信息Uuid';
COMMENT
    ON COLUMN dingtalk_user.org_uuid IS '行政组织UUID';
COMMENT
    ON COLUMN dingtalk_user.org_version_uuid IS '行政组织版本UUID';
COMMENT
    ON COLUMN dingtalk_user.app_id IS '应用的App ID';
COMMENT
    ON COLUMN dingtalk_user.oa_user_id IS 'oa系统的用户ID';
COMMENT
    ON COLUMN dingtalk_user.union_id IS '用户在当前开发者企业账号范围内的唯一标识';
COMMENT
    ON COLUMN dingtalk_user.user_id IS '用户的userId';
COMMENT
    ON COLUMN dingtalk_user.name IS '用户名';
COMMENT
    ON COLUMN dingtalk_user.avatar IS '头像地址';
COMMENT
    ON COLUMN dingtalk_user.mobile IS '手机号';
COMMENT
    ON COLUMN dingtalk_user.hide_mobile IS '是否号码隐藏';
COMMENT
    ON COLUMN dingtalk_user.telephone IS '分机号';
COMMENT
    ON COLUMN dingtalk_user.job_number IS '员工工号';
COMMENT
    ON COLUMN dingtalk_user.title IS '职位';
COMMENT
    ON COLUMN dingtalk_user.email IS '邮箱';
COMMENT
    ON COLUMN dingtalk_user.leader IS '是否是部门的主管';
COMMENT
    ON COLUMN dingtalk_user.leader_dept_id IS '主管部门的部门ID';
COMMENT
    ON COLUMN dingtalk_user.leader_org_element_uuid IS '主管部门的组织元素UUID';
COMMENT
    ON COLUMN dingtalk_user.dept_id_list IS '所属部门id列表，多个以分号隔开';
COMMENT
    ON COLUMN dingtalk_user.dept_order IS '员工在部门中的排序';
COMMENT
    ON COLUMN dingtalk_user.hired_date IS '入职时间';
COMMENT
    ON COLUMN dingtalk_user.active IS '是否激活了钉钉';
COMMENT
    ON COLUMN dingtalk_user.remark IS '备注';
COMMENT
    ON COLUMN dingtalk_user.extension IS '扩展属性';
-- 添加索引以提高查询效率
CREATE INDEX idx_dingtalk_user_config_uuid ON dingtalk_user (config_uuid);
CREATE INDEX idx_dingtalk_user_org_v_uuid ON dingtalk_user (org_version_uuid);
CREATE INDEX idx_dingtalk_user_app_id ON dingtalk_user (app_id);
CREATE INDEX idx_dingtalk_user_id ON dingtalk_user (user_id);
CREATE INDEX idx_dingtalk_user_oa_user_id ON dingtalk_user (oa_user_id);


-- 钉钉组织同步日志表
CREATE TABLE dingtalk_sync_log
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
    ON TABLE dingtalk_sync_log IS '钉钉同步日志表';
COMMENT
    ON COLUMN dingtalk_sync_log.uuid IS '主键';
COMMENT
    ON COLUMN dingtalk_sync_log.create_time IS '创建时间';
COMMENT
    ON COLUMN dingtalk_sync_log.modify_time IS '更新时间';
COMMENT
    ON COLUMN dingtalk_sync_log.creator IS '创建用户';
COMMENT
    ON COLUMN dingtalk_sync_log.modifier IS '更新用户';
COMMENT
    ON COLUMN dingtalk_sync_log.rec_ver IS '版本号';
COMMENT
    ON COLUMN dingtalk_sync_log.system IS '系统标识';
COMMENT
    ON COLUMN dingtalk_sync_log.tenant IS '租户标识';
COMMENT
    ON COLUMN dingtalk_sync_log.config_uuid IS '钉钉配置信息Uuid';
COMMENT
    ON COLUMN dingtalk_sync_log.org_uuid IS '同步的组织UUID';
COMMENT
    ON COLUMN dingtalk_sync_log.org_name IS '同步的组织名称';
COMMENT
    ON COLUMN dingtalk_sync_log.org_version_uuid IS '同步的组织版本UUID';
COMMENT
    ON COLUMN dingtalk_sync_log.sync_content IS '同步内容';
COMMENT
    ON COLUMN dingtalk_sync_log.sync_type IS '同步类型：1:手动触发、2:定时任务触发';
COMMENT
    ON COLUMN dingtalk_sync_log.sync_time IS '同步时间';
COMMENT
    ON COLUMN dingtalk_sync_log.sync_status IS '同步状态：1:成功、0:失败';
COMMENT
    ON COLUMN dingtalk_sync_log.error_message IS '错误信息';
COMMENT
    ON COLUMN dingtalk_sync_log.remark IS '钉钉同步配置简要说明';

-- 钉钉同步日志明细表
CREATE TABLE dingtalk_sync_log_detail
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
    ON TABLE dingtalk_sync_log_detail IS '钉钉同步日志明细表';
COMMENT
    ON COLUMN dingtalk_sync_log_detail.uuid IS '主键';
COMMENT
    ON COLUMN dingtalk_sync_log_detail.create_time IS '创建时间';
COMMENT
    ON COLUMN dingtalk_sync_log_detail.modify_time IS '更新时间';
COMMENT
    ON COLUMN dingtalk_sync_log_detail.creator IS '创建用户';
COMMENT
    ON COLUMN dingtalk_sync_log_detail.modifier IS '更新用户';
COMMENT
    ON COLUMN dingtalk_sync_log_detail.rec_ver IS '版本号';
COMMENT
    ON COLUMN dingtalk_sync_log_detail.system IS '系统标识';
COMMENT
    ON COLUMN dingtalk_sync_log_detail.tenant IS '租户标识';
COMMENT
    ON COLUMN dingtalk_sync_log_detail.sync_log_uuid IS '同步日志uuid';
COMMENT
    ON COLUMN dingtalk_sync_log_detail.target_operation_type IS '目标操作类型：新增、修改、删除';
COMMENT
    ON COLUMN dingtalk_sync_log_detail.target_table IS '目标表：部门表：dingtalk_dept、职位表：dingtalk_job、用户表：dingtalk_user';
COMMENT
    ON COLUMN dingtalk_sync_log_detail.target_uuid IS '目标表uuid';
COMMENT
    ON COLUMN dingtalk_sync_log_detail.target_name IS '目标表uuid对应名称';
COMMENT
    ON COLUMN dingtalk_sync_log_detail.target_data IS '目标表原数据';
COMMENT
    ON COLUMN dingtalk_sync_log_detail.sync_status IS '同步状态：1:成功、0:失败';
COMMENT
    ON COLUMN dingtalk_sync_log_detail.error_message IS '错误信息';
-- 添加索引以提高查询效率
CREATE INDEX idx_dingtalk_sld_sync_log_uuid ON dingtalk_sync_log_detail (sync_log_uuid);


-- 钉钉事件表
CREATE TABLE dingtalk_event
(
    uuid            NUMBER(20, 0) PRIMARY KEY,
    create_time     TIMESTAMP,
    modify_time     TIMESTAMP,
    creator         VARCHAR2(64),
    modifier        VARCHAR2(64),
    rec_ver         NUMBER(10, 0),
    system          VARCHAR2(64),
    tenant          VARCHAR2(64),
    app_id          VARCHAR2(100),
    handle_status   NUMBER(4),
    handle_result   CLOB,
    event_id        VARCHAR2(64),
    event_type      VARCHAR2(64),
    event_type_name VARCHAR2(200),
    event_data      CLOB
);
-- 添加字段说明
COMMENT
    ON TABLE dingtalk_event IS '钉钉事件表';
COMMENT
    ON COLUMN dingtalk_event.uuid IS '主键';
COMMENT
    ON COLUMN dingtalk_event.create_time IS '创建时间';
COMMENT
    ON COLUMN dingtalk_event.modify_time IS '更新时间';
COMMENT
    ON COLUMN dingtalk_event.creator IS '创建用户';
COMMENT
    ON COLUMN dingtalk_event.modifier IS '更新用户';
COMMENT
    ON COLUMN dingtalk_event.rec_ver IS '版本号';
COMMENT
    ON COLUMN dingtalk_event.system IS '系统标识';
COMMENT
    ON COLUMN dingtalk_event.tenant IS '租户标识';
COMMENT
    ON COLUMN dingtalk_event.app_id IS '钉钉appId';
COMMENT
    ON COLUMN dingtalk_event.handle_status IS '处理状态：0:待分发，1：已分发';
COMMENT
    ON COLUMN dingtalk_event.handle_result IS '处理结果';
COMMENT
    ON COLUMN dingtalk_event.event_id IS '事件id';
COMMENT
    ON COLUMN dingtalk_event.event_type IS '事件类型';
COMMENT
    ON COLUMN dingtalk_event.event_type_name IS '事件类型名称';
COMMENT
    ON COLUMN dingtalk_event.event_data IS '事件数据';


-- 钉钉待办工作信息
CREATE TABLE DINGTALK_WORK_RECORD
(
    uuid            NUMBER(20, 0) PRIMARY KEY,
    create_time     TIMESTAMP,
    modify_time     TIMESTAMP,
    creator         VARCHAR2(64),
    modifier        VARCHAR2(64),
    rec_ver         NUMBER(10, 0),
    system          VARCHAR2(64),
    tenant          VARCHAR2(64),
    config_uuid     NUMBER(20, 0),
    app_id          VARCHAR2(100),
    title           VARCHAR2(255),
    flow_inst_uuid  VARCHAR2(50),
    task_inst_uuid  VARCHAR2(50),
    url             VARCHAR2(200),
    content         CLOB,
    oa_user_id      VARCHAR2(4000),
    user_id         VARCHAR2(4000),
    owner_id        VARCHAR2(64),
    msg_task_id     VARCHAR2(64),
    chat_id         VARCHAR2(64),
    conversation_id VARCHAR2(64),
    is_group_chat   NUMBER(1),
    type            NUMBER(4, 0),
    state           NUMBER(4, 0),
    err_msg         CLOB
);
-- 添加字段说明
COMMENT
    ON TABLE dingtalk_work_record IS '钉钉待办工作信息';
COMMENT
    ON COLUMN dingtalk_work_record.uuid IS '主键';
COMMENT
    ON COLUMN dingtalk_work_record.create_time IS '创建时间';
COMMENT
    ON COLUMN dingtalk_work_record.modify_time IS '更新时间';
COMMENT
    ON COLUMN dingtalk_work_record.creator IS '创建用户';
COMMENT
    ON COLUMN dingtalk_work_record.modifier IS '更新用户';
COMMENT
    ON COLUMN dingtalk_work_record.rec_ver IS '版本号';
COMMENT
    ON COLUMN dingtalk_work_record.system IS '系统标识';
COMMENT
    ON COLUMN dingtalk_work_record.tenant IS '租户标识';
COMMENT
    ON COLUMN dingtalk_work_record.config_uuid IS '钉钉配置信息Uuid';
COMMENT
    ON COLUMN dingtalk_work_record.app_id IS '钉钉应用的App ID';
COMMENT
    ON COLUMN dingtalk_work_record.title IS '流程标题';
COMMENT
    ON COLUMN dingtalk_work_record.flow_inst_uuid IS '流程实例UUID';
COMMENT
    ON COLUMN dingtalk_work_record.task_inst_uuid IS '环节实例UUID';
COMMENT
    ON COLUMN dingtalk_work_record.url IS '工作详情访问地址';
COMMENT
    ON COLUMN dingtalk_work_record.content IS '工作内容';
COMMENT
    ON COLUMN dingtalk_work_record.oa_user_id IS 'oa系统的用户ID';
COMMENT
    ON COLUMN dingtalk_work_record.user_id IS '钉钉用户的user_id，应用内用户的唯一标识';
COMMENT
    ON COLUMN dingtalk_work_record.owner_id IS '钉钉群主的user_id';
COMMENT
    ON COLUMN dingtalk_work_record.msg_task_id IS '消息通知推送返回的ID';
COMMENT
    ON COLUMN dingtalk_work_record.chat_id IS '消息推送返回的会话ID';
COMMENT
    ON COLUMN dingtalk_work_record.conversation_id IS '消息推送返回的对话ID';
COMMENT
    ON COLUMN dingtalk_work_record.is_group_chat IS '是否群聊';
COMMENT
    ON COLUMN dingtalk_work_record.type IS '0系统创建、1用户创建';
COMMENT
    ON COLUMN dingtalk_work_record.state IS '消息推送状态';
COMMENT
    ON COLUMN dingtalk_work_record.err_msg IS '消息推送错误信息';
-- 添加索引以提高查询效率
CREATE INDEX idx_dingtalk_wc_ti_uuid ON dingtalk_work_record (task_inst_uuid);
CREATE INDEX idx_dingtalk_wc_fi_uuid ON dingtalk_work_record (flow_inst_uuid);
CREATE INDEX idx_dingtalk_wc_msg_id ON dingtalk_work_record (msg_task_id);
CREATE INDEX idx_dingtalk_wc_chat_id ON dingtalk_work_record (chat_id);
CREATE INDEX idx_dingtalk_wc_cvt_id ON dingtalk_work_record (conversation_id);


-- 钉钉待办任务信息
CREATE TABLE DINGTALK_TODO_TASK
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
    app_id         VARCHAR2(100),
    title          VARCHAR2(255),
    flow_inst_uuid VARCHAR2(50),
    task_inst_uuid VARCHAR2(50),
    url            VARCHAR2(200),
    content        CLOB,
    oa_user_id     VARCHAR2(4000),
    user_union_id  VARCHAR2(4000),
    owner_union_id VARCHAR2(64),
    dt_task_id     VARCHAR2(64),
    state          NUMBER(4, 0),
    err_msg        CLOB
);
-- 添加字段说明
COMMENT
    ON TABLE dingtalk_todo_task IS '钉钉待办任务信息';
COMMENT
    ON COLUMN dingtalk_todo_task.uuid IS '主键';
COMMENT
    ON COLUMN dingtalk_todo_task.create_time IS '创建时间';
COMMENT
    ON COLUMN dingtalk_todo_task.modify_time IS '更新时间';
COMMENT
    ON COLUMN dingtalk_todo_task.creator IS '创建用户';
COMMENT
    ON COLUMN dingtalk_todo_task.modifier IS '更新用户';
COMMENT
    ON COLUMN dingtalk_todo_task.rec_ver IS '版本号';
COMMENT
    ON COLUMN dingtalk_todo_task.system IS '系统标识';
COMMENT
    ON COLUMN dingtalk_todo_task.tenant IS '租户标识';
COMMENT
    ON COLUMN dingtalk_todo_task.config_uuid IS '钉钉配置信息Uuid';
COMMENT
    ON COLUMN dingtalk_todo_task.app_id IS '钉钉应用的App ID';
COMMENT
    ON COLUMN dingtalk_todo_task.title IS '流程标题';
COMMENT
    ON COLUMN dingtalk_todo_task.flow_inst_uuid IS '流程实例UUID';
COMMENT
    ON COLUMN dingtalk_todo_task.task_inst_uuid IS '环节实例UUID';
COMMENT
    ON COLUMN dingtalk_todo_task.url IS '工作详情访问地址';
COMMENT
    ON COLUMN dingtalk_todo_task.content IS '工作内容';
COMMENT
    ON COLUMN dingtalk_todo_task.oa_user_id IS 'oa系统的用户ID';
COMMENT
    ON COLUMN dingtalk_todo_task.user_union_id IS '钉钉用户的union_id，应用内用户的唯一标识';
COMMENT
    ON COLUMN dingtalk_todo_task.owner_union_id IS '钉钉群主的union_id';
COMMENT
    ON COLUMN dingtalk_todo_task.dt_task_id IS '钉钉待办任务推送返回的ID';
COMMENT
    ON COLUMN dingtalk_todo_task.state IS '消息推送状态';
COMMENT
    ON COLUMN dingtalk_todo_task.err_msg IS '消息推送错误信息';
-- 添加索引以提高查询效率
CREATE INDEX idx_dingtalk_tt_ti_uuid ON dingtalk_todo_task (task_inst_uuid);
CREATE INDEX idx_dingtalk_tt_fi_uuid ON dingtalk_todo_task (flow_inst_uuid);
CREATE INDEX idx_dingtalk_tt_dt_task_id ON dingtalk_todo_task (dt_task_id);
