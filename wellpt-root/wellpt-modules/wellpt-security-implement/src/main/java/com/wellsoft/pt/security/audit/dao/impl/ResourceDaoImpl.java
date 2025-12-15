package com.wellsoft.pt.security.audit.dao.impl;

// Generated 2011-4-26 9:37:16 by Hibernate Tools 3.4.0.CR1

import com.wellsoft.pt.jpa.dao.impl.AbstractJpaDaoImpl;
import com.wellsoft.pt.security.audit.dao.ResourceDao;
import com.wellsoft.pt.security.audit.entity.Resource;
import com.wellsoft.pt.security.enums.ResourceType;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 *
 */
@Repository
public class ResourceDaoImpl extends AbstractJpaDaoImpl<Resource, String> implements ResourceDao {
    private static final String QUERY_URLS = "from Resource resource where resource.type = 'MENU'";
    private static final String QUERY_METHODS = "from Resource resource where resource.type = 'METHOD'";
    private static final String QUERY_BUTTONS = "from Resource resource where resource.type = 'BUTTON'";
    private static final String QUERY_TOP_LEVEL = "from Resource resource where resource.parent.uuid is null or resource.parent.uuid = '' order by resource.code asc";

    /**
     * <p>
     * Title: delete
     * </p>
     * <p>
     * Description: 重载函数,因为Resource中没有建立与authority的关联,
     * 因此需要以较低效率的方式进行删除Resource与Authority的多对多中间表.
     * </p>
     *
     * @param id
     * @see org.springside.modules.orm.hibernate.SimpleHibernateDao#delete(java.io.Serializable)
     */
    @Override
    @Transactional
    public void delete(String uuid) {
        Resource resource = getOne(uuid);
        // 查询resource对的authority
        // List<Permission> authorities = createQuery(
        // QUERY_AUTHORITY_BY_RESOURCEID, resource.getUuid()).list();
        // for (Permission a : authorities) {
        // a.getResourceList().remove(resource);
        // }
        super.delete(resource);
    }

    /**
     * @return
     */
    public List<Resource> getToLevel() {
        return this.listByHQL(QUERY_TOP_LEVEL, null);
    }

    /**
     * @return
     */
    public List<Resource> getUrlResources() {
        return this.listByHQL(QUERY_URLS, null);
    }

    /**
     * @return
     */
    public List<Resource> getMethodResources() {
        return this.listByHQL(QUERY_METHODS, null);
    }

    /**
     * Description how to use this method
     *
     * @return
     */
    public List<Resource> getButtonResources() {
        return this.listByHQL(QUERY_BUTTONS, null);
    }

    /**
     * @param code
     * @return
     */
    @SuppressWarnings("unchecked")
    public List<Resource> getDynamicButtonsByCode(String code) {
        return this.getSession().createCriteria(Resource.class)
                .add(Restrictions.eq("type", ResourceType.BUTTON.getValue()))
                .add(Restrictions.like("code", "B" + code, MatchMode.START)).addOrder(Order.asc("code")).list();
    }
}
