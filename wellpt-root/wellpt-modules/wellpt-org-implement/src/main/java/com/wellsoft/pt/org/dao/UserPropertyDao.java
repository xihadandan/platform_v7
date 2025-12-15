package com.wellsoft.pt.org.dao;

import com.google.common.collect.Maps;
import com.wellsoft.pt.jpa.dao.impl.AbstractJpaDaoImpl;
import com.wellsoft.pt.org.entity.UserProperty;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

@Repository
public class UserPropertyDao extends AbstractJpaDaoImpl<UserProperty, String> {

    private static final String DELETE_BY_USER = "delete from UserProperty user_property where user_property.userUuid = :userUuid";


    public void deleteByUser(String userUuid) {
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("userUuid", userUuid);
        this.deleteByHQL(DELETE_BY_USER, values);
    }

    public String getUserPropertyValue(String prop, String userUuid) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("propName", prop);
        params.put("userUuid", userUuid);
        return this.getCharSequenceBySQL(
                "select value from org_user_property where user_uuid=:userUuid and name=:propName",
                params);
    }
}
