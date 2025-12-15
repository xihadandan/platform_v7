package com.wellsoft.pt.app.iexport;

import com.wellsoft.context.enums.Separator;
import com.wellsoft.pt.app.entity.AppFunction;
import com.wellsoft.pt.app.support.AppType;
import com.wellsoft.pt.basicdata.iexport.acceptor.IexportData;
import com.wellsoft.pt.basicdata.iexport.service.AbstractIexportDataProvider;
import com.wellsoft.pt.basicdata.iexport.suport.GenerateHql;
import com.wellsoft.pt.basicdata.iexport.suport.IexportType;
import com.wellsoft.pt.basicdata.iexport.suport.ProtoDataHql;
import com.wellsoft.pt.jpa.util.HqlUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.*;

/**
 * @author yt
 * @title: AppFunctionParentIexportDataProvider
 * @date 2020/10/22 11:09
 */
@Service
@Transactional(readOnly = true)
public class AppFunctionParentIexportDataProvider extends AbstractIexportDataProvider<AppFunction, String> {
    @Override
    public String getType() {
        return IexportType.AppFunctionParent;
    }

    @Override
    public IexportData getData(String uuid) {
        return null;
    }

    @Override
    public String getTreeName(AppFunction appFunction) {
        return "上级功能定义: " + appFunction.getName();
    }

    @Override
    public void putChildProtoDataHqlParams(AppFunction appFunction, Map<String, AppFunction> parentMap, Map<String, ProtoDataHql> hqlMap) {
        String start = getType() + Separator.UNDERLINE.getValue();
        String key = start + appFunction.getUuid();
        parentMap.put(key, appFunction);

        String uuid = appFunction.getUuid();

        String childHqlkey = this.getChildHqlKey(IexportType.AppProductIntegrationParent);

        ProtoDataHql protoDataHql = hqlMap.get(childHqlkey);
        if (protoDataHql == null) {
            protoDataHql = new ProtoDataHql(getType(), "AppProductIntegration");
            protoDataHql.setGenerateHql(new GenerateHql() {
                @Override
                public void build(ProtoDataHql protoDataHql) {
                    protoDataHql.getSbHql().append("from ").append(protoDataHql.getEntityName()).append(" where dataType=:dataType and ");
                    protoDataHql.getParams().put("dataType", AppType.FUNCTION.toString());
                    HqlUtils.appendSql("dataUuid", protoDataHql.getParams(), protoDataHql.getSbHql(), (Set<Serializable>) protoDataHql.getParams().get("dataUuids"));
                }
            });
            hqlMap.put(childHqlkey, protoDataHql);
        }
        Set<Serializable> uuids = (Set<Serializable>) protoDataHql.getParams().get("dataUuids");
        if (uuids == null) {
            uuids = new HashSet<>();
            protoDataHql.getParams().put("dataUuids", uuids);
        }
        uuids.add(uuid);

    }

    @Override
    public Map<String, List<AppFunction>> getParentMapList(ProtoDataHql protoDataHql) {
        List<AppFunction> list = this.dao.find(protoDataHql.getHql().toString(), protoDataHql.getParams(), AppFunction.class);
        Map<String, List<AppFunction>> map = new HashMap<>();
        for (AppFunction appFunction : list) {
            String key = protoDataHql.getParentType() + Separator.UNDERLINE.getValue() + appFunction.getUuid();
            this.putParentMap(map, appFunction, key);
        }
        return map;
    }
}
