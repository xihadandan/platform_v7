package com.wellsoft.pt;

import com.wellsoft.context.config.Config;
import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.pt.basicdata.selective.support.SqlQuerySelectiveData;
import com.wellsoft.pt.basicdata.selective.support.SqlQuerySelectiveDataFactory;
import com.wellsoft.pt.multi.org.facade.service.OrgApiFacade;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Description:
 *
 * @author chenq
 * @date 2019/11/15
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2019/11/15    chenq		2019/11/15		Create
 * </pre>
 */
@Configuration
public class CommonConfiguration {


    @Bean
    public SqlQuerySelectiveDataFactory commonSqlQuerySelectiveData() {
        SqlQuerySelectiveDataFactory factory = new SqlQuerySelectiveDataFactory();
        factory.setSessionFactoryId(Config.COMMON_SESSION_FACTORY_BEAN_NAME);
        factory.setItemLabel("name");
        factory.setItemValue("id");
        factory.setConfigKey(SqlQuerySelectiveData.SqlQrySelectiveDataType.TENANT_NAME_ID.name());
        factory.setQuerySql(
                "select t.account as accout, t.id as id ,t.name as name from mt_tenant t order by t.id asc");
        return factory;
    }


    @Bean
    public SqlQuerySelectiveDataFactory orgElementSqlQuerySelectiveData() {
        SqlQuerySelectiveDataFactory factory = new SqlQuerySelectiveDataFactory();
        factory.setCacheName("sc");
        factory.setConfigKey(SqlQuerySelectiveData.SqlQrySelectiveDataType.ORG_ALL_ELE.name());
        factory.setQuerySql(
                "select t.name as label, t.id as value from multi_org_element t");
        return factory;
    }

    @Bean
    public SqlQuerySelectiveDataFactory orgDepartmentSqlQuerySelectiveData() {
        SqlQuerySelectiveDataFactory factory = new SqlQuerySelectiveDataFactory();
        factory.setCacheName("sc");
        factory.setConfigKey(
                SqlQuerySelectiveData.SqlQrySelectiveDataType.ORG_DEPARTMENT_NAME_ID.name());
        factory.setQuerySql(
                "select id as value,name as label from multi_org_element where type='D'");
        return factory;
    }


    @Bean
    public SqlQuerySelectiveDataFactory orgAccountsSqlQuerySelectiveData() {
        SqlQuerySelectiveDataFactory factory = new SqlQuerySelectiveDataFactory();
        factory.setCacheName("sc");
        factory.setConfigKey(SqlQuerySelectiveData.SqlQrySelectiveDataType.ORG_USER_NAME_ID.name());
        factory.setQuerySql(
                "select t.user_name as label, t.user_id as value from user_info t");
        return factory;
    }


    @Bean
    public SqlQuerySelectiveDataFactory orgJobPathSqlQuerySelectiveData() {
        SqlQuerySelectiveDataFactory factory = new SqlQuerySelectiveDataFactory();
        factory.setCacheName("sc");
        factory.setConfigKey(SqlQuerySelectiveData.SqlQrySelectiveDataType.ORG_JOB_PATH_ID.name());
        factory.setLabelRender(new SqlQuerySelectiveData.SqlSelectDataRender() {
            @Override
            public String render(String l, String v) {
                return ApplicationContextHolder.getBean(
                        OrgApiFacade.class).getDepartmentNamePathByJobIdPath(v, true);

            }
        });
        factory.setQuerySql(
                "select e.name as label,t.ele_id_path as value from multi_org_tree_node t " +
                        ",multi_org_element e where t.ele_id=e.id and e.type='J'");
        return factory;
    }

    @Bean
    public SqlQuerySelectiveDataFactory orgPublicGroupSqlQuerySelectiveData() {
        SqlQuerySelectiveDataFactory factory = new SqlQuerySelectiveDataFactory();
        factory.setCacheName("sc");
        factory.setConfigKey(
                SqlQuerySelectiveData.SqlQrySelectiveDataType.ORG_PUBLIC_GROUP_NAME_ID.name());
        factory.setQuerySql(
                " select t.name as label, t.id as value from multi_org_group t where t.type = 0");
        return factory;
    }

    @Bean
    public SqlQuerySelectiveDataFactory appPageDefinitionSqlQuerySelectiveData() {
        SqlQuerySelectiveDataFactory factory = new SqlQuerySelectiveDataFactory();
        factory.setCacheName("APP");
        factory.setConfigKey(
                SqlQuerySelectiveData.SqlQrySelectiveDataType.APP_PAGE_DEFINITION_NAME_UUID.name());
        factory.setCacheable(false);
        factory.setQuerySql(
                " select t.title as label, t.uuid as value from APP_PAGE_DEFINITION t ");
        return factory;
    }


}
