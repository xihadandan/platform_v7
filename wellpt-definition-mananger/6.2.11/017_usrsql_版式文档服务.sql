
/* 签章服务配置 */
create table LAYOUT_DOCUMENT_SERVICE_CONF
(
  uuid                        VARCHAR2(64) primary key,
  create_time                 TIMESTAMP(6),
  creator                     VARCHAR2(255),
  modifier                    VARCHAR2(255),
  modify_time                 TIMESTAMP(6),
  rec_ver                     NUMBER(10),
  system_unit_id              VARCHAR2(64),
  server_name                 VARCHAR2(64),
  server_unique_code          VARCHAR2(64),
  code                        VARCHAR2(64),
  server_url                  VARCHAR2(1000),
  file_extensions             VARCHAR2(64),
  status                      VARCHAR2(10)
);

-- Add comments to the table
comment on table LAYOUT_DOCUMENT_SERVICE_CONF
  is '签章服务配置';
-- Add comments to the columns
comment on column LAYOUT_DOCUMENT_SERVICE_CONF.server_name
  is '服务名称';
comment on column LAYOUT_DOCUMENT_SERVICE_CONF.server_unique_code
  is '服务唯一标识符';
comment on column LAYOUT_DOCUMENT_SERVICE_CONF.code
  is '编号';
comment on column LAYOUT_DOCUMENT_SERVICE_CONF.server_url
  is '服务地址';
comment on column LAYOUT_DOCUMENT_SERVICE_CONF.file_extensions
  is '支持的文件扩展名';
comment on column LAYOUT_DOCUMENT_SERVICE_CONF.status
  is '状态';

