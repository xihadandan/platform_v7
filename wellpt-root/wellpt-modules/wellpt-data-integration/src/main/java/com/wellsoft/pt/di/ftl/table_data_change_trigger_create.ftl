create or replace trigger trg_4_di_${callName}
after insert or update or delete on ${tableName}
for each row
declare
trgUuid varchar2(255);
new_flag varchar2(255);
old_flag varchar2(255);
begin
trgUuid := SYS_GUID();
case
when inserting then
if :NEW.IS_SYN_BACK is null then
<#-- 插入从表列数据新增记录 -->
<list tabColsInfos as tabCol>
    <if tabCol.dataType='LONG' || tabCol.dataType='FLOAT' || tabCol.dataType='NUMBER' || tabCol.dataType='CHAR'>
        if :NEW.${tabCol.columnName} is not null then
        insert into DI_TABLE_COLUMN_DATA_CHANGE(UUID, CREATE_TIME, COLUMN_NAME, DATA_TYPE,DATA_BASIC_VALUE)
        values(trgUuid,systimestamp, '${tabCol.columnName}', '${tabCol.dataType}', :NEW.${tabCol.columnName});
        end if;
    </if>
    <if tabCol.dataType='TIMESTAMP(6)'>
        if :NEW.${tabCol.columnName} is not null then
        insert into DI_TABLE_COLUMN_DATA_CHANGE(UUID, CREATE_TIME, COLUMN_NAME, DATA_TYPE,DATA_BASIC_VALUE)
        values(trgUuid,systimestamp, '${tabCol.columnName}', '${tabCol.dataType}', to_char(:NEW.${tabCol.columnName}
        ,'YYYY-MM-DD HH24:MI:SS.FF3'));
        end if;
    </if>
    <if tabCol.dataType='CLOB'>
        if :NEW.${tabCol.columnName} is not null then
        insert into DI_TABLE_COLUMN_DATA_CHANGE(UUID, CREATE_TIME, COLUMN_NAME, DATA_TYPE,DATA_CLOB_VALUE)
        values(trgUuid,systimestamp, '${tabCol.columnName}', '${tabCol.dataType}',:NEW.${tabCol.columnName});
        end if;
    </if>


    <if tabCol.dataType='VARCHAR2'>
        if :NEW.${tabCol.columnName} is not null then
        insert into DI_TABLE_COLUMN_DATA_CHANGE(UUID, CREATE_TIME, COLUMN_NAME, DATA_TYPE,DATA_TEXT_VALUE)
        values(trgUuid,systimestamp, '${tabCol.columnName}', '${tabCol.dataType}',:NEW.${tabCol.columnName});
        end if;
    </if>
    <if tabCol.dataType='BLOB'>
        if :NEW.${tabCol.columnName} is not null then
        insert into DI_TABLE_COLUMN_DATA_CHANGE(UUID, CREATE_TIME, COLUMN_NAME, DATA_TYPE,DATA_BLOB_VALUE)
        values(trgUuid,systimestamp, '${tabCol.columnName}', '${tabCol.dataType}',:NEW.${tabCol.columnName});
        end if;
    </if>
</list>

<#-- 插入主表数据新增记录 -->
<#-- 联合主键处理 -->
<if isCompositeKey=true>
    insert into DI_TABLE_DATA_CHANGE (UUID,CREATE_TIME,TABLE_NAME,PK_UUID,PK_COL_NAME,ACTION)
    values(trgUuid,systimestamp, '${tableName}',
    <list pkInfos as pkCol>:NEW.${pkCol.columnName}<#if pkCol_has_next>||';'||</#if></list>
    ,'
    <list pkInfos as pkCol>${pkCol.columnName}<#if pkCol_has_next>;</#if></list>
    ','INSERT');
</if>
<#-- 非联合主键处理 -->
<if isCompositeKey=false>
    insert into DI_TABLE_DATA_CHANGE (UUID,CREATE_TIME,TABLE_NAME,PK_UUID,PK_COL_NAME,ACTION)
    values(trgUuid,systimestamp, '${tableName}', :NEW.
    <if (pkInfos?size=0)>UUID
        <else>${pkInfos[0]['columnName']!}</if>
    ,
    <if (pkInfos?size=0)>''
        <else>'${pkInfos[0]['columnName']!}'
    </if>
    ,'INSERT');
</if>
end if;
when updating then
new_flag := :NEW.IS_SYN_BACK;
old_flag := :OLD.IS_SYN_BACK;
if new_flag is null or (new_flag is not null and new_flag=old_flag) then
<list tabColsInfos as tabCol>
    <#-- 不更新版本号 -->
    <if tabCol.columnName !='REC_VER'>
        <if tabCol.dataType='LONG' || tabCol.dataType='FLOAT' || tabCol.dataType='NUMBER' || tabCol.dataType='CHAR'>
            if updating('${tabCol.columnName}') then
            insert into DI_TABLE_COLUMN_DATA_CHANGE(UUID, CREATE_TIME, COLUMN_NAME, DATA_TYPE,DATA_BASIC_VALUE)
            values(trgUuid,systimestamp, '${tabCol.columnName}', '${tabCol.dataType}', :NEW.${tabCol.columnName});
            end if;
        </if>
        <if tabCol.dataType='TIMESTAMP(6)'>
            if updating('${tabCol.columnName}') then
            insert into DI_TABLE_COLUMN_DATA_CHANGE(UUID, CREATE_TIME, COLUMN_NAME, DATA_TYPE,DATA_BASIC_VALUE)
            values(trgUuid,systimestamp, '${tabCol.columnName}', '${tabCol.dataType}', to_char(:NEW.${tabCol.columnName}
            ,'YYYY-MM-DD HH24:MI:SS.FF3'));
            end if;
        </if>
        <if tabCol.dataType='VARCHAR2'>
            if updating('${tabCol.columnName}') then
            insert into DI_TABLE_COLUMN_DATA_CHANGE(UUID, CREATE_TIME, COLUMN_NAME, DATA_TYPE,DATA_TEXT_VALUE)
            values(trgUuid,systimestamp, '${tabCol.columnName}', '${tabCol.dataType}',:NEW.${tabCol.columnName});
            end if;
        </if>
        <if tabCol.dataType='CLOB'>
            if updating('${tabCol.columnName}') then
            insert into DI_TABLE_COLUMN_DATA_CHANGE(UUID, CREATE_TIME, COLUMN_NAME, DATA_TYPE,DATA_CLOB_VALUE)
            values(trgUuid,systimestamp, '${tabCol.columnName}', '${tabCol.dataType}',:NEW.${tabCol.columnName});

            end if;
        </if>


        <if tabCol.dataType='BLOB'>
            if updating('${tabCol.columnName}') then
            insert into DI_TABLE_COLUMN_DATA_CHANGE(UUID, CREATE_TIME, COLUMN_NAME, DATA_TYPE,DATA_BLOB_VALUE)
            values(trgUuid,systimestamp, '${tabCol.columnName}', '${tabCol.dataType}',:NEW.${tabCol.columnName});
            end if;
        </if>
    </if>
</list>

<#-- 插入主表数据新增记录 -->
<#-- 联合主键处理 -->
<if isCompositeKey=true>
    insert into DI_TABLE_DATA_CHANGE (UUID,CREATE_TIME,TABLE_NAME,PK_UUID,PK_COL_NAME,ACTION)
    values(trgUuid,systimestamp, '${tableName}',
    <list pkInfos as pkCol>:NEW.${pkCol.columnName}<#if pkCol_has_next>||';'||</#if></list>
    ,'
    <list pkInfos as pkCol>${pkCol.columnName}<#if pkCol_has_next>;</#if></list>
    ','UPDATE');
</if>
<#-- 非联合主键处理 -->
<if isCompositeKey=false>
    insert into DI_TABLE_DATA_CHANGE (UUID,CREATE_TIME,TABLE_NAME,PK_UUID,PK_COL_NAME,ACTION)
    values(trgUuid,systimestamp, '${tableName}', :NEW.
    <if (pkInfos?size=0)>UUID
        <else>${pkInfos[0]['columnName']!}</if>
    ,
    <if (pkInfos?size=0)>''
        <else>'${pkInfos[0]['columnName']!}'
    </if>
    ,'UPDATE');
</if>
end if;
when deleting then
<#-- 插入主表数据新增记录 -->
<#-- 联合主键处理 -->
<if isCompositeKey=true>
    insert into DI_TABLE_DATA_CHANGE (UUID,CREATE_TIME,TABLE_NAME,PK_UUID,PK_COL_NAME,ACTION)
    values(trgUuid,systimestamp, '${tableName}',
    <list pkInfos as pkCol>:OLD.${pkCol.columnName}<#if pkCol_has_next>||';'||</#if></list>
    ,'
    <list pkInfos as pkCol>${pkCol.columnName}<#if pkCol_has_next>;</#if></list>
    ','DELETE');
</if>
<#-- 非联合主键处理 -->
<if isCompositeKey=false>
    insert into DI_TABLE_DATA_CHANGE (UUID,CREATE_TIME,TABLE_NAME,PK_UUID,PK_COL_NAME,ACTION)
    values(trgUuid,systimestamp, '${tableName}', :OLD.
    <if (pkInfos?size=0)>UUID
        <else>${pkInfos[0]['columnName']!}</if>
    ,
    <if (pkInfos?size=0)>''
        <else>'${pkInfos[0]['columnName']!}'
    </if>
    ,'DELETE');
</if>

end case;

end trg_4_di_${callName};