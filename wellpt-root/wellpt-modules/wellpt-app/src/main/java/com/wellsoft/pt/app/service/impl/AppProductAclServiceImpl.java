package com.wellsoft.pt.app.service.impl;

import com.google.common.collect.Maps;
import com.wellsoft.pt.app.dao.AppProductAclDao;
import com.wellsoft.pt.app.entity.AppProductAclEntity;
import com.wellsoft.pt.app.service.AppProductAclService;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
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
 * 2023年08月02日   chenq	 Create
 * </pre>
 */
@Service
public class AppProductAclServiceImpl extends AbstractJpaServiceImpl<AppProductAclEntity, AppProductAclDao, Long> implements AppProductAclService {
    @Override
    @Transactional
    public void deleteProdAcl(String prodId, String type) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("prodId", prodId);
        StringBuilder sql = new StringBuilder("delete from AppProductAclEntity where prodId=:prodId");
        if (StringUtils.isNotBlank(type)) {
            params.put("type", type);
            sql.append(" and type =:type");
        }
        this.dao.deleteByHQL(sql.toString(), params);
    }

    @Override
    @Transactional
    public void addProductAcl(List<AppProductAclEntity> list) {
        saveAll(list);
    }

    @Override
    @Transactional
    public void saveProdAcl(List<AppProductAclEntity> list, String prodId) {
        deleteProdAcl(prodId, null);
        if (CollectionUtils.isNotEmpty(list)) {
            for (AppProductAclEntity acl : list) {
                acl.setProdId(prodId);
            }
            saveAll(list);
        }
    }

    @Override
    public List<AppProductAclEntity> getProdAcl(String prodId) {
        return dao.listByFieldEqValue("prodId", prodId);
    }
}
