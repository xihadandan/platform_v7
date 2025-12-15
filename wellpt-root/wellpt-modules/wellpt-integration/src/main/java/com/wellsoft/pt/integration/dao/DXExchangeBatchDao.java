package com.wellsoft.pt.integration.dao;

import com.wellsoft.pt.integration.entity.DXExchangeBatch;
import com.wellsoft.pt.jpa.dao.JpaDao;

import java.util.List;

/**
 * Description: ExchangeDataBathDao
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
public interface DXExchangeBatchDao extends JpaDao<DXExchangeBatch, String> {

    /**
     * 根据typeId获得所有批次
     *
     * @param typeId
     * @return
     */
    List<DXExchangeBatch> getDXExchangeBatchByTypeId(String typeId);

    /**
     * 如何描述该方法
     *
     * @param id
     * @return
     */
    DXExchangeBatch getDXExchangeBatchById(String id);

    /**
     * 获取ExchangeData中所有不同的发送单位名
     *
     * @return
     */
    List<String> getDistinctFromUnitList();

    /**
     * 根据类别获取ExchangeData中所有不同的发送单位名
     *
     * @return
     */
    List<String> getDistinctFromUnitListByTypeId(String typeId);

    /**
     * 获取ExchangeData中所有的发送单位名
     *
     * @return
     */
    List<String> getFromUnitList();

    /**
     * 如何描述该方法
     *
     * @return
     */
    List<String> getAllUnitId();

}
