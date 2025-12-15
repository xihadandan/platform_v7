package com.wellsoft.pt.org.dao;

import com.wellsoft.pt.org.entity.Employee;
import org.springframework.stereotype.Repository;

/**
 *
 */
@Repository
public class EmployeeDao extends OrgHibernateDao<Employee, String> {
    /**
     * @param leaderId
     * @return
     */
    public Employee getById(String id) {
        return this.findUniqueBy("id", id);
    }

}
