package com.wellsoft.pt.app.iexport;

import com.google.common.collect.Lists;
import com.wellsoft.context.component.tree.TreeNode;
import com.wellsoft.pt.app.entity.AppProdVersionEntity;
import com.wellsoft.pt.app.entity.AppProdVersionSettingEntity;
import com.wellsoft.pt.app.entity.AppProduct;
import com.wellsoft.pt.app.service.AppProdVersionSettingService;
import com.wellsoft.pt.app.service.AppProductService;
import com.wellsoft.pt.basicdata.iexport.service.AbstractIexportDataProvider;
import com.wellsoft.pt.basicdata.iexport.suport.IExportTable;
import com.wellsoft.pt.basicdata.iexport.suport.IexportType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
public class AppProdVersionIexportDataProvider extends AbstractIexportDataProvider<AppProdVersionEntity, Long> {
    @Autowired
    AppProductService appProductService;

    @Autowired
    AppProdVersionSettingService appProdVersionSettingService;

    @Override
    public String getType() {
        return IexportType.AppProdVersion;
    }

    @Override
    public String getTreeName(AppProdVersionEntity appProdVersionEntity) {
        AppProduct appProduct = appProductService.getProductByProdVersionUuid(appProdVersionEntity.getUuid());
        return new StringBuilder("产品版本: ").append(appProduct.getName()).append("(").append(appProdVersionEntity.getVersion()).append(")").toString();
    }

    @Override
    public TreeNode treeNode(Long uuid) {
        TreeNode node = super.treeNode(uuid);
        // 版本设置
        AppProdVersionSettingEntity settingEntity = appProdVersionSettingService.getByProdVersionUuid(uuid);
        if (settingEntity != null) {
            node.appendChild(this.exportTreeNodeByDataProvider(settingEntity.getUuid(), IexportType.AppProdVersionSetting));
        }

        return node;
    }

    @Override
    protected List<IExportTable> childTableStream() {
        return Lists.newArrayList(
                /* 产品版本日志 */
                new IExportTable("select * from APP_PROD_VERSION_LOG where PROD_VERSION_UUID=:uuid"));
    }
}
