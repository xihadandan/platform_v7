package com.wellsoft.pt.integration.dao.impl;

import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.pt.integration.dao.ExchangeDataMonitorDao;
import com.wellsoft.pt.integration.entity.ExchangeDataMonitor;
import com.wellsoft.pt.jpa.dao.impl.AbstractJpaDaoImpl;
import org.springframework.stereotype.Repository;

import java.util.*;

/**
 * Description: 交换数据监控Dao
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
public class ExchangeDataMonitorDaoImpl extends AbstractJpaDaoImpl<ExchangeDataMonitor, String> implements
        ExchangeDataMonitorDao {

    @Override
    public ExchangeDataMonitor getObjByUnitAndDataId(String unitId, String dataId, Integer recVer) {
        String hql = "from ExchangeDataMonitor e where e.exchangeData.dataId = :dataId and e.unitId = :unitId and e.exchangeData.dataRecVer = :recVer";
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("dataId", dataId);
        values.put("unitId", unitId);
        values.put("recVer", recVer);
        List<ExchangeDataMonitor> exchangeDataMonitors = this.listByHQL(hql, values);
        if (exchangeDataMonitors != null && exchangeDataMonitors.size() > 0) {
            return exchangeDataMonitors.get(0);
        } else {
            return null;
        }

    }

    /**
     * 获取所有不同的接收单位名
     *
     * @return
     */
    @Override
    public List<String> getDistinctUnitList() {
        String hql = "select distinct unitId from ExchangeDataMonitor e where e.replyLimitStatus is not null ";
        return listCharSequenceByHQL(hql, null);
    }

    /**
     * 接收逾期汇总
     *
     * @return
     */
    @Override
    public List<QueryItem> getJSYQ() {
        Date date = Calendar.getInstance().getTime();
        String hql = "select e.unitId as unit ,count(e.uuid) as count1 "
                + "from ExchangeDataMonitor e "
                + "where ((e.replyStatus = 'default' "
                + "and e.exchangeDataSendMonitor.limitTime < :date) or (e.replyLimitNum  is not null and e.replyStatus != 'default')) "
                + "group by  e.unitId " + "order by count1 desc";
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("date", date);
        List<QueryItem> result = this.listQueryItemByHQL(hql, values, null);
        return result;
    }

    /**
     * 获得需要重发收件
     *
     * @return
     */
    @Override
    public List<ExchangeDataMonitor> getRepeatExchangeDataMonitor() {
        // sendNum为空时（一次发送成功）发送成功，到达状态为默认，重发成功时置0（非0时，发布成功或者为配置，不需要重发发）,发送次数小于重发次数时
        String hql = "from ExchangeDataMonitor e where e.sendNum > 0 and e.receiveStatus = 'default' and e.retransmissionNum>0 and e.sendNum<e.retransmissionNum";
        Map<String, Object> values = new HashMap<String, Object>();
        return this.listByHQL(hql, values);
    }
}
