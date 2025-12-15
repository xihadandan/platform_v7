package com.wellsoft.pt.app.service.impl;

import com.google.common.collect.Maps;
import com.wellsoft.pt.app.dao.AppProdVersionParamDao;
import com.wellsoft.pt.app.entity.AppProdVersionParamEntity;
import com.wellsoft.pt.app.service.AppProdVersionParamService;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * Description: 如何描述该类
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2023年09月11日   chenq	 Create
 * </pre>
 */
@Service
public class AppProdVersionParamServiceImpl extends AbstractJpaServiceImpl<AppProdVersionParamEntity, AppProdVersionParamDao, Long> implements AppProdVersionParamService {

    @Override
    public List<AppProdVersionParamEntity> getAllParamsDetail(Long prodVersionUuid) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("prodVersionUuid", prodVersionUuid);
        return this.dao.listByHQL("from AppProdVersionParamEntity where prodVersionUuid=:prodVersionUuid order by createTime desc", params);
    }

    @Override
    public List<AppProdVersionParamEntity> getAllParamProp(Long prodVersionUuid) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("prodVersionUuid", prodVersionUuid);
        return this.dao.listByHQL("select propKey, propValue from AppProdVersionParamEntity where prodVersionUuid=:prodVersionUuid order by createTime desc", params);
    }

    @Override
    @Transactional
    public void deleteVersionParams(Long prodVersionUuid) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("prodVersionUuid", prodVersionUuid);
        dao.deleteByHQL("delete from AppProdVersionParamEntity where prodVersionUuid=:prodVersionUuid ", params);
    }

    @Override
    @Transactional
    public void deleteParam(Long uuid) {
        delete(uuid);
    }

    @Override
    @Transactional
    public Long saveParam(AppProdVersionParamEntity temp) {
        AppProdVersionParamEntity param = temp.getUuid() == null ? temp : getOne(temp.getUuid());
        if (temp.getUuid() != null) {
            param.setName(temp.getName());
            param.setPropKey(temp.getPropKey());
            param.setPropValue(temp.getPropValue());
            param.setRemark(temp.getRemark());
        }
        save(param);
        return temp.getUuid();
    }

    @Override
    public AppProdVersionParamEntity getParam(Long prodVersionUuid, String propKey) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("prodVersionUuid", prodVersionUuid);
        params.put("propKey", propKey);
        return this.dao.getOneByHQL("from AppProdVersionParamEntity where prodVersionUuid=:prodVersionUuid and propKey=:propKey", params);
    }

    @Override
    @Transactional
    public void deleteParams(List<Long> uuid) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("uuid", uuid);
        dao.deleteByHQL("delete from AppProdVersionParamEntity where uuid in :uuid ", params);
    }
}
