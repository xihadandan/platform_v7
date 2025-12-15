package com.wellsoft.pt.unit.dao;

import com.wellsoft.pt.org.dao.OrgHibernateDao;
import com.wellsoft.pt.unit.entity.CommonUnitTree;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Description: CommonUnitTreeDao.java
 *
 * @author liuzq
 * @date 2013-11-5
 */
@Repository
public class CommonUnitTreeDao extends OrgHibernateDao<CommonUnitTree, String> {
    private static final String QUERY_TOPLEVEL_POSITION = "from CommonUnitTree tree where tree.parent.uuid is null or tree.parent.uuid ='' order by tree.code asc";
    private static final String QUERY_TOPLEVEL_POSITION_ALL = "from CommonUnitTree tree order by tree.code asc";

    @SuppressWarnings("deprecation")
    public List<CommonUnitTree> getCommonUnitTreeRootList() {
        return this.find(QUERY_TOPLEVEL_POSITION);
    }

    @SuppressWarnings("deprecation")
    public List<CommonUnitTree> getCommonUnitTreeAllIdList() {
        return this.find(QUERY_TOPLEVEL_POSITION_ALL);
    }
}
