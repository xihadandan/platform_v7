package com.wellsoft.pt.app.iexport;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.wellsoft.context.component.tree.TreeNode;
import com.wellsoft.pt.app.entity.*;
import com.wellsoft.pt.app.service.*;
import com.wellsoft.pt.basicdata.iexport.service.AbstractIexportDataProvider;
import com.wellsoft.pt.basicdata.iexport.suport.IExportTable;
import com.wellsoft.pt.basicdata.iexport.suport.IexportType;
import com.wellsoft.pt.security.audit.entity.Role;
import com.wellsoft.pt.security.audit.facade.service.RoleFacadeService;
import com.wellsoft.pt.theme.entity.ThemePackEntity;
import com.wellsoft.pt.theme.service.ThemePackService;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Iterator;
import java.util.List;

/**
 * Description: 如何描述该类
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2023年11月20日   chenq	 Create
 * </pre>
 */
@Service
@Transactional(readOnly = true)
public class AppProdVersionSettingIexportDataProvider extends AbstractIexportDataProvider<AppProdVersionSettingEntity, Long> {

    @Autowired
    AppProdVersionSettingService appProdVersionSettingService;

    @Autowired
    AppProductService appProductService;

    @Autowired
    AppProdVersionService appProdVersionService;

    @Autowired
    AppPageDefinitionService appPageDefinitionService;

    @Autowired
    AppProdVersionParamService appProdVersionParamService;

    @Autowired
    RoleFacadeService roleFacadeService;

    @Autowired
    ThemePackService themePackService;

    @Override
    public String getType() {
        return IexportType.AppProdVersionSetting;
    }

    @Override
    public String getTreeName(AppProdVersionSettingEntity settingEntity) {
        AppProduct appProduct = appProductService.getProductByProdVersionUuid(settingEntity.getProdVersionUuid());
        AppProdVersionEntity versionEntity = appProdVersionService.getOne(settingEntity.getProdVersionUuid());
        return new StringBuilder("产品版本配置: ").append(appProduct.getName()).append("(").append(versionEntity.getVersion()).append(")").toString();
    }

    @Override
    public TreeNode treeNode(Long uuid) {
        TreeNode node = super.treeNode(uuid);
        AppProdVersionSettingEntity settingEntity = getEntity(uuid);
        AppProdVersionEntity versionEntity = appProdVersionService.getOne(settingEntity.getProdVersionUuid());

        // 系统首页
        List<AppPageDefinition> pageDefinitions = appProdVersionService.getProdVersionPages(versionEntity.getUuid());
        if (CollectionUtils.isNotEmpty(pageDefinitions)) {
            for (AppPageDefinition page : pageDefinitions) {
                this.exportTreeNodeByDataProvider(page.getUuid(), IexportType.AppPageDefinition, node, new Function<TreeNode, TreeNode>() {

                    @javax.annotation.Nullable
                    @Override
                    public TreeNode apply(@javax.annotation.Nullable TreeNode n) {
                        n.setName("系统首页: " + page.getName() + "(" + page.getVersion() + ")");
                        return n;
                    }
                });
//                TreeNode n = this.exportTreeNodeByDataProvider(page.getUuid(), IexportType.AppPageDefinition);
//                if (n != null) {
//                    n.setName("系统首页: " + page.getName() + "(" + page.getVersion() + ")");
//                    node.appendChild(n);
//                }
            }
        }

        // 登录页
        List<AppProdVersionLoginEntity> loginEntities = appProdVersionService.queryProdVersionLoginByProdVersionUuid(versionEntity.getUuid());
        if (CollectionUtils.isNotEmpty(loginEntities)) {
            for (AppProdVersionLoginEntity loginEntity : loginEntities) {
                this.exportTreeNodeByDataProvider(loginEntity.getUuid(), IexportType.AppProdVersionLogin, node);
//                node.appendChild(this.exportTreeNodeByDataProvider(loginEntity.getUuid(), IexportType.AppProdVersionLogin));
            }
        }

        //  模块
        List<AppModule> appModules = appProdVersionService.getVersionModulesByVersionId(versionEntity.getVersionId());
        if (CollectionUtils.isNotEmpty(appModules)) {
            for (AppModule module : appModules) {
                this.exportTreeNodeByDataProvider(module.getUuid(), IexportType.AppModule, node);
//                node.appendChild(this.exportTreeNodeByDataProvider(module.getUuid(), IexportType.AppModule));
            }
        }


        // 系统参数
        List<AppProdVersionParamEntity> paramEntities = appProdVersionParamService.getAllParamsDetail(versionEntity.getUuid());
        if (CollectionUtils.isNotEmpty(paramEntities)) {
            for (AppProdVersionParamEntity param : paramEntities) {
                node.appendChild(this.exportTreeNodeByDataProvider(param.getUuid(), IexportType.AppProdVersionParam));
            }
        }

        // 角色
        List<Role> roles = roleFacadeService.getRolesByAppId(versionEntity.getVersionId());
        if (CollectionUtils.isNotEmpty(roles)) {
            for (Role r : roles) {
                this.exportTreeNodeByDataProvider(r.getUuid(), IexportType.Role, node);
//                node.appendChild(exportTreeNodeByDataProvider(r.getUuid(), IexportType.Role));
            }
        }

        // 主题导出
        if (StringUtils.isNotBlank(settingEntity.getTheme())) {
            JSONObject jsonObject = JSONObject.fromObject(settingEntity.getTheme());
            String[] keys = new String[]{"pc", "mobile"};
            for (String k : keys) {
                if (jsonObject.containsKey(k)) {
                    JSONObject subObject = jsonObject.getJSONObject(k);
                    if (subObject.containsKey("theme")) {
                        JSONArray themes = subObject.getJSONArray("theme");
                        Iterator<JSONObject> iterator = themes.iterator();
                        while (iterator.hasNext()) {
                            JSONObject theme = iterator.next();
                            if (theme.containsKey("themeClass")) {
                                ThemePackEntity themePackEntity = themePackService.getByThemeClass(theme.getString("themeClass"));
                                if (themePackEntity != null) {
                                    node.appendChild(exportTreeNodeByDataProvider(themePackEntity.getUuid(), IexportType.ThemePack));
                                }
                            }
                        }
                    }
                }
            }
        }

        return node;
    }

    @Override
    protected List<IExportTable> childTableStream() {
        return Lists.newArrayList(
                /* 产品版本下的模块关联 */
                new IExportTable(Lists.newArrayList("PROD_VERSION_UUID", "MODULE_UUID"), "select * from APP_PROD_MODULE where PROD_VERSION_UUID=:prodVersionUuid"),
                /* 产品版本下的匿名地址 */
                new IExportTable(Lists.newArrayList("PROD_VERSION_UUID", "PAGE_ID", "TYPE", "URL", "DEVICE_TYPE"), "select * from APP_PROD_ANON_URL where prod_version_uuid=:prodVersionUuid"),
                /* 产品版本下的系统首页关联 */
                new IExportTable("select * from APP_PROD_RELA_PAGE where prod_version_uuid=:prodVersionUuid")
        );
    }
}
