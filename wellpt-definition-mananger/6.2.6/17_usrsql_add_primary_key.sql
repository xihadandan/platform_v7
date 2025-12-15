

alter table DYFORM_FILE_LIST_SOURCE_CONFIG
  add primary key (UUID);
  
  
 alter table DYFORM_FORM_DEFINITION
  add primary key (UUID);
  
   alter table DYFORM_FORM_DEFINITION_LOG
  add primary key (UUID);
  
  alter table MULTI_ORG_DING_ROLE
  drop constraint MULTI_ORG_DING_ROLE cascade;
  
    alter table MULTI_ORG_DING_ROLE
  add primary key (UUID);

  
  