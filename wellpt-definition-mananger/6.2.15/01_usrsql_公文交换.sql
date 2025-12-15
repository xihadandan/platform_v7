-- 文档交换-配置
-- Add/modify columns
alter table dms_doc_exchange_config add(
    receive_dyform_uuid VARCHAR2(64)
);

comment on column dms_doc_exchange_config.receive_dyform_uuid is '接收文档展示单据UUID';




