package com.wellsoft.pt.integration.dao;

import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.pt.integration.entity.ExchangeDataMonitor;
import com.wellsoft.pt.jpa.dao.JpaDao;

import java.util.List;

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
public interface ExchangeDataMonitorDao extends JpaDao<ExchangeDataMonitor, String> {

    List<ExchangeDataMonitor> getRepeatExchangeDataMonitor();

    List<QueryItem> getJSYQ();

    List<String> getDistinctUnitList();

    ExchangeDataMonitor getObjByUnitAndDataId(String unitId, String dataId, Integer recVer);

}
