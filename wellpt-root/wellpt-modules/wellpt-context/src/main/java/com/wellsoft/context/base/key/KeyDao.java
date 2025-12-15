package com.wellsoft.context.base.key;

import com.wellsoft.context.jdbc.support.CommonSqlManager;
import org.springframework.stereotype.Service;

@Service
public class KeyDao {
    private CommonSqlManager sqlManager;

    public String getMaxId(String tableName) {
        String sql = "select max(" + KeyConstants.ID + ") from " + tableName;
        String id = sqlManager.getString(sql);
        return id;
    }
}
