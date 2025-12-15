package com.wellsoft.pt.integration.dao.impl;

import com.wellsoft.pt.integration.dao.ExchangeDataBatchDao;
import com.wellsoft.pt.integration.entity.ExchangeDataBatch;
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
public class ExchangeDataBatchDaoImpl extends AbstractJpaDaoImpl<ExchangeDataBatch, String> implements
        ExchangeDataBatchDao {

    /**
     * 根据typeId获得所有批次
     *
     * @param typeId
     * @return
     */
    @Override
    public List<ExchangeDataBatch> getExchangeDataBatchByTypeId(String typeId) {
        String hql = "from ExchangeDataBatch e where e.typeId = :typeId";
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
    public List<ExchangeDataBatch> getExchangeDataBatchById(String id) {
        String hql = "from ExchangeDataBatch e where e.id = :id";
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("id", id);
        return listByHQL(hql, values);
    }

    /**
     * 获取ExchangeData中所有不同的发送单位名
     *
     * @return
     */
    @Override
    public List<String> getDistinctFromUnitList() {
        String hql = "select distinct fromId from ExchangeDataBatch";
        return listCharSequenceByHQL(hql, null);
    }

    /**
     * 根据类别获取ExchangeData中所有不同的发送单位名
     *
     * @return
     */
    @Override
    public List<String> getDistinctFromUnitListByTypeId(String typeId) {
        String hql = "select distinct fromId from ExchangeDataBatch e where e.typeId = :typeId ";
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("typeId", typeId);
        return listCharSequenceByHQL(hql, values);
    }

    /**
     * 获取ExchangeData中所有的发送单位名
     *
     * @return
     */
    @Override
    public List<String> getFromUnitList() {
        String hql = "select  fromId from ExchangeDataBatch";
        return listCharSequenceByHQL(hql, null);
    }

    /**
     * 如何描述该方法
     *
     * @return
     */
    @Override
    public List<String> getAllUnitId() {
        String hql = "select  allUnit from ExchangeDataBatch";
        return listCharSequenceByHQL(hql, null);
    }

}
