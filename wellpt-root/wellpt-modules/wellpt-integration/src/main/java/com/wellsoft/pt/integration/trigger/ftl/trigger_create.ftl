create or replace trigger ${callName}_TIG
after insert or update or delete on ${tableName}
for each row
declare
<#-- 主表交换数据UUID -->
tigOwnerUuid varchar2(255);
new_flag varchar2(255);
old_flag varchar2(255);
begin
tigOwnerUuid := SYS_GUID();
case
when inserting then
if :NEW.IS_SYN_BACK is null then
<#-- 插入从表列数据新增记录 -->
<list tabColsInfos as tabCol>
    <if tabCol.dataType='LONG'>
        if :NEW.${tabCol.columnName} is not null then
        insert into TIG_COLUMN_DATA(TIG_OWNER_UUID, TIG_COLUMN_NAME, TIG_DATA_TYPE, DATA_VARCHAR_2)
        values(tigOwnerUuid, '${tabCol.columnName}', 'LONG', :NEW.${tabCol.columnName});
        end if;
    </if>
    <if tabCol.dataType='TIMESTAMP(6)'>
        if :NEW.${tabCol.columnName} is not null then
        insert into TIG_COLUMN_DATA(TIG_OWNER_UUID, TIG_COLUMN_NAME, TIG_DATA_TYPE, DATA_TIME)
        values(tigOwnerUuid, '${tabCol.columnName}', 'TIMESTAMP(6)', :NEW.${tabCol.columnName});
        end if;
    </if>
    <if tabCol.dataType='FLOAT'>
        if :NEW.${tabCol.columnName} is not null then
        insert into TIG_COLUMN_DATA(TIG_OWNER_UUID, TIG_COLUMN_NAME, TIG_DATA_TYPE, DATA_FLOAT)
        values(tigOwnerUuid, '${tabCol.columnName}', 'FLOAT', :NEW.${tabCol.columnName});
        end if;
    </if>
    <if tabCol.dataType='NUMBER'>
        if :NEW.${tabCol.columnName} is not null then
        insert into TIG_COLUMN_DATA(TIG_OWNER_UUID, TIG_COLUMN_NAME, TIG_DATA_TYPE, DATA_NUMBER)
        values(tigOwnerUuid, '${tabCol.columnName}', 'NUMBER', :NEW.${tabCol.columnName});
        end if;
    </if>
    <if tabCol.dataType='CLOB'>
        if :NEW.${tabCol.columnName} is not null then
        insert into TIG_COLUMN_DATA(TIG_OWNER_UUID, TIG_COLUMN_NAME, TIG_DATA_TYPE, DATA_CLOB)
        values(tigOwnerUuid, '${tabCol.columnName}', 'CLOB', :NEW.${tabCol.columnName});
        <#-- 联合主键处理
      <if isCompositeKey = true>
            insert into TIG_COLUMN_CLOB(TIG_OWNER_UUID,TIG_COLUMN_NAME,DATA_UUID,STABLE_NAME,COMPOSITE_KEY,DATA_CLOB,DATA_STATUS,DIRECTION, CREATE_TIME)
            values(tigOwnerUuid, '${tabCol.columnName}', <list pkInfos as pkCol>:NEW.${pkCol.columnName}<#if pkCol_has_next>||';'||</#if></list>,'${tableName}','<list pkInfos as pkCol>${pkCol.columnName}<#if pkCol_has_next>;</#if></list>',:NEW.${tabCol.columnName},1,${direction}, SYSDATE());
      </if>
      <if isCompositeKey = false>
          insert into TIG_COLUMN_CLOB(TIG_OWNER_UUID,TIG_COLUMN_NAME,DATA_UUID,STABLE_NAME,COMPOSITE_KEY,DATA_CLOB,DATA_STATUS,DIRECTION, CREATE_TIME)
          values(tigOwnerUuid, '${tabCol.columnName}',:NEW.<if (pkInfos?size=0) >UUID<else>${pkInfos[0]['columnName']!}</if>,'${tableName}',<if (pkInfos?size=0) >''<else>'${pkInfos[0]['columnName']!}'</if>,:NEW.${tabCol.columnName},1,${direction}, SYSDATE());
      </if>
       -->
        end if;
    </if>
    <if tabCol.dataType='CHAR'>
        if :NEW.${tabCol.columnName} is not null then
        insert into TIG_COLUMN_DATA(TIG_OWNER_UUID, TIG_COLUMN_NAME, TIG_DATA_TYPE, DATA_CHAR)
        values(tigOwnerUuid, '${tabCol.columnName}', 'CHAR', :NEW.${tabCol.columnName});
        end if;
    </if>
    <if tabCol.dataType='RAW'>
        if :NEW.${tabCol.columnName} is not null then
        insert into TIG_COLUMN_DATA(TIG_OWNER_UUID, TIG_COLUMN_NAME, TIG_DATA_TYPE, DATA_RAW)
        values(tigOwnerUuid, '${tabCol.columnName}', 'RAW', :NEW.${tabCol.columnName});
        end if;
    </if>
    <if tabCol.dataType='DATE'>
        if :NEW.${tabCol.columnName} is not null then
        insert into TIG_COLUMN_DATA(TIG_OWNER_UUID, TIG_COLUMN_NAME, TIG_DATA_TYPE, DATA_DATE)
        values(tigOwnerUuid, '${tabCol.columnName}', 'DATE', :NEW.${tabCol.columnName});
        end if;
    </if>
    <if tabCol.dataType='VARCHAR2'>
        if :NEW.${tabCol.columnName} is not null then
        insert into TIG_COLUMN_DATA(TIG_OWNER_UUID, TIG_COLUMN_NAME, TIG_DATA_TYPE, DATA_VARCHAR_2)
        values(tigOwnerUuid, '${tabCol.columnName}', 'VARCHAR2', :NEW.${tabCol.columnName});
        end if;
    </if>
    <if tabCol.dataType='BLOB'>
        if :NEW.${tabCol.columnName} is not null then
        insert into TIG_COLUMN_DATA(TIG_OWNER_UUID, TIG_COLUMN_NAME, TIG_DATA_TYPE, DATA_BLOB)
        values(tigOwnerUuid, '${tabCol.columnName}', 'BLOB', :NEW.${tabCol.columnName});
        end if;
    </if>
</list>

<#-- 插入主表数据新增记录 -->
<#-- 联合主键处理 -->
<if isCompositeKey=true>
    insert into tig_table_data
    (UUID,CREATE_TIME,ORDER_ID,ACTION,FEEDBACK,DIRECTION,REMARK,STABLE_NAME,STATUS,SUUID,SYN_TIME,COMPOSITE_KEY)
    values(tigOwnerUuid,SYSDATE(),syn_order_id.NEXTVAL,1,0,${direction},'','${tableName}',1,
    <list pkInfos as pkCol>:NEW.${pkCol.columnName}<#if pkCol_has_next>||';'||</#if></list>
    ,null,'
    <list pkInfos as pkCol>${pkCol.columnName}<#if pkCol_has_next>;</#if></list>
    ');
</if>
<#-- 非联合主键处理 -->
<if isCompositeKey=false>
    insert into tig_table_data
    (UUID,CREATE_TIME,ORDER_ID,ACTION,FEEDBACK,DIRECTION,REMARK,STABLE_NAME,STATUS,SUUID,SYN_TIME,COMPOSITE_KEY)
    values(tigOwnerUuid,SYSDATE(),syn_order_id.NEXTVAL,1,0,${direction},'','${tableName}',1,:NEW.
    <if (pkInfos?size=0)>UUID
        <else>${pkInfos[0]['columnName']!}</if>
    ,null,
    <if (pkInfos?size=0)>''
        <else>'${pkInfos[0]['columnName']!}'
    </if>
    );
</if>
end if;

when updating then
new_flag := :NEW.IS_SYN_BACK;
old_flag := :OLD.IS_SYN_BACK;
if new_flag is null or (new_flag is not null and new_flag=old_flag) then
<#-- 插入从表列数据更新记录 -->
<list tabColsInfos as tabCol>
    <#-- 不更新版本号 -->
    <if tabCol.columnName !='REC_VER'>
        <if tabCol.dataType='LONG'>
            if updating('${tabCol.columnName}') then
            insert into TIG_COLUMN_DATA(TIG_OWNER_UUID, TIG_COLUMN_NAME, TIG_DATA_TYPE, DATA_VARCHAR_2)
            values(tigOwnerUuid, '${tabCol.columnName}', 'LONG', :NEW.${tabCol.columnName});
            end if;
        </if>
        <if tabCol.dataType='TIMESTAMP(6)'>
            if updating('${tabCol.columnName}') then
            insert into TIG_COLUMN_DATA(TIG_OWNER_UUID, TIG_COLUMN_NAME, TIG_DATA_TYPE, DATA_TIME)
            values(tigOwnerUuid, '${tabCol.columnName}', 'TIMESTAMP(6)', :NEW.${tabCol.columnName});
            end if;
        </if>
        <if tabCol.dataType='FLOAT'>
            if updating('${tabCol.columnName}') then
            insert into TIG_COLUMN_DATA(TIG_OWNER_UUID, TIG_COLUMN_NAME, TIG_DATA_TYPE, DATA_FLOAT)
            values(tigOwnerUuid, '${tabCol.columnName}', 'FLOAT', :NEW.${tabCol.columnName});
            end if;
        </if>
        <if tabCol.dataType='NUMBER'>
            if updating('${tabCol.columnName}') then
            insert into TIG_COLUMN_DATA(TIG_OWNER_UUID, TIG_COLUMN_NAME, TIG_DATA_TYPE, DATA_NUMBER)
            values(tigOwnerUuid, '${tabCol.columnName}', 'NUMBER', :NEW.${tabCol.columnName});
            end if;
        </if>
        <if tabCol.dataType='CLOB'>
            if updating('${tabCol.columnName}') then
            insert into TIG_COLUMN_DATA(TIG_OWNER_UUID, TIG_COLUMN_NAME, TIG_DATA_TYPE, DATA_CLOB)
            values(tigOwnerUuid, '${tabCol.columnName}', 'CLOB', :NEW.${tabCol.columnName});
            <#-- 联合主键处理
          <if isCompositeKey = true>
                insert into TIG_COLUMN_CLOB(TIG_OWNER_UUID,TIG_COLUMN_NAME,DATA_UUID,STABLE_NAME,COMPOSITE_KEY,DATA_CLOB,DATA_STATUS,DIRECTION, CREATE_TIME)
                values(tigOwnerUuid, '${tabCol.columnName}', <list pkInfos as pkCol>:NEW.${pkCol.columnName}<#if pkCol_has_next>||';'||</#if></list>,'${tableName}','<list pkInfos as pkCol>${pkCol.columnName}<#if pkCol_has_next>;</#if></list>',:NEW.${tabCol.columnName},1,${direction}, SYSDATE());
          </if>
          <if isCompositeKey = false>
              insert into TIG_COLUMN_CLOB(TIG_OWNER_UUID,TIG_COLUMN_NAME,DATA_UUID,STABLE_NAME,COMPOSITE_KEY,DATA_CLOB,DATA_STATUS,DIRECTION, CREATE_TIME)
              values(tigOwnerUuid, '${tabCol.columnName}',:NEW.<if (pkInfos?size=0) >UUID<else>${pkInfos[0]['columnName']!}</if>,'${tableName}',<if (pkInfos?size=0) >''<else>'${pkInfos[0]['columnName']!}'</if>,:NEW.${tabCol.columnName},1,${direction}, SYSDATE());
          </if>
           -->
            end if;
        </if>
        <if tabCol.dataType='CHAR'>
            if updating('${tabCol.columnName}') then
            insert into TIG_COLUMN_DATA(TIG_OWNER_UUID, TIG_COLUMN_NAME, TIG_DATA_TYPE, DATA_CHAR)
            values(tigOwnerUuid, '${tabCol.columnName}', 'CHAR', :NEW.${tabCol.columnName});
            end if;
        </if>
        <if tabCol.dataType='RAW'>
            if updating('${tabCol.columnName}') then
            insert into TIG_COLUMN_DATA(TIG_OWNER_UUID, TIG_COLUMN_NAME, TIG_DATA_TYPE, DATA_RAW)
            values(tigOwnerUuid, '${tabCol.columnName}', 'RAW', :NEW.${tabCol.columnName});
            end if;
        </if>
        <if tabCol.dataType='DATE'>
            if updating('${tabCol.columnName}') then
            insert into TIG_COLUMN_DATA(TIG_OWNER_UUID, TIG_COLUMN_NAME, TIG_DATA_TYPE, DATA_DATE)
            values(tigOwnerUuid, '${tabCol.columnName}', 'DATE', :NEW.${tabCol.columnName});
            end if;
        </if>
        <if tabCol.dataType='VARCHAR2'>
            if updating('${tabCol.columnName}') then
            insert into TIG_COLUMN_DATA(TIG_OWNER_UUID, TIG_COLUMN_NAME, TIG_DATA_TYPE, DATA_VARCHAR_2)
            values(tigOwnerUuid, '${tabCol.columnName}', 'VARCHAR2', :NEW.${tabCol.columnName});
            end if;
        </if>
        <if tabCol.dataType='BLOB'>
            if updating('${tabCol.columnName}') then
            insert into TIG_COLUMN_DATA(TIG_OWNER_UUID, TIG_COLUMN_NAME, TIG_DATA_TYPE, DATA_BLOB)
            values(tigOwnerUuid, '${tabCol.columnName}', 'BLOB', :NEW.${tabCol.columnName});
            end if;
        </if>
    </if>
</list>

<#-- 插入主表数据更新记录 -->
<#-- 联合主键处理 -->
<if isCompositeKey=true>
    insert into tig_table_data
    (UUID,CREATE_TIME,ORDER_ID,ACTION,FEEDBACK,DIRECTION,REMARK,STABLE_NAME,STATUS,SUUID,SYN_TIME,COMPOSITE_KEY)
    values(tigOwnerUuid,SYSDATE(),syn_order_id.NEXTVAL,2,0,${direction},'','${tableName}',1,
    <list pkInfos as pkCol>:NEW.${pkCol.columnName}<#if pkCol_has_next>||';'||</#if></list>
    ,null,'
    <list pkInfos as pkCol>${pkCol.columnName}<#if pkCol_has_next>;</#if></list>
    ');
</if>
<#-- 非联合主键处理 -->
<if isCompositeKey=false>
    insert into tig_table_data
    (UUID,CREATE_TIME,ORDER_ID,ACTION,FEEDBACK,DIRECTION,REMARK,STABLE_NAME,STATUS,SUUID,SYN_TIME,COMPOSITE_KEY)
    values(tigOwnerUuid,SYSDATE(),syn_order_id.NEXTVAL,2,0,${direction},'','${tableName}',1,:NEW.
    <if (pkInfos?size=0)>UUID
        <else>${pkInfos[0]['columnName']!}</if>
    ,null,
    <if (pkInfos?size=0)>''
        <else>'${pkInfos[0]['columnName']!}'
    </if>
    );
</if>
end if;

when deleting then
<#-- 插入主表数据删除记录 -->
<#-- 联合主键处理 -->
<if isCompositeKey=true>
    insert into tig_table_data
    (UUID,CREATE_TIME,ORDER_ID,ACTION,FEEDBACK,DIRECTION,REMARK,STABLE_NAME,STATUS,SUUID,SYN_TIME,COMPOSITE_KEY)
    values(tigOwnerUuid,SYSDATE(),syn_order_id.NEXTVAL,3,0,${direction},'','${tableName}',1,
    <list pkInfos as pkCol>:OLD.${pkCol.columnName}<#if pkCol_has_next>||';'||</#if></list>
    ,null,'
    <list pkInfos as pkCol>${pkCol.columnName}<#if pkCol_has_next>;</#if></list>
    ');
</if>

<#-- 非联合主键处理 -->
<if isCompositeKey=false>
    insert into tig_table_data
    (UUID,CREATE_TIME,ORDER_ID,ACTION,FEEDBACK,DIRECTION,REMARK,STABLE_NAME,STATUS,SUUID,SYN_TIME,COMPOSITE_KEY)
    values(tigOwnerUuid,SYSDATE(),syn_order_id.NEXTVAL,3,0,${direction},'','${tableName}',1,:OLD.
    <if (pkInfos?size=0)>UUID
        <else>${pkInfos[0]['columnName']!}</if>
    ,null,
    <if (pkInfos?size=0)>''
        <else>'${pkInfos[0]['columnName']!}'
    </if>
    );
</if>

end case;

end ${callName}_TIG;