/*
 * @(#)2018年6月6日 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.workflow.work.bean;

import com.wellsoft.pt.bpm.engine.entity.TaskInfoDistribution;
import com.wellsoft.pt.repository.entity.LogicFileInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2018年6月6日.1	zhulh		2018年6月6日		Create
 * </pre>
 * @date 2018年6月6日
 */
public class TaskInfoDistributionBean extends TaskInfoDistribution {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = -5454141323493448056L;

    private String distributorName;

    private List<LogicFileInfo> logicFileInfos = new ArrayList<LogicFileInfo>(0);

    /**
     * @return the distributorName
     */
    public String getDistributorName() {
        return distributorName;
    }

    /**
     * @param distributorName 要设置的distributorName
     */
    public void setDistributorName(String distributorName) {
        this.distributorName = distributorName;
    }

    /**
     * @return the logicFileInfos
     */
    public List<LogicFileInfo> getLogicFileInfos() {
        return logicFileInfos;
    }

    /**
     * @param logicFileInfos 要设置的logicFileInfos
     */
    public void setLogicFileInfos(List<LogicFileInfo> logicFileInfos) {
        this.logicFileInfos = logicFileInfos;
    }

}
