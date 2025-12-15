/*
 * @(#)12/4/24 V1.0
 *
 * Copyright 2024 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.management.support;

import com.wellsoft.context.base.BaseObject;
import com.wellsoft.pt.bpm.engine.query.FlowDefinitionUserQueryItem;
import com.wellsoft.pt.bpm.engine.support.FlowDefinitionUserModifyParams;

import java.util.List;

/**
 * Description: 办理人替换请求参数
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本		修改人		修改日期			修改内容
 * 12/4/24.1	    zhulh		12/4/24		    Create
 * </pre>
 * @date 12/4/24
 */
public class IdentityReplaceRequest extends BaseObject {

    private static final long serialVersionUID = -6112621203846730498L;

    private IdentityReplaceParams params;

    private List<IdentityReplaceFlowDefinitionUserItem> records;

    /**
     * @return the params
     */
    public IdentityReplaceParams getParams() {
        return params;
    }

    /**
     * @param params 要设置的params
     */
    public void setParams(IdentityReplaceParams params) {
        this.params = params;
    }

    /**
     * @return the records
     */
    public List<IdentityReplaceFlowDefinitionUserItem> getRecords() {
        return records;
    }

    /**
     * @param records 要设置的records
     */
    public void setRecords(List<IdentityReplaceFlowDefinitionUserItem> records) {
        this.records = records;
    }

    /**
     *
     */
    public static class IdentityReplaceFlowDefinitionUserItem extends FlowDefinitionUserQueryItem {
        private static final long serialVersionUID = 3068390696170191581L;

        public static final Integer SUCCESS = 1;
        public static final Integer ERROR = 2;

        // 修改结果状态
        private Integer status;
        // 修改结果信息
        private String resultMsg;

        /**
         * @return the status
         */
        public Integer getStatus() {
            return status;
        }

        /**
         * @param status 要设置的status
         */
        public void setStatus(Integer status) {
            this.status = status;
        }

        /**
         * @return the resultMsg
         */
        public String getResultMsg() {
            return resultMsg;
        }

        /**
         * @param resultMsg 要设置的resultMsg
         */
        public void setResultMsg(String resultMsg) {
            this.resultMsg = resultMsg;
        }
    }

    /**
     *
     */
    public static class IdentityReplaceParams extends FlowDefinitionUserModifyParams {

        private static final long serialVersionUID = -7429459865000129292L;

        private Boolean expandSearch;

        private String flowRange;

        private String flowDefId;

        private Boolean includeHisVersion;

        private String orgRange;

        private String orgId;

        private String saveAs;

        /**
         * @return the expandSearch
         */
        public Boolean getExpandSearch() {
            return expandSearch;
        }

        /**
         * @param expandSearch 要设置的expandSearch
         */
        public void setExpandSearch(Boolean expandSearch) {
            this.expandSearch = expandSearch;
        }

        /**
         * @return the flowRange
         */
        public String getFlowRange() {
            return flowRange;
        }

        /**
         * @param flowRange 要设置的flowRange
         */
        public void setFlowRange(String flowRange) {
            this.flowRange = flowRange;
        }

        /**
         * @return the flowDefId
         */
        public String getFlowDefId() {
            return flowDefId;
        }

        /**
         * @param flowDefId 要设置的flowDefId
         */
        public void setFlowDefId(String flowDefId) {
            this.flowDefId = flowDefId;
        }

        /**
         * @return the includeHisVersion
         */
        public Boolean getIncludeHisVersion() {
            return includeHisVersion;
        }

        /**
         * @param includeHisVersion 要设置的includeHisVersion
         */
        public void setIncludeHisVersion(Boolean includeHisVersion) {
            this.includeHisVersion = includeHisVersion;
        }

        /**
         * @return the orgRange
         */
        public String getOrgRange() {
            return orgRange;
        }

        /**
         * @param orgRange 要设置的orgRange
         */
        public void setOrgRange(String orgRange) {
            this.orgRange = orgRange;
        }

        /**
         * @return the orgId
         */
        public String getOrgId() {
            return orgId;
        }

        /**
         * @param orgId 要设置的orgId
         */
        public void setOrgId(String orgId) {
            this.orgId = orgId;
        }

        /**
         * @return the saveAs
         */
        public String getSaveAs() {
            return saveAs;
        }

        /**
         * @param saveAs 要设置的saveAs
         */
        public void setSaveAs(String saveAs) {
            this.saveAs = saveAs;
        }
        
    }

}
