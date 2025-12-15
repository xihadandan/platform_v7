package com.wellsoft.pt.integration.dao;

import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.pt.integration.entity.DXExchangeData;
import com.wellsoft.pt.integration.entity.DXExchangeRouteDate;
import com.wellsoft.pt.jpa.dao.JpaDao;

import java.util.Date;
import java.util.List;

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
public interface DXExchangeDataDao extends JpaDao<DXExchangeData, String> {

    DXExchangeData getDXExchangeDataByDataId(String dataId, Integer recVer);

    DXExchangeData getDXExchangeDataByDataIdAll(String dataId, Integer recVer);

    public List<DXExchangeData> getDXExchangeDatasByBatchId(String batchId);

    List<DXExchangeData> getDXExchangeDatasByBatchUuid(String batchUuid);

    DXExchangeData getDXExchangeDatasByDataId(String dataId);

    List<DXExchangeData> getDXExchangeDataListByDataId(String dataId);

    DXExchangeRouteDate getDXExchangeDataMonitorByCorrelationandUnitId(String correlationId, Integer correlationRecVer,
                                                                       String unitId);

    /**
     * 上报逾期汇总
     *
     * @return
     */
    List<QueryItem> getSBYQ();

    /**
     * 部门许可汇总
     *
     * @return
     */
    List<QueryItem> getBMXK();

    /**
     * 根据typeId获取所有商事登记数量
     *
     * @param typeId
     * @return
     */
    long getAllCommercialSubjectByTypeId(String typeId);

    /**
     * 根据typeId获取本月商事登记数量
     *
     * @param typeId
     * @return
     */
    long getCurrentMonthCommercialRegistrationByTypeId(String typeId, Date firstDay, Date lastDay);

    /**
     * 根据业务流水号、数据类型查找最高版本的行政许可过程信息
     *
     * @param ywlsh
     * @param string
     * @return
     */
    List<DXExchangeData> findNewestDataByYwlshAndTypeId(String ywlsh, String typeId, String other);

    /**
     * 获取指定注册号的商事主体登记的最新数据
     *
     * @return
     */
    List<DXExchangeData> getDXExchangeDatesBySpecial(String zch, String typeId);
}
