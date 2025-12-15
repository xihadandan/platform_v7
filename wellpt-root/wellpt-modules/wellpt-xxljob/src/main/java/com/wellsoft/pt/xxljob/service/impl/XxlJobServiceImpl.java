package com.wellsoft.pt.xxljob.service.impl;

import com.wellsoft.pt.xxljob.service.XxlJobService;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.well.WellXxlJobBiz;
import com.xxl.job.core.well.model.TmpJobParam;
import com.xxl.job.core.well.model.req.JobIdReqParam;
import com.xxl.job.core.well.model.req.TmpJobReqParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class XxlJobServiceImpl implements XxlJobService {

    @Autowired
    private WellXxlJobBiz wellXxlJobBiz;


    /**
     * 添加并启动临时任务
     * 返回 List<JobId>
     *
     * @param tmpJobParam
     * @return
     */
    @Override
    public List<String> addTmpStart(TmpJobParam tmpJobParam) {
        TmpJobReqParam tmpJobReqParam = new TmpJobReqParam();
        tmpJobReqParam.setTmpJobParam(tmpJobParam);
        ReturnT<List<String>> returnT = wellXxlJobBiz.addTmpJobStart(tmpJobReqParam);
        if (returnT.getCode() != ReturnT.SUCCESS_CODE) {
            throw new RuntimeException(returnT.getMsg());
        }
        return returnT.getContent();
    }

    /**
     * 根据JobIdList 批量删除任务
     *
     * @param jobIdList
     */
    @Override
    public void remove(List<String> jobIdList) {
        JobIdReqParam jobIdReqParam = new JobIdReqParam();
        jobIdReqParam.setJobIds(jobIdList);
        ReturnT<String> returnT = wellXxlJobBiz.remove(jobIdReqParam);
        if (returnT.getCode() != 200) {
            throw new RuntimeException(returnT.getMsg());
        }
    }
}
