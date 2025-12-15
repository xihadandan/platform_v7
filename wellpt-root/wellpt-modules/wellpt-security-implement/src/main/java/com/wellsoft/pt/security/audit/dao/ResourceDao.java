package com.wellsoft.pt.security.audit.dao;

// Generated 2011-4-26 9:37:16 by Hibernate Tools 3.4.0.CR1

import com.wellsoft.pt.jpa.dao.JpaDao;
import com.wellsoft.pt.security.audit.entity.Resource;

import java.util.List;

/**
 *
 */
public interface ResourceDao extends JpaDao<Resource, String> {

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

    void delete(String uuid);

    /**
     * @return
     */
    List<Resource> getToLevel();

    /**
     * @return
     */
    List<Resource> getUrlResources();

    /**
     * @return
     */
    List<Resource> getMethodResources();

    /**
     * Description how to use this method
     *
     * @return
     */
    List<Resource> getButtonResources();

    /**
     * @param code
     * @return
     */
    @SuppressWarnings("unchecked")
    public List<Resource> getDynamicButtonsByCode(String code);
}
