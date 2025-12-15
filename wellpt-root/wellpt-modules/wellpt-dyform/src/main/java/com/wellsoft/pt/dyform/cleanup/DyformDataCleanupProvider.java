package com.wellsoft.pt.dyform.cleanup;

import com.google.common.collect.Maps;
import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.pt.basicdata.cleanup.AbstractDataCleanupDataCleanupProvider;
import com.wellsoft.pt.dyform.implement.definition.service.FormDefinitionService;
import com.wellsoft.pt.security.access.RequestSystemContextPathResolver;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * Description: 如何描述该类
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2024年04月24日   chenq	 Create
 * </pre>
 */
@Service
public class DyformDataCleanupProvider extends AbstractDataCleanupDataCleanupProvider {

    @Autowired
    FormDefinitionService formDefinitionService;


    @Override
    public String getType() {
        return "dyformData";
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public ExpectCleanupResult cleanup(Params params) {
        String t = params.optString("table");
        List<String> clearTypes = (List<String>) params.get("clearTypes");
        long total = 0L;
        if (StringUtils.isNotBlank(t)) {
            try {
                String system = StringUtils.defaultIfBlank(params.optString("system"), RequestSystemContextPathResolver.system());
                String underSystem = "";
                Map<String, Object> map = Maps.newHashMap();
                if (StringUtils.isNotBlank(system)) {
                    underSystem = " and a.system=:system and a.tenant=:tenant ";
                    map.put("system", system);
                    map.put("tenant", SpringSecurityUtils.getCurrentTenantId());
                }
                StringBuilder sql = null;

                // 删除表关联数据
                try {
                    sql = new StringBuilder("delete from ").append(t).append("_rl r where 1=1 ");
                    if (StringUtils.isNotBlank(underSystem)) {
                        sql.append(" and exists ( select 1 from ").append(t).append(" a where a.uuid=r.mainform_data_uuid ").append(underSystem).append(")");
                    }
                    if (!clearTypes.contains("dyformData") && clearTypes.contains("dyformFixedData")) {
                        sql.append(" and exists ( select 1 from data_mark_type t where t.data_uuid = r.mainform_data_uuid) ");
                    }
                    nativeDao.batchExecute(sql.toString(), map);
                } catch (Exception e) {
                    logger.warn("删除表单关系数据异常: {}", e.getMessage());
                }

                try {
                    sql = new StringBuilder("delete from ").append(t).append("_dl r where 1=1");
                    if (StringUtils.isNotBlank(underSystem)) {
                        sql.append(" and exists ( select 1 from ").append(t).append(" a where a.uuid=r.data_uuid ").append(underSystem).append(")");
                    }
                    if (!clearTypes.contains("dyformData") && clearTypes.contains("dyformFixedData")) {
                        sql.append(" and  exists ( select 1 from data_mark_type t where t.data_uuid = r.data_uuid) ");
                    }
                    nativeDao.batchExecute(sql.toString(), map);

                } catch (Exception e) {
                    logger.warn("删除表单关系数据异常: {}", e.getMessage());
                }

                // 删除表单上关联的附件
                sql = new StringBuilder("delete from repo_file r where exists ( select 1 from repo_file_in_folder f , ");
                sql.append(t).append(" a where f.folder_uuid = a.uuid and r.uuid=f.file_uuid ");
                sql.append(underSystem);
                if (!clearTypes.contains("dyformData") && clearTypes.contains("dyformFixedData")) {
                    // 仅清理内置数据
                    sql.append(" and exists ( select 1 from data_mark_type t where t.data_uuid = a.uuid) ");
                }
                sql.append(" ) ");
                nativeDao.batchExecute(sql.toString(), map);

                sql = new StringBuilder("delete from repo_file_in_folder f where exists ( select 1 from ");
                sql.append(t).append(" a where f.folder_uuid = a.uuid ");
                sql.append(underSystem);
                if (!clearTypes.contains("dyformData") && clearTypes.contains("dyformFixedData")) {
                    // 仅清理内置数据
                    sql.append(" and exists ( select 1 from data_mark_type t where t.data_uuid = a.uuid) ");
                }
                sql.append(" ) ");
                nativeDao.batchExecute(sql.toString(), map);


                sql = new StringBuilder("delete from ").append(t).append(" a where 1=1");
                sql.append(underSystem);
                if (!clearTypes.contains("dyformData") && clearTypes.contains("dyformFixedData")) {
                    // 仅清理内置数据
                    sql.append(" and exists ( select 1 from data_mark_type t where t.data_uuid = a.uuid) ");
                }
                total += formDefinitionService.getDao().deleteBySQL(sql.toString(), map);
                logger.info("删除表数据: {} - {} ", t, total);

            } catch (Exception e) {
                logger.warn("{} - 统计表数据异常: {}", t, e.getMessage());
            }


            // 表单数据归档的文件
            if (clearTypes.contains("dmsFileData")) {
                Map<String, Object> map = Maps.newHashMap();
                map.put("tableName", t);
                //FIXME: dms file 系统租户数据隔离
                nativeDao.batchExecute("delete from dms_file f where f.content_type ='application/dyform' and " +
                        "exists (select 1 from dyform_form_definition d where d.uuid = f.data_def_uuid  and d.table_name=:tableName )", map);
            }

        }
        return new ExpectCleanupResult(total);
    }

    @Override
    public ExpectCleanupResult expectCleanupRows(Params params) {
        String table = params.optString("table");
        List<String> clearTypes = (List<String>) params.get("clearTypes");
        long total = 0L;
        if (StringUtils.isNotBlank(table)) {
            String system = StringUtils.defaultIfBlank(params.optString("system"), RequestSystemContextPathResolver.system());
            String underSystem = "";
            Map<String, Object> map = Maps.newHashMap();
            if (StringUtils.isNotBlank(system)) {
                underSystem = " and a.system=:system and a.tenant=:tenant ";
                map.put("system", system);
                map.put("tenant", SpringSecurityUtils.getCurrentTenantId());
            }


            try {
                StringBuilder sql = new StringBuilder(" select count(1) as total  from ");
                sql.append(table).append(" a where 1=1 ").append(underSystem);
                if (clearTypes.contains("dyformFixedData") && !clearTypes.contains("dyformData")) {
                    sql.append(" and  exists ( select 1 from data_mark_type d where d.data_uuid = a.uuid )");
                }
                List<QueryItem> formDataCount = nativeDao.query(sql.toString()
                        , map, QueryItem.class);
                logger.info("预计删除表数据: {} - {}条数据 ", table, formDataCount.get(0).getLong("total"));
                total += formDataCount.get(0).getLong("total");
            } catch (Exception e) {
                logger.warn("预计表单数据量异常: {}", e.getMessage());
            }


            // 统计表单数据归档的文件
            if (clearTypes.contains("dmsFileData")) {
                map.put("tableName", table);
                List<QueryItem> dmsCount = nativeDao.query("select count(1) as total from dms_file f where f.content_type ='application/dyform' " +
                        " and exists (select 1 from dyform_form_definition a where a.uuid = f.data_def_uuid  and a.table_name=:tableName " + underSystem + " ) ", map, QueryItem.class);
                logger.info("预计删除表单归档文件数据: {}条数据 ", dmsCount.get(0).getLong("total"));
                total += dmsCount.get(0).getLong("total");
            }

            return new ExpectCleanupResult(total);
        }
        return new ExpectCleanupResult(0);
    }
}
