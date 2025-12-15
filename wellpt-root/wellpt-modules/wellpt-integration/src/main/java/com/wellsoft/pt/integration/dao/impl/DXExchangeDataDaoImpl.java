package com.wellsoft.pt.integration.dao.impl;

import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.pt.integration.dao.DXExchangeDataDao;
import com.wellsoft.pt.integration.entity.DXExchangeData;
import com.wellsoft.pt.integration.entity.DXExchangeRouteDate;
import com.wellsoft.pt.jpa.dao.impl.AbstractJpaDaoImpl;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Description: 交换数据Dao
 *
 * @author Administrator
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-11-17.1	ruanhg		2013-11-17		Create
 * </pre>
 * @date 2013-11-17
 */
@Repository
public class DXExchangeDataDaoImpl extends AbstractJpaDaoImpl<DXExchangeData, String> implements DXExchangeDataDao {

    @Override
    public DXExchangeData getDXExchangeDataByDataId(String dataId, Integer recVer) {
        String hql = "from DXExchangeData e where e.dataId = :dataId and e.dataRecVer = :recVer and e.validData='yes'";
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("dataId", dataId);
        values.put("recVer", recVer);
        List<DXExchangeData> eList = listByHQL(hql, values);
        if (eList.size() > 0) {
            return eList.get(0);
        } else {
            return null;
        }
    }

    @Override
    public DXExchangeData getDXExchangeDataByDataIdAll(String dataId, Integer recVer) {
        String hql = "from DXExchangeData e where e.dataId = :dataId and e.dataRecVer = :recVer";
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("dataId", dataId);
        values.put("recVer", recVer);
        List<DXExchangeData> eList = listByHQL(hql, values);
        if (eList.size() > 0) {
            return eList.get(0);
        } else {
            return null;
        }
    }

    @Override
    public List<DXExchangeData> getDXExchangeDatasByBatchId(String batchId) {
        String hql = "from DXExchangeData e where e.dXExchangeBatch.id = :batchId";
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("batchId", batchId);
        return listByHQL(hql, values);
    }

    @Override
    public List<DXExchangeData> getDXExchangeDatasByBatchUuid(String batchUuid) {
        String hql = "from DXExchangeData e where e.dXExchangeBatch.uuid = :batchUuid";
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("batchUuid", batchUuid);
        return listByHQL(hql, values);
    }

    @Override
    public DXExchangeData getDXExchangeDatasByDataId(String dataId) {
        String hql = "from DXExchangeData e where e.dataId = :dataId and e.newestData='yes'";
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("dataId", dataId);
        List<DXExchangeData> exchangeDatas = listByHQL(hql, values);
        if (exchangeDatas != null && exchangeDatas.size() > 0) {
            return exchangeDatas.get(0);
        } else {
            return null;
        }

    }

    @Override
    public List<DXExchangeData> getDXExchangeDataListByDataId(String dataId) {
        String hql = "from DXExchangeData e where e.dataId = :dataId and e.validData='yes' order by e.createTime";
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("dataId", dataId);
        List<DXExchangeData> exchangeDatas = listByHQL(hql, values);
        return exchangeDatas;
    }

    @Override
    public DXExchangeRouteDate getDXExchangeDataMonitorByCorrelationandUnitId(String correlationId,
                                                                              Integer correlationRecVer, String unitId) {
        String hql = "from DXExchangeData e where e.dataId = :correlationDataId and e.dataRecVer = :correlationRecver";
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("correlationDataId", correlationId);
        values.put("correlationRecver", correlationRecVer);
        List<DXExchangeData> exchangeDatas = listByHQL(hql, values);
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
        List<QueryItem> result = listQueryItemByHQL(hql, values, null);
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
        return getNumberByHQL(hql, values);
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
        return getNumberByHQL(hql, values);
    }

    /**
     * 根据业务流水号、数据类型查找最高版本的行政许可过程信息
     *
     * @param ywlsh
     * @param string
     * @return
     */
    @Override
    public List<DXExchangeData> findNewestDataByYwlshAndTypeId(String ywlsh, String typeId, String other) {
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
    public List<DXExchangeData> getDXExchangeDatesBySpecial(String zch, String typeId) {
        StringBuffer hql = new StringBuffer();
        hql.append(" from ExchangeData e where e.reservedNumber2 = :reservedNumber2 and e.exchangeDataBatch.typeId = :typeId and e.newestData='yes' ");
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("reservedNumber2", zch);
        values.put("typeId", typeId);
        return listByHQL(hql.toString(), values);
    }

}
