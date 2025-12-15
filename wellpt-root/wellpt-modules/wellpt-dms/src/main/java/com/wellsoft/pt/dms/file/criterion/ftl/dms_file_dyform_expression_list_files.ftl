select distinct data_uuid from (
<if isNotBlank(readFileField) || isNotBlank(editFileField)>
    select a_.uuid as data_uuid
    from ${tableName} a_
    inner join(${userOrgIdsTemplate}) u_
    <if isNotBlank(readFileField) && isNotBlank(editFileField)>
        on a_.${readFileField} like '%' || u_.org_id || '%'
        or a_.${editFileField} like '%' || u_.org_id || '%'
    </if>
    <if isNotBlank(readFileField) && isBlank(editFileField)>
        on a_.${readFileField} like '%' || u_.org_id || '%'
    </if>
    <if isBlank(readFileField) && isNotBlank(editFileField)>
        on a_.${editFileField} like '%' || u_.org_id || '%'
    </if>
    union all
</if>

select    f1.data_uuid     as data_uuid
from dms_file f1,
(select c_.folder_uuid
from (select distinct fc.folder_uuid
from (select fc_.folder_uuid              as folder_uuid,
fc_.ref_object_identity_uuid as ref_object_identity_uuid
from dms_folder_configuration fc_
inner join dms_folder f_
on fc_.folder_uuid = f_.uuid
and f_.uuid =
:folderUuid) fc
inner join (select oar.object_identity_uuid
from dms_object_assign_role oar,
dms_role r,
dms_role_action ra,
(${userOrgIdsTemplate}) u_ -- 用户相关ID
where oar.role_uuid = r.uuid
and r.uuid = ra.role_uuid
and ra.action = 'readFile'
and oar.org_ids like '%' || u_.org_id || '%'
and oar.permit = 'Y'
and (oar.deny <> 'Y' or oar.deny is null)) c2
on fc.ref_object_identity_uuid = c2.object_identity_uuid) c_
where c_.folder_uuid not in
(select distinct fc.folder_uuid
from (select fc_.folder_uuid              as folder_uuid,
fc_.ref_object_identity_uuid as ref_object_identity_uuid
from dms_folder_configuration fc_
inner join dms_folder f_
on fc_.folder_uuid = f_.uuid
and f_.uuid =
:folderUuid) fc
inner join (select t.object_identity_uuid
from dms_object_assign_role t,
dms_role r,
dms_role_action ra,
(${userOrgIdsTemplate}) u_ -- 用户相关ID
where t.role_uuid = r.uuid
and r.uuid = ra.role_uuid
and ra.action = 'readFile'
and t.org_ids like '%' || u_.org_id || '%'
and t.deny = 'Y') c2
on fc.ref_object_identity_uuid = c2.object_identity_uuid)) f2
where f1.folder_uuid = :folderUuid and f1.status <> '0'
and f1.folder_uuid = f2.folder_uuid
)