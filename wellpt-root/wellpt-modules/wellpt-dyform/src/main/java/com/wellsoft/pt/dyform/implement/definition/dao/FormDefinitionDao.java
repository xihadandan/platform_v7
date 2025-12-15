package com.wellsoft.pt.dyform.implement.definition.dao;

import com.wellsoft.context.jdbc.support.PagingInfo;
import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.pt.dyform.implement.definition.entity.FormDefinition;
import com.wellsoft.pt.dyform.implement.definition.enums.DyformTypeEnum;
import com.wellsoft.pt.jpa.dao.JpaDao;

import java.util.List;
import java.util.Map;

public interface FormDefinitionDao extends JpaDao<FormDefinition, String> {

    long countById(String id);

    List<FormDefinition> getAll();

    public boolean isTableExist(final String tblName);

    List<FormDefinition> queryByName(String searchValue, PagingInfo pagingInfo);

    String queryDbCharacterSet();

    List<FormDefinition> queryByNameOrIdOrTableName(String searchValue, PagingInfo pagingInfo, List<String> excludeUuids, List<String> systemUnitIds);


    public abstract List<FormDefinition> getFormDefinitionsByPformUuidAndFormType(String pFromUuid,
                                                                                  DyformTypeEnum formType);

    List<FormDefinition> getAllFormDefinitionBySystemUnitId(String systemUnitId);

    public abstract List<FormDefinition> queryTypeByNameOrIdOrTableName(String pFormUuid, PagingInfo pagingInfo,
                                                                        String searchValue, String... fromType);

    List<FormDefinition> queryByNameAndTableName(String tableName, String searchValue, PagingInfo pagingInfo);

    List<QueryItem> queryFormDefinitionSelect(Map<String, Object> param);
}
