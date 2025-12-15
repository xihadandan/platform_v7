package com.wellsoft.pt.jpa.hibernate4;

import com.wellsoft.context.Context;
import com.wellsoft.context.config.Config;
import com.wellsoft.pt.jpa.support.HibernateMappingLocationsFactoryBean;
import com.wellsoft.pt.jpa.support.SupportMultiDatabaseConfigurationBuilder;
import com.wellsoft.pt.jpa.template.TemplateEngineFactory;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;
import org.hibernate.engine.spi.NamedSQLQueryDefinition;
import org.hibernate.internal.SessionFactoryImpl;
import org.hibernate.persister.entity.NamedQueryLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;

import java.util.Map;

/**
 * Description:
 *
 * @author chenq
 * @date 2019/3/11
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2019/3/11    chenq		2019/3/11		Create
 * </pre>
 */
public class NamedQueryScriptLoader {

    private static final Logger LOGGER = LoggerFactory.getLogger(NamedQueryLoader.class);

    public static String generateDynamicNamedQueryString(SessionFactory sessionFactory, String queryName,
                                                         Map<String, Object> values) {
        return generateDynamicNamedQueryString(sessionFactory, queryName, values, false);
    }

    /**
     * 增加是否需要注释
     * oracle注释特殊意义，强制hash关联场景
     *
     * @return
     * @author baozh
     * @date 2022/1/12 11:00
     * @params *@params
     */
    public static String generateDynamicNamedQueryString(SessionFactory sessionFactory, String queryName,
                                                         Map<String, Object> values, boolean isNeedNotes) {
        String dataBaseTypeSuffix = SupportMultiDatabaseConfigurationBuilder
                .resolveDatabaseType(((SessionFactoryImpl) sessionFactory).getDialect());
        boolean namedSqlReloadable = Boolean.TRUE.toString().equals(
                Config.getValue(HibernateMappingLocationsFactoryBean.KEY_HIBERNATE_NAMED_SQL_RELOADABLE));
        //优先获取匹配数据库的命名查询
        String queryScriptStr = getQueryScriptString(sessionFactory, dataBaseTypeSuffix + queryName, namedSqlReloadable);
        if (queryScriptStr == null) {
            queryScriptStr = getQueryScriptString(sessionFactory, queryName, namedSqlReloadable);
        }
        if (StringUtils.isBlank(queryScriptStr)) {
            throw new HibernateException("未查询到命名查询[" + queryName + "]!");
        }
        // FreeMarker模板编译
        try {
            if (isNeedNotes) {
                queryScriptStr = TemplateEngineFactory.getDefaultTemplateEngine().process(
                        queryScriptStr, values);
            } else {
                // 去掉/* */的注释
                queryScriptStr = TemplateEngineFactory.getDefaultTemplateEngine().process(
                        queryScriptStr.replaceAll("/\\*.*\\*/", ""), values);
            }
        } catch (Exception e) {
            LOGGER.error(ExceptionUtils.getStackTrace(e));
            throw new RuntimeException(e);
        }
        return queryScriptStr;
    }

    private static String getQueryScriptString(SessionFactory sessionFactory, String queryName,
                                               boolean namedSqlReloadable) {
        NamedSQLQueryDefinition definition = null;
        if (namedSqlReloadable || Context.isDebug()) {
            try {
                Resource resource = HibernateMappingLocationsFactoryBean.getResource(queryName);
                if (resource != null) {
                    SupportMultiDatabaseConfigurationBuilder cfg = new SupportMultiDatabaseConfigurationBuilder(null);
                    cfg.currentResource(resource);
                    cfg.addInputStream(resource.getInputStream());
                    cfg.buildMappings();
                    definition = (NamedSQLQueryDefinition) cfg.getNamedSQLQueries().get(queryName);

                } else {

                    definition = ((SessionFactoryImpl) sessionFactory).getNamedSQLQuery(queryName);
                }
            } catch (Exception e) {
                LOGGER.error(ExceptionUtils.getStackTrace(e));
                definition = ((SessionFactoryImpl) sessionFactory).getNamedSQLQuery(queryName);
            }
        } else {
            definition = ((SessionFactoryImpl) sessionFactory).getNamedSQLQuery(queryName);
        }
        return definition != null ? definition.getQueryString() : null;
    }
}
