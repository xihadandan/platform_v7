package com.wellsoft.pt.app.iexport;

import com.wellsoft.context.component.tree.TreeNode;
import com.wellsoft.context.jdbc.entity.JpaEntity;
import com.wellsoft.pt.basicdata.iexport.service.AbstractIexportDataProvider;
import com.wellsoft.pt.basicdata.iexport.suport.IExportEntityStream;
import com.wellsoft.pt.basicdata.iexport.suport.IexportType;
import com.wellsoft.pt.theme.entity.ThemePackEntity;
import com.wellsoft.pt.theme.service.ThemePackService;
import com.wellsoft.pt.theme.service.ThemeSpecificationService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
public class ThemePackIexportDataProvider extends AbstractIexportDataProvider<ThemePackEntity, Long> {
    @Autowired
    ThemePackService themePackService;

    @Autowired
    ThemeSpecificationService themeSpecificationService;

    @Override
    public String getType() {
        return IexportType.ThemePack;
    }

    @Override
    public String getTreeName(ThemePackEntity themePackEntity) {
        return new StringBuilder("主题包: ").append(themePackEntity.getName()).toString();
    }

    @Override
    public TreeNode treeNode(Long uuid) {
        TreeNode node = super.treeNode(uuid);
        ThemePackEntity entity = getEntity(uuid);
        if (entity.getSpecifyUuid() != null) {
            node.appendChild(exportTreeNodeByDataProvider(entity.getSpecifyUuid(), IexportType.ThemeSpecification));
        }
        return node;
    }


    @Override
    @Transactional
    public ThemePackEntity saveEntityStream(IExportEntityStream stream) {
        // 主题包类不重复，按类定位保存
        String string = stream.getMetadata().getData();
        try {
            if (StringUtils.isNotBlank(string)) {
                ThemePackEntity entity = objectMapper.readValue(string, entityClass);
                ThemePackEntity persist = themePackService.getByThemeClass(entity.getThemeClass());
                if (persist != null) {
                    BeanUtils.copyProperties(entity, persist, new String[]{JpaEntity.REC_VER, JpaEntity.UUID});
                    entity = persist;
                }
                this.beforeSaveEntityStream(entity);
                if (persist != null) {
                    dao.save(entity);
                } else {
                    dao.getSession().save(entity);
                }
                return entity;
            }
        } catch (Exception e) {
            logger.error("导入主题包异常:", e);
        }
        return null;
    }
}
