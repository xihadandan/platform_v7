package com.wellsoft.pt.integration.dao;

import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.pt.integration.entity.DXExchangeRouteDate;
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
public interface DXExchangeRouteDateDao extends JpaDao<DXExchangeRouteDate, String> {

    DXExchangeRouteDate getObjByUnitAndDataId(String unitId, String dataId, Integer recVer);

    /**
     * 获取所有不同的接收单位名
     *
     * @return
     */
    List<String> getDistinctUnitList();

    /**
     * 接收逾期汇总
     *
     * @return
     */
    List<QueryItem> getJSYQ();

    /**
     * 获得批次下的所有收件
     *
     * @param batchId
     * @return
     */
    List<DXExchangeRouteDate> getDXExchangeDataReceiveByBatchId(String batchId);

    /**
     * 获得需要重发收件
     *
     * @return
     */
    List<DXExchangeRouteDate> getRepeatDXExchangeDataReceive();
}
