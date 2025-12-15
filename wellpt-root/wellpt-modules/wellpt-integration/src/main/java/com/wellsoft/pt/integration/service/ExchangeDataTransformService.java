package com.wellsoft.pt.integration.service;

import com.wellsoft.pt.integration.dao.ExchangeDataTransformDao;
import com.wellsoft.pt.integration.entity.ExchangeDataTransform;
import com.wellsoft.pt.jpa.service.JpaService;

import java.util.List;

/**
 * Description: 数据转换接口
 *
 * @author Administrator
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-11-18.1	wbx		2013-11-18		Create
 * </pre>
 * @date 2013-11-18
 */
public interface ExchangeDataTransformService extends
        JpaService<ExchangeDataTransform, ExchangeDataTransformDao, String> {

    public List<ExchangeDataTransform> getListBySourceTypeId(String typeid);

    public List<ExchangeDataTransform> getBeanByids(String transformIds);

    /**
     * @param string
     * @param transformId
     * @return
     */
    public ExchangeDataTransform getById(String transformId);

    void save(ExchangeDataTransform entity);

    void delete(String uuid);

    void deleteByUuids(List<String> uuids);

    ExchangeDataTransform getBeanByUuid(String uuid);

}
