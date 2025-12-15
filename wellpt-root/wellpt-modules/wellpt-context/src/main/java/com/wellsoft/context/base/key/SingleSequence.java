package com.wellsoft.context.base.key;

import com.wellsoft.context.jdbc.support.CommonSqlManager;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.concurrent.locks.ReentrantLock;

/**
 * @author lilin
 * @ClassName: SingleSequence
 * @Description: TODO(这里用一句话描述这个类的作用)
 */
public class SingleSequence {
    private final ReentrantLock lock = new ReentrantLock(false);

    public long currVal = 0L;

    private CommonSqlManager sqlManager;

    private String tableName;
    private String prefix;

    public SingleSequence(String tableName) {
        this.tableName = tableName;
        initPrefix();
        initCurrId();
    }

    private void initCurrId() {
        String id = getMaxId();
        // 初始值id为null 或者""时候 直接返回 currVal默认为0
        if (id == null || id.equals("")) {
            return;
        }
        int i = id.indexOf(prefix) + prefix.length();
        currVal = Long.parseLong(id.substring(i));
    }

    private void initPrefix() {
        if (tableName.equals(KeyConstants.TABLE_USER)) {
            prefix = KeyConstants.PREFIX_USER;
        } else if (tableName.equals(KeyConstants.TABLE_GROUP)) {
            prefix = KeyConstants.PREFIX_GROUP;
        } else if (tableName.equals(KeyConstants.TABLE_DEP)) {
            prefix = KeyConstants.PREFIX_DEP;
        }
    }

    // 返回下一个值
    public String getNextVal() {
        try {
            lock.lock();
            currVal++;
            return prefix + getFullVal(currVal);
        } finally {
            lock.unlock();
        }
    }

    // 返回补位后信息
    private String getFullVal(long currVal) {
        String value = String.valueOf(currVal);
        String temp = "";
        for (int i = 0; i < KeyConstants.LENGTH - value.length(); i++) {
            temp = temp + "0";
        }
        return temp + value;
    }

    private String getMaxId() {
        String sql = "select max(" + KeyConstants.ID + ") from " + tableName;
        String id = sqlManager.getString(sql);
        return id;
    }

    @Autowired
    public void setSqlManager(CommonSqlManager sqlManager) {
        this.sqlManager = sqlManager;
    }
}