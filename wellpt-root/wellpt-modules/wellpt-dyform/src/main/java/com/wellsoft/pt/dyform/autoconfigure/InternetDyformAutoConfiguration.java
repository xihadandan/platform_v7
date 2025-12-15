/*
 * @(#)2020年10月27日 V1.0
 *
 * Copyright 2020 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dyform.autoconfigure;

import com.google.common.collect.Lists;
import com.wellsoft.context.component.tree.TreeNode;
import com.wellsoft.pt.common.web.json.JsonDataServicesConfig;
import com.wellsoft.pt.security.audit.support.AbstractResourceDataSource;
import com.wellsoft.pt.security.audit.support.ResourceDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Description: 互联网表单配置类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2020年10月27日.1	zhulh		2020年10月27日		Create
 * </pre>
 * @date 2020年10月27日
 */
@Configuration
@Conditional(OnInternetDyformCondition.class)
public class InternetDyformAutoConfiguration {

    /**
     * @param jsonDataServicesConfig
     */
    public InternetDyformAutoConfiguration(JsonDataServicesConfig jsonDataServicesConfig) {
        // 添加互联网表单可匿名访问的JDS服务
        addInternetDyformAnonymousServices(jsonDataServicesConfig);
    }

    /**
     * 添加互联网表单可匿名访问的JDS服务
     *
     * @param jsonDataServicesConfig
     */
    private void addInternetDyformAnonymousServices(JsonDataServicesConfig jsonDataServicesConfig) {
        List<String> anonymousServices = jsonDataServicesConfig.getAnonymousServices();
        anonymousServices.add("dataDictionaryService.getKeyValuePair");
        anonymousServices.add("dataDictionaryService.getAllDataDicAsTree");
        anonymousServices.add("cdDataStoreService.loadData");
        anonymousServices.add("cdDataStoreService.loadTreeNodes");
        anonymousServices.add("orgApiFacade.getNameByOrgEleIds");
        anonymousServices.add("mongoFileService.getNonioFilesFromFolder");
        anonymousServices.add("appContextService.getJavaScriptModuleConfigScript");
    }

    /**
     * @return
     */
    @Bean
    public ResourceDataSource getInternetDyformResourceDataSource() {
        return new InternetDyformResourceDataSource();
    }

    /**
     * Description: 互联网表单可访问的URL资源
     *
     * @author zhulh
     * @version 1.0
     *
     * <pre>
     * 修改记录:
     * 修改后版本	修改人		修改日期			修改内容
     * 2020年10月27日.1	zhulh		2020年10月27日		Create
     * </pre>
     * @date 2020年10月27日
     */
    private static class InternetDyformResourceDataSource extends AbstractResourceDataSource {

        /**
         * (non-Javadoc)
         *
         * @see com.wellsoft.pt.security.audit.support.AbstractResourceDataSource#getName()
         */
        @Override
        public String getName() {
            return "互联网表单权限资源";
        }

        /**
         * (non-Javadoc)
         *
         * @see com.wellsoft.pt.security.audit.support.AbstractResourceDataSource#getId()
         */
        @Override
        public String getId() {
            return "InternetDyformResource";
        }

        /**
         * (non-Javadoc)
         *
         * @see com.wellsoft.pt.security.audit.support.AbstractResourceDataSource#getOrder()
         */
        @Override
        public int getOrder() {
            return Integer.MAX_VALUE;
        }

        /**
         * (non-Javadoc)
         *
         * @see com.wellsoft.pt.security.audit.support.ResourceDataSource#getData(java.util.Map)
         */
        @Override
        public List<TreeNode> getData(Map<String, Object> params) {
            return Collections.emptyList();
        }

        /**
         * (non-Javadoc)
         *
         * @see com.wellsoft.pt.security.audit.support.AbstractResourceDataSource#getAnonymousResources()
         */
        @Override
        public List<String> getAnonymousResources() {
            return Lists.newArrayList("/pt/dyform/definition/open",// 打开表单
                    "/pt/dyform/definition/getFormDefinition",// 获取表单定义
                    "/pt/dyform/data/getFormDefinitionData",// 获取表单定义及数据
                    "/pt/dyform/data/validate/exists", // 表单数据验证是否存在
                    //"/pt/dyform/data/saveFormData", // 保存表单数据
                    //"/serialNumberService.generateSerialNumber", // 生成流水号
                    "/repository/file/mongo/download",// MONGO附件下载
                    "/repository/file/mongo/downAllFiles",// MONGO附件下载
                    "/json/data/services",// JSON数据服务
                    "/security/user/details"// 当前用户信息
            );
        }
    }

}
