package com.wellsoft.pt.app.iexport;

import com.wellsoft.context.enums.Separator;
import com.wellsoft.pt.app.entity.AppProductIntegration;
import com.wellsoft.pt.app.support.AppType;
import com.wellsoft.pt.basicdata.iexport.acceptor.IexportData;
import com.wellsoft.pt.basicdata.iexport.service.AbstractIexportDataProvider;
import com.wellsoft.pt.basicdata.iexport.suport.IexportType;
import com.wellsoft.pt.basicdata.iexport.suport.ProtoDataHql;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author yt
 * @title: AppProductIntegrationParentIexportDataProvider
 * @date 2020/10/22 11:34
 */
@Service
@Transactional(readOnly = true)
public class AppProductIntegrationParentIexportDataProvider extends AbstractIexportDataProvider<AppProductIntegration, String> {
    @Override
    public String getType() {
        return IexportType.AppProductIntegrationParent;
    }

    @Override
    public IexportData getData(String uuid) {
        return null;
    }

    @Override
    public String getTreeName(AppProductIntegration appProductIntegration) {
        return "上级" + AppType.getName(Integer.valueOf(appProductIntegration.getDataType())) + ": " + appProductIntegration.getDataName();
    }

    @Override
    public Map<String, List<AppProductIntegration>> getParentMapList(ProtoDataHql protoDataHql) {
        List<AppProductIntegration> list = this.dao.find(protoDataHql.getHql().toString(), protoDataHql.getParams(), AppProductIntegration.class);
        Map<String, List<AppProductIntegration>> map = new HashMap<>();
        if (protoDataHql.getParentType().equals(IexportType.AppFunctionParent)) {
            for (AppProductIntegration appProductIntegration : list) {
                String key = protoDataHql.getParentType() + Separator.UNDERLINE.getValue() + appProductIntegration.getDataUuid();
                this.putParentMap(map, appProductIntegration, key);
            }
        } else {
            super.getParentMapList(protoDataHql);
        }
        return map;
    }
}
