/*==============================================================*/
/* Table: AUDIT_DINGTALK_ROLE                                   */
/*==============================================================*/
create table AUDIT_DINGTALK_ROLE 
(
   UUID                 VARCHAR2(255 BYTE)   not null,
   CREATE_TIME          TIMESTAMP,
   CREATOR              VARCHAR2(255 BYTE),
   MODIFIER             VARCHAR2(255 BYTE),
   MODIFY_TIME          TIMESTAMP,
   REC_VER              NUMBER(10,2),
   ROLE_ID              VARCHAR2(255 BYTE),
   NAME                 VARCHAR2(255 BYTE),
   IS_PERMISSION_ASSIGN NUMBER(1),
   constraint PK_AUDIT_DINGTALK_ROLE primary key (UUID)
);

comment on table AUDIT_DINGTALK_ROLE is
'角色表';

comment on column AUDIT_DINGTALK_ROLE.UUID is
'唯一主键';

comment on column AUDIT_DINGTALK_ROLE.CREATE_TIME is
'创建时间';

comment on column AUDIT_DINGTALK_ROLE.CREATOR is
'创建人';

comment on column AUDIT_DINGTALK_ROLE.MODIFIER is
'更新人';

comment on column AUDIT_DINGTALK_ROLE.MODIFY_TIME is
'更新时间';

comment on column AUDIT_DINGTALK_ROLE.REC_VER is
'数据版本';

comment on column AUDIT_DINGTALK_ROLE.ROLE_ID is
'ID';

comment on column AUDIT_DINGTALK_ROLE.NAME is
'角色名称';

comment on column AUDIT_DINGTALK_ROLE.IS_PERMISSION_ASSIGN is
'是否权限分配';
