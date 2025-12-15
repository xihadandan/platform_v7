/*
 * @(#)2018年1月16日 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.constant;

import com.wellsoft.pt.bpm.engine.support.CustomRuntimeData;

/**
 * Description: 如何描述该类
 *
 * @author chenqiong
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2018年1月16日.1	chenqiong		2018年1月16日		Create
 * </pre>
 * @date 2018年1月16日
 */
public class FlowConstant {

    public static final String BEGIN = "BEGIN";

    public static final String START_FLOW_ID = "<StartFlow>";

    public static final String END_FLOW_ID = "<EndFlow>";

    public static final String END_FLOW = "END_FLOW";

    public static final String AS_DRAFT = "DRAFT";

    public static final String AUTO_SUBMIT = "AUTO_SUBMIT";

    public static final String START_TODO = "START_TODO";

    public static final String CUSTOM_JS_MODULE = "customJsModule";

    // 承办情况数据来源
    public interface UNDERTAKE_SITUATION_DATA_SOURCE {
        // 分支流
        public static final String BRANCH_TASK = "1";
        // 子流程
        public static final String SUBFLOW = "2";
    }

    // 分支
    public interface BRANCH {
        // 是否并行分支
        public static final String IS_PARALLEL = "isParallel";
        // 并行发支的环节实例UUID
        public static final String PARALLEL_TASK_INST_UUID = "parallelTaskInstUuid";
        // 是否最后的分发任务
        public static final String IS_THE_LAST_FORKING_TASK = "isTheLastForkingTask";
        // 合并的分支任务是否都完成
        public static final String IS_MERGED_BRANCH_TASK_FINISHED = "isMergedBranchTaskFinished";
    }

    // 流向分支模式
    public interface BRANCH_MODE {
        // 静态分支
        public static final String STATIC = "1";
        // 动态多分支
        public static final String DYNAMIC = "2";
    }

    // 分支创建方式
    public interface BRANCH_CREATE_WAY {
        // 表单字段
        public static final String DYFORM = "12";
        // 自定义
        public static final String CUSTOM = "3";
    }

    // 分支类型
    public interface BRANCH_TYPE {
        // 静态分支
        public static final String STATIC = "0";
        // 独立分支
        public static final String INDEPENDENT = "1";
        // 主办分支
        public static final String MAJOR = "2";
        // 协办分支
        public static final String MINOR = "3";
    }

    // 创建方式
    public interface CREATE_WAY {
        // 表单字段
        public static final String DYFORM = "12";
        // 主表
        public static final String MAIN_FORM = "1";
        // 从表
        public static final String SUBFORM = "2";
        // 自定义
        public static final String CUSTOM = "3";
    }

    // 创建实例方式
    public interface CREATE_INSTANCE_WAY {
        // 单一实例
        public static final String SINGLETON = "1";
        // 按办理人生成
        public static final String BY_USER = "2";
    }

    // 子流程信息
    public interface SUB_FLOW {
        // 跟进人员
        public static final String KEY_FOLLOW_UP_USERS = "followUpUsers";
        // 当前批次的表单信息
        public static final String KEY_BATCH_FORM_INFO = "batchFormInfo";
        // 完成的批次
        public static final String KEY_COMPLATED_BATCH = "complatedBatch";
        // 同步的单据转换规则ID
        public static final String KEY_SYNC_BOT_RULE_ID = CustomRuntimeData.PREFIX + "sync_bot_rule_id";
        // 同步的子流程实例UUID
        public static final String KEY_SYNC_SUB_FLOW_INST_UUIDS = CustomRuntimeData.PREFIX + "sync_sub_flow_inst_uuids";
    }

}
