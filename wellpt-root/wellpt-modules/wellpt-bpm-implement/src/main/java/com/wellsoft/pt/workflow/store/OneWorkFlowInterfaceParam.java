package com.wellsoft.pt.workflow.store;

import com.wellsoft.pt.basicdata.datastore.support.DataStoreInterfaceField;
import com.wellsoft.pt.basicdata.datastore.support.DataStoreInterfaceFieldElement;
import com.wellsoft.pt.jpa.criteria.InterfaceParam;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

/**
 * Description:
 *
 * @author chenq
 * @date 2019/10/23
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2019/10/23    chenq		2019/10/23		Create
 * </pre>
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class OneWorkFlowInterfaceParam implements InterfaceParam {

    @DataStoreInterfaceField(name = "流程", domType = DataStoreInterfaceFieldElement.SELECT, service = "flowDefinitionService.loadSelectDataWorkflowDefinition")
    private String flowId;

    @DataStoreInterfaceField(name = "左连接数据模型", domType = DataStoreInterfaceFieldElement.CUSTOM, defaultValue = "wf-datastore-left-join-config")
    private WorkFlowLeftJoinConfig leftJoinConfig;

    public String getFlowId() {
        return flowId;
    }

    public void setFlowId(String flowId) {
        this.flowId = flowId;
    }

    /**
     * @return the leftJoinConfig
     */
    public WorkFlowLeftJoinConfig getLeftJoinConfig() {
        return leftJoinConfig;
    }

    /**
     * @param leftJoinConfig 要设置的leftJoinConfig
     */
    public void setLeftJoinConfig(WorkFlowLeftJoinConfig leftJoinConfig) {
        this.leftJoinConfig = leftJoinConfig;
    }
}
