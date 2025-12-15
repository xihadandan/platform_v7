package com.wellsoft.pt.integration.service.impl;

import com.wellsoft.pt.integration.dao.ExchangeDataTransformDao;
import com.wellsoft.pt.integration.entity.ExchangeDataTransform;
import com.wellsoft.pt.integration.service.ExchangeDataTransformService;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Description: 数据转换业务类
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
@Service
public class ExchangeDataTransformServiceImpl extends
        AbstractJpaServiceImpl<ExchangeDataTransform, ExchangeDataTransformDao, String> implements
        ExchangeDataTransformService {

    @Override
    public List<ExchangeDataTransform> getListBySourceTypeId(String typeid) {
        return dao.getListBySourceTypeId(typeid);
    }

    @Override
    public List<ExchangeDataTransform> getBeanByids(String transformIds) {
        return dao.getBeanByids(transformIds);
    }

    @Override
    public ExchangeDataTransform getById(String transformId) {
        return dao.getOneByHQL("from ExchangeDataTransform where id='" + transformId + "'", null);
    }

    @Override
    @Transactional
    public void save(ExchangeDataTransform entity) {
        super.save(entity);
    }

    @Override
    @Transactional
    public void delete(String uuid) {
        super.delete(uuid);
    }

    @Override
    @Transactional
    public void deleteByUuids(List<String> uuids) {
        super.deleteByUuids(uuids);
    }

    @Override
    public ExchangeDataTransform getBeanByUuid(String uuid) {
        return dao.getOne(uuid);
    }

}
