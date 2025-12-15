create table APP_LOGIN_SECURITY_CONFIG
(
  uuid                        VARCHAR2(64) primary key,
  create_time                 TIMESTAMP(6),
  creator                     VARCHAR2(255),
  modifier                    VARCHAR2(255),
  modify_time                 TIMESTAMP(6),
  rec_ver                     NUMBER(10),
  is_allow_multi_device_login NUMBER(1) default 1,
  system_unit_id              VARCHAR2(64)
);

-- Add comments to the table 
comment on table APP_LOGIN_SECURITY_CONFIG
  is '登录安全配置';
-- Add comments to the columns 
comment on column APP_LOGIN_SECURITY_CONFIG.is_allow_multi_device_login
  is '是否允许多设备同时登录';