/*
 * @(#)2015年9月17日 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.selective.support;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2015年9月17日.1	zhulh		2015年9月17日		Create
 * </pre>
 * @date 2015年9月17日
 */
public class SqlQuerySelectiveData extends SelectiveData {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 2310237849956421862L;

    private String sessionFactoryId;
    // 查询的SQL
    private String querySql;

    //值回调
    private SqlSelectDataRender valueRender;

    private SqlSelectDataRender labelRender;


    /**
     * @return the sessionFactoryId
     */
    public String getSessionFactoryId() {
        return sessionFactoryId;
    }

    /**
     * @param sessionFactoryId 要设置的sessionFactoryId
     */
    public void setSessionFactoryId(String sessionFactoryId) {
        this.sessionFactoryId = sessionFactoryId;
    }

    /**
     * @return the querySql
     */
    public String getQuerySql() {
        return querySql;
    }

    /**
     * @param querySql 要设置的querySql
     */
    public void setQuerySql(String querySql) {
        this.querySql = querySql;
    }


    public SqlSelectDataRender getValueRender() {
        return valueRender;
    }

    public void setValueRender(
            SqlSelectDataRender valueRender) {
        this.valueRender = valueRender;
    }


    public SqlSelectDataRender getLabelRender() {
        return labelRender;
    }

    public void setLabelRender(
            SqlSelectDataRender labelRender) {
        this.labelRender = labelRender;
    }

    public static enum SqlQrySelectiveDataType {
        TENANT_NAME_ID("租户数据"), ORG_ALL_ELE("组织元素"), ORG_DEPARTMENT_NAME_ID("部门"), ORG_JOB_PATH_ID(
                "职位"), ORG_PUBLIC_GROUP_NAME_ID("公共群组"), APP_PAGE_DEFINITION_NAME_UUID(
                "页面配置"), ORG_USER_NAME_ID("用户");

        private String name;

        SqlQrySelectiveDataType(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    public static interface SqlSelectDataRender {

        String render(String label, String value);

    }

}
