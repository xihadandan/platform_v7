package com.wellsoft.pt.dm.controller.request;

import com.wellsoft.pt.dm.enums.AclPer;

import java.io.Serializable;
import java.util.List;

/**
 * Description: 如何描述该类
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2023年06月12日   Qiong	 Create
 * </pre>
 */
public class AclDataRequest implements Serializable {
    private String dataModelId;
    List<AclData> dataList;

    public String getDataModelId() {
        return dataModelId;
    }

    public void setDataModelId(String dataModelId) {
        this.dataModelId = dataModelId;
    }

    public List<AclData> getDataList() {
        return dataList;
    }

    public void setDataList(List<AclData> dataList) {
        this.dataList = dataList;
    }

    public static class AclData implements Serializable {
        private Long dataUuid;
        private List<String> sids;
        private List<AclPer> aclPerList;
        private Boolean delete = false;

        public Long getDataUuid() {
            return dataUuid;
        }

        public void setDataUuid(Long dataUuid) {
            this.dataUuid = dataUuid;
        }

        public List<AclPer> getAclPerList() {
            return aclPerList;
        }

        public void setAclPerList(List<AclPer> aclPerList) {
            this.aclPerList = aclPerList;
        }

        public List<String> getSids() {
            return sids;
        }

        public void setSids(List<String> sids) {
            this.sids = sids;
        }

        public Boolean getDelete() {
            return delete;
        }

        public void setDelete(Boolean delete) {
            this.delete = delete;
        }
    }


}
