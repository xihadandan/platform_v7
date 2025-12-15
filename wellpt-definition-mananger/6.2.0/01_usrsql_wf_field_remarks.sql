-- 1、流程定义
-- Add comments to the columns 
comment on table WF_FLOW_DEFINITION	
  is '流程定义';
-- Add comments to the columns 
comment on column WF_FLOW_DEFINITION.uuid
  is 'UUID，系统字段';
comment on column WF_FLOW_DEFINITION.create_time
  is '创建时间';
comment on column WF_FLOW_DEFINITION.creator
  is '创建人';
comment on column WF_FLOW_DEFINITION.modifier
  is '修改人';
comment on column WF_FLOW_DEFINITION.modify_time
  is '修改时间';
comment on column WF_FLOW_DEFINITION.rec_ver
  is '版本号';
comment on column WF_FLOW_DEFINITION.category
  is '流程分类UUID';
comment on column WF_FLOW_DEFINITION.code
  is '编号';
comment on column WF_FLOW_DEFINITION.enabled
  is '是否启用';
comment on column WF_FLOW_DEFINITION.equal_flow_id
  is '等价流程定义ID';
comment on column WF_FLOW_DEFINITION.form_name
  is '对应表单名称';
comment on column WF_FLOW_DEFINITION.form_uuid
  is '对应表单UUID';
comment on column WF_FLOW_DEFINITION.freeed
  is '是否是自由流程';
comment on column WF_FLOW_DEFINITION.id
  is 'ID';
comment on column WF_FLOW_DEFINITION.name
  is '名称';
comment on column WF_FLOW_DEFINITION.flow_schema_uuid
  is '流程定义内容规划UUID';
comment on column WF_FLOW_DEFINITION.title_expression
  is '流程标题表达式';
comment on column WF_FLOW_DEFINITION.develop_json
  is '二次开发配置JSON信息';
comment on column WF_FLOW_DEFINITION.version
  is '流程版本';


-- 2、流程定义引用
-- Add comments to the columns 
comment on column WF_FLOW_DEFINITION_REF.uuid
  is 'UUID，系统字段';
comment on column WF_FLOW_DEFINITION_REF.create_time
  is '创建时间';
comment on column WF_FLOW_DEFINITION_REF.creator
  is '创建人';
comment on column WF_FLOW_DEFINITION_REF.modify_time
  is '修改时间';
comment on column WF_FLOW_DEFINITION_REF.rec_ver
  is '版本号';
comment on column WF_FLOW_DEFINITION_REF.system_unit_id
  is '系统单位ID';
comment on column WF_FLOW_DEFINITION_REF.modifier
  is '修改人';


-- 3、流程定义规划
-- Add comments to the table 
comment on table WF_FLOW_SCHEMA
  is '流程定义规划';
-- Add comments to the columns 
comment on column WF_FLOW_SCHEMA.uuid
  is 'UUID，系统字段';
comment on column WF_FLOW_SCHEMA.create_time
  is '创建时间';
comment on column WF_FLOW_SCHEMA.creator
  is '创建人';
comment on column WF_FLOW_SCHEMA.modifier
  is '修改人';
comment on column WF_FLOW_SCHEMA.modify_time
  is '修改时间';
comment on column WF_FLOW_SCHEMA.rec_ver
  is '版本号';
comment on column WF_FLOW_SCHEMA.content
  is '流程定义内容';
comment on column WF_FLOW_SCHEMA.name
  is '名称';

 
-- 4、流程定义规划日志
-- Add comments to the table 
comment on table WF_FLOW_SCHEMA_LOG
  is '流程定义规划日志';
-- Add comments to the columns 
comment on column WF_FLOW_SCHEMA_LOG.uuid
  is 'UUID，系统字段';
comment on column WF_FLOW_SCHEMA_LOG.content
  is '流程定义内容';
comment on column WF_FLOW_SCHEMA_LOG.create_time
  is '创建时间';
comment on column WF_FLOW_SCHEMA_LOG.parent_flow_schemauuid
  is '流程定义内容规划UUID';
comment on column WF_FLOW_SCHEMA_LOG.log
  is '变更内容';
comment on column WF_FLOW_SCHEMA_LOG.flow_version
  is '流程版本';


-- 5、流程分类
-- Add comments to the table 
comment on table WF_DEF_CATEGORY
  is '流程分类';
-- Add comments to the columns 
comment on column WF_DEF_CATEGORY.uuid
  is 'UUID，系统字段';
comment on column WF_DEF_CATEGORY.create_time
  is '创建时间';
comment on column WF_DEF_CATEGORY.creator
  is '创建人';
comment on column WF_DEF_CATEGORY.modifier
  is '修改人';
comment on column WF_DEF_CATEGORY.modify_time
  is '修改时间';
comment on column WF_DEF_CATEGORY.rec_ver
  is '版本号';
comment on column WF_DEF_CATEGORY.code
  is '编号';
comment on column WF_DEF_CATEGORY.name
  is '名称';
comment on column WF_DEF_CATEGORY.parent_uuid
  is '上级分类UUID';

 
-- 6、信息格式
-- Add comments to the table 
comment on table WF_DEF_FORMAT
  is '信息格式';
-- Add comments to the columns 
comment on column WF_DEF_FORMAT.uuid
  is 'UUID，系统字段';
comment on column WF_DEF_FORMAT.create_time
  is '创建时间';
comment on column WF_DEF_FORMAT.creator
  is '创建人';
comment on column WF_DEF_FORMAT.modifier
  is '修改人';
comment on column WF_DEF_FORMAT.modify_time
  is '修改时间';
comment on column WF_DEF_FORMAT.rec_ver
  is '版本号';
comment on column WF_DEF_FORMAT.code
  is '编号';
comment on column WF_DEF_FORMAT.is_clear
  is '清除HTML格式';
comment on column WF_DEF_FORMAT.name
  is '名称';
comment on column WF_DEF_FORMAT.value
  is '格式内容';


-- 7、意见分类
-- Add comments to the table 
comment on table WF_DEF_OPINION_CATEGORY
  is '意见分类';


-- 8、个人意见
-- Add comments to the table 
comment on table WF_DEF_OPINION
  is '个人意见';
-- Add comments to the columns 
comment on column WF_DEF_OPINION.content
  is '意见内容';

  
-- 9、委托设置
-- Add comments to the table 
comment on table WF_FLOW_DELEGATION_SETTINGS
  is '委托设置';


-- 10、流程督办、监控、查阅权限管理
-- Add comments to the table 
comment on table WF_FLOW_MANAGEMENT
  is '流程督办、监控、查阅权限管理';
-- Add comments to the columns 
comment on column WF_FLOW_MANAGEMENT.uuid
  is 'UUID，系统字段';
comment on column WF_FLOW_MANAGEMENT.create_time
  is '创建时间';
comment on column WF_FLOW_MANAGEMENT.creator
  is '创建人';
comment on column WF_FLOW_MANAGEMENT.modifier
  is '修改人';
comment on column WF_FLOW_MANAGEMENT.modify_time
  is '修改时间';
comment on column WF_FLOW_MANAGEMENT.rec_ver
  is '版本号';
comment on column WF_FLOW_MANAGEMENT.flow_def_uuid
  is '流程定义UUID';
comment on column WF_FLOW_MANAGEMENT.org_id
  is '组织机构ID';
comment on column WF_FLOW_MANAGEMENT.type
  is '管理权限 1监控人，2督办人，3阅读人';

  
-- 11、流程实例
-- Add comments to the table 
comment on table WF_FLOW_INSTANCE
  is '流程实例';
-- Add comments to the columns 
comment on column WF_FLOW_INSTANCE.uuid
  is 'UUID，系统字段';
comment on column WF_FLOW_INSTANCE.create_time
  is '创建时间';
comment on column WF_FLOW_INSTANCE.creator
  is '创建人';
comment on column WF_FLOW_INSTANCE.modifier
  is '修改人';
comment on column WF_FLOW_INSTANCE.modify_time
  is '修改时间';
comment on column WF_FLOW_INSTANCE.rec_ver
  is '版本号';
comment on column WF_FLOW_INSTANCE.alarm_time
  is '预警时间';
comment on column WF_FLOW_INSTANCE.data_uuid
  is '表单数据UUID';
comment on column WF_FLOW_INSTANCE.due_time
  is '到期时间、承诺时间';
comment on column WF_FLOW_INSTANCE.duration
  is '流程经历的总时间，以毫秒为单位';
comment on column WF_FLOW_INSTANCE.end_time
  is '结束时间';
comment on column WF_FLOW_INSTANCE.form_uuid
  is '表单定义UUID';
comment on column WF_FLOW_INSTANCE.id
  is '流程定义ID';
comment on column WF_FLOW_INSTANCE.is_active
  is '当前流程是否处理活动状态';
comment on column WF_FLOW_INSTANCE.name
  is '流程实例名称';
comment on column WF_FLOW_INSTANCE.owner_department_id
  is '流程所属部门ID';
comment on column WF_FLOW_INSTANCE.owner_unit_id
  is '流程所属单位ID';
comment on column WF_FLOW_INSTANCE.reserved_date1
  is '预留时间字段1';
comment on column WF_FLOW_INSTANCE.reserved_date2
  is '预留时间字段2';
comment on column WF_FLOW_INSTANCE.reserved_number1
  is '预留数字字段1';
comment on column WF_FLOW_INSTANCE.reserved_number2
  is '预留数字字段2';
comment on column WF_FLOW_INSTANCE.reserved_number3
  is '预留数字字段3';
comment on column WF_FLOW_INSTANCE.reserved_text1
  is '预留文本字段1';
comment on column WF_FLOW_INSTANCE.reserved_text10
  is '预留文本字段10';
comment on column WF_FLOW_INSTANCE.reserved_text2
  is '预留文本字段2';
comment on column WF_FLOW_INSTANCE.reserved_text3
  is '预留文本字段3';
comment on column WF_FLOW_INSTANCE.reserved_text4
  is '预留文本字段4';
comment on column WF_FLOW_INSTANCE.reserved_text5
  is '预留文本字段5';
comment on column WF_FLOW_INSTANCE.reserved_text6
  is '预留文本字段6';
comment on column WF_FLOW_INSTANCE.reserved_text7
  is '预留文本字段7';
comment on column WF_FLOW_INSTANCE.reserved_text8
  is '预留文本字段8';
comment on column WF_FLOW_INSTANCE.reserved_text9
  is '预留文本字段9';
comment on column WF_FLOW_INSTANCE.start_department_id
  is '流程发起部门ID';
comment on column WF_FLOW_INSTANCE.start_time
  is '开始时间';
comment on column WF_FLOW_INSTANCE.start_unit_id
  is '流程发起单位ID';
comment on column WF_FLOW_INSTANCE.start_user_id
  is '流程启动者ID';
comment on column WF_FLOW_INSTANCE.title
  is '标题';
comment on column WF_FLOW_INSTANCE.flow_def_uuid
  is '流程定义UUID';
comment on column WF_FLOW_INSTANCE.parent_flow_inst_uuid
  is '上级流程实例UUID';
comment on column WF_FLOW_INSTANCE.reserved_text11
  is '预留文本字段11';
comment on column WF_FLOW_INSTANCE.reserved_text12
  is '预留文本字段12';
comment on column WF_FLOW_INSTANCE.owner_id
  is '流程实例所有者ID';
comment on column WF_FLOW_INSTANCE.is_timing
  is '是否计时';
comment on column WF_FLOW_INSTANCE.timing_state
  is '计时状态(0正常、1预警、2到期、3逾期)';
comment on column WF_FLOW_INSTANCE.is_over_due
  is '是否逾期';
comment on column WF_FLOW_INSTANCE.system_unit_id
  is '系统单位ID';

  
-- 12、流程实例参数
-- Add comments to the table 
comment on table WF_FLOW_INSTANCE_PARAM
  is '流程实例参数';
-- Add comments to the columns 
comment on column WF_FLOW_INSTANCE_PARAM.uuid
  is 'UUID，系统字段';
comment on column WF_FLOW_INSTANCE_PARAM.flow_inst_uuid
  is '流程实例UUID';
comment on column WF_FLOW_INSTANCE_PARAM.name
  is '参数名';
comment on column WF_FLOW_INSTANCE_PARAM.value
  is '参数值';
comment on column WF_FLOW_INSTANCE_PARAM.creator
  is '创建人';
comment on column WF_FLOW_INSTANCE_PARAM.create_time
  is '创建时间';
comment on column WF_FLOW_INSTANCE_PARAM.modifier
  is '修改人';
comment on column WF_FLOW_INSTANCE_PARAM.modify_time
  is '修改时间';
comment on column WF_FLOW_INSTANCE_PARAM.rec_ver
  is '版本号';


-- 13、环节实例
-- Add comments to the table 
comment on table WF_TASK_INSTANCE
  is '环节实例';
-- Add comments to the columns 
comment on column WF_TASK_INSTANCE.uuid
  is 'UUID，系统字段';
comment on column WF_TASK_INSTANCE.create_time
  is '创建时间';
comment on column WF_TASK_INSTANCE.creator
  is '创建人';
comment on column WF_TASK_INSTANCE.modifier
  is '修改人';
comment on column WF_TASK_INSTANCE.modify_time
  is '修改时间';
comment on column WF_TASK_INSTANCE.rec_ver
  is '版本号';
comment on column WF_TASK_INSTANCE.action
  is '操作动作，对应WorkFlowOperation类型名称';
comment on column WF_TASK_INSTANCE.action_type
  is '操作动作类型，对应WorkFlowOperation类型';
comment on column WF_TASK_INSTANCE.alarm_time
  is '预警时间';
comment on column WF_TASK_INSTANCE.assignee
  is '前办理人ID';
comment on column WF_TASK_INSTANCE.data_uuid
  is '表单数据UUID';
comment on column WF_TASK_INSTANCE.due_time
  is '到期时间、承诺时间';
comment on column WF_TASK_INSTANCE.duration
  is '持续时间';
comment on column WF_TASK_INSTANCE.end_time
  is '结束时间';
comment on column WF_TASK_INSTANCE.form_uuid
  is '表单定义UUID';
comment on column WF_TASK_INSTANCE.id
  is '环节ID';
comment on column WF_TASK_INSTANCE.name
  is '环节名称';
comment on column WF_TASK_INSTANCE.owner
  is '环节所有者';
comment on column WF_TASK_INSTANCE.serial_no
  is '流水号';
comment on column WF_TASK_INSTANCE.start_time
  is '开始时间';
comment on column WF_TASK_INSTANCE.suspension_state
  is '挂起状态(0正常、1挂起、2删除)';
comment on column WF_TASK_INSTANCE.type
  is '环节类型1普通环节、2子流程环节';
comment on column WF_TASK_INSTANCE.flow_def_uuid
  is '流程定义UUID';
comment on column WF_TASK_INSTANCE.flow_inst_uuid
  is '流程实例UUID';
comment on column WF_TASK_INSTANCE.parent_task_inst_uuid
  is '上级环节实例UUID';
comment on column WF_TASK_INSTANCE.is_parallel
  is '是否并行任务';
comment on column WF_TASK_INSTANCE.parallel_task_inst_uuid
  is '发起并行任务的任务UUID';
comment on column WF_TASK_INSTANCE.todo_user_id
  is '任务待办人员ID，多个以";"隔开';
comment on column WF_TASK_INSTANCE.todo_user_name
  is '任务待办人员名称，多个以";"隔开';
comment on column WF_TASK_INSTANCE.alarm_state
  is '预警状态(0未预警、1预警中)';
comment on column WF_TASK_INSTANCE.over_due_state
  is '逾期状态(0未逾期、1逾期中)';
comment on column WF_TASK_INSTANCE.timing_state
  is '计时状态(0正常、1预警、2到期、3逾期)';

 
-- 14、环节实例置顶
-- Add comments to the table 
comment on table WF_TASK_INSTANCE_TOPPING
  is '环节实例置顶';
-- Add comments to the columns 
comment on column WF_TASK_INSTANCE_TOPPING.uuid
  is 'UUID，系统字段';
comment on column WF_TASK_INSTANCE_TOPPING.create_time
  is '创建时间';
comment on column WF_TASK_INSTANCE_TOPPING.creator
  is '创建人';
comment on column WF_TASK_INSTANCE_TOPPING.modifier
  is '修改人';
comment on column WF_TASK_INSTANCE_TOPPING.modify_time
  is '修改时间';
comment on column WF_TASK_INSTANCE_TOPPING.rec_ver
  is '版本号';
comment on column WF_TASK_INSTANCE_TOPPING.user_id
  is '用户ID';
comment on column WF_TASK_INSTANCE_TOPPING.task_inst_uuid
  is '环节实例UUID';
comment on column WF_TASK_INSTANCE_TOPPING.is_topping
  is '是否置顶';

 
-- 15、环节流转
-- Add comments to the table 
comment on table WF_TASK_ACTIVITY
  is '环节流转';
-- Add comments to the columns 
comment on column WF_TASK_ACTIVITY.uuid
  is 'UUID，系统字段';
comment on column WF_TASK_ACTIVITY.create_time
  is '创建时间';
comment on column WF_TASK_ACTIVITY.creator
  is '创建人';
comment on column WF_TASK_ACTIVITY.modifier
  is '修改人';
comment on column WF_TASK_ACTIVITY.modify_time
  is '修改时间';
comment on column WF_TASK_ACTIVITY.rec_ver
  is '版本号';
comment on column WF_TASK_ACTIVITY.end_time
  is '开始时间';
comment on column WF_TASK_ACTIVITY.flow_inst_uuid
  is '流程实例UUID';
comment on column WF_TASK_ACTIVITY.pre_task_id
  is '前一环节ID';
comment on column WF_TASK_ACTIVITY.pre_task_inst_uuid
  is '前一环节实例UUID';
comment on column WF_TASK_ACTIVITY.start_time
  is '开始时间';
comment on column WF_TASK_ACTIVITY.task_id
  is '所在环节ID';
comment on column WF_TASK_ACTIVITY.task_inst_uuid
  is '所在环节实例UUID';
comment on column WF_TASK_ACTIVITY.transfer_code
  is '环节流转类型，枚举类TransferCode';

  
-- 16、环节操作
-- Add comments to the table 
comment on table WF_TASK_OPERATION
  is '环节操作';
-- Add comments to the columns 
comment on column WF_TASK_OPERATION.uuid
  is 'UUID，系统字段';
comment on column WF_TASK_OPERATION.create_time
  is '创建时间';
comment on column WF_TASK_OPERATION.creator
  is '创建人';
comment on column WF_TASK_OPERATION.modifier
  is '修改人';
comment on column WF_TASK_OPERATION.modify_time
  is '修改时间';
comment on column WF_TASK_OPERATION.rec_ver
  is '版本号';
comment on column WF_TASK_OPERATION.action
  is '操作动作，对应WorkFlowOperation类型名称';
comment on column WF_TASK_OPERATION.action_type
  is '操作类型，对应WorkFlowOperation类型';
comment on column WF_TASK_OPERATION.assignee
  is '操作人ID';
comment on column WF_TASK_OPERATION.copy_user_id
  is '抄送人ID';
comment on column WF_TASK_OPERATION.flow_inst_uuid
  is '所在流程实例UUID';
comment on column WF_TASK_OPERATION.opinion_label
  is '办理意见立场文本';
comment on column WF_TASK_OPERATION.opinion_value
  is '办理意见立场';
comment on column WF_TASK_OPERATION.task_id
  is '所在环节ID';
comment on column WF_TASK_OPERATION.task_inst_uuid
  is '所在环节实例UUID';
comment on column WF_TASK_OPERATION.user_id
  is '主送人ID';
comment on column WF_TASK_OPERATION.task_name
  is '所在环节名称';
comment on column WF_TASK_OPERATION.extra_info
  is '附加信息';
comment on column WF_TASK_OPERATION.action_code
  is '操作代码，枚举类ActionCode';
comment on column WF_TASK_OPERATION.assignee_name
  is '操作人名称';
comment on column WF_TASK_OPERATION.task_identity_uuid
  is '所在待办实体UUID';
comment on column WF_TASK_OPERATION.opinion_text
  is '办理意见内容';

  
-- 17、环节待办标识
-- Add comments to the table 
comment on table WF_TASK_IDENTITY
  is '环节待办标识';
-- Add comments to the columns 
comment on column WF_TASK_IDENTITY.uuid
  is 'UUID，系统字段';
comment on column WF_TASK_IDENTITY.create_time
  is '创建时间';
comment on column WF_TASK_IDENTITY.creator
  is '创建人';
comment on column WF_TASK_IDENTITY.modifier
  is '修改人';
comment on column WF_TASK_IDENTITY.modify_time
  is '修改时间';
comment on column WF_TASK_IDENTITY.rec_ver
  is '版本号';
comment on column WF_TASK_IDENTITY.sort_order
  is '按人员顺序依次办理';
comment on column WF_TASK_IDENTITY.user_id
  is '用户ID';
comment on column WF_TASK_IDENTITY.task_inst_uuid
  is '环节实例UUID';
comment on column WF_TASK_IDENTITY.suspension_state
  is '挂起状态(0正常、1挂起、2删除)';
comment on column WF_TASK_IDENTITY.todo_type
  is '办理类型工作待办(1)、会签待办(2)、转办待办(3)、移交待办(4)、委托待办(5)';
comment on column WF_TASK_IDENTITY.source_task_identity_uuid
  is '源待办标识UUID';
comment on column WF_TASK_IDENTITY.owner_id
  is '环节办理所有者ID(工作委托人ID)';
comment on column WF_TASK_IDENTITY.related_task_operation_uuid
  is '相关联的操作记录UUID';

  
-- 18、环节委托
-- Add comments to the table 
comment on table WF_TASK_DELEGATION
  is '环节委托';

  
-- 19、计时器
-- Add comments to the table 
comment on table WF_TASK_TIMER
  is '计时器';
-- Add comments to the columns 
comment on column WF_TASK_TIMER.uuid
  is 'UUID，系统字段';
comment on column WF_TASK_TIMER.create_time
  is '创建时间';
comment on column WF_TASK_TIMER.creator
  is '创建人';
comment on column WF_TASK_TIMER.modifier
  is '修改人';
comment on column WF_TASK_TIMER.modify_time
  is '修改时间';
comment on column WF_TASK_TIMER.rec_ver
  is '版本号';
comment on column WF_TASK_TIMER.affect_main_flow
  is '计时暂停/恢复影响主流程';
comment on column WF_TASK_TIMER.alarm_done
  is '预警提醒是否已经提醒过';
comment on column WF_TASK_TIMER.alarm_flow_id
  is '预警提醒发起的流程ID';
comment on column WF_TASK_TIMER.alarm_flow_started
  is '预警提醒流程已发起';
comment on column WF_TASK_TIMER.alarm_frequency
  is '预警提醒总次数';
comment on column WF_TASK_TIMER.alarm_repeat_interval
  is '预警提醒时间间隔';
comment on column WF_TASK_TIMER.alarm_time
  is '提醒时间';
comment on column WF_TASK_TIMER.alarm_unit
  is '提醒单位';
comment on column WF_TASK_TIMER.due_action
  is '逾期处理动作，对应TimerDueAction中的代码';
comment on column WF_TASK_TIMER.due_doing_done
  is '逾期处理是否已经处理过';
comment on column WF_TASK_TIMER.due_flow_id
  is '逾期处理发起的流程ID';
comment on column WF_TASK_TIMER.due_flow_started
  is '逾期处理流程已发起';
comment on column WF_TASK_TIMER.due_frequency
  is '逾期处理总次数';
comment on column WF_TASK_TIMER.due_time
  is '逾期时间';
comment on column WF_TASK_TIMER.due_to_task_id
  is '逾期自动进入下一个办理环节ID';
comment on column WF_TASK_TIMER.due_unit
  is '逾期处理时间单位';
comment on column WF_TASK_TIMER.enable_alarm
  is '是否启用预警提醒';
comment on column WF_TASK_TIMER.enable_due_doing
  is '是否启用逾期处理';
comment on column WF_TASK_TIMER.flow_inst_uuid
  is '流程实例UUID';
comment on column WF_TASK_TIMER.last_start_time
  is '最新开始时间';
comment on column WF_TASK_TIMER.limit_time1
  is '办理时限数字、或日期';
comment on column WF_TASK_TIMER.limit_time2
  is '办理时限动态表单字段';
comment on column WF_TASK_TIMER.limit_time_type
  is '办理时限类型1数字、2表单字段、3指定日期';
comment on column WF_TASK_TIMER.limit_unit
  is '办理时限单位';
comment on column WF_TASK_TIMER.name
  is '名称';
comment on column WF_TASK_TIMER.start_time
  is '开始计时时间点';
comment on column WF_TASK_TIMER.status
  is '计时器是运行状态(0未启动、1已启动、2暂停、3结束)';
comment on column WF_TASK_TIMER.task_alarm_time
  is '环节预警时间';
comment on column WF_TASK_TIMER.task_due_time
  is '环节到期时间';
comment on column WF_TASK_TIMER.task_id
  is '当前环节ID';
comment on column WF_TASK_TIMER.task_ids
  is '计时环节ID';
comment on column WF_TASK_TIMER.task_inst_uuid
  is '当前环节实例UUID';
comment on column WF_TASK_TIMER.task_limit_time
  is '流程最新计算后的办理时限数字';
comment on column WF_TASK_TIMER.tenant_id
  is '租户ID';
comment on column WF_TASK_TIMER.listener
  is '事件监听';
comment on column WF_TASK_TIMER.task_init_limit_time
  is '流程计算后初始化的办理时限数字';
comment on column WF_TASK_TIMER.alarm_state
  is '预警状态(0未预警、1预警中)';
comment on column WF_TASK_TIMER.over_due_state
  is '逾期状态(0未逾期、1逾期中)';

  
-- 20、计时器预警、逾期处理办理人
-- Add comments to the table 
comment on table WF_TASK_TIMER_USER
  is '计时器预警、逾期处理办理人';
-- Add comments to the columns 
comment on column WF_TASK_TIMER_USER.uuid
  is 'UUID，系统字段';
comment on column WF_TASK_TIMER_USER.create_time
  is '创建时间';
comment on column WF_TASK_TIMER_USER.creator
  is '创建人';
comment on column WF_TASK_TIMER_USER.modifier
  is '修改人';
comment on column WF_TASK_TIMER_USER.modify_time
  is '修改时间';
comment on column WF_TASK_TIMER_USER.rec_ver
  is '版本号';
comment on column WF_TASK_TIMER_USER.arg_value
  is 'unit用户参数值';
comment on column WF_TASK_TIMER_USER.type
  is 'unit用户类型';
comment on column WF_TASK_TIMER_USER.user_type
  is '用户类型(责任人、预警提醒人、预警流程办理人、逾期处理人、逾期处理流程办理人)';
comment on column WF_TASK_TIMER_USER.value
  is 'unit用户值';
comment on column WF_TASK_TIMER_USER.task_timer_uuid
  is '定时器UUID';

  
-- 21、计时器日志
-- Add comments to the table 
comment on table WF_TASK_TIMER_LOG
  is '计时器日志';
-- Add comments to the columns 
comment on column WF_TASK_TIMER_LOG.uuid
  is 'UUID，系统字段';
comment on column WF_TASK_TIMER_LOG.create_time
  is '创建时间';
comment on column WF_TASK_TIMER_LOG.creator
  is '创建人';
comment on column WF_TASK_TIMER_LOG.modifier
  is '修改人';
comment on column WF_TASK_TIMER_LOG.modify_time
  is '修改时间';
comment on column WF_TASK_TIMER_LOG.rec_ver
  is '版本号';
comment on column WF_TASK_TIMER_LOG.flow_inst_uuid
  is '流程实例UUID';
comment on column WF_TASK_TIMER_LOG.log_time
  is '记录时间';
comment on column WF_TASK_TIMER_LOG.task_inst_uuid
  is '环节实例UUID';
comment on column WF_TASK_TIMER_LOG.type
  is '记录类型启动START、暂停PAUSE、重启RESUME、结束END、预警ALARM、到期DUE_DOING、逾期OVER_DUE、强制终止预警FORCE_STOP_ALARM、强制终止到期处理FORCE_STOP_DUE_DOING、信息INFO、错误ERROR';
comment on column WF_TASK_TIMER_LOG.task_timer_uuid
  is '计时器UUID';

  
-- 22、分支流
-- Add comments to the table 
comment on table WF_TASK_BRANCH
  is '分支流';

  
-- 23、子流程
-- Add comments to the table 
comment on table WF_TASK_SUB_FLOW
  is '子流程';
-- Add comments to the columns 
comment on column WF_TASK_SUB_FLOW.uuid
  is 'UUID，系统字段';
comment on column WF_TASK_SUB_FLOW.create_time
  is '创建时间';
comment on column WF_TASK_SUB_FLOW.creator
  is '创建人';
comment on column WF_TASK_SUB_FLOW.modifier
  is '修改人';
comment on column WF_TASK_SUB_FLOW.modify_time
  is '修改时间';
comment on column WF_TASK_SUB_FLOW.rec_ver
  is '版本号';
comment on column WF_TASK_SUB_FLOW.completed
  is '完成状态 0运行中、1正常结束、2终止、3撤销、4退回主流程';
comment on column WF_TASK_SUB_FLOW.copy_fields
  is '复制字段';
comment on column WF_TASK_SUB_FLOW.flow_id
  is '子流程ID';
comment on column WF_TASK_SUB_FLOW.flow_inst_uuid
  is '子流程实例UUID';
comment on column WF_TASK_SUB_FLOW.is_merge
  is '是否合并';
comment on column WF_TASK_SUB_FLOW.is_share
  is '是否共享';
comment on column WF_TASK_SUB_FLOW.is_wait
  is '是否等待';
comment on column WF_TASK_SUB_FLOW.notify_doing
  is '办结通知其他子流程在办人员';
comment on column WF_TASK_SUB_FLOW.parent_flow_inst_uuid
  is '父流程实例UUID';
comment on column WF_TASK_SUB_FLOW.parent_task_id
  is '父流程任务ID';
comment on column WF_TASK_SUB_FLOW.parent_task_inst_uuid
  is '父环节实例UUID';
comment on column WF_TASK_SUB_FLOW.return_addition_fields
  is '返回附加的字段';
comment on column WF_TASK_SUB_FLOW.return_override_fields
  is '返回复盖的字段';
comment on column WF_TASK_SUB_FLOW.sort_order
  is '排序号';
comment on column WF_TASK_SUB_FLOW.todo_name
  is '办理对象名称';
comment on column WF_TASK_SUB_FLOW.todo_id
  is '办理对象ID';


-- 24、子流程前后置关系
-- Add comments to the table 
comment on table WF_TASK_SUB_FLOW_RELATION
  is '子流程前后置关系';
-- Add comments to the columns 
comment on column WF_TASK_SUB_FLOW_RELATION.uuid
  is 'UUID，系统字段';
comment on column WF_TASK_SUB_FLOW_RELATION.create_time
  is '创建时间';
comment on column WF_TASK_SUB_FLOW_RELATION.creator
  is '创建人';
comment on column WF_TASK_SUB_FLOW_RELATION.modifier
  is '修改人';
comment on column WF_TASK_SUB_FLOW_RELATION.modify_time
  is '修改时间';
comment on column WF_TASK_SUB_FLOW_RELATION.rec_ver
  is '版本号';
comment on column WF_TASK_SUB_FLOW_RELATION.allow_submit
  is '是否允许后置环节提交，如果允许后置环节不用等待前置环节提交便可提交';
comment on column WF_TASK_SUB_FLOW_RELATION.front_new_flow_id
  is '前置子流程ID';
comment on column WF_TASK_SUB_FLOW_RELATION.front_new_flow_inst_uuid
  is '前置子流程实例UUID';
comment on column WF_TASK_SUB_FLOW_RELATION.front_new_flow_name
  is '前置子流程名称';
comment on column WF_TASK_SUB_FLOW_RELATION.front_task_id
  is '前置环节ID';
comment on column WF_TASK_SUB_FLOW_RELATION.front_task_name
  is '前置环节名称';
comment on column WF_TASK_SUB_FLOW_RELATION.new_flow_id
  is '子流程ID';
comment on column WF_TASK_SUB_FLOW_RELATION.new_flow_inst_uuid
  is '子流程实例UUID';
comment on column WF_TASK_SUB_FLOW_RELATION.new_flow_name
  is '子流程名称';
comment on column WF_TASK_SUB_FLOW_RELATION.submit_status
  is '前置流程环节提交状态，(1已提交、0未提交)';
comment on column WF_TASK_SUB_FLOW_RELATION.task_id
  is '环节ID';
comment on column WF_TASK_SUB_FLOW_RELATION.task_name
  is '环节名称';
comment on column WF_TASK_SUB_FLOW_RELATION.task_sub_flow_uuid
  is '子流程关系所属的子流程UUID';

  
-- 25、信息分发
-- Add comments to the table 
comment on table WF_TASK_INFO_DISTRIBUTION
  is '信息分发';

  
-- 26、流程表单意见
-- Add comments to the table 
comment on table WF_TASK_FORM_OPINION
  is '流程表单意见';
-- Add comments to the columns 
comment on column WF_TASK_FORM_OPINION.uuid
  is 'UUID，系统字段';
comment on column WF_TASK_FORM_OPINION.create_time
  is '创建时间';
comment on column WF_TASK_FORM_OPINION.creator
  is '创建人';
comment on column WF_TASK_FORM_OPINION.modifier
  is '修改人';
comment on column WF_TASK_FORM_OPINION.modify_time
  is '修改时间';
comment on column WF_TASK_FORM_OPINION.rec_ver
  is '版本号';
comment on column WF_TASK_FORM_OPINION.data_uuid
  is '表单数据UUID';
comment on column WF_TASK_FORM_OPINION.field_name
  is '表单意见域字段名';
comment on column WF_TASK_FORM_OPINION.flow_inst_uuid
  is '流程实例UUID';
comment on column WF_TASK_FORM_OPINION.error_data
  is '是否错误数据';
comment on column WF_TASK_FORM_OPINION.content
  is '表单意见域内容';

  
-- 27、流程表单意见日志
-- Add comments to the table 
comment on table WF_TASK_FORM_OPINION_LOG
  is '流程表单意见日志';
-- Add comments to the columns 
comment on column WF_TASK_FORM_OPINION_LOG.uuid
  is 'UUID，系统字段';
comment on column WF_TASK_FORM_OPINION_LOG.create_time
  is '创建时间';
comment on column WF_TASK_FORM_OPINION_LOG.creator
  is '创建人';
comment on column WF_TASK_FORM_OPINION_LOG.modifier
  is '修改人';
comment on column WF_TASK_FORM_OPINION_LOG.modify_time
  is '修改时间';
comment on column WF_TASK_FORM_OPINION_LOG.rec_ver
  is '版本号';
comment on column WF_TASK_FORM_OPINION_LOG.flow_inst_uuid
  is '流程实例UUID';
comment on column WF_TASK_FORM_OPINION_LOG.task_inst_uuid
  is '环节实例UUID';
comment on column WF_TASK_FORM_OPINION_LOG.task_operation_uuid
  is '环节操作UUID';
comment on column WF_TASK_FORM_OPINION_LOG.task_form_opinion_uuid
  is '意见记录UUID';
comment on column WF_TASK_FORM_OPINION_LOG.field_name
  is '表单意见域字段名';
comment on column WF_TASK_FORM_OPINION_LOG.record_way
  is '记录方式(1不替换、2替换原值、3附加)';
comment on column WF_TASK_FORM_OPINION_LOG.content
  is '表单意见域内容';

  
-- 28、流程表单附件
-- Add comments to the table 
comment on table WF_TASK_FORM_ATTACHMENT
  is '流程表单附件';

  
-- 29、流程表单附件日志
-- Add comments to the table 
comment on table WF_TASK_FORM_ATTACHMENT_LOG
  is '流程表单附件日志';
-- Add comments to the columns 
comment on column WF_TASK_FORM_ATTACHMENT_LOG.operate_type
  is '操作类型，枚举类EnumOperateType';

  
-- 30、流程删除日志
-- Add comments to the table 
comment on table WF_TASK_DELETE_LOG
  is '流程删除日志';
-- Add comments to the columns 
comment on column WF_TASK_DELETE_LOG.uuid
  is 'UUID，系统字段';
comment on column WF_TASK_DELETE_LOG.create_time
  is '创建时间';
comment on column WF_TASK_DELETE_LOG.creator
  is '创建人';
comment on column WF_TASK_DELETE_LOG.modifier
  is '修改人';
comment on column WF_TASK_DELETE_LOG.modify_time
  is '修改时间';
comment on column WF_TASK_DELETE_LOG.rec_ver
  is '版本号';
comment on column WF_TASK_DELETE_LOG.name
  is '流程名称';
comment on column WF_TASK_DELETE_LOG.title
  is '流程实例标题 ';
comment on column WF_TASK_DELETE_LOG.serial_no
  is '流水号';
comment on column WF_TASK_DELETE_LOG.task_name
  is '流程环节';
comment on column WF_TASK_DELETE_LOG.operator
  is '操作人ID';
comment on column WF_TASK_DELETE_LOG.operator_name
  is '操作人名称';
comment on column WF_TASK_DELETE_LOG.operation
  is '操作类型Delete';
comment on column WF_TASK_DELETE_LOG.operate_time
  is '操作时间';
comment on column WF_TASK_DELETE_LOG.content
  is '删除的数据内容';
comment on column WF_TASK_DELETE_LOG.remark
  is '备注';

  