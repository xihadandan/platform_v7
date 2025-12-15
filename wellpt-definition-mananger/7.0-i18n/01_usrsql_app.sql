/*==============================================================*/
/* Table: CD_DATA_DICT_I18N                                     */
/*==============================================================*/
create table CD_DATA_DICT_I18N 
(
   UUID                 NUMBER(19)           not null,
   DATA_UUID            NUMBER(19)           not null,
   DATA_ID              VARCHAR(64),
   DATA_CODE            VARCHAR(30)          not null,
   LOCALE               VARCHAR(12),
   CONTENT              VARCHAR(1200 CHAR),
   CREATE_TIME          TIMESTAMP,
   CREATOR              VARCHAR2(64),
   MODIFIER             VARCHAR2(64),
   MODIFY_TIME          TIMESTAMP,
   REC_VER              FLOAT(3)             default 1,
   SYSTEM               VARCHAR2(64),
   TENANT               VARCHAR2(64),
   constraint PK_CD_DATA_DICT_I18N primary key (UUID)
);

comment on table CD_DATA_DICT_I18N is
'数据字典国际化';

comment on column CD_DATA_DICT_I18N.DATA_UUID is
'编码';



/*==============================================================*/
/* Table: USER_NAME_I18N                                        */
/*==============================================================*/
create table USER_NAME_I18N 
(
   UUID                 NUMBER(19)           not null,
   USER_UUID            VARCHAR(64)          not null,
   USER_ID              VARCHAR(64)          not null,
   LOCALE               VARCHAR(12)          not null,
   USER_NAME            VARCHAR(300 CHAR),
   CREATE_TIME          TIMESTAMP,
   CREATOR              VARCHAR2(64),
   MODIFIER             VARCHAR2(64),
   MODIFY_TIME          TIMESTAMP,
   REC_VER              FLOAT(3)             default 1,
   constraint PK_USER_NAME_I18N primary key (UUID)
);

comment on table USER_NAME_I18N is
'用户姓名国际化';

comment on column USER_NAME_I18N.USER_UUID is
'用户UUID';





/*==============================================================*/
/* Table: APP_DEF_ELEMENT_I18N                                  */
/*==============================================================*/
create table APP_DEF_ELEMENT_I18N 
(
   UUID                 NUMBER(19)           not null,
   CODE                 VARCHAR(240 CHAR)    not null,
   VERSION              NUMBER(4,1),
   ELEMENT_ID           VARCHAR(120 CHAR),
   DEF_ID               VARCHAR(120 CHAR)    not null,
   APPLY_TO             VARCHAR(120 CHAR)    not null,
   LOCALE               VARCHAR(6)           not null,
   CONTENT              VARCHAR(2000 CHAR),
   CREATE_TIME          TIMESTAMP,
   CREATOR              VARCHAR2(64),
   MODIFIER             VARCHAR2(64),
   MODIFY_TIME          TIMESTAMP,
   SYSTEM               VARCHAR2(64),
   TENANT               VARCHAR2(64),
   REC_VER              FLOAT(3)             default 1,
   constraint PK_APP_DEF_ELEMENT_I18N primary key (UUID)
);

comment on table APP_DEF_ELEMENT_I18N is
'设计元素国际化';

comment on column APP_DEF_ELEMENT_I18N.CODE is
'编码';

comment on column APP_DEF_ELEMENT_I18N.VERSION is
'版本号';

comment on column APP_DEF_ELEMENT_I18N.ELEMENT_ID is
'元素归属的父ID: 组件ID 或者 其他类型ID';

comment on column APP_DEF_ELEMENT_I18N.DEF_ID is
'组件所属的定义ID: 页面ID 、 表单ID、流程ID';

comment on column APP_DEF_ELEMENT_I18N.APPLY_TO is
'应用于';

comment on column APP_DEF_ELEMENT_I18N.LOCALE is
'语言本地化编码';




/*==============================================================*/
/* Table: APP_I18N_LOCALE                                       */
/*==============================================================*/
create table APP_I18N_LOCALE 
(
   UUID                 NUMBER(19)           not null,
   NAME                 VARCHAR(600 CHAR)    not null,
   LOCALE               VARCHAR(24 CHAR)     not null,
   SEQ                  NUMBER(2)            default 1,
   TRANSLATE_CODE       VARCHAR(24),
   REMARK               VARCHAR(300),
   SORT_LETTERS         VARCHAR(120 CHAR),
   CREATE_TIME          TIMESTAMP,
   CREATOR              VARCHAR2(64),
   MODIFIER             VARCHAR2(64),
   MODIFY_TIME          TIMESTAMP,
   REC_VER              FLOAT(3)             default 1,
   constraint PK_APP_I18N_LOCALE primary key (UUID)
);

comment on table APP_I18N_LOCALE is
'国际化地区编码';

comment on column APP_I18N_LOCALE.NAME is
'编码';

comment on column APP_I18N_LOCALE.LOCALE is
'语言环境编码';

comment on column APP_I18N_LOCALE.TRANSLATE_CODE is
'翻译码';

insert into app_i18n_locale (UUID, NAME, LOCALE, SEQ, TRANSLATE_CODE, REMARK, CREATE_TIME, CREATOR, MODIFIER, MODIFY_TIME, REC_VER, SORT_LETTERS)
values ('1', '中文', 'zh_CN', '1', null, null, null, null, null, null, '1', null);

insert into app_i18n_locale (UUID, NAME, LOCALE, SEQ, TRANSLATE_CODE, REMARK, CREATE_TIME, CREATOR, MODIFIER, MODIFY_TIME, REC_VER, SORT_LETTERS)
values ('2', 'English', 'en_US', '2', null, '英文', null, null, null, null, '1', null);

insert into app_i18n_locale (UUID, NAME, LOCALE, SEQ, TRANSLATE_CODE, REMARK, CREATE_TIME, CREATOR, MODIFIER, MODIFY_TIME, REC_VER, SORT_LETTERS)
values ('3', 'ภาษาไทย', 'th_TH', '3', null, '泰语', null, null, null, null, '1', 'ก,ข,ฃ,ค,ฅ,ฆ,ง,จ,ฉ,ช,ซ,ฌ,ญ,ฎ,ฏ,ฐ,ฑ,ฒ,ณ,ด,ต,ถ,ท,ธ,น,บ,ป,ผ,ฝ,พ,ฟ,ภ,ม,ย,ร,ล,ว,ศ,ษ,ส,ห,ฬ,อ,ฮ

');

drop table APP_CODE_I18N cascade constraints;

/*==============================================================*/
/* Table: APP_CODE_I18N                                         */
/*==============================================================*/
create table APP_CODE_I18N 
(
   UUID                 NUMBER(19)           not null,
   CODE                 VARCHAR(600 CHAR)    not null,
   APPLY_TO             VARCHAR(240 CHAR),
   LOCALE               VARCHAR(6)           not null,
   CONTENT              VARCHAR(300 CHAR),
   CREATE_TIME          TIMESTAMP,
   CREATOR              VARCHAR2(64),
   MODIFIER             VARCHAR2(64),
   MODIFY_TIME          TIMESTAMP,
   REC_VER              FLOAT(3)             default 1,
   constraint PK_APP_CODE_I18N primary key (UUID)
);

comment on table APP_CODE_I18N is
'代码国际化';

comment on column APP_CODE_I18N.CODE is
'编码';

comment on column APP_CODE_I18N.APPLY_TO is
'应用于';

comment on column APP_CODE_I18N.LOCALE is
'语言本地化编码';


