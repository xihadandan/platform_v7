package com.wellsoft.pt.dyform.implement.definition.dao.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.wellsoft.context.jdbc.support.PagingInfo;
import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.pt.dyform.implement.definition.dao.FormDefinitionDao;
import com.wellsoft.pt.dyform.implement.definition.entity.FormDefinition;
import com.wellsoft.pt.dyform.implement.definition.enums.DyformTypeEnum;
import com.wellsoft.pt.jpa.dao.impl.AbstractJpaDaoImpl;
import com.wellsoft.pt.jpa.hibernate4.NamedQueryScriptLoader;
import com.wellsoft.pt.security.access.RequestSystemContextPathResolver;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class FormDefinitionDaoImpl extends AbstractJpaDaoImpl<FormDefinition, String> implements
        FormDefinitionDao {

    @Override
    public long countById(String id) {
        Map<String, Object> hashmap = new HashMap<String, Object>();
        hashmap.put("id", id);
        StringBuilder hql = new StringBuilder();
        hql.append("select count(1) from FormDefinition form_def where form_def.id = :id");
        return countByHQL(hql.toString(), hashmap);

    }

    @Override
    public List<FormDefinition> getAll() {
        return this.listByHQL("from FormDefinition", new HashMap<String, Object>());
    }

    @Override
    public boolean isTableExist(final String tblName) {
        if (StringUtils.isBlank(tblName)) {
            return false;
        }
        Map<String, Object> params = Maps.newHashMap();
        params.put("tblName", tblName);
        return ((Number) this.getNumberBySQL(
                generateDynamicNamedQueryString(this.sessionFactory(), "isExistTable", null),
                params)).longValue() > 0;
    }

    @Override
    public List<FormDefinition> queryByName(String displayName, PagingInfo paginInfo) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("name", "%" + displayName + "%");
        // return this.query(" from FormDefinition o where o.name like :name ",
        // params, FormDefinition.class, paginInfo);
        return this.listByHQL(" from FormDefinition o where o.name like :name ", params);
    }

    @Override
    public String queryDbCharacterSet() {

        String queryScript = NamedQueryScriptLoader.generateDynamicNamedQueryString(
                sessionFactory(), "queryDatabaseCharacter", null);
        return getCharSequenceBySQL(queryScript, null);
    }

    @Override
    public List<FormDefinition> queryByNameOrIdOrTableName(String searchValue,
                                                           PagingInfo pagingInfo,
                                                           List<String> excludeUuids, List<String> systemUnitIds) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("searchValue", "%" + searchValue + "%");
        params.put("excludeUuids", excludeUuids);
        params.put("formTypes", Lists.<String>newArrayList("P"));
        params.put("currentSystem", RequestSystemContextPathResolver.system());
        params.put("tenantId", SpringSecurityUtils.getCurrentTenantId());
        if (CollectionUtils.isNotEmpty(systemUnitIds)) {
            params.put("systemUnitIds", systemUnitIds);
        }
        return this.listByNameHQLQuery("queryByNameOrIdOrTableName4new", params, pagingInfo);
    }


    @Override
    public List<FormDefinition> queryTypeByNameOrIdOrTableName(String pFormUuid,
                                                               PagingInfo pagingInfo,
                                                               String searchValue,
                                                               String... fromType) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("formType", fromType);
        params.put("pFormUuid", pFormUuid);
        params.put("searchValue", "%" + searchValue + "%");
        FormDefinition pFormDefinition = null;
        if (StringUtils.isNotBlank(pFormUuid) && (pFormDefinition = getOne(pFormUuid)) != null) {
            params.put("pTableName", pFormDefinition.getTableName());
        }

        return this.listByNameHQLQuery("queryTypeByNameOrIdOrTableName4new", params, pagingInfo);
    }

    @Override
    public List<FormDefinition> queryByNameAndTableName(String tableName, String searchValue, PagingInfo pagingInfo) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("searchValue", "%" + searchValue + "%");
        params.put("tableName", tableName);
        return this.listByNameHQLQuery("queryByNameAndTableName4new", params, pagingInfo);
    }

    @Override
    public List<QueryItem> queryFormDefinitionSelect(Map<String, Object> param) {
        return this.listQueryItemByNameHQLQuery("queryFormDefinitionSelect", param, new PagingInfo(1, 20));
    }

    @Override
    public List<FormDefinition> getFormDefinitionsByPformUuidAndFormType(String pFromUuid,
                                                                         DyformTypeEnum formType) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("pFromUuid", pFromUuid);
        params.put("formType", formType.getValue());
        return listByHQL(
                "from FormDefinition form_def where form_def.pFormUuid = :pFromUuid and form_def.formType = :formType",
                params);
    }

    @Override
    public List<FormDefinition> getAllFormDefinitionBySystemUnitId(String systemUnitId) {
        StringBuffer hql = new StringBuffer();
        Map<String, Object> params = new HashMap<String, Object>();
        hql.append("from FormDefinition form_def where 1 = 1");
        /*于2018-05-25注释  创建流程引用表单*/
        // if (StringUtils.isNotBlank(systemUnitId)) {
        // hql.append(" and form_def.systemUnitId = :systemUnitId ");
        // params.put("systemUnitId", systemUnitId);
        //
        // }
        hql.append("   order by form_def.code, form_def.id, form_def.version desc ");

        return listByHQL(hql.toString(), params);
    }
}
