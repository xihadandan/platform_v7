-- Create table
create table msg_message_queue
(
  uuid             varchar(255) not null,
  create_time      timestamp,
  creator          varchar(255),
  modify_time      timestamp,
  modifier         varchar(255),
  rec_ver          number(10),
  name             varchar(255),
  template_id      varchar(255),
  code             varchar(255),
  sent_time        timestamp,
  correlation_uuid varchar(255),
  message          BLOB
);