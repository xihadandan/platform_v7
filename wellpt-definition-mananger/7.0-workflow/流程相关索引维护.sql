-- 该脚本用于维护流程相关索引，用于清理、重建丢失的索引
create index wf_fi_fduuid_id_batch_idx on wf_flow_instance (flow_def_uuid, id);
create index wf_fi_pfi_uuid_idx on wf_flow_instance (parent_flow_inst_uuid);
create index wf_fi_data_uuid_idx on wf_flow_instance (data_uuid);
create index wf_fi_start_user_id_idx on wf_flow_instance (start_user_id);
create index wf_fip_fiuuid_idx on wf_flow_instance_param (flow_inst_uuid);
create index wf_fip_batch_idx on wf_flow_instance_param (flow_inst_uuid, name);

create index wf_ti_flow_def_uuid_idx on wf_task_instance (flow_def_uuid);
create index wf_ti_fiuuid_idx on wf_task_instance (flow_inst_uuid);
create index wf_ti_ptiuuid_idx on wf_task_instance (parent_task_inst_uuid);

create index wf_to_fiuuid_idx on wf_task_operation (flow_inst_uuid);
create index wf_to_tiuuid_idx on wf_task_operation (task_inst_uuid);
create index wf_to_assignee_idx on wf_task_operation (assignee);

create index wf_ta_fiuuid_idx on wf_task_activity (flow_inst_uuid);
create index wf_ta_tiuuid_idx on wf_task_activity (task_inst_uuid);
create index wf_ta_pre_tiuuid_idx on wf_task_activity (pre_task_inst_uuid);

create index wf_ti_tiuuid_idx on wf_task_identity (task_inst_uuid);
create index wf_ti_stiuuid_idx on wf_task_identity (source_task_identity_uuid);
create index wf_ti_os_idx on wf_task_identity (overdue_state);
create index wf_ti_tiuuid_state_batch_idx on wf_task_identity (task_inst_uuid, suspension_state);

create index wf_tb_fiuuid_idx on wf_task_branch (flow_inst_uuid);
create index wf_tb_ctiuuid_idx on wf_task_branch (current_task_inst_uuid);
create index wf_tb_pltiuuid_idx on wf_task_branch (parallel_task_inst_uuid);

create index wf_td_batch_idx on wf_task_delegation (task_inst_uuid, trustee, completion_state);

create index wf_tfa_fifn_batch_idx on wf_task_form_attachment (flow_inst_uuid, field_name);

create index wf_tfo_fidufn_batch_idx on wf_task_form_opinion (flow_inst_uuid, data_uuid, field_name);
create index wf_tfo_opuuid_idx on wf_task_form_opinion_log (task_operation_uuid);

create index wf_tid_fiuuid_idx on wf_task_info_distribution (flow_inst_uuid);
create index wf_tid_tiuuid_idx on wf_task_info_distribution (task_inst_uuid);

create index wf_tit_user_id_idx on wf_task_instance_topping (user_id);

create index wf_tsf_fiuuid_idx on wf_task_sub_flow (flow_inst_uuid);
create index wf_tsf_pfiuuid_idx on wf_task_sub_flow (parent_flow_inst_uuid);
create index wf_tsf_ptiuuid_idx on wf_task_sub_flow (parent_task_inst_uuid);

create index wf_tsfd_batch_idx on wf_task_sub_flow_dispatch (parent_flow_inst_uuid, completion_state);
create index wf_tsfr_tsfuuid_idx on wf_task_sub_flow_relation (task_sub_flow_uuid);

create index wf_tt_fiuuid_idx on wf_task_timer (flow_inst_uuid);
create index wf_tt_tiuuid_idx on wf_task_timer (task_inst_uuid);
create index wf_ttl_task_timer_uuid_idx on wf_task_timer_log (task_timer_uuid);
create index wf_ttu_task_timer_uuid_idx on wf_task_timer_user (task_timer_uuid);

create index wf_cds_user_id_idx on wf_common_delegation_setting (user_id);

create index wf_do_batch_idx on wf_def_opinion (creator, flow_def_id, task_id);
create index wf_do_ocuuid_idx on wf_def_opinion (opinion_category_uuid);

create index wf_fd_flow_schema_uuid_idx on wf_flow_definition (flow_schema_uuid);
create index wf_fd_id_idx on wf_flow_definition (id);
create index wf_fdu_flow_def_uuid_idx on wf_flow_definition_user (flow_def_uuid);
create index wf_fsl_pfsuuid_idx on wf_flow_schema_log (parent_flow_schemauuid);

create index wf_fds_consignor_idx on wf_flow_delegation_settings (consignor);


-- acl
create index acl_task_entry_identity_idx on acl_task_entry (object_id_identity, sid);
create index acl_task_entry_sid_idx on acl_task_entry (sid);
create index acl_tdm_acl_task_uuid_idx on acl_task_done_marker (acl_task_uuid);
create index acl_trm_acl_task_uuid_idx on acl_task_read_marker (acl_task_uuid);
create index acl_oi_oii_idx on acl_object_identity (object_id_identity);
create index acl_sid_idx on acl_sid (sid);
create index acl_entry_aoi_idx on acl_entry (acl_object_identity);
create index acl_entry_oid_idx on acl_entry (object_id_identity);
create index acl_entry_sid_idx on acl_entry (sid);
create index acl_entry_mask_idx on acl_entry (mask);
create index acl_entry_batch_idx on acl_entry (object_id_identity, sid, mask, granting);

-- 公共模块
create index cd_rm_entity_uuid_idx on cd_read_marker (entity_uuid);
create index cd_rm_eu_batch_idx on cd_read_marker (entity_uuid, user_id);
create index cd_ru_batch_idx on cd_recent_use (object_id_identity, user_id, module_id);
create index cd_up_batch_idx on cd_user_preferences (user_id, module_id);
