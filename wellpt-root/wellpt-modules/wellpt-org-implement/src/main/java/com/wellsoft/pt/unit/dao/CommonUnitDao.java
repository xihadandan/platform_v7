package com.wellsoft.pt.unit.dao;

import com.wellsoft.pt.org.dao.OrgHibernateDao;
import com.wellsoft.pt.unit.entity.CommonUnit;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Description: CommonUnitDao.java
 *
 * @author liuzq
 * @date 2013-11-5
 */
@Repository
public class CommonUnitDao extends OrgHibernateDao<CommonUnit, String> {
    private static final String QUERY_UNIT_BY_TENANTID = "from CommonUnit unit where unit.tenantId = :tenantId";

    private static final String QUERY_BY_UUID_ID = "from CommonUnit unit where unit.uuid != :uuid and unit.id = :id";

    private static final String QUERY_COMMONUNIT_BY_IDS = "select user.unit from CommonUser user where user.id=:userId and user.unit.tenantId=:tenantId";

    private static final String QUERY_COMMONUNIT_BY_BUSINESSTYPEID = "select unit from CommonUnit unit left join unit.businessTypes busi where busi.id=:businessTypeId";

    private static final String QUERY_COMMONUNIT_BY_BLUR_UNITNAME = " from CommonUnit u where u.name like :unitName";

    /**
     * 根据租户ID获取租户下的所有单位列表
     *
     * @param paramMap
     * @return
     */
    public List<CommonUnit> getListByParamMap(Map<String, Object> paramMap) {
        return this.find(QUERY_UNIT_BY_TENANTID, paramMap);
    }

    /**
     * 查询是否已经存在ID的实体列表
     *
     * @param paramMap
     * @return
     */
    public List<CommonUnit> getListByUuidId(Map<String, Object> paramMap) {
        return this.find(QUERY_BY_UUID_ID, paramMap);
    }

    /**
     * 根据ID获取公共库单位
     *
     * @param id
     * @return
     */
    public CommonUnit getById(String id) {
        return this.findUniqueBy("id", id);
    }

    /**
     * 根据用户ID和租户ID获取单位
     *
     * @param userId
     * @param tenantId
     * @return
     */
    public CommonUnit getCommonUnitByIds(String userId, String tenantId) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("userId", userId);
        paramMap.put("tenantId", tenantId);
        CommonUnit commonUnit = this.findUnique(QUERY_COMMONUNIT_BY_IDS, paramMap);
        return commonUnit;
    }

    public CommonUnit getCommonUnitByBusinessTypeId(String businessTypeId) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("businessTypeId", businessTypeId);
        return this.findUnique(QUERY_COMMONUNIT_BY_BUSINESSTYPEID, paramMap);
    }

    public List<CommonUnit> getCommonUnitsByBlurUnitName(String unitNameKey) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("unitName", "%" + unitNameKey + "%");
        return this.find(QUERY_COMMONUNIT_BY_BLUR_UNITNAME, paramMap);
    }

    public CommonUnit getByUnitId(String unitId) {
        // TODO Auto-generated method stub
        return this.findUniqueBy("unitId", unitId);
    }
}
