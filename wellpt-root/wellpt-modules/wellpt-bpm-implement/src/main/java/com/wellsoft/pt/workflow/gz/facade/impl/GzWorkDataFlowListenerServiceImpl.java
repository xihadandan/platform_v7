/*
 * @(#)2015年7月23日 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.workflow.gz.facade.impl;

import com.wellsoft.pt.bpm.engine.support.WorkFlowSuspensionState;
import com.wellsoft.pt.jpa.service.impl.BaseServiceImpl;
import com.wellsoft.pt.jpa.util.BeanUtils;
import com.wellsoft.pt.workflow.gz.entity.WfGzDataSync;
import com.wellsoft.pt.workflow.gz.entity.WfGzDataSyncHis;
import com.wellsoft.pt.workflow.gz.facade.GzWorkDataFlowListenerService;
import com.wellsoft.pt.workflow.gz.service.WfGzDataSyncHisService;
import com.wellsoft.pt.workflow.gz.service.WfGzDataSyncService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2015年7月23日.1	zhulh		2015年7月23日		Create
 * </pre>
 * @date 2015年7月23日
 */
@Service
@Transactional
public class GzWorkDataFlowListenerServiceImpl extends BaseServiceImpl implements GzWorkDataFlowListenerService {

    @Autowired
    private WfGzDataSyncService wfGzDataSyncService;

    @Autowired
    private WfGzDataSyncHisService wfGzDataSyncHisService;

    /**
     * @param wfGzDataSyncs
     * @return
     */
    private static Map<String, WfGzDataSync> distinctByFlowInstUuidAndUserId(List<WfGzDataSync> wfGzDataSyncs) {
        Map<String, WfGzDataSync> map = new LinkedHashMap<String, WfGzDataSync>();
        for (WfGzDataSync wfGzSyncData : wfGzDataSyncs) {
            String key = wfGzSyncData.getSourceFlowInstUuid() + wfGzSyncData.getUserId();
            map.put(key, wfGzSyncData);
        }
        return map;
    }

    /**
     * 如何描述该方法
     *
     * @param wfGzDataSyncHis
     * @return
     */
    private static Map<String, WfGzDataSyncHis> distinctHisByFlowInstUuidAndUserId(List<WfGzDataSyncHis> wfGzDataSyncHis) {
        Map<String, WfGzDataSyncHis> map = new LinkedHashMap<String, WfGzDataSyncHis>();
        for (WfGzDataSyncHis wDataSyncHis : wfGzDataSyncHis) {
            String key = wDataSyncHis.getSourceFlowInstUuid() + wDataSyncHis.getUserId();
            map.put(key, wDataSyncHis);
        }
        return map;
    }

    /**
     * 如何描述该方法
     *
     * @param event
     */
    @Override
    public void addCompletedSyncData(String flowInstUuid) {
        Date currentTime = Calendar.getInstance().getTime();
        WfGzDataSync example = new WfGzDataSync();
        example.setSourceFlowInstUuid(flowInstUuid);
        List<WfGzDataSync> wfGzDataSyncs = wfGzDataSyncService.findByExampleByModifyTimeAsc(example);
        if (!wfGzDataSyncs.isEmpty()) {
            Map<String, WfGzDataSync> map = distinctByFlowInstUuidAndUserId(wfGzDataSyncs);
            for (String key : map.keySet()) {
                WfGzDataSync wfGzDataSync = map.get(key);
                WfGzDataSync dataSync = new WfGzDataSync();
                BeanUtils.copyProperties(wfGzDataSync, dataSync);
                dataSync.setUuid(null);
                dataSync.setCreateTime(currentTime);
                dataSync.setModifyTime(currentTime);
                dataSync.setSuspensionState(WorkFlowSuspensionState.Delete);
                this.getCommonDao().save(dataSync);
            }
        } else {
            WfGzDataSyncHis dataSyncHis = new WfGzDataSyncHis();
            dataSyncHis.setSourceFlowInstUuid(flowInstUuid);
            List<WfGzDataSyncHis> wfGzDataSyncHis = wfGzDataSyncHisService.findByExampleByModifyTimeAsc(dataSyncHis);
            Map<String, WfGzDataSyncHis> map = distinctHisByFlowInstUuidAndUserId(wfGzDataSyncHis);
            for (String key : map.keySet()) {
                WfGzDataSyncHis wDataSyncHis = map.get(key);
                WfGzDataSync dataSync = new WfGzDataSync();
                BeanUtils.copyProperties(wDataSyncHis, dataSync);
                dataSync.setUuid(null);
                dataSync.setCreateTime(currentTime);
                dataSync.setModifyTime(currentTime);
                dataSync.setSuspensionState(WorkFlowSuspensionState.Delete);
                this.getCommonDao().save(dataSync);
            }
        }
    }

}
