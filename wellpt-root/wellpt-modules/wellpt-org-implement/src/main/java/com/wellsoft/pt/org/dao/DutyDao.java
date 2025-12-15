package com.wellsoft.pt.org.dao;

import com.wellsoft.pt.org.entity.Duty;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Description: 职务DAO
 *
 * @author zhengky
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	       修改人		修改日期	      修改内容
 * 2014-8-22.1  zhengky	2014-8-22	  Create
 * </pre>
 * @date 2014-8-22
 */
@Repository
public class DutyDao extends OrgHibernateDao<Duty, String> {

    private static final String GET_ALL_DUTY = " from Duty duty order by duty.ilevel desc ,duty.code asc ,duty.name ";

    private static final String GET_ALL_DUTY_ORDER_BY_EXTENID = " from Duty duty order by duty.externalId asc ";

    /**
     * @param id
     * @return
     */
    public Duty getByUuId(String uuid) {
        return this.findUniqueBy("uuid", uuid);
    }

    /**
     * @param id
     * @return
     */
    public Duty getById(String id) {
        return this.findUniqueBy("id", id);
    }

    public List<Duty> getAllDutys() {
        Map<String, Object> values = new HashMap<String, Object>();
        return this.find(GET_ALL_DUTY, values);
    }

    public List<Duty> getAllDutysOrderByExtenId() {
        Map<String, Object> values = new HashMap<String, Object>();
        return this.find(GET_ALL_DUTY_ORDER_BY_EXTENID, values);
    }
}
