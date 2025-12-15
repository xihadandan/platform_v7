package com.wellsoft.pt.unit.dao;

import com.wellsoft.pt.org.dao.OrgHibernateDao;
import com.wellsoft.pt.unit.entity.BusinessUnitTree;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Description: BusinessUnitTreeDao.java
 *
 * @author liuzq
 * @date 2013-11-5
 */
@Repository
public class BusinessUnitTreeDao extends OrgHibernateDao<BusinessUnitTree, String> {
    private static final String QUERY_TOPLEVEL_POSITION = "from BusinessUnitTree tree where (tree.parent.uuid is null or tree.parent.uuid ='') and tree.businessType.uuid = :businessTypeUuid order by tree.code asc";

    private static final String QUERY_USER_BY_UNIT_AND_BUSINESSTYPE = "from BusinessUnitTree tree where (tree.unit.id=:unitId  or  tree.unit.unitId=:unitId) and tree.businessType.id=:businessTypeId";

    private static final String QUERY_BY_BUSINESS_TYPE_AND_UNIT_ID = "from BusinessUnitTree tree where tree.businessType.id = :businessTypeId and (tree.unit.id=:unitId  or  tree.unit.unitId=:unitId)";

    private static final String QUERY_USER_BY_BUSINESSTYPE = "from BusinessUnitTree tree where tree.businessType.id=:businessTypeId";

    private static final String QUERY_BUSINESS_UNIT_TREE = "from BusinessUnitTree tree where tree.businessType.id=:businessTypeId and (tree.businessManagerUserId like '%' || :userId || '%' "
            + " or tree.businessSenderId like '%' || :userId || '%' or tree.businessReceiverId like '%' || :userId || '%' "
            + " or exists(select role.uuid from BusinessUnitTreeRole role where role.businessUnitTreeUuid = tree.uuid and role.memberId like '%' || :userId || '%'))";

    /**
     * 根据业务类别获取业务单位通讯录
     *
     * @param paramMap
     * @return
     */
    public List<BusinessUnitTree> getUnitTreeRoot(Map<String, Object> paramMap) {
        return this.find(QUERY_TOPLEVEL_POSITION, paramMap);
    }

    public List<BusinessUnitTree> getBusinessUnitTreeByParams(Map<String, Object> paramMap) {
        return this.find(QUERY_USER_BY_UNIT_AND_BUSINESSTYPE, paramMap);
    }

    /**
     * @param businessType
     * @param commonUnitId
     * @return
     */
    public List<BusinessUnitTree> getByBusinessTypeAndUnitId(String businessType, String commonUnitId) {
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("businessTypeId", businessType);
        values.put("unitId", commonUnitId);
        return this.find(QUERY_BY_BUSINESS_TYPE_AND_UNIT_ID, values);
    }

    /**
     * @param paramMap
     * @return
     */
    public List<BusinessUnitTree> getBusinessUnitManagerByBusinessTypeId(Map<String, Object> paramMap) {
        return this.find(QUERY_USER_BY_BUSINESSTYPE, paramMap);
    }

    /**
     * @param businessTypeId
     * @param userId
     * @return
     */
    public List<BusinessUnitTree> getByBusinessTypeIdAndUserId(String businessTypeId, String userId) {
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("businessTypeId", businessTypeId);
        values.put("userId", userId);
        return this.find(QUERY_BUSINESS_UNIT_TREE, values);
    }

}
