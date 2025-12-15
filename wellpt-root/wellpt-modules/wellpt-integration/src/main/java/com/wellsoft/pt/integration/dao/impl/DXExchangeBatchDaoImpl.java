package com.wellsoft.pt.integration.dao.impl;

import com.wellsoft.pt.integration.dao.DXExchangeBatchDao;
import com.wellsoft.pt.integration.entity.DXExchangeBatch;
import com.wellsoft.pt.jpa.dao.impl.AbstractJpaDaoImpl;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
@Repository
public class DXExchangeBatchDaoImpl extends AbstractJpaDaoImpl<DXExchangeBatch, String> implements DXExchangeBatchDao {

    /**
     * 根据typeId获得所有批次
     *
     * @param typeId
     * @return
     */
    @Override
    public List<DXExchangeBatch> getDXExchangeBatchByTypeId(String typeId) {
        String hql = "from DXExchangeBatch e where e.typeId = :typeId";
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("typeId", typeId);
        return listByHQL(hql, values);
    }

    /**
     * 如何描述该方法
     *
     * @param id
     * @return
     */
    @Override
    public DXExchangeBatch getDXExchangeBatchById(String id) {
        String hql = "from DXExchangeBatch e where e.id = :id";
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("id", id);
        List<DXExchangeBatch> dXExchangeBatchs = listByHQL(hql, values);
        if (dXExchangeBatchs.size() > 0) {
            return dXExchangeBatchs.get(0);
        } else {
            return null;
        }

    }

    /**
     * 获取ExchangeData中所有不同的发送单位名
     *
     * @return
     */
    public List<String> getDistinctFromUnitList() {
        String hql = "select distinct fromId from DXExchangeBatch";
        Map<String, Object> values = new HashMap<String, Object>();
        return listCharSequenceByHQL(hql, values);
    }

    /**
     * 根据类别获取ExchangeData中所有不同的发送单位名
     *
     * @return
     */
    public List<String> getDistinctFromUnitListByTypeId(String typeId) {
        String hql = "select distinct fromId from DXExchangeBatch e where e.typeId = :typeId ";
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("typeId", typeId);
        return listCharSequenceByHQL(hql, values);
    }

    /**
     * 获取ExchangeData中所有的发送单位名
     *
     * @return
     */
    public List<String> getFromUnitList() {
        String hql = "select  fromId from DXExchangeBatch";
        Map<String, Object> values = new HashMap<String, Object>();
        return listCharSequenceByHQL(hql, values);
    }

    /**
     * 如何描述该方法
     *
     * @return
     */
    public List<String> getAllUnitId() {
        String hql = "select  allUnit from DXExchangeBatch";
        Map<String, Object> values = new HashMap<String, Object>();
        return listCharSequenceByHQL(hql, values);
    }

}
