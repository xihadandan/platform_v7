package com.wellsoft.pt.basicdata.sso.dao;

import com.wellsoft.pt.basicdata.sso.entity.SsoDetails;
import com.wellsoft.pt.jpa.hibernate.HibernateDao;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

@Repository
public class SsoDetailsDao extends HibernateDao<SsoDetails, String> {
    private static String DELETE_BY_NAME = "delete from SsoDetails sso_details where sso_details.sysName = :sysName";

    @Deprecated
    public void deleteByName(String name) {
        Map<String, String> values = new HashMap<String, String>();
        values.put("sysName", name);
        batchExecute(DELETE_BY_NAME, values);
    }

    public boolean existSysId(String sysId) {
        Map<String, String> values = new HashMap<String, String>();
        values.put("sysId", sysId);
        return countHqlResult("from SsoDetails t where t.sysId = :sysId", values) > 0;
    }

    public boolean exist(String uuid, String sysId) {
        Map<String, String> values = new HashMap<String, String>();
        values.put("uuid", uuid);
        values.put("sysId", sysId);
        return countHqlResult("from SsoDetails t where t.uuid <> :uuid and t.sysId = :sysId", values) > 0;
    }

    public SsoDetails getBySYSID(String sysId) {
        Map<String, String> values = new HashMap<String, String>();
        values.put("sysId", sysId);
        return (SsoDetails) find("from SsoDetails t where t.sysId = :sysId", values).get(0);
    }
}