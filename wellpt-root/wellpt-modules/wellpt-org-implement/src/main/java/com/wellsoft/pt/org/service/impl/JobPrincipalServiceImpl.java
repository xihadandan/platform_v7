package com.wellsoft.pt.org.service.impl;


import com.wellsoft.pt.jpa.service.impl.BaseServiceImpl;
import com.wellsoft.pt.org.entity.JobPrincipal;
import com.wellsoft.pt.org.service.JobPrincipalService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class JobPrincipalServiceImpl extends BaseServiceImpl implements JobPrincipalService {


    private static final String GET_PRINCIPAL_BY_JOB_UUID = "from JobPrincipal jp where jp.type = '0' and jp.jobUuid = :jobUuid ";
    private static final String DELETE_PRINCIPAL_BY_JOB_UUID = "delete from JobPrincipal jp where jp.type = '0' and jp.jobUuid = :jobUuid";

    /**
     * 根据部门UUID，获取部门负责人
     *
     * @param uuid
     */
    public List<JobPrincipal> getPrincipal(String deptUuid) {
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("jobUuid", deptUuid);
        return this.dao.query(GET_PRINCIPAL_BY_JOB_UUID, values);
    }

    /**
     * 根据部门UUID，删除部门负责人
     *
     * @param uuid
     */
    public void deletePrincipal(String deptUuid) {
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("jobUuid", deptUuid);
        this.dao.query(DELETE_PRINCIPAL_BY_JOB_UUID, values);
    }


}
