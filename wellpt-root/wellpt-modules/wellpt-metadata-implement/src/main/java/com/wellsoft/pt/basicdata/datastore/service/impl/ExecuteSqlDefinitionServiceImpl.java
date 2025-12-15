package com.wellsoft.pt.basicdata.datastore.service.impl;

import com.google.common.collect.Maps;
import com.wellsoft.pt.basicdata.datastore.dao.impl.ExecuteSqlDefinitionDaoImpl;
import com.wellsoft.pt.basicdata.datastore.entity.ExecuteSqlDefinitionEntity;
import com.wellsoft.pt.basicdata.datastore.service.ExecuteSqlDefinitionService;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import com.wellsoft.pt.jpa.support.NativeAliasToEntityMapResultTransformer;
import com.wellsoft.pt.jpa.template.TemplateEngineFactory;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.io.IOUtils;
import org.hibernate.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * Description:
 *
 * @author chenq
 * @date 2018/9/4
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2018/9/4    chenq		2018/9/4		Create
 * </pre>
 */
@Service
public class ExecuteSqlDefinitionServiceImpl extends
        AbstractJpaServiceImpl<ExecuteSqlDefinitionEntity, ExecuteSqlDefinitionDaoImpl, String> implements
        ExecuteSqlDefinitionService {


    @Override
    public String getSqlById(String id) {
        try {
            ExecuteSqlDefinitionEntity entity = this.getById(id);
            if (entity != null) {
                return IOUtils.toString(entity.getSqlContent().getCharacterStream());
            }
        } catch (Exception e) {
        }
        return null;
    }

    @Override
    public ExecuteSqlDefinitionEntity getById(String id) {
        try {
            List<ExecuteSqlDefinitionEntity> list = dao.listByFieldEqValue("id", id);
            if (CollectionUtils.isNotEmpty(list)) {
                return list.get(0);
            }
        } catch (Exception e) {
        }
        return null;
    }

    @Override
    @Transactional
    public void deleteByIds(List<String> ids) {
        this.dao.deleteByIds(ids);
    }

    @Override
    @Transactional
    public int executeUpdate(String sql, Map<String, Object> params) {
        return buildQuery(sql, params).executeUpdate();
    }

    @Override
    public List executeQrySQL(String sql, Map<String, Object> params) {
        return buildQuery(sql, params).setResultTransformer(
                NativeAliasToEntityMapResultTransformer.INSTANCE).list();
    }

    private Query buildQuery(String sql, Map<String, Object> params) {

        Query query = this.dao.getSession().createSQLQuery(sql);
        Map<String, Object> baseParams = TemplateEngineFactory.getExplainRootModel();
        if (MapUtils.isNotEmpty(params)) {
            baseParams.putAll(params);
        }
        String[] namedParemeters = query.getNamedParameters();
        Map<String, Object> executeParams = Maps.newHashMap();
        for (String p : namedParemeters) {
            if (baseParams.containsKey(p)) {
                executeParams.put(p, baseParams.get(p));
            } else {
                throw new RuntimeException("不存在命名参数[" + p + "]的设值");
            }
        }
        if (MapUtils.isNotEmpty(executeParams)) {
            query.setProperties(executeParams);
        }
        return query;
    }


}
