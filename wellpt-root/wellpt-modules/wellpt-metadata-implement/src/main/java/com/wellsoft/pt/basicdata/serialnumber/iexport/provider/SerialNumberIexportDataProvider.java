/*
 * @(#)2015-6-16 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.serialnumber.iexport.provider;

import com.wellsoft.context.enums.Separator;
import com.wellsoft.context.jdbc.entity.JpaEntity;
import com.wellsoft.pt.basicdata.iexport.acceptor.ErrorDataIexportData;
import com.wellsoft.pt.basicdata.iexport.acceptor.IexportData;
import com.wellsoft.pt.basicdata.iexport.service.AbstractIexportDataProvider;
import com.wellsoft.pt.basicdata.iexport.suport.*;
import com.wellsoft.pt.basicdata.serialnumber.entity.SerialNumber;
import com.wellsoft.pt.basicdata.serialnumber.iexport.acceptor.SerialNumberIexportData;
import com.wellsoft.pt.basicdata.serialnumber.service.SerialNumberService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.*;

/**
 * Description:流水号定义
 *
 * @author linz
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2016-1-18	linz		2016-1-18		Create
 * </pre>
 * @date 2016-1-18
 */
@Service
@Transactional(readOnly = true)
public class SerialNumberIexportDataProvider extends AbstractIexportDataProvider<SerialNumber, String> {

    static {
        // 4.1、 流水号
        TableMetaData.register(IexportType.SerialNumber, "流水号", SerialNumber.class);
    }

    @Autowired
    private SerialNumberService serialNumberService;

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.iexport.service.IexportDataProvider#getType()
     */
    @Override
    public String getType() {
        return IexportType.SerialNumber;
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.iexport.service.IexportDataProvider#getData(java.lang.String)
     */
    @Override
    public IexportData getData(String uuid) {
        SerialNumber serialNumber = this.dao.get(SerialNumber.class, uuid);
        if (serialNumber == null) {
            return new ErrorDataIexportData(IexportType.SerialNumber, "找不到对应的流水号定义依赖关系,可能已经被删除", "流水号定义", uuid);
        }
        return new SerialNumberIexportData(serialNumber);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.iexport.service.AbstractIexportDataProvider#getMetaData()
     */
    @Override
    public IexportMetaData getMetaData() {
        IexportMetaData iexportMetaData = super.getMetaData();
        // 流水号ID生成方式
        iexportMetaData.registerColumnValueProcessor(TableMetaData.getTableName(IexportType.SerialNumber), "id",
                EntityIdColumnValueProcessorFactory.getColumnValueProcessor(SerialNumber.class));
        return iexportMetaData;
    }

    @Override
    public String getTreeName(SerialNumber serialNumber) {
        return new SerialNumberIexportData(serialNumber).getName();
    }

    @Override
    public <P extends JpaEntity<String>, C extends JpaEntity<String>> BusinessProcessor<SerialNumber> saveOrUpdate(
            Map<String, ProtoDataBeanTree<SerialNumber, P, C>> map, Collection<Serializable> uuids) {
        List<SerialNumber> oldList = this.getList(uuids);
        List<SerialNumber> list = new ArrayList<>();
        for (SerialNumber old : oldList) {
            ProtoDataBeanTree<SerialNumber, P, C> t = map.get(old.getUuid());
            // 版本号不一致 修改
            if (!old.getRecVer().equals(t.getProtoDataBean().getData().getRecVer())) {
                String sql = super.entityToUpdateSql(t.getProtoDataBean().getData());
                super.executeUpdateSql(sql, t);
            }
            map.remove(old.getUuid());
        }
        // 剩余的添加
        for (ProtoDataBeanTree<SerialNumber, P, C> t : map.values()) {
            String sql = this.entityToInsertSql(t.getProtoDataBean().getData());
            list.add(t.getProtoDataBean().getData());
            this.executeUpdateSql(sql, t);
        }

        BusinessProcessor<SerialNumber> businessProcessor = new BusinessProcessor<SerialNumber>(list) {
            @Override
            public void handle(Map<String, ProtoDataBean> beanMap) {
                for (SerialNumber serialNumber : this.getAddList()) {
                    serialNumberService.saveAcl(serialNumber);
                }
            }
        };
        return businessProcessor;
    }

    @Override
    public void putChildProtoDataHqlParams(SerialNumber serialNumber, Map<String, SerialNumber> parentMap,
                                           Map<String, ProtoDataHql> hqlMap) {
        if (StringUtils.isNotBlank(serialNumber.getModuleId())) {
            this.putAppFunctionParentMap(serialNumber, parentMap, hqlMap);
        }
    }

    @Override
    public Map<String, List<SerialNumber>> getParentMapList(ProtoDataHql protoDataHql) {
        Map<String, List<SerialNumber>> map = new HashMap<>();
        List<SerialNumber> list = this.dao.find(protoDataHql.getHql().toString(), protoDataHql.getParams(),
                SerialNumber.class);
        // 页面或组件定义依赖的流水号定义
        if (protoDataHql.getParentType().equals(IexportType.AppPageDefinition)
                || protoDataHql.getParentType().equals(IexportType.AppWidgetDefinition)) {
            for (SerialNumber serialNumber : list) {
                String key = protoDataHql.getParentType() + Separator.UNDERLINE.getValue()
                        + protoDataHql.getParams().get("dependencyUuid");
                this.putParentMap(map, serialNumber, key);
            }
        } else if (protoDataHql.getParentType().equals(IexportType.FlowDefinition)) {
            for (SerialNumber serialNumber : list) {
                String key = protoDataHql.getParentType() + Separator.UNDERLINE.getValue() + "serialNumber"
                        + Separator.UNDERLINE.getValue() + serialNumber.getId();
                this.putParentMap(map, serialNumber, key);
            }
        } else if (protoDataHql.getParentType().equals(IexportType.AppFunction)) {
            for (SerialNumber serialNumber : list) {
                String key = protoDataHql.getParentType() + Separator.UNDERLINE.getValue() + serialNumber.getUuid();
                this.putParentMap(map, serialNumber, key);
            }
        } else {
            super.getParentMapList(protoDataHql);
        }
        return map;

    }
}
