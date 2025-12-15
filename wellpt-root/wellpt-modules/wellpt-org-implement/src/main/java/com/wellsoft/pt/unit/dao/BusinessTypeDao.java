package com.wellsoft.pt.unit.dao;

import com.wellsoft.pt.org.dao.OrgHibernateDao;
import com.wellsoft.pt.unit.entity.BusinessType;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * Description: BusinessTypeDao.java
 *
 * @author liuzq
 * @date 2013-11-5
 */
@Repository
public class BusinessTypeDao extends OrgHibernateDao<BusinessType, String> {
    private static final String QUERY_BY_UUID_ID = "from BusinessType type where type.uuid != :uuid and type.id = :id";

    private static final String QUERY_BY_IDS = "from BusinessType type where type.id = :id and type.unit.id = :unitId";

    /**
     * 查询是否已经存在ID的实体列表
     *
     * @param paramMap
     * @return
     */
    public List<BusinessType> getListByUuidId(Map<String, Object> paramMap) {
        return this.find(QUERY_BY_UUID_ID, paramMap);
    }

    /**
     * 根据ID获取实体列表
     *
     * @param paramMap
     * @return
     */
    public BusinessType getById(String id) {
        return this.findUniqueBy("id", id);
    }

    public BusinessType getByIds(Map<String, Object> paramMap) {
        return this.findUniqueBy(QUERY_BY_IDS, paramMap);
    }
}
