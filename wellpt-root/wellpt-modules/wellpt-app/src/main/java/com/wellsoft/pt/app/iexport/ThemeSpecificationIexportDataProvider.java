package com.wellsoft.pt.app.iexport;

import com.wellsoft.pt.basicdata.iexport.service.AbstractIexportDataProvider;
import com.wellsoft.pt.basicdata.iexport.suport.IexportType;
import com.wellsoft.pt.theme.dto.ThemeSpecificationDto;
import com.wellsoft.pt.theme.entity.ThemeSpecificationEntity;
import com.wellsoft.pt.theme.service.ThemeSpecificationService;
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
public class ThemeSpecificationIexportDataProvider extends AbstractIexportDataProvider<ThemeSpecificationEntity, Long> {


    @Autowired
    ThemeSpecificationService themeSpecificationService;

    @Override
    public String getType() {
        return IexportType.ThemeSpecification;
    }

    @Override
    public String getTreeName(ThemeSpecificationEntity specificationEntity) {
        return new StringBuilder("主题规范: 版本_v").append(specificationEntity.getVersion()).toString();
    }

    @Override
    protected void beforeSaveEntityStream(ThemeSpecificationEntity entity) {
        // 判断是否存在启用的规范
        ThemeSpecificationDto dto = themeSpecificationService.getEnableThemeSpecify();
        if (dto != null && !entity.getUuid().equals(dto.getUuid())) {
            // 默认导入的规范不启用
            entity.setEnabled(false);
        }
    }
}
