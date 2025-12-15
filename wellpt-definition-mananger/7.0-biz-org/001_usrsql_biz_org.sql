 

/*==============================================================*/
/* Table: BIZ_ORG_CONFIG                                        */
/*==============================================================*/
create table BIZ_ORG_CONFIG 
(
   UUID                 NUMBER(19)           not null,
   BIZ_ORG_UUID         NUMBER(19)           not null,
   BIZ_ORG_DIMENSION_ID VARCHAR(120),
   ALLOW_DIMENSION_LEVEL NUMBER(1)            not null,
   SYNC_ORG_OPTION      VARCHAR(120),
   ENABLE_SYNC_ORG      NUMBER(1)            default 1 not null,
   ALLOW_ORG_ELE_MODEL  VARCHAR(1200),
   ALLOW_ORG_LEVEL      NUMBER(1),
   CREATE_TIME          TIMESTAMP,
   CREATOR              VARCHAR2(64),
   MODIFIER             VARCHAR2(64),
   MODIFY_TIME          TIMESTAMP,
   SYSTEM               VARCHAR2(64),
   TENANT               VARCHAR2(64),
   REC_VER              FLOAT(3)             default 1,
   constraint PK_BIZ_ORG_CONFIG primary key (UUID)
);

/*==============================================================*/
/* Table: BIZ_ORG_DIMENSION                                     */
/*==============================================================*/
create table BIZ_ORG_DIMENSION 
(
   UUID                 NUMBER(19)           not null,
   NAME                 VARCHAR2(120 CHAR)   not null,
   ID                   VARCHAR2(64 CHAR)    not null,
   ICON                 VARCHAR(120),
   REMARK               VARCHAR2(300 CHAR),
   CREATE_TIME          TIMESTAMP,
   CREATOR              VARCHAR2(64),
   MODIFIER             VARCHAR2(64),
   MODIFY_TIME          TIMESTAMP,
   SYSTEM               VARCHAR2(64),
   TENANT               VARCHAR2(64),
   REC_VER              FLOAT(3)             default 1,
   constraint PK_BIZ_ORG_DIMENSION primary key (UUID)
);

comment on table BIZ_ORG_DIMENSION is
'业务组织资源分配维度';

comment on column BIZ_ORG_DIMENSION.NAME is
'名称';

comment on column BIZ_ORG_DIMENSION.ICON is
'图标';

comment on column BIZ_ORG_DIMENSION.REMARK is
'描述';

/*==============================================================*/
/* Table: BIZ_ORG_ELEMENT                                       */
/*==============================================================*/
create table BIZ_ORG_ELEMENT 
(
   UUID                 NUMBER(20)           not null,
   BIZ_ORG_UUID         NUMBER(19)           not null,
   ID                   VARCHAR2(32)         not null,
   ORG_ELEMENT_ID       VARCHAR(32),
   ELEMENT_TYPE         VARCHAR(64)          not null,
   ENABLED              NUMBER(1)            default 1 not null,
   PARENT_DIMENSION_UUID               NUMBER(20),
   IS_DIMENSION         NUMBER(1)            default 0 not null,
   NAME                 VARCHAR2(120 char)   not null,
   PARENT_UUID          NUMBER(20),
   CREATE_TIME          TIMESTAMP,
   CREATOR              VARCHAR2(64),
   MODIFIER             VARCHAR2(64),
   MODIFY_TIME          TIMESTAMP,
   REMARK               VARCHAR2(300 char),
   SYSTEM               VARCHAR2(64),
   TENANT               VARCHAR2(64),
   REC_VER              FLOAT(3)             default 1,
   SEQ                  NUMBER(4)            default 1
);

comment on column BIZ_ORG_ELEMENT.ELEMENT_TYPE is
'对应组织单元模型的ID';

/*==============================================================*/
/* Table: BIZ_ORG_ELEMENT_MEMBER                                */
/*==============================================================*/
create table BIZ_ORG_ELEMENT_MEMBER 
(
   UUID                 NUMBER(20)           not null,
   BIZ_ORG_UUID         NUMBER(20)           not null,
   MEMBER_ID            VARCHAR(120)         not null,
   BIZ_ORG_ELEMENT_ID   VARCHAR(120)         not null,
   BIZ_ORG_ROLE_ID      VARCHAR(120),
   CREATE_TIME          TIMESTAMP,
   CREATOR              VARCHAR2(64),
   MODIFIER             VARCHAR2(64),
   MODIFY_TIME          TIMESTAMP,
   REC_VER              FLOAT(3)             default 1,
   SYSTEM               VARCHAR2(64),
   TENANT               VARCHAR2(64),
   constraint PK_BIZ_ORG_ELEMENT_MEMBER primary key (UUID)
);

comment on table BIZ_ORG_ELEMENT_MEMBER is
'业务组织下的节点成员';

comment on column BIZ_ORG_ELEMENT_MEMBER.BIZ_ORG_ELEMENT_ID is
'关系对象ID';

/*==============================================================*/
/* Table: BIZ_ORG_ELEMENT_PATH                                  */
/*==============================================================*/
create table BIZ_ORG_ELEMENT_PATH 
(
   UUID                 NUMBER(20)           not null,
   BIZ_ORG_ELEMENT_ID   VARCHAR2(64)         not null,
   CN_PATH              VARCHAR2(3000),
   PIN_YIN_PATH         VARCHAR2(3000),
   ID_PATH              VARCHAR2(3000)       not null,
   BIZ_ORG_UUID         NUMBER(20)           not null,
   CREATE_TIME          TIMESTAMP,
   CREATOR              VARCHAR2(64),
   MODIFIER             VARCHAR2(64),
   MODIFY_TIME          TIMESTAMP,
   SYSTEM               VARCHAR2(64),
   TENANT               VARCHAR2(64),
   REC_VER              FLOAT(3)             default 1,
   LEAF                 NUMBER(1)            default 0,
   constraint PK_BIZ_ORG_ELEMENT_PATH primary key (UUID)
);

/*==============================================================*/
/* Table: BIZ_ORG_ELEMENT_PATH_CHAIN                            */
/*==============================================================*/
create table BIZ_ORG_ELEMENT_PATH_CHAIN 
(
   UUID                 NUMBER(20)           not null,
   ID                   VARCHAR2(64)         not null,
   ELEMENT_TYPE         VARCHAR(64)          not null,
   SUB_ID               VARCHAR2(64)         not null,
   SUB_ELEMENT_TYPE     VARCHAR(64)          not null,
   "LEVEL"              NUMBER(1)            default 1,
   BIZ_ORG_UUID         NUMBER(20)           not null,
   CREATE_TIME          TIMESTAMP,
   CREATOR              VARCHAR2(64),
   MODIFIER             VARCHAR2(64),
   MODIFY_TIME          TIMESTAMP,
   SYSTEM               VARCHAR2(64),
   TENANT               VARCHAR2(64),
   REC_VER              FLOAT(3)             default 1,
   constraint PK_BIZ_ORG_ELEMENT_PATH_CHAIN primary key (UUID)
);

comment on table BIZ_ORG_ELEMENT_PATH_CHAIN is
'业务组织单元实例路径链';

/*==============================================================*/
/* Table: BIZ_ORG_ELEMENT_ROLE_RELA                             */
/*==============================================================*/
create table BIZ_ORG_ELEMENT_ROLE_RELA 
(
   UUID                 NUMBER(20)           not null,
   BIZ_ORG_ELEMENT_ID   VARCHAR2(64)         not null,
   ROLE_UUID            VARCHAR(64)          not null,
   CREATE_TIME          TIMESTAMP,
   CREATOR              VARCHAR2(64),
   MODIFIER             VARCHAR2(64),
   MODIFY_TIME          TIMESTAMP,
   SYSTEM               VARCHAR2(64),
   TENANT               VARCHAR2(64),
   REC_VER              FLOAT(3)             default 1,
   constraint PK_BIZ_ORG_ELEMENT_ROLE_RELA primary key (UUID)
);

comment on table BIZ_ORG_ELEMENT_ROLE_RELA is
'业务组织单元实例关联权限角色';

/*==============================================================*/
/* Table: BIZ_ORG_ORGANIZATION                                  */
/*==============================================================*/
create table BIZ_ORG_ORGANIZATION 
(
   UUID                 NUMBER(19)           not null,
   NAME                 VARCHAR2(120 CHAR)   not null,
   ID                   VARCHAR2(32)         not null,
   ORG_UUID             NUMBER(19)           not null,
   NEVER_EXPIRE         NUMBER(1)            default 1 not null,
   EXPIRE_TIME          TIMESTAMP,
   ENABLE               NUMBER(1)            default 1 not null,
   EXPIRED              NUMBER(1)            default 0 not null,
   SYNC_LOCKED          NUMBER(1)            default 0 not null,
   REMARK               VARCHAR2(300 CHAR),
   CREATE_TIME          TIMESTAMP,
   CREATOR              VARCHAR2(64),
   MODIFIER             VARCHAR2(64),
   MODIFY_TIME          TIMESTAMP,
   SYSTEM               VARCHAR2(64),
   TENANT               VARCHAR2(64),
   REC_VER              FLOAT(3)             default 1,
   constraint PK_BIZ_ORG_ORGANIZATION primary key (UUID)
);

comment on table BIZ_ORG_ORGANIZATION is
'业务组织';

comment on column BIZ_ORG_ORGANIZATION.NAME is
'业务组织名称';

comment on column BIZ_ORG_ORGANIZATION.ID is
'业务组织ID';

comment on column BIZ_ORG_ORGANIZATION.ORG_UUID is
'关联组织';

comment on column BIZ_ORG_ORGANIZATION.NEVER_EXPIRE is
'永久';

comment on column BIZ_ORG_ORGANIZATION.EXPIRE_TIME is
'失效时间';

comment on column BIZ_ORG_ORGANIZATION.ENABLE is
'是否启用';

comment on column BIZ_ORG_ORGANIZATION.EXPIRED is
'是否失效';

comment on column BIZ_ORG_ORGANIZATION.REMARK is
'REMARK';

/*==============================================================*/
/* Table: BIZ_ORG_ROLE                                          */
/*==============================================================*/
create table BIZ_ORG_ROLE 
(
   UUID                 NUMBER(19)           not null,
   BIZ_ORG_UUID         number(19)           not null,
   ID                   VARCHAR(64 CHAR)     not null,
   NAME                 VARCHAR(120 CHAR)    not null,
   APPLY_TO             VARCHAR(64),
   SEQ                  NUMBER(6),
   REMARK               VARCHAR(300 CHAR),
   ALLOW_MEMBER_TYPE    VARCHAR(1000 CHAR)   default '1',
   MULTIPLE_SELECT_MEMBER NUMBER(1),
   CREATE_TIME          TIMESTAMP,
   CREATOR              VARCHAR2(64),
   MODIFIER             VARCHAR2(64),
   MODIFY_TIME          TIMESTAMP,
   SYSTEM               VARCHAR2(64),
   TENANT               VARCHAR2(64),
   REC_VER              FLOAT(3)             default 1,
   constraint PK_BIZ_ORG_ROLE primary key (UUID)
);

comment on table BIZ_ORG_ROLE is
'业务组织角色';

comment on column BIZ_ORG_ROLE.NAME is
'名称';

comment on column BIZ_ORG_ROLE.APPLY_TO is
'应用于';

comment on column BIZ_ORG_ROLE.SEQ is
'排序号';

comment on column BIZ_ORG_ROLE.REMARK is
'描述';

comment on column BIZ_ORG_ROLE.ALLOW_MEMBER_TYPE is
'成员可选类型';

comment on column BIZ_ORG_ROLE.MULTIPLE_SELECT_MEMBER is
'成员是否多选';

/*==============================================================*/
/* Table: BIZ_ORG_ROLE_TEMPLATE                                 */
/*==============================================================*/
create table BIZ_ORG_ROLE_TEMPLATE 
(
   UUID                 NUMBER(19)           not null,
   NAME                 VARCHAR2(120 CHAR)   not null,
   ICON                 VARCHAR(120),
   REMARK               VARCHAR2(300 CHAR),
   ROLE_TEMPLATE        CLOB,
   CREATE_TIME          TIMESTAMP,
   CREATOR              VARCHAR2(64),
   MODIFIER             VARCHAR2(64),
   MODIFY_TIME          TIMESTAMP,
   SYSTEM               VARCHAR2(64),
   TENANT               VARCHAR2(64),
   REC_VER              FLOAT(3)             default 1,
   constraint PK_BIZ_ORG_ROLE_TEMPLATE primary key (UUID)
);

comment on table BIZ_ORG_ROLE_TEMPLATE is
'业务组织角色模板';

comment on column BIZ_ORG_ROLE_TEMPLATE.NAME is
'名称';

comment on column BIZ_ORG_ROLE_TEMPLATE.ICON is
'图标';

comment on column BIZ_ORG_ROLE_TEMPLATE.REMARK is
'描述';

comment on column BIZ_ORG_ROLE_TEMPLATE.ROLE_TEMPLATE is
'角色列表';




create index SYS_BIZ_ORG_E_ID_QRY on BIZ_ORG_ELEMENT (id);
create index SYS_BIZ_ORG_E_P_ID_QRY on BIZ_ORG_ELEMENT_PATH_CHAIN (id);
create index SYS_BIZ_ORG_E_P_SUBID_QRY on BIZ_ORG_ELEMENT_PATH_CHAIN (sub_id);
create index SYS_BIZ_ORG_R_RELA_ID_QRY on BIZ_ORG_ELEMENT_ROLE_RELA (biz_org_element_id);
