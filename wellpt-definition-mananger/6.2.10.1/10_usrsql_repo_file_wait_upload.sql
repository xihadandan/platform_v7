create table REPO_FILE_WAIT_UPLOAD
(
  uuid             VARCHAR2(64) primary key,
  create_time      TIMESTAMP(6),
  creator          VARCHAR2(64),
  file_uuid        VARCHAR2(64),
  file_name   VARCHAR2(120),
  md5              VARCHAR2(64),
  modifier         VARCHAR2(64),
  modify_time      TIMESTAMP(6),
  rec_ver          NUMBER(10),
  fail_count  NUMBER(4) default 0,
  log         CLOB,
  data_lock   NUMBER(1) default 0,
  retry_time  TIMESTAMP(6)
);

comment on column REPO_FILE_WAIT_UPLOAD.file_uuid
  is '文件ID';
comment on column REPO_FILE_WAIT_UPLOAD.md5
  is '文件MD5';
comment on column REPO_FILE_WAIT_UPLOAD.fail_count
  is '上传失败次数';
comment on column REPO_FILE_WAIT_UPLOAD.log
  is '日志内容';
comment on column REPO_FILE_WAIT_UPLOAD.file_name
  is '文件名';
comment on column REPO_FILE_WAIT_UPLOAD.data_lock
  is '锁';
comment on column REPO_FILE_WAIT_UPLOAD.retry_time
  is '重试时间';
  
create index IDX_FILE_LK_COUNT on REPO_FILE_WAIT_UPLOAD (FAIL_COUNT, DATA_LOCK);
