package com.wellsoft.pt.org.dao.impl;

import com.google.common.collect.Maps;
import com.wellsoft.pt.jpa.dao.impl.AbstractJpaDaoImpl;
import com.wellsoft.pt.org.entity.OrgElementEntity;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * Description: 如何描述该类
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2022年11月09日   chenq	 Create
 * </pre>
 */
@Repository
public class OrgElementDaoImpl extends AbstractJpaDaoImpl<OrgElementEntity, Long> {
    public void deleteByIdsAndOrgVersionUuid(List<String> ids, Long orgVersionUuid) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("ids", ids);
        params.put("orgVersionUuid", orgVersionUuid);
        deleteByHQL("delete OrgElementEntity where id in (:ids) and orgVersionUuid = :orgVersionUuid", params);
    }
}
