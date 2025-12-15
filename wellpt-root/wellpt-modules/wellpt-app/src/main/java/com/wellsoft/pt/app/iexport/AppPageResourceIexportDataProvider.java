package com.wellsoft.pt.app.iexport;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.wellsoft.context.enums.Separator;
import com.wellsoft.pt.app.entity.AppFunction;
import com.wellsoft.pt.app.entity.AppPageResourceEntity;
import com.wellsoft.pt.basicdata.iexport.acceptor.IexportData;
import com.wellsoft.pt.basicdata.iexport.service.AbstractIexportDataProvider;
import com.wellsoft.pt.basicdata.iexport.suport.IexportType;
import com.wellsoft.pt.basicdata.iexport.suport.ProtoDataHql;
import com.wellsoft.pt.jpa.util.HqlUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author yt
 * @title: AppPageResourceIexportDataProvider
 * @date 2020/10/20 14:19
 */
@Service
@Transactional(readOnly = true)
public class AppPageResourceIexportDataProvider extends AbstractIexportDataProvider<AppPageResourceEntity, String> {
    @Override
    public String getType() {
        return IexportType.AppPageResource;
    }

    @Override
    public IexportData getData(String uuid) {
        return null;
    }

    @Override
    public String getTreeName(AppPageResourceEntity appPageResourceEntity) {
        AppFunction appFunction = this.dao.get(AppFunction.class, appPageResourceEntity.getAppFunctionUuid());
        return "引用资源：" + appFunction.getName();
    }

    @Override
    public Map<String, String> getTreeNameMap(Collection<AppPageResourceEntity> list) {
        Map<String, String> map = new HashMap<>();
        Multimap<Serializable, String> uuidMap = HashMultimap.create();
        for (AppPageResourceEntity appPageResourceEntity : list) {
            uuidMap.put(appPageResourceEntity.getAppFunctionUuid(), appPageResourceEntity.getUuid());
        }
        HashMap<String, Object> query = new HashMap<String, Object>();
        StringBuilder hql = new StringBuilder("from ").append("AppFunction").append(" where ");
        HqlUtils.appendSql("uuid", query, hql, uuidMap.keySet());
        List<AppFunction> functionList = this.dao.find(hql.toString(), query, AppFunction.class);
        for (AppFunction appFunction : functionList) {
            String treeName = "引用资源：" + appFunction.getName();
            for (String uuid : uuidMap.get(appFunction.getUuid())) {
                map.put(uuid, treeName);
            }
        }
        return map;
    }

    @Override
    public void putChildProtoDataHqlParams(AppPageResourceEntity appPageResourceEntity, Map<String, AppPageResourceEntity> parentMap, Map<String, ProtoDataHql> hqlMap) {
        String start = getType() + Separator.UNDERLINE.getValue();
        String key = start + appPageResourceEntity.getAppFunctionUuid();
        parentMap.put(key, appPageResourceEntity);

        if (!hqlMap.containsKey(this.getChildHqlKey(IexportType.AppFunction))) {
            hqlMap.put(this.getChildHqlKey(IexportType.AppFunction), this.getProtoDataHql("AppFunction"));
        }
        this.addDataUuid(hqlMap.get(this.getChildHqlKey(IexportType.AppFunction)), appPageResourceEntity.getAppFunctionUuid());
    }

    @Override
    public Map<String, List<AppPageResourceEntity>> getParentMapList(ProtoDataHql protoDataHql) {
        List<AppPageResourceEntity> list = this.dao.find(protoDataHql.getHql().toString(), protoDataHql.getParams(), AppPageResourceEntity.class);
        Map<String, List<AppPageResourceEntity>> map = new HashMap<>();
        if (protoDataHql.getParentType().equals(IexportType.AppPageDefinition)) {
            for (AppPageResourceEntity appPageResourceEntity : list) {
                String key = protoDataHql.getParentType() + Separator.UNDERLINE.getValue() + appPageResourceEntity.getAppPageUuid();
                this.putParentMap(map, appPageResourceEntity, key);
            }
        } else {
            super.getParentMapList(protoDataHql);
        }
        return map;
    }

}
