package com.wellsoft.pt.repository.dao.impl;

import com.wellsoft.pt.jpa.hibernate.HibernateDao;
import com.wellsoft.pt.repository.dao.FolderOperateLogDao;
import com.wellsoft.pt.repository.entity.FolderOperateLog;
import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class FolderOperateLogDaoImpl extends HibernateDao<FolderOperateLog, String> implements FolderOperateLogDao {
    Logger logger = Logger.getLogger(FolderOperateLogDaoImpl.class);

    @Override
    public List<FolderOperateLog> getLogsAfterTime(String folderID, Date date) {
        if (date == null) {
            return this.createCriteria(Restrictions.and(Restrictions.eq("folder.uuid", folderID))).list();
        } else {
            return this
                    .createCriteria(
                            Restrictions.and(Restrictions.eq("folder.uuid", folderID),
                                    Restrictions.ge("createTime", date))).addOrder(Order.asc("createTime")).list();
        }

    }

    @Override
    public Date getLastModifyTimeOfFolder(String folderID) {
        Map<String, String> map = new HashMap<String, String>();
        map.put("folder_uuid", folderID);
        String sql = "select max(createTime) from FolderOperateLog t where t.folder.uuid = :folder_uuid";

        Query query = this.createQuery(sql, map);
        List l = query.list();
        if (l == null || l.size() == 0) {
            return null;
        } else {
            return (Date) l.get(0);
        }

    }

    @Override
    public FolderOperateLog getLogByUuid(String uuid) {
        return this.findUnique(Restrictions.eq("uuid", uuid));

    }
}
