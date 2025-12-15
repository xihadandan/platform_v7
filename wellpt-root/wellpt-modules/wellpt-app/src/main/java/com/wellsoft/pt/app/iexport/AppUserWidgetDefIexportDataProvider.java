package com.wellsoft.pt.app.iexport;

import com.wellsoft.pt.app.entity.AppUserWidgetDefEntity;
import com.wellsoft.pt.basicdata.iexport.service.AbstractIexportDataProvider;
import com.wellsoft.pt.basicdata.iexport.suport.IexportType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Description: 如何描述该类
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2023年11月17日   chenq	 Create
 * </pre>
 */
@Service
@Transactional(readOnly = true)
public class AppUserWidgetDefIexportDataProvider extends AbstractIexportDataProvider<AppUserWidgetDefEntity, String> {

    @Override
    public String getType() {
        return IexportType.AppUserWidgetDef;
    }


    @Override
    public String getTreeName(AppUserWidgetDefEntity appUserWidgetDefEntity) {
        return "功能组件: " + appUserWidgetDefEntity.getTitle();
    }

}
