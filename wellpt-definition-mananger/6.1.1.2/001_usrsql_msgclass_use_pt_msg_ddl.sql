create table MSG_MESSAGE_CLASSIFY 
(
   UUID                 VARCHAR2(255 CHAR)  not null,
   CREATE_TIME          TIMESTAMP(6),
   CREATOR              VARCHAR2(255 CHAR),
   MODIFIER             VARCHAR2(255 CHAR),
   MODIFY_TIME          TIMESTAMP(6),
   REC_VER              NUMBER(10,0),
   SYSTEM_UNIT_ID       VARCHAR2(50 CHAR),
   NAME                 VARCHAR2(100 CHAR),
   ICON                 VARCHAR2(100 CHAR),
   ICON_BG              VARCHAR2(100 CHAR),
   CODE                 VARCHAR2(100 CHAR),
   IS_ENABLE             NUMBER(1,0),
   NOTE               VARCHAR2(255 CHAR),
   constraint PK_MSG_MESSAGE_CLASSIFY primary key (UUID)
);

comment on table MSG_MESSAGE_CLASSIFY is '消息分类';
comment on column MSG_MESSAGE_CLASSIFY.UUID is 'UUID';
comment on column MSG_MESSAGE_CLASSIFY.CREATE_TIME is '创建时间';
comment on column MSG_MESSAGE_CLASSIFY.CREATOR is '创建人';
comment on column MSG_MESSAGE_CLASSIFY.MODIFIER is '修改人';
comment on column MSG_MESSAGE_CLASSIFY.MODIFY_TIME is '修改时间';
comment on column MSG_MESSAGE_CLASSIFY.REC_VER is '版本号';
comment on column MSG_MESSAGE_CLASSIFY.SYSTEM_UNIT_ID is '系统单位Id';
comment on column MSG_MESSAGE_CLASSIFY.NAME is '分类名称';
comment on column MSG_MESSAGE_CLASSIFY.ICON is '分类图标';
comment on column MSG_MESSAGE_CLASSIFY.ICON_BG is '分类图标颜色';
comment on column MSG_MESSAGE_CLASSIFY.CODE is '编号';
comment on column MSG_MESSAGE_CLASSIFY.IS_ENABLE is '启用分类';
comment on column MSG_MESSAGE_CLASSIFY.NOTE is '描述';

CREATE UNIQUE INDEX unique_mmc_name_sysId ON MSG_MESSAGE_CLASSIFY("SYSTEM_UNIT_ID", "NAME");


create table MSG_USER_PERSONALISE 
(
   UUID                 VARCHAR2(255 CHAR)   not null,
   CREATE_TIME          TIMESTAMP(6),
   CREATOR              VARCHAR2(255 CHAR),
   MODIFIER             VARCHAR2(255 CHAR),
   MODIFY_TIME          TIMESTAMP(6),
   REC_VER              NUMBER(10,0),
   SYSTEM_UNIT_ID       VARCHAR2(50 CHAR),
   USER_ID              VARCHAR2(100 CHAR),
   TEMPLATE_ID          VARCHAR2(255 CHAR),
   IS_POPUP             NUMBER(1,0),
   constraint PK_MSG_USER_PERSONALISE primary key (UUID)
);

comment on table MSG_USER_PERSONALISE is '消息用户个性化设置';
comment on column MSG_USER_PERSONALISE.UUID is 'UUID';
comment on column MSG_USER_PERSONALISE.CREATE_TIME is '创建时间';
comment on column MSG_USER_PERSONALISE.CREATOR is '创建人';
comment on column MSG_USER_PERSONALISE.MODIFIER is '修改人';
comment on column MSG_USER_PERSONALISE.MODIFY_TIME is '修改时间';
comment on column MSG_USER_PERSONALISE.REC_VER is '版本号';
comment on column MSG_USER_PERSONALISE.SYSTEM_UNIT_ID is'系统单位Id';
comment on column MSG_USER_PERSONALISE.USER_ID is '用户Id';
comment on column MSG_USER_PERSONALISE.TEMPLATE_ID is '消息格式ID';
comment on column MSG_USER_PERSONALISE.IS_POPUP is '是否弹窗(0:否，1:是)';

-- Add/modify columns
alter table MSG_MESSAGE_TEMPLATE add(
    CLASSIFY_UUID VARCHAR2(255 CHAR),
    CLASSIFY_NAME VARCHAR2(255 CHAR),
    CALLBACK_JSON VARCHAR2(4000 CHAR),
    REMINDER_TYPE NUMBER(1,0),
    POPUP_POSITION NUMBER(1,0),
    DISPLAY_MASK NUMBER(1,0),
    AUTO_TIME_CLOSE_WIN NUMBER(1,0));
-- Add comments to the columns
comment on column MSG_MESSAGE_TEMPLATE.CLASSIFY_UUID is '消息分类Id';
comment on column MSG_MESSAGE_TEMPLATE.CLASSIFY_NAME is '消息分类名称';
comment on column MSG_MESSAGE_TEMPLATE.CALLBACK_JSON is '回调事件json';
comment on column MSG_MESSAGE_TEMPLATE.REMINDER_TYPE is '消息提醒方式（1：徽标，2：徽标+弹窗）';
comment on column MSG_MESSAGE_TEMPLATE.POPUP_POSITION is '弹窗位置：1 浏览器右下角，弹窗在浏览器右下角弹出 2 浏览器中间，弹窗在浏览器中间弹出';
comment on column MSG_MESSAGE_TEMPLATE.DISPLAY_MASK is '显示遮罩 0否 1是';
comment on column MSG_MESSAGE_TEMPLATE.AUTO_TIME_CLOSE_WIN is '自动计时关闭弹窗  0否 1是 ';

-- create index
create index IDX_CLASSIFY_UUID on MSG_MESSAGE_TEMPLATE (
   CLASSIFY_UUID ASC
);

-- Add/modify columns
alter table MSG_MESSAGE_INBOX add(
    CLASSIFY_UUID VARCHAR2(255 CHAR),
    CLASSIFY_NAME VARCHAR2(255 CHAR));
-- Add comments to the columns
comment on column MSG_MESSAGE_INBOX.CLASSIFY_UUID is '消息分类Id';
comment on column MSG_MESSAGE_INBOX.CLASSIFY_NAME is '消息分类名称';
-- create index
create index IDX_MSG_INBOX_CLASSIFY_UUID on MSG_MESSAGE_INBOX (
   CLASSIFY_UUID ASC
);

-- Add/modify columns
alter table MSG_MESSAGE_INBOX_BAK add(
    CLASSIFY_UUID VARCHAR2(255 CHAR),
    CLASSIFY_NAME VARCHAR2(255 CHAR));
-- Add comments to the columns
comment on column MSG_MESSAGE_INBOX_BAK.CLASSIFY_UUID is '消息分类Id';
comment on column MSG_MESSAGE_INBOX_BAK.CLASSIFY_NAME is '消息分类名称';
-- create index
create index IDX_MSG_I_BAK_CLASSIFY_UUID on MSG_MESSAGE_INBOX_BAK (
   CLASSIFY_UUID ASC
);

-- Add/modify columns
alter table MSG_USER_PERSONALISE add TYPE NUMBER(1,0);
-- Add comments to the columns
comment on column MSG_USER_PERSONALISE.TYPE is '开关类型 1：主开关，2：消息格式';


-- Add/modify columns
alter table MSG_MESSAGE_TEMPLATE add(
    POPUP_SIZE NUMBER(1,0),
    POPUP_WIDTH VARCHAR2(10 CHAR),
    POPUP_HEIGHT VARCHAR2(10 CHAR));
-- Add comments to the columns
comment on column MSG_MESSAGE_TEMPLATE.POPUP_SIZE is '弹窗大小';
comment on column MSG_MESSAGE_TEMPLATE.POPUP_WIDTH is '弹窗宽度';
comment on column MSG_MESSAGE_TEMPLATE.POPUP_HEIGHT is '弹窗高度';

-- Add/modify columns
alter table MSG_MESSAGE_INBOX add ON_LINE NUMBER(1,0);
-- Add comments to the columns
comment on column MSG_MESSAGE_INBOX.ON_LINE is '是否在线';

alter table WM_MAIL_CONFIG modify MAIL_SERVER_TYPE DEFAULT 'JamesMail';
comment on column WM_MAIL_CONFIG.MAIL_SERVER_TYPE is '邮件服务类型';