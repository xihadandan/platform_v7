package com.wellsoft.pt.basicdata.sso.dao;

import com.wellsoft.pt.basicdata.sso.entity.SsoParams;
import com.wellsoft.pt.jpa.hibernate.HibernateDao;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class SsoParamsDao extends HibernateDao<SsoParams, String> {
    private static String DELETE_BY_SYSID = "delete from SsoParams t where t.sysId = :sysId";

    public void deleteBySYSID(String sysId) {
        Map<String, String> values = new HashMap<String, String>();
        values.put("sysId", sysId);
        batchExecute(DELETE_BY_SYSID, values);
    }

    public List<SsoParams> queryBySYSID(String sysId) {
        Map<String, String> values = new HashMap<String, String>();
        values.put("sysId", sysId);
        return find("from SsoParams t where t.sysId = :sysId", values);
    }
}