package com.wellsoft.pt.xxljob.service;

import com.xxl.job.core.well.model.TmpJobParam;

import java.util.List;

public interface XxlJobService {


    /**
     * 添加并启动临时任务
     * 返回 List<JobId>
     *
     * @return
     */
    List<String> addTmpStart(TmpJobParam tmpJobParam);

    /**
     * 根据JobIdList 批量删除任务
     *
     * @param jobIdList
     */
    void remove(List<String> jobIdList);

}
