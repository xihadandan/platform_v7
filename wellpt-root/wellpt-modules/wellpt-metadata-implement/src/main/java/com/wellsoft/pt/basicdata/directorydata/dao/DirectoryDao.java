package com.wellsoft.pt.basicdata.directorydata.dao;

import com.wellsoft.pt.basicdata.directorydata.entity.Directory;
import com.wellsoft.pt.jpa.hibernate.HibernateDao;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;

@Repository
public class DirectoryDao extends HibernateDao<Directory, String> {

    @SuppressWarnings("all")
    public Directory findByUniqueKeys(String name, String parentUuid) {
        if (StringUtils.trimToEmpty(parentUuid).equals("")) {
            return findUnique("from Directory a where a.name=? and a.parent is null", name);
        } else {
            return findUnique("from Directory a where a.name=? and a.parent.uuid=?", name, parentUuid);
        }
    }

    public Directory findByUuid(String uuid) {
        return this.findUniqueBy("uuid", uuid);
    }

    @SuppressWarnings("all")
    public List<Directory> getTopDirectory() {
        return this.find("from Directory a where a.parent is null", new HashMap<String, Object>(0));
    }

    @SuppressWarnings("all")
    public List<Directory> getDirectoryByParentUuid(String parentUuid) {
        return this.find("from Directory a where a.parent.uuid = ?", parentUuid);
    }

    @SuppressWarnings("all")
    public List<Directory> getTopDirectoryByParam(String param) {
        return this.find("from Directory a where a.parent is null and a.uuid = ?", param);
    }

    public List<Directory> findByDirectoryTopUuid(String directoryTopUuid) {
        return this.find("from Directory a where a.directoryTopUuid=?", directoryTopUuid);
    }
}
