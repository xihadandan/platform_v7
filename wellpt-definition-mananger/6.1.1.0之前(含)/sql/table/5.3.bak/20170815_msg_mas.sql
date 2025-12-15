-- Add/modify columns 
alter table MSG_MAS_CONFIG add S_WEB_SERVICE    VARCHAR2(255 CHAR);
alter table MSG_MAS_CONFIG add S_LOGIN_NAME     VARCHAR2(255 CHAR);
alter table MSG_MAS_CONFIG add S_LOGIN_PASSWORD VARCHAR2(255 CHAR);
alter table MSG_MAS_CONFIG add S_SEND_LIMIT     NUMBER(10);
alter table MSG_MAS_CONFIG add S_IS_OPEN        NUMBER(1);
alter table MSG_MAS_CONFIG add IS_SYN_BACK      VARCHAR2(255 CHAR);
alter table MSG_MAS_CONFIG add API_ASYNC        NUMBER(1);
alter table MSG_MAS_CONFIG add CLIENT_BEAN      VARCHAR2(255 CHAR);
alter table MSG_MAS_CONFIG add CLIENT_BEAN_NAME VARCHAR2(255 CHAR);
alter table MSG_MAS_CONFIG add cloud_mas_http VARCHAR2(255 CHAR);
alter table MSG_MAS_CONFIG add ec_name VARCHAR2(255 CHAR);
alter table MSG_MAS_CONFIG add ap_id VARCHAR2(255 CHAR);
alter table MSG_MAS_CONFIG add secret_key VARCHAR2(255 CHAR);
alter table MSG_MAS_CONFIG add cloud_sign VARCHAR2(255 CHAR);
alter table MSG_MAS_CONFIG add add_serial VARCHAR2(255 CHAR);
alter table MSG_MAS_CONFIG add cloud_send_limit NUMBER(10);
alter table MSG_MAS_CONFIG add cloud_is_open NUMBER(1);

alter table msg_short_message add msg_group VARCHAR2(255 CHAR);
alter table msg_short_message add reserved_text4 VARCHAR2(255 CHAR);
alter table msg_short_message add reserved_text5 VARCHAR2(255 CHAR);
alter table msg_short_message add reserved_text6 VARCHAR2(255 CHAR);

alter table msg_message_template add BREAK_WORD  NUMBER(10);