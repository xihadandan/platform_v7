/*
 * @(#)2018年4月16日 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.integration.service.impl;

import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.pt.integration.dao.ExchangeDataDao;
import com.wellsoft.pt.integration.entity.ExchangeData;
import com.wellsoft.pt.integration.entity.ExchangeDataMonitor;
import com.wellsoft.pt.integration.service.ExchangeDataService;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Description: 如何描述该类
 *
 * @author chenqiong
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2018年4月16日.1	chenqiong		2018年4月16日		Create
 * </pre>
 * @date 2018年4月16日
 */
@Service
public class ExchangeDataServiceImpl extends AbstractJpaServiceImpl<ExchangeData, ExchangeDataDao, String> implements
        ExchangeDataService {
    @Override
    public ExchangeData getExchangeDataByDataId(String dataId, Integer recVer) {
        String hql = "from ExchangeData e where e.dataId = :dataId and e.dataRecVer = :recVer and e.validData='yes'";
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("dataId", dataId);
        values.put("recVer", recVer);
        List<ExchangeData> eList = listByHQL(hql, values);
        if (eList.size() > 0) {
            return eList.get(0);
        } else {
            return null;
        }
    }

    @Override
    public ExchangeData getExchangeDataByDataIdAll(String dataId, Integer recVer) {
        String hql = "from ExchangeData e where e.dataId = :dataId and e.dataRecVer = :recVer";
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("dataId", dataId);
        values.put("recVer", recVer);
        List<ExchangeData> eList = listByHQL(hql, values);
        if (eList.size() > 0) {
            return eList.get(0);
        } else {
            return null;
        }
    }

    @Override
    public List<ExchangeData> getExchangeDatasByBatchId(String batchId) {
        String hql = "from ExchangeData e where e.exchangeDataBatch.id = :batchId";
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("batchId", batchId);
        return listByHQL(hql, values);
    }

    @Override
    public ExchangeData getExchangeDatasByDataId(String dataId) {
        String hql = "from ExchangeData e where e.dataId = :dataId and e.newestData='yes'";
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("dataId", dataId);
        List<ExchangeData> exchangeDatas = listByHQL(hql, values);
        if (exchangeDatas != null && exchangeDatas.size() > 0) {
            return exchangeDatas.get(0);
        } else {
            return null;
        }

    }

    @Override
    public List<ExchangeData> getExchangeDataListByDataId(String dataId) {
        String hql = "from ExchangeData e where e.dataId = :dataId and e.validData='yes' order by e.createTime";
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("dataId", dataId);
        List<ExchangeData> exchangeDatas = listByHQL(hql, values);
        return exchangeDatas;
    }

    @Override
    public ExchangeDataMonitor getExchangeDataMonitorByCorrelationandUnitId(String correlationId,
                                                                            Integer correlationRecVer, String unitId) {
        String hql = "from ExchangeData e where e.dataId = :correlationDataId and e.dataRecVer = :correlationRecver";
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("correlationDataId", correlationId);
        values.put("correlationRecver", correlationRecVer);
        List<ExchangeData> exchangeDatas = this.listByHQL(hql, values);
        if (exchangeDatas != null && exchangeDatas.size() > 0) {
            // ExchangeData exchangeData = exchangeDatas.get(0);
            // Set<ExchangeDataMonitor> edms =
            // exchangeData.getExchangeDataMonitors();
            // for (ExchangeDataMonitor edm : edms) {
            // if (edm.getUnitId() != null && edm.getUnitId().equals(unitId)) {
            // return edm;
            // }
            // }
        }
        return null;
    }

    /**
     * 上报逾期汇总
     *
     * @return
     */
    @Override
    public List<QueryItem> getSBYQ() {
        String hql = "select e.exchangeDataBatch.fromId as unit ,count(e.uuid) as count1 " + "from ExchangeData e "
                + "where e.validData = 'yes' " + "and e.uploadLimitNum  is not null "
                + "group by e.exchangeDataBatch.fromId " + "order by count1 desc";
        Map<String, Object> values = new HashMap<String, Object>();
        List<QueryItem> result = this.listQueryItemByHQL(hql, values, null);
        return result;
    }

    /**
     * 部门许可汇总
     *
     * @return
     */
    @Override
    public List<QueryItem> getBMXK() {
        String hql = "select e.exchangeDataBatch.fromId as unit ,count(e.uuid) as count1 " + "from ExchangeData e "
                + "where e.validData = 'yes' " + "and e.exchangeDataBatch.typeId = '000000000XK' "
                + "group by e.exchangeDataBatch.fromId " + "order by count1 desc";
        Map<String, Object> values = new HashMap<String, Object>();
        List<QueryItem> result = this.listQueryItemByHQL(hql, values, null);
        return result;
    }

    /**
     * 根据typeId获取所有商事登记数量
     *
     * @param typeId
     * @return
     */
    @Override
    public long getAllCommercialSubjectByTypeId(String typeId) {
        String hql = "";
        // 商事主体需要判断最新版本
        if ("004140203SZ".equals(typeId)) {
            hql = "from ExchangeData e where e.exchangeDataBatch.typeId = :typeId and e.newestData='yes' and e.validData ='yes' ";
        } else {
            hql = "from ExchangeData e where e.exchangeDataBatch.typeId = :typeId and e.validData ='yes' ";
        }
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("typeId", typeId);
        return this.dao.getNumberByHQL(hql, values);
    }

    /**
     * 根据typeId获取本月商事登记数量
     *
     * @param typeId
     * @return
     */
    @Override
    public long getCurrentMonthCommercialRegistrationByTypeId(String typeId, Date firstDay, Date lastDay) {
        String hql = "";
        // 商事主体需要判断最新版本
        if ("004140203SZ".equals(typeId)) {
            hql = "from ExchangeData e where e.exchangeDataBatch.typeId = :typeId and e.newestData='yes' and e.validData ='yes' and e.createTime between :firstDay and :lastDay ";
        } else {
            hql = "from ExchangeData e where e.exchangeDataBatch.typeId = :typeId and e.validData ='yes' and e.createTime between :firstDay and :lastDay ";
        }
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("typeId", typeId);
        values.put("firstDay", firstDay);
        values.put("lastDay", lastDay);
        return this.dao.getNumberByHQL(hql, values);
    }

    /**
     * 根据业务流水号、数据类型查找最高版本的行政许可过程信息
     *
     * @param ywlsh
     * @param string
     * @return
     */
    @Override
    public List<ExchangeData> findNewestDataByYwlshAndTypeId(String ywlsh, String typeId, String other) {
        StringBuffer hql = new StringBuffer();
        hql.append(" from ExchangeData e where e.reservedNumber2 = :reservedNumber2 and e.exchangeDataBatch.typeId = :typeId and e.newestData='yes' ");
        Map<String, Object> values = new HashMap<String, Object>();
        if (other != null) {
            hql.append(" and reservedText1= :reservedText1");
            values.put("reservedText1", other);
        }
        values.put("reservedNumber2", ywlsh);
        values.put("typeId", typeId);
        return listByHQL(hql.toString(), values);
    }

    /**
     * 获取指定注册号的商事主体登记的最新数据
     *
     * @return
     */
    @Override
    public List<ExchangeData> getExchangeDatesBySpecial(String zch, String typeId) {
        StringBuffer hql = new StringBuffer();
        hql.append(" from ExchangeData e where e.reservedNumber2 = :reservedNumber2 and e.exchangeDataBatch.typeId = :typeId and e.newestData='yes' ");
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("reservedNumber2", zch);
        values.put("typeId", typeId);
        return listByHQL(hql.toString(), values);
    }
}
