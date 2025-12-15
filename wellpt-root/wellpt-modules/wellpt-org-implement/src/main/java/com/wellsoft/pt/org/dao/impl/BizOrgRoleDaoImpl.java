package com.wellsoft.pt.org.dao.impl;

import com.google.common.collect.Maps;
import com.wellsoft.pt.jpa.dao.impl.AbstractJpaDaoImpl;
import com.wellsoft.pt.org.entity.BizOrgRoleEntity;
import org.springframework.stereotype.Repository;

import java.util.Map;

/**
 * Description: 如何描述该类
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2024年11月25日   chenq	 Create
 * </pre>
 */
@Repository
public class BizOrgRoleDaoImpl extends AbstractJpaDaoImpl<BizOrgRoleEntity, Long> {
    public void deleteByBizOrgUuid(Long bizOrgUuid) {
        Map<String, Object> param = Maps.newHashMap();
        param.put("bizOrgUuid", bizOrgUuid);
        this.deleteByHQL("delete from BizOrgRoleEntity where bizOrgUuid = :bizOrgUuid", param);
    }
}
