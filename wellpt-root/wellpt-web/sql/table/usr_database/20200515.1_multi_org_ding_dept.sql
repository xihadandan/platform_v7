/*==============================================================*/
/* Table: MULTI_ORG_DING_DEPT                                   */
/*==============================================================*/
create table MULTI_ORG_DING_DEPT
(
   CREATE_DEPT_GROUP    NUMBER(1),
   UUID                 VARCHAR2(255 CHAR)   not null,
   ELE_ID               VARCHAR2(255 CHAR),
   ID                   VARCHAR2(255 CHAR),
   NAME                 VARCHAR2(255 CHAR),
   PARENT_ID            VARCHAR2(255 CHAR),
   AUTO_ADD_USER        NUMBER(1),
   EXT                  VARCHAR2(255 CHAR),
   CREATE_TIME          TIMESTAMP,
   CREATOR              VARCHAR2(255 BYTE),
   MODIFIER             VARCHAR2(255 BYTE),
   MODIFY_TIME          TIMESTAMP,
   REC_VER              NUMBER(10,2),
   constraint PK_MULTI_ORG_DING_DEPT primary key (UUID)
);

comment on table MULTI_ORG_DING_DEPT is
'钉钉部门信息表';

comment on column MULTI_ORG_DING_DEPT.CREATE_DEPT_GROUP is
'是否同步创建一个关联此部门的企业群，true表示是，false表示不是';

comment on column MULTI_ORG_DING_DEPT.ELE_ID is
'部门节点id';

comment on column MULTI_ORG_DING_DEPT.ID is
'部门id';

comment on column MULTI_ORG_DING_DEPT.NAME is
'部门名称';

comment on column MULTI_ORG_DING_DEPT.PARENT_ID is
'父部门id，根部门为1';

comment on column MULTI_ORG_DING_DEPT.AUTO_ADD_USER is
'当群已经创建后，是否有新人加入部门会自动加入该群，true表示是，false表示不是';

comment on column MULTI_ORG_DING_DEPT.CREATE_TIME is
'创建时间';

comment on column MULTI_ORG_DING_DEPT.CREATOR is
'创建人';

comment on column MULTI_ORG_DING_DEPT.MODIFIER is
'更新人';

comment on column MULTI_ORG_DING_DEPT.MODIFY_TIME is
'更新时间';

comment on column MULTI_ORG_DING_DEPT.REC_VER is
'数据版本';
