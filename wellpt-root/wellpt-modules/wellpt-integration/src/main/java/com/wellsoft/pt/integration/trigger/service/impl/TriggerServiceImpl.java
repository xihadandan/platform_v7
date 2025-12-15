/*
 * @(#)2014-12-3 V1.0
 *
 * Copyright 2014 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.integration.trigger.service.impl;

import com.wellsoft.context.config.Config;
import com.wellsoft.context.jdbc.support.PropertyFilter;
import com.wellsoft.context.jdbc.support.QueryInfo;
import com.wellsoft.pt.integration.bean.TableTigInfo;
import com.wellsoft.pt.integration.bean.TigPkInfo;
import com.wellsoft.pt.integration.bean.TigTabColsInfo;
import com.wellsoft.pt.integration.entity.SysProperties;
import com.wellsoft.pt.integration.facade.ExchangedataApiFacade;
import com.wellsoft.pt.integration.security.ExchangeConfig;
import com.wellsoft.pt.integration.support.ExchangeDataResultTransformer;
import com.wellsoft.pt.integration.trigger.service.TriggerService;
import com.wellsoft.pt.jpa.service.impl.BaseServiceImpl;
import com.wellsoft.pt.jpa.template.TemplateEngineFactory;
import com.wellsoft.pt.jpa.util.DaoUtils;
import com.wellsoft.pt.mt.entity.Tenant;
import com.wellsoft.pt.mt.facade.service.TenantFacadeService;
import com.wellsoft.pt.security.util.TenantContextHolder;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ClassUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.wellsoft.pt.integration.security.ExchangeConfig.ISSYNBACKFIELD;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2014-12-3.1	zhulh		2014-12-3		Create
 * </pre>
 * @date 2014-12-3
 */
@Service
@Transactional
public class TriggerServiceImpl extends BaseServiceImpl implements TriggerService {

    public static final String TRIGGER_CREATE = "trigger_create.ftl";

    public static final String TRIGGER_DISABLE = "trigger_disable.ftl";

    public static final String TRIGGER_ENABLE = "trigger_enable.ftl";

    public static final String TRIGGER_DROP = "trigger_drop.ftl";

    private ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();

    @Autowired
    private TenantFacadeService tenantService;
    @Autowired
    private ExchangedataApiFacade exchangedataApiFacade;

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.app.xzsp.dx.trigger.service.TriggerService#create()
     */
    @Override
    public String[] generateCreateTriggerSql(String tableName) {
        Tenant tenant = tenantService.getById(TenantContextHolder.getTenantId());
        String owner = tenant.getJdbcUsername();

        List<String> triggerSql = new ArrayList<String>();
        List<TigTabColsInfo> tabColsInfos = getTabColsInfos(owner, tableName);
        List<TigPkInfo> pkInfos = getPkInfos(owner, tableName);

        Map<String, SysProperties> sysPropertiess = exchangedataApiFacade.getAllSysProperties(
                "SYSPROPERTIES");
        String xt_wl = sysPropertiess.get("xt_wl").getProValue();
        int direction = xt_wl.equals("in") ? 1 : 2;

        // 添加同步标识(如果表不存在同步标识)
        boolean notExistSynBackField = true;
        for (TigTabColsInfo tabColsInfo : tabColsInfos) {
            if (ISSYNBACKFIELD.equals(tabColsInfo.getColumnName())) {
                notExistSynBackField = false;
                break;
            }
        }
        if (notExistSynBackField) {
            triggerSql.add(
                    "alter table " + tableName + " add " + ISSYNBACKFIELD + " VARCHAR2(255 CHAR)");
        }

        // 添加触发器
        String ftlString = getTriggerFtl(TRIGGER_CREATE);
        Map<String, Object> root = new HashMap<String, Object>();
        root.put("tableName", tableName);
        root.put("callName", getCallName(tableName));
        root.put("tabColsInfos", tabColsInfos);
        root.put("pkInfos", pkInfos);
        root.put("isCompositeKey", pkInfos.size() > 1 ? true : false);
        root.put("direction", direction);
        try {
            triggerSql.add(
                    TemplateEngineFactory.getDefaultTemplateEngine().process(ftlString, root));
            System.out.println("********************" + tableName + "********************");
            // System.out.println(triggerSql);
        } catch (Exception e) {
            logger.info(e.getMessage());
            throw new RuntimeException(e);
        }
        return triggerSql.toArray(new String[0]);
    }

    @Override
    public void generateCreateTrigger(String tableName, String triggerFtl) {
        Tenant tenant = tenantService.getById(TenantContextHolder.getTenantId());
        String owner = tenant.getJdbcUsername();

        Map<String, Object> root = new HashMap<String, Object>();
        List<TigTabColsInfo> tabColsInfos = getTabColsInfos(owner, tableName);
        List<TigPkInfo> pkInfos = getPkInfos(owner, tableName);
        root.put("tableName", tableName);
        root.put("callName", getCallName(tableName));
        root.put("tabColsInfos", tabColsInfos);
        root.put("pkInfos", pkInfos);
        root.put("isCompositeKey", pkInfos.size() > 1 ? true : false);
        try {
            // 添加同步标识(如果表不存在同步标识)
            boolean notExistSynBackField = true;
            for (TigTabColsInfo tabColsInfo : tabColsInfos) {
                if (ISSYNBACKFIELD.equals(tabColsInfo.getColumnName())) {
                    notExistSynBackField = false;
                    break;
                }
            }
            if (notExistSynBackField) {
                DaoUtils.executeSql(
                        "alter table " + tableName + " add " + ISSYNBACKFIELD + " VARCHAR2(255 CHAR)");
            }

            String trigSql = TemplateEngineFactory.getDefaultTemplateEngine().process(triggerFtl,
                    root);
            DaoUtils.executeSql(trigSql);


        } catch (Exception e) {
            // throw new RuntimeException("创建表的DML触发器异常");
        }

    }

    /**
     * @return
     */
    private List<TigTabColsInfo> getTabColsInfos(String owner, String tableName) {
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("owner", owner);
        values.put("tableName", tableName);
        return this.nativeDao.namedQuery("getTabColsInfo", values, TigTabColsInfo.class);
    }

    /**
     * @return
     */
    public List<TableTigInfo> query(QueryInfo queryInfo) {
        Map<String, Object> values = new HashMap<String, Object>();
        List<PropertyFilter> propertyFilters = null;
        if (queryInfo != null && (propertyFilters = queryInfo.getPropertyFilters()) != null) {
            PropertyFilter propertyFilter = null;
            if (propertyFilters != null && propertyFilters.size() > 0) {
                propertyFilter = propertyFilters.get(0);
                values.put("tableName", propertyFilter.getMatchValue());
            }
        }
        List<TableTigInfo> tableTigInfos = this.nativeDao.namedQuery("getTableTigInfo", values,
                TableTigInfo.class);
        List<TableTigInfo> list = new ArrayList<TableTigInfo>();
        if (tableTigInfos == null || tableTigInfos.size() <= 0) {
            return list;
        }
        String[] notTigTable = ExchangeConfig.DONOTTIGDATA.split(";");
        for (TableTigInfo tableTigInfo : tableTigInfos) {
            String tbName = tableTigInfo.getTableName();
            boolean flag = true;
            for (int i = 0; i < notTigTable.length; i++) {
                if (!StringUtils.isBlank(notTigTable[i]) && notTigTable[i].endsWith("_")) {
                    if (tbName.startsWith(notTigTable[i])) {
                        flag = false;
                        break;
                    }
                } else if (!StringUtils.isBlank(notTigTable[i]) && !notTigTable[i].endsWith("_")) {
                    if (tbName.equals(notTigTable[i])) {
                        flag = false;
                        break;
                    }
                }
            }
            if (flag) {
                list.add(tableTigInfo);
            }
        }
        return list;
    }

    /**
     * @return
     */
    private List<TigPkInfo> getPkInfos(String owner, String tableName) {
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("owner", owner);
        values.put("tableName", tableName);
        return this.nativeDao.namedQuery("getPkInfo", values, TigPkInfo.class);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.app.xzsp.dx.trigger.service.TriggerService#disable()
     */
    @Override
    public String generateDisableTriggerSql(String tableName) {
        String ftlString = getTriggerFtl(TRIGGER_DISABLE);
        Map<String, Object> root = new HashMap<String, Object>();
        root.put("tableName", getCallName(tableName));
        String triggerSql = null;
        try {
            triggerSql = TemplateEngineFactory.getDefaultTemplateEngine().process(ftlString, root);
        } catch (Exception e) {
            logger.info(e.getMessage());
            throw new RuntimeException(e);
        }
        return triggerSql;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.app.xzsp.dx.trigger.service.TriggerService#enable()
     */
    @Override
    public String generateEnableTriggerSql(String tableName) {
        String ftlString = getTriggerFtl(TRIGGER_ENABLE);
        Map<String, Object> root = new HashMap<String, Object>();
        root.put("tableName", getCallName(tableName));
        String triggerSql = null;
        try {
            triggerSql = TemplateEngineFactory.getDefaultTemplateEngine().process(ftlString, root);
        } catch (Exception e) {
            logger.info(e.getMessage());
            throw new RuntimeException(e);
        }
        return triggerSql;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.app.xzsp.dx.trigger.service.TriggerService#drop()
     */
    @Override
    public String generateDropTriggerSql(String tableName) {
        String ftlString = getTriggerFtl(TRIGGER_DROP);
        Map<String, Object> root = new HashMap<String, Object>();
        root.put("tableName", getCallName(tableName));
        String triggerSql = null;
        try {
            triggerSql = TemplateEngineFactory.getDefaultTemplateEngine().process(ftlString, root);
            // this.nativeDao.executeSql(triggerSql);
        } catch (Exception e) {
            logger.info(e.getMessage());
            throw new RuntimeException(e);
        }
        return triggerSql;
    }

    /**
     * @param triggerCreate
     * @return
     */
    private String getTriggerFtl(String triggerFtl) {
        try {
            String basePackage = Config.BASE_PACKAGES;
            String ftlPath = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX
                    + ClassUtils.convertClassNameToResourcePath(basePackage) + "/**/*" + triggerFtl;
            Resource[] resources = resourcePatternResolver.getResources(ftlPath);
            return IOUtils.toString(resources[0].getInputStream());
        } catch (Exception e) {
            logger.info(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<String> getAlltableName() {

        String sql = "select t.TABLE_NAME as tablename  from user_tables t order by t.TABLE_NAME";
        Session session = this.dao.getSession();
        List<Map<String, Object>> queryItems = session.createSQLQuery(sql)
                .setResultTransformer(ExchangeDataResultTransformer.INSTANCE).list();
        List<String> list = new ArrayList<String>();
        for (Map<String, Object> nameMap : queryItems) {
            String tbName = nameMap.get("tablename").toString();
            String[] notTigTable = ExchangeConfig.DONOTTIGDATA.split(";");
            boolean flag = true;
            for (int i = 0; i < notTigTable.length; i++) {
                if (!StringUtils.isBlank(notTigTable[i]) && notTigTable[i].endsWith("_")) {
                    if (tbName.startsWith(notTigTable[i])) {
                        flag = false;
                        break;
                    }
                } else if (!StringUtils.isBlank(notTigTable[i]) && !notTigTable[i].endsWith("_")) {
                    if (tbName.equals(notTigTable[i])) {
                        flag = false;
                        break;
                    }
                }
            }
            if (flag) {
                list.add(tbName);
            }
        }
        return list;
    }

    public String getCallName(String tableName) {
        int ws = 26;
        if (tableName.length() > ws) {
            String tableName_1 = tableName.split("_")[0] + "_";
            String callName = tableName_1
                    + tableName.substring(tableName.length() - (ws - tableName_1.length()),
                    tableName.length());
            return callName;
        } else {
            return tableName;
        }

    }
}
