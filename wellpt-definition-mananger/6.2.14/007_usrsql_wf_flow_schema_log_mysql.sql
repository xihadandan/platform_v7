alter table WF_FLOW_SCHEMA_LOG add CREATOR VARCHAR(50) COMMENT '创建人';
alter table WF_FLOW_SCHEMA_LOG add MODIFIER VARCHAR(50) COMMENT '修改人';
alter table WF_FLOW_SCHEMA_LOG add MODIFY_TIME datetime COMMENT '修改时间';
alter table WF_FLOW_SCHEMA_LOG add REC_VER decimal(10,0) COMMENT '版本号';