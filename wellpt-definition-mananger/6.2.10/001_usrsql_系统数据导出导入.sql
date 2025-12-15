-- Create table
create table DATA_EXPORT_RECORD
(
  UUID              VARCHAR2(255) not null,
  CREATE_TIME       TIMESTAMP(6),
  CREATOR           VARCHAR2(255),
  MODIFIER          VARCHAR2(255),
  MODIFY_TIME       TIMESTAMP(6),
  REC_VER           NUMBER(10),
  DATA_TYPE         VARCHAR2(255),
  DATA_TYPE_JSON    VARCHAR2(2000),
  SYSTEM_UNIT_IDS   VARCHAR2(2000),
  SYSTEM_UNIT_NAMES VARCHAR2(2000),
  EXPORT_PATH       VARCHAR2(2000),
  BATCH_QUANTITY    NUMBER,
  OPERATOR          VARCHAR2(255),
  EXPORT_TIME       TIMESTAMP(6),
  constraint PK_DATA_EXPORT_RECORD primary key (UUID)
);

-- Add comments to the table
comment on table DATA_EXPORT_RECORD
  is '数据导出记录表';
-- Add comments to the columns
comment on column DATA_EXPORT_RECORD.UUID
  is '主键uuid';
comment on column DATA_EXPORT_RECORD.CREATE_TIME
  is '创建时间';
comment on column DATA_EXPORT_RECORD.CREATOR
  is '创建人';
comment on column DATA_EXPORT_RECORD.MODIFIER
  is '更新人';
comment on column DATA_EXPORT_RECORD.MODIFY_TIME
  is '更新时间';
comment on column DATA_EXPORT_RECORD.REC_VER
  is '数据版本';
comment on column DATA_EXPORT_RECORD.DATA_TYPE
  is '导出数据类型，多个用逗号隔开';
comment on column DATA_EXPORT_RECORD.DATA_TYPE_JSON
  is '导出数据类型的详细JSON串，用于表示类型下的哪些子类型需要导出。Eg：{"org_data":{"version":"1","user":"1","group":"1","type":"1","duty":"1","rank":"1"},"flow_data":"1","email_data":{"email":{"receivce":"1","send":"1","draft":"0","recovery":"0"},"folder":"1","contact_group":"1","contact":"1","tag":"1","paper":"1"}}';
comment on column DATA_EXPORT_RECORD.SYSTEM_UNIT_IDS
  is '数据归属单位ID，多个用逗号隔开';
comment on column DATA_EXPORT_RECORD.SYSTEM_UNIT_NAMES
  is '数据归属单位名称，多个用逗号隔开';
comment on column DATA_EXPORT_RECORD.EXPORT_PATH
  is '导出文件路径';
comment on column DATA_EXPORT_RECORD.BATCH_QUANTITY
  is '导出批次数量';
comment on column DATA_EXPORT_RECORD.OPERATOR
  is '操作人名称';
comment on column DATA_EXPORT_RECORD.EXPORT_TIME
  is '导出时间';

-- Create table
create table DATA_EXPORT_TASK
(
  UUID              VARCHAR2(255) not null,
  CREATE_TIME       TIMESTAMP(6),
  CREATOR           VARCHAR2(255),
  MODIFIER          VARCHAR2(255),
  MODIFY_TIME       TIMESTAMP(6),
  REC_VER           NUMBER(10),
  DATA_TYPE         VARCHAR2(255),
  SYSTEM_UNIT_IDS   VARCHAR2(2000),
  SYSTEM_UNIT_NAMES VARCHAR2(2000),
  EXPORT_PATH       VARCHAR2(2000),
  TASK_STATUS       NUMBER,
  TASK_STATUS_CN    VARCHAR2(255),
  PROGRESS          NUMBER,
  DATA_TOTAL        NUMBER,
  OPERATOR          VARCHAR2(255),
  EXPORT_TIME       TIMESTAMP(6),
  FINISH_TIME       TIMESTAMP(6),
  RECORD_UUID       VARCHAR2(255),
  constraint PK_DATA_EXPORT_TASK primary key (UUID)
);

-- Add comments to the table
comment on table DATA_EXPORT_TASK
  is '数据导出任务';
-- Add comments to the columns
comment on column DATA_EXPORT_TASK.UUID
  is '主键uuid';
comment on column DATA_EXPORT_TASK.CREATE_TIME
  is '创建时间';
comment on column DATA_EXPORT_TASK.CREATOR
  is '创建人';
comment on column DATA_EXPORT_TASK.MODIFIER
  is '更新人';
comment on column DATA_EXPORT_TASK.MODIFY_TIME
  is '更新时间';
comment on column DATA_EXPORT_TASK.REC_VER
  is '数据版本';
comment on column DATA_EXPORT_TASK.DATA_TYPE
  is '导出数据类型';
comment on column DATA_EXPORT_TASK.SYSTEM_UNIT_IDS
  is '数据归属单位ID，多个用逗号隔开';
comment on column DATA_EXPORT_TASK.SYSTEM_UNIT_NAMES
  is '数据归属单位名称，多个用逗号隔开';
comment on column DATA_EXPORT_TASK.EXPORT_PATH
  is '导出文件路径';
comment on column DATA_EXPORT_TASK.TASK_STATUS
  is '任务状态0：取消，1：完成，2：导出中，3：异常终止';
comment on column DATA_EXPORT_TASK.TASK_STATUS_CN
  is '任务状态中文';
comment on column DATA_EXPORT_TASK.PROGRESS
  is '导出进度';
comment on column DATA_EXPORT_TASK.DATA_TOTAL
  is '任务总数据量';
comment on column DATA_EXPORT_TASK.OPERATOR
  is '操作人名称';
comment on column DATA_EXPORT_TASK.EXPORT_TIME
  is '导出时间';
comment on column DATA_EXPORT_TASK.FINISH_TIME
  is '完成时间';
comment on column DATA_EXPORT_TASK.RECORD_UUID
  is '导出记录的UUID，标识该任务是哪个导出记录的';



-- Create table
create table DATA_EXPORT_TASK_LOG
(
  UUID            VARCHAR2(255) not null,
  CREATE_TIME     TIMESTAMP(6),
  CREATOR         VARCHAR2(255),
  MODIFIER        VARCHAR2(255),
  MODIFY_TIME     TIMESTAMP(6),
  REC_VER         NUMBER(10),
  DATA_TYPE       VARCHAR2(255),
  DATA_CHILD_TYPE VARCHAR2(255),
  DATA_UUID       VARCHAR2(255),
  EXPORT_TIME     TIMESTAMP(6),
  EXPORT_STATUS   NUMBER,
  ERROR_MSG       VARCHAR2(2000),
  TASK_UUID       VARCHAR2(255),
  EXPORT_STATUS_CN  VARCHAR2(255),
  constraint PK_DATA_EXPORT_TASK_LOG primary key (UUID)
);

-- Add comments to the table
comment on table DATA_EXPORT_TASK_LOG
  is '数据导出任务日志';
-- Add comments to the columns
comment on column DATA_EXPORT_TASK_LOG.UUID
  is '主键uuid';
comment on column DATA_EXPORT_TASK_LOG.CREATE_TIME
  is '创建时间';
comment on column DATA_EXPORT_TASK_LOG.CREATOR
  is '创建人';
comment on column DATA_EXPORT_TASK_LOG.MODIFIER
  is '更新人';
comment on column DATA_EXPORT_TASK_LOG.MODIFY_TIME
  is '更新时间';
comment on column DATA_EXPORT_TASK_LOG.REC_VER
  is '数据版本';
comment on column DATA_EXPORT_TASK_LOG.DATA_TYPE
  is '导出数据类型';
comment on column DATA_EXPORT_TASK_LOG.DATA_CHILD_TYPE
  is '导出数据子类';
comment on column DATA_EXPORT_TASK_LOG.DATA_UUID
  is '导出数据的UUID';
comment on column DATA_EXPORT_TASK_LOG.EXPORT_TIME
  is '导出时间';
comment on column DATA_EXPORT_TASK_LOG.EXPORT_STATUS
  is '导出状态0：失败，1：正常';
comment on column DATA_EXPORT_TASK_LOG.ERROR_MSG
  is '异常原因';
comment on column DATA_EXPORT_TASK_LOG.TASK_UUID
  is '导出任务的UUID，标识该任务日志是属于哪个任务';
comment on column DATA_EXPORT_TASK_LOG.EXPORT_STATUS_CN
  is '导入状态中文名称';



-- Create table
create table DATA_IMPORT_RECORD
(
  UUID              VARCHAR2(255) not null,
  CREATE_TIME       TIMESTAMP(6),
  CREATOR           VARCHAR2(255),
  MODIFIER          VARCHAR2(255),
  MODIFY_TIME       TIMESTAMP(6),
  REC_VER           NUMBER(10),
  DATA_TYPE         VARCHAR2(255),
  DATA_TYPE_JSON    VARCHAR2(2000),
  SOURCE_UUID       VARCHAR2(255),
  SYSTEM_UNIT_ID   VARCHAR2(255),
  SYSTEM_UNIT_NAME VARCHAR2(255),
  VERSION_ID        VARCHAR2(255),
  VERSION_NAME      VARCHAR2(255),
  SETTING_PWD      VARCHAR2(255),
  IMPORT_PATH       VARCHAR2(2000),
  IMPORT_FILES      VARCHAR2(4000),
  REPEAT_STRATEGY   NUMBER,
  ERROR_STRATEGY    NUMBER,
  BATCH_NO          VARCHAR2(255),
  IMPORT_STATUS     NUMBER,
  IMPORT_STATUS_CN  VARCHAR2(255),
  OPERATOR          VARCHAR2(255),
  IMPORT_TIME       TIMESTAMP(6),
  constraint PK_DATA_IMPORT_RECORD primary key (UUID)
);

-- Add comments to the table
comment on table DATA_IMPORT_RECORD
  is '数据导入记录';
-- Add comments to the columns
comment on column DATA_IMPORT_RECORD.UUID
  is '主键uuid';
comment on column DATA_IMPORT_RECORD.CREATE_TIME
  is '创建时间';
comment on column DATA_IMPORT_RECORD.CREATOR
  is '创建人';
comment on column DATA_IMPORT_RECORD.MODIFIER
  is '更新人';
comment on column DATA_IMPORT_RECORD.MODIFY_TIME
  is '更新时间';
comment on column DATA_IMPORT_RECORD.REC_VER
  is '数据版本';
comment on column DATA_IMPORT_RECORD.DATA_TYPE
  is '导入数据类型，值有：组织数据、流程数据、邮件。';
comment on column DATA_IMPORT_RECORD.DATA_TYPE_JSON
  is '导入数据类型的详细JSON串，用于表示类型下的哪些子类型需要导出。Eg：{"org_data":{"version":"1","user":"1","group":"1","type":"1","duty":"1","rank":"1"},"flow_data":"1","email_data":{"email":{"receivce":"1","send":"1","draft":"0","recovery":"0"},"folder":"1","contact_group":"1","contact":"1","tag":"1","paper":"1"}}';
comment on column DATA_IMPORT_RECORD.SOURCE_UUID
  is '源数据UUID字段';
comment on column DATA_IMPORT_RECORD.SYSTEM_UNIT_ID
  is '数据归属单位ID，导入只有一个单位';
comment on column DATA_IMPORT_RECORD.SYSTEM_UNIT_NAME
  is '数据归属单位名称，导入只有一个单位';
comment on column DATA_IMPORT_RECORD.VERSION_ID
  is '组织版本ID';
comment on column DATA_IMPORT_RECORD.VERSION_NAME
  is '组织版本名称';
comment on column DATA_IMPORT_RECORD.SETTING_PWD
  is '设置登录密码';
comment on column DATA_IMPORT_RECORD.IMPORT_PATH
  is '导入文件路径';
comment on column DATA_IMPORT_RECORD.IMPORT_FILES
  is '导入的数据，文件名称';
comment on column DATA_IMPORT_RECORD.REPEAT_STRATEGY
  is '重复策略：1：替换，2：跳过';
comment on column DATA_IMPORT_RECORD.ERROR_STRATEGY
  is '异常策略：1：终止，2跳过';
comment on column DATA_IMPORT_RECORD.BATCH_NO
  is '处理批号';
comment on column DATA_IMPORT_RECORD.IMPORT_STATUS
  is '处理结果：0：取消，1：完成，2：异常终止';
comment on column DATA_IMPORT_RECORD.IMPORT_STATUS_CN
  is '处理结果中文名，用于在列表上的模糊查询';
comment on column DATA_IMPORT_RECORD.OPERATOR
  is '操作人名称';
comment on column DATA_IMPORT_RECORD.IMPORT_TIME
  is '导入时间';



-- Create table
create table DATA_IMPORT_TASK
(
  UUID              VARCHAR2(255) not null,
  CREATE_TIME       TIMESTAMP(6),
  CREATOR           VARCHAR2(255),
  MODIFIER          VARCHAR2(255),
  MODIFY_TIME       TIMESTAMP(6),
  REC_VER           NUMBER(10),
  DATA_TYPE         VARCHAR2(255),
  SYSTEM_UNIT_ID   VARCHAR2(255),
  SYSTEM_UNIT_NAME VARCHAR2(255),
  IMPORT_FILES      VARCHAR2(4000),
  TASK_STATUS       NUMBER,
  TASK_STATUS_CN    VARCHAR2(255),
  PROGRESS          NUMBER,
  DATA_TOTAL        NUMBER,
  OPERATOR          VARCHAR2(255),
  IMPORT_TIME       TIMESTAMP(6),
  FINISH_TIME       TIMESTAMP(6),
  RECORD_UUID       VARCHAR2(255),
  constraint PK_DATA_IMPORT_TASK primary key (UUID)
);

-- Add comments to the table
comment on table DATA_IMPORT_TASK
  is '数据导入任务';
-- Add comments to the columns
comment on column DATA_IMPORT_TASK.UUID
  is '主键uuid';
comment on column DATA_IMPORT_TASK.CREATE_TIME
  is '创建时间';
comment on column DATA_IMPORT_TASK.CREATOR
  is '创建人';
comment on column DATA_IMPORT_TASK.MODIFIER
  is '更新人';
comment on column DATA_IMPORT_TASK.MODIFY_TIME
  is '更新时间';
comment on column DATA_IMPORT_TASK.REC_VER
  is '数据版本';
comment on column DATA_IMPORT_TASK.DATA_TYPE
  is '导入数据类型';
comment on column DATA_IMPORT_TASK.SYSTEM_UNIT_ID
  is '数据归属单位ID，导入只有一个单位';
comment on column DATA_IMPORT_TASK.SYSTEM_UNIT_NAME
  is '数据归属单位名称，导入只有一个单位';
comment on column DATA_IMPORT_TASK.IMPORT_FILES
  is '导入数据，文件名称';
comment on column DATA_IMPORT_TASK.TASK_STATUS
  is '任务状态0：取消，1：完成，2：导入中，3：异常终止';
comment on column DATA_IMPORT_TASK.TASK_STATUS_CN
  is '任务状态中文名，用于在列表的模糊查询';
comment on column DATA_IMPORT_TASK.PROGRESS
  is '导入进度';
comment on column DATA_IMPORT_TASK.DATA_TOTAL
  is '任务总数据量';
comment on column DATA_IMPORT_TASK.OPERATOR
  is '操作人名称';
comment on column DATA_IMPORT_TASK.IMPORT_TIME
  is '导入时间';
comment on column DATA_IMPORT_TASK.FINISH_TIME
  is '完成时间';
comment on column DATA_IMPORT_TASK.RECORD_UUID
  is '导入记录的UUID，标识该任务是哪个导入记录的';


-- Create table
create table DATA_IMPORT_TASK_LOG
(
  UUID              VARCHAR2(255) not null,
  CREATE_TIME       TIMESTAMP(6),
  CREATOR           VARCHAR2(255),
  MODIFIER          VARCHAR2(255),
  MODIFY_TIME       TIMESTAMP(6),
  REC_VER           NUMBER(10),
  DATA_TYPE         VARCHAR2(255),
  DATA_CHILD_TYPE   VARCHAR2(255),
  SOURCE_UUID       VARCHAR2(255),
  AFTER_IMPORT_UUID VARCHAR2(255),
  SOURCE_ID         VARCHAR2(255),
  AFTER_IMPORT_ID   VARCHAR2(255),
  IMPORT_TIME       TIMESTAMP(6),
  IMPORT_STATUS     NUMBER,
  ERROR_MSG         VARCHAR2(2000),
  TASK_UUID         VARCHAR2(255),
  IS_REPEAT         NUMBER,
  SOURCE_DATA       CLOB,
  IMPORT_STATUS_CN  VARCHAR2(255),
  constraint PK_DATA_IMPORT_TASK_LOG primary key (UUID)
);

-- Add comments to the table
comment on table DATA_IMPORT_TASK_LOG
  is '数据导入任务日志';
-- Add comments to the columns
comment on column DATA_IMPORT_TASK_LOG.UUID
  is '主键uuid';
comment on column DATA_IMPORT_TASK_LOG.CREATE_TIME
  is '创建时间';
comment on column DATA_IMPORT_TASK_LOG.CREATOR
  is '创建人';
comment on column DATA_IMPORT_TASK_LOG.MODIFIER
  is '更新人';
comment on column DATA_IMPORT_TASK_LOG.MODIFY_TIME
  is '更新时间';
comment on column DATA_IMPORT_TASK_LOG.REC_VER
  is '数据版本';
comment on column DATA_IMPORT_TASK_LOG.DATA_TYPE
  is '导入数据类型';
comment on column DATA_IMPORT_TASK_LOG.DATA_CHILD_TYPE
  is '导入数据子类型';
comment on column DATA_IMPORT_TASK_LOG.SOURCE_UUID
  is '导入数据的UUID';
comment on column DATA_IMPORT_TASK_LOG.AFTER_IMPORT_UUID
  is '导入数据后的UUID';
comment on column DATA_IMPORT_TASK_LOG.SOURCE_ID
  is '导入数据的ID';
comment on column DATA_IMPORT_TASK_LOG.AFTER_IMPORT_ID
  is '导入数据后的ID';
comment on column DATA_IMPORT_TASK_LOG.IMPORT_TIME
  is '导入时间';
comment on column DATA_IMPORT_TASK_LOG.IMPORT_STATUS
  is '导入状态0：失败，1：正常';
comment on column DATA_IMPORT_TASK_LOG.ERROR_MSG
  is '异常原因';
comment on column DATA_IMPORT_TASK_LOG.TASK_UUID
  is '导入任务的UUID，标识该任务日志是属于哪个任务';
comment on column DATA_IMPORT_TASK_LOG.IS_REPEAT
  is '是否重复：0：否，1：是';
comment on column DATA_IMPORT_TASK_LOG.SOURCE_DATA
  is '源数据信息';
comment on column DATA_IMPORT_TASK_LOG.IMPORT_STATUS_CN
  is '导入状态中文名称';
