package com.wellsoft.pt.org.service.impl;


import com.wellsoft.pt.jpa.service.impl.BaseServiceImpl;
import com.wellsoft.pt.org.entity.JobFunction;
import com.wellsoft.pt.org.service.JobFunctionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;

@Service
@Transactional
public class JobFunctionServiceImpl extends BaseServiceImpl implements JobFunctionService {

    private String QUERY_FUNCTION = "from JobFunction job_function where job_function.functionUuid = :uuid";
    private String QUERY_BY_JOB_UUID = "from JobFunction job_function where job_function.job.uuid = :uuid";
    private String DELETE_BY_JOB_UUID = "delete from JobFunction job_function where job_function.job.uuid = :uuid";
    private String DELETE_FUNCTION_BY_JOB_UUID = "delete from JobFunction job_function where job_function.functionUuid = :uuid";

    /**
     * @param uuid
     * @return
     */
    public List<JobFunction> getByJob(String uuid) {
        HashMap<String, Object> queryMap = new HashMap<String, Object>();
        queryMap.put("uuid", uuid);
        return this.dao.query(QUERY_BY_JOB_UUID, queryMap);
    }

    /**
     * 如何描述该方法
     *
     * @param uuid
     */
    public void deleteByJob(String uuid) {
        HashMap<String, Object> queryMap = new HashMap<String, Object>();
        queryMap.put("uuid", uuid);
        this.dao.batchExecute(DELETE_BY_JOB_UUID, queryMap);
    }

    /**
     * 如何描述该方法
     *
     * @param uuid
     */
    public void deleteFunction(String uuid) {
        HashMap<String, Object> queryMap = new HashMap<String, Object>();
        queryMap.put("uuid", uuid);
        this.dao.batchExecute(DELETE_FUNCTION_BY_JOB_UUID, queryMap);
    }

    /**
     * @param uuid
     * @return
     */
    public List<JobFunction> getFunctions(String uuid) {
        HashMap<String, Object> queryMap = new HashMap<String, Object>();
        queryMap.put("uuid", uuid);
        return this.dao.query(QUERY_FUNCTION, queryMap);
    }

    @Override
    public void save(JobFunction entity) {
        // TODO Auto-generated method stub
        this.dao.save(entity);
    }


}
