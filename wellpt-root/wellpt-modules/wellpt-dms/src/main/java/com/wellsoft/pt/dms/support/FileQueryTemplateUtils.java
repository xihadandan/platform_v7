/*
 * @(#)Jan 24, 2018 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dms.support;

import com.google.common.collect.Sets;
import com.wellsoft.context.base.BaseObject;
import com.wellsoft.context.config.Config;
import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.pt.cache.CacheManager;
import com.wellsoft.pt.cache.config.CacheName;
import com.wellsoft.pt.dms.DmsDataManagementWebAppConfig;
import com.wellsoft.pt.jpa.datasource.DatabaseType;
import com.wellsoft.pt.jpa.template.TemplateEngineFactory;
import com.wellsoft.pt.org.facade.service.OrgFacadeService;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.cache.Cache;
import org.springframework.cache.Cache.ValueWrapper;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.HashSet;
import java.util.Set;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * Jan 24, 2018.1	zhulh		Jan 24, 2018		Create
 * </pre>
 * @date Jan 24, 2018
 */
public class FileQueryTemplateUtils {

    /**
     * 获取跟用户相关的所有组织ID
     *
     * @return
     */
    public static FileQueryTemplate getTemplateOfUserOrgIds(OrgFacadeService orgFacadeService, String userId) {
        CacheManager cacheManager = ApplicationContextHolder.getBean(CacheManager.class);
        Cache cache = cacheManager.getCache(CacheName.DEFAULT);
        String dataBaseType = Config.getValue("database.type");
        FileQueryTemplate template = null;
        String tpl = null;
        if (cache != null) {
            ValueWrapper valueWrapper = cache.get("getTemplateOfUserOrgIds_" + dataBaseType + "_" + userId);
            if (valueWrapper != null) {
                template = (FileQueryTemplate) valueWrapper.get();
            }
        }
        if (template == null) {
            Set<String> relatedIds = orgFacadeService.getUserRelatedIds(userId);
            if (CollectionUtils.isEmpty(relatedIds)) {
                relatedIds.add("-1");
            }
            Set<String> roleIds = getCurrentUserRoleIds();
            Set<String> orgIds = Sets.newHashSet(relatedIds);
            orgIds.addAll(roleIds);
            tpl = "select * from( select u_.user_id as org_id from user_info u_ where u_.user_id='" + userId + "'";
            if (CollectionUtils.isNotEmpty(orgIds)) {
                Set<String> idSet = new HashSet<String>(orgIds);
                for (String id : idSet) {
                    if (DatabaseType.MySQL5.getName().equalsIgnoreCase(dataBaseType)) {
                        tpl += " union all select org_id from (select '" + id + "' as org_id from user_info limit 1) org_" + id;
                    } else {
                        tpl += " union all select '" + id + "' as org_id from user_info where rownum = 1 ";
                    }
                }
            }
            tpl += " )";
            template = new FileQueryTemplate(tpl, relatedIds, roleIds);
            if (cache != null) {
                cache.put("getTemplateOfUserOrgIds_" + dataBaseType + "_" + userId, template);
            }
        }
        return template;
        // String templateName = "ftl/unit_in_expression_userorgids.ftl";
        // String tpl = "";
        // try {
        // tpl =
        // TemplateEngineFactory.getDefaultTemplateEngine().getTemplateAsString(
        // Class.forName("com.wellsoft.pt.unit.support.criterion.AbstractUnitTreeCriterion"),
        // templateName);
        // } catch (ClassNotFoundException e) {
        // throw new RuntimeException(e);
        // }
        // return tpl;
    }

    /**
     * 获取当前用户分配的角色
     *
     * @return
     */
    private static Set<String> getCurrentUserRoleIds() {
        Set<String> roleIds = Sets.newHashSet();
        UserDetails user = SpringSecurityUtils.getCurrentUser();
        user.getAuthorities().forEach(auth -> {
            roleIds.add(auth.getAuthority());
        });
        return roleIds;
    }

    /**
     * @return
     */
    public static String getTemplateOfListAllFiles() {
        String templateName = "file/criterion/ftl/dms_file_dyform_expression_list_all_files.ftl";
        String tpl = TemplateEngineFactory.getDefaultTemplateEngine().getTemplateAsString(DmsDataManagementWebAppConfig.class, templateName);
        return tpl;
    }

    public static final class FileQueryTemplate extends BaseObject {
        private static final long serialVersionUID = 908989395236270280L;

        private String template;

        private Set<String> orgIds;

        private Set<String> roleIds;

        /**
         * @param template
         * @param orgIds
         * @param roleIds
         */
        public FileQueryTemplate(String template, Set<String> orgIds, Set<String> roleIds) {
            this.template = template;
            this.orgIds = orgIds;
            this.roleIds = roleIds;
        }

        /**
         * @return the template
         */
        public String getTemplate() {
            return template;
        }

        /**
         * @param template 要设置的template
         */
        public void setTemplate(String template) {
            this.template = template;
        }

        /**
         * @return the orgIds
         */
        public Set<String> getOrgIds() {
            return orgIds;
        }

        /**
         * @param orgIds 要设置的orgIds
         */
        public void setOrgIds(Set<String> orgIds) {
            this.orgIds = orgIds;
        }

        /**
         * @return the roleIds
         */
        public Set<String> getRoleIds() {
            return roleIds;
        }

        /**
         * @param roleIds 要设置的roleIds
         */
        public void setRoleIds(Set<String> roleIds) {
            this.roleIds = roleIds;
        }

        /**
         * @return
         */
        public Set<String> getSids() {
            Set<String> sids = Sets.newHashSet();
            if (CollectionUtils.isNotEmpty(orgIds)) {
                sids.addAll(orgIds);
            }
            if (CollectionUtils.isNotEmpty(roleIds)) {
                sids.addAll(roleIds);
            }
            return sids;
        }
    }

}
