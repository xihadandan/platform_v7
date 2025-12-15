package com.wellsoft.pt.dms.file.store;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.pt.basicdata.datastore.support.AbstractDataStoreQueryInterface;
import com.wellsoft.pt.basicdata.datastore.support.DataStoreInterfaceField;
import com.wellsoft.pt.basicdata.datastore.support.DataStoreInterfaceFieldElement;
import com.wellsoft.pt.dms.entity.DmsFolderConfigurationEntity;
import com.wellsoft.pt.dms.entity.DmsRoleEntity;
import com.wellsoft.pt.dms.service.DmsFolderConfigurationService;
import com.wellsoft.pt.dms.service.DmsFolderService;
import com.wellsoft.pt.dms.service.DmsRoleService;
import com.wellsoft.pt.jpa.criteria.CriteriaMetadata;
import com.wellsoft.pt.jpa.criteria.InterfaceParam;
import com.wellsoft.pt.jpa.criteria.QueryContext;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Description:
 *
 * @author chenq
 * @date 2020/3/20
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2020/3/20    chenq		2020/3/20		Create
 * </pre>
 */
@Component
public class DmsFolderDataStoreQuery extends AbstractDataStoreQueryInterface {


    @Autowired
    DmsRoleService dmsRoleService;

    @Autowired
    DmsFolderService dmsFolderService;

    @Autowired
    DmsFolderConfigurationService dmsFolderConfigurationService;

    @Override
    public List<QueryItem> query(QueryContext context) {
        DmsFolderDsIntfParam param = context.interfaceParam(
                DmsFolderDsIntfParam.class);
        Map<String, Object> params = Maps.newHashMap();
        params.put("whereSql", context.getWhereSqlString());
        params.put("orderBy", context.getOrderString());
        List<QueryItem> queryItemList = dmsFolderService.listByParams(params, context.getPagingInfo());
        String[] authorities = StringUtils.isNotBlank(
                param.getFolderAuthorities()) ? param.getFolderAuthorities().split(";") : null;
        for (QueryItem item : queryItemList) {
            DmsFolderConfigurationEntity configurationEntity = dmsFolderConfigurationService.getByFolderUuid(
                    item.getString("uuid"));
            if (configurationEntity != null) {
                if (StringUtils.isNotBlank(configurationEntity.getConfiguration())) {
                    Map<String, Object> conf = new Gson().fromJson(
                            configurationEntity.getConfiguration(), HashMap.class);
                    item.put("formUuid", conf.get("formUuid"));
                    item.put("formName", conf.get("formName"));
                    item.put("displayFormUuid", conf.get("displayFormUuid"));
                    item.put("displayFormName", conf.get("displayFormName"));
                    List<Map<String, Object>> assignRoles = (List<Map<String, Object>>) conf.get(
                            "assignRoles");
                    if (ArrayUtils.isNotEmpty(authorities) && CollectionUtils.isNotEmpty(
                            assignRoles)) {
                        for (String au : authorities) {
                            for (Map<String, Object> roles : assignRoles) {
                                if (roles.get("roleUuid") != null && roles.get("roleUuid").toString().equals(au)) {
                                    item.put(roles.get("roleUuid") + "_orgids",
                                            roles.get("orgIds"));
                                    item.put(roles.get("roleUuid") + "_orgnames",
                                            roles.get("orgNames"));
                                    item.put(roles.get("roleUuid").toString(), roles.get("roleUuid"));
                                }
                            }
                        }
                    }
                }
            }

        }

        return queryItemList;
    }

    @Override
    public String getQueryName() {
        return "数据管理_文件库_文件夹(适用于文件夹组件)";
    }

    @Override
    public CriteriaMetadata initCriteriaMetadata(QueryContext context) {
        DmsFolderDsIntfParam param = context.interfaceParam(
                DmsFolderDsIntfParam.class);
        CriteriaMetadata criteriaMetadata = CriteriaMetadata.createMetadata();
        criteriaMetadata.add("uuid", "uuid", "uuid", String.class);
        criteriaMetadata.add("name", "名称", "名称", String.class);
        criteriaMetadata.add("code", "编码", "编码", String.class);
        criteriaMetadata.add("contentType", "夹类型", "夹类型", String.class);
        criteriaMetadata.add("absolutePath", "夹名称路径", "夹名称路径", String.class);
        criteriaMetadata.add("uuidPath", "夹UUID路径", "夹UUID路径", String.class);
        criteriaMetadata.add("parentUuid", "上级夹UUID", "上级夹UUID", String.class);
        criteriaMetadata.add("status", "夹状态", "夹状态", String.class);
        criteriaMetadata.add("remark", "备注", "备注", String.class);
        criteriaMetadata.add("formUuid", "动态表单UUID", "动态表单UUID", String.class);
        criteriaMetadata.add("formName", "动态表单名称", "动态表单名称", String.class);
        criteriaMetadata.add("displayFormName", "显示表单名称", "显示表单名称", String.class);
        criteriaMetadata.add("displayFormUuid", "显示表单UUID", "显示表单UUID", String.class);

        if (param != null && StringUtils.isNotBlank(param.getFolderAuthorities())) {
            String[] strs = param.getFolderAuthorities().split(";");
            for (String s : strs) {
                DmsRoleEntity dmsRoleEntity = dmsRoleService.get(s);
                if (dmsRoleEntity != null) {
                    criteriaMetadata.add(dmsRoleEntity.getUuid() + "_orgids",
                            dmsRoleEntity.getName() + "的所选人员真实值",
                            dmsRoleEntity.getName() + "的所选人员真实值", String.class);
                    criteriaMetadata.add(dmsRoleEntity.getUuid() + "_orgnames",
                            dmsRoleEntity.getName() + "的所选人员显示值",
                            dmsRoleEntity.getName() + "的所选人员显示值", String.class);
                    criteriaMetadata.add(dmsRoleEntity.getUuid(), dmsRoleEntity.getName(), dmsRoleEntity.getName(), String.class);
                }
            }
        }
        return criteriaMetadata;
    }

    @Override
    public long count(QueryContext context) {
        //该数据仓库不用于表格组件分页，可不计算数量
        return 0;
    }

    @Override
    public Class<? extends InterfaceParam> interfaceParamsClass() {
        return DmsFolderDsIntfParam.class;
    }

    public static final class DmsFolderDsIntfParam implements InterfaceParam {


        @DataStoreInterfaceField(name = "选择权限列定义", domType = DataStoreInterfaceFieldElement.MULTI_SELECT, service = "fileManagerComponentService.getDmsRoleSelectData")
        private String folderAuthorities;


        public String getFolderAuthorities() {
            return folderAuthorities;
        }

        public void setFolderAuthorities(String folderAuthorities) {
            this.folderAuthorities = folderAuthorities;
        }
    }
}
