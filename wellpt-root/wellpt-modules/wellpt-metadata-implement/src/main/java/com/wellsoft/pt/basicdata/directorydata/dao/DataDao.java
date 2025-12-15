package com.wellsoft.pt.basicdata.directorydata.dao;

import com.wellsoft.pt.basicdata.directorydata.entity.Data;
import com.wellsoft.pt.jpa.hibernate.HibernateDao;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class DataDao extends HibernateDao<Data, String> {

    @SuppressWarnings("all")
    public Data findByUuid(String uuid) {
        return findUnique("from Data a where a.uuid=?", uuid);
    }

    public List<Data> findByDirectoryUuid(String directoryUuid) {
        return this.find("from Data a where a.directory.uuid=?", directoryUuid);
    }

    public List<Data> findByDirectoryTopUuid(String directoryTopUuid) {
        return this.find("from Data a where a.directoryTopUuid=?", directoryTopUuid);
    }
}
