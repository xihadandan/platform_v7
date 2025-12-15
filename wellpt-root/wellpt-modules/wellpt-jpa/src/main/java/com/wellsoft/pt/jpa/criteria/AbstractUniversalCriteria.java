package com.wellsoft.pt.jpa.criteria;

import com.wellsoft.pt.jpa.dao.UniversalDao;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

public abstract class AbstractUniversalCriteria extends AbstractCriteria {
    protected final UniversalDao dao;

    public AbstractUniversalCriteria(UniversalDao dao) {
        this.dao = dao;
    }

    /**
     * @return the nativeDao
     */
    protected UniversalDao getNativeDao() {
        return dao;
    }

    @Override
    public SessionFactory getSessionFactory() {
        return getSession().getSessionFactory();
    }

    @Override
    public Session getSession() {
        return this.getNativeDao().getSession();
    }
}
