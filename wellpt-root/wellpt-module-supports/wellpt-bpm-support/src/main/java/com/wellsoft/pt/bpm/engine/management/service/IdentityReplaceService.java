package com.wellsoft.pt.bpm.engine.management.service;

import com.wellsoft.context.component.jqgrid.JqGridQueryInfo;
import com.wellsoft.context.jdbc.support.PagingInfo;
import com.wellsoft.context.jdbc.support.QueryData;
import com.wellsoft.context.jdbc.support.QueryInfo;
import com.wellsoft.pt.bpm.engine.element.FlowElement;
import com.wellsoft.pt.bpm.engine.entity.FlowSchema;
import com.wellsoft.pt.bpm.engine.management.entity.FlowSchemaLog;
import com.wellsoft.pt.bpm.engine.management.support.IdentityReplaceRequest;
import com.wellsoft.pt.log.entity.UserOperationLog;

import java.util.List;
import java.util.Map;

/**
 * 流程参与人员批量修改
 * Service层接口
 *
 * @author linz
 */
public interface IdentityReplaceService {

    /**
     * 获取xml的数据
     *
     * @return
     * @throws Exception
     * @author linz
     */
    public List<String> getXml() throws Exception;

    /**
     * 解析XML根据查询条件查询对应的流程
     *
     * @param jqGridQueryInfo
     * @param queryInfo
     * @param name
     * @param station
     * @param flowGroup
     * @param department
     * @param flowName
     * @return
     * @author linz
     */
    public QueryData getForPageAsTree(JqGridQueryInfo jqGridQueryInfo, QueryInfo queryInfo, String name,
                                      String station, String flowGroup, String department, String flowName, String webContent);

    /**
     * 解析XML根据查询条件查询对应的流程节点
     *
     * @param jqGridQueryInfo
     * @param queryInfo
     * @param uuid
     * @return
     */
    public QueryData getForPageAsTreeDetail(JqGridQueryInfo jqGridQueryInfo, QueryInfo queryInfo, String uuid);

    /**
     * @param uuids                流程ID
     * @param taskIds              节点ID
     * @param isUpdatecreator      true:修改发起人 false:不修改发起人
     * @param isUpdatepropertyUser true:修改参与人 false:不修改参与人
     * @param isUpdatemonitor      true:修改督办人 false:不修改督办人
     * @param isUpdateadmin        true:修改监控者 false:不修改监控者
     * @param isUpdateviewer       true:修改阅读者 false:不修改阅读者
     * @param isUpdatetaskUser     true:修改办理人 false:不修改办理人
     * @param isUpdatecopyUser     true:修改抄送人 false:不修改抄送人
     * @param isInsert             true:找不到直接替换  false:不替换抛出异常
     * @param oldUser              旧人员/组织/群组
     * @param newUser              新人员/组织/群组
     * @return
     * @author linz
     */
    public String saveBatchDateByGrid(String uuids, String taskIds, Boolean isUpdatecreator,
                                      Boolean isUpdatepropertyUser, Boolean isUpdatemonitor, Boolean isUpdateadmin, Boolean isUpdateviewer,
                                      Boolean isUpdatetaskUser, Boolean isUpdatecopyUser, Boolean isInsert, String oldUser, String newUser,
                                      String oldUserId, String newUserId);

    /**
     * 根据ID集合查询flowSchema并解析出flowElement集合
     *
     * @param uuids
     * @return
     */
    public List<FlowElement> loadFlowElementByIds(String uuids);

    /**
     * 根据页面查询条件获取flowElement对象，用于数据导出
     *
     * @param nameSearch
     * @param stationSearch
     * @param flowGroupSearch
     * @param departmentSearch
     * @param flowNameSearch
     * @return
     */
    public List<FlowElement> queryFlow(String selectRowIds, String nameSearch, String stationSearch,
                                       String flowGroupSearch, String departmentSearch, String flowNameSearch);

    /**
     * 查看修改日记
     *
     * @param jqGridQueryInfo
     * @param queryInfo
     * @param uuid
     * @return
     */
    public QueryData getUpdateContentLog(JqGridQueryInfo jqGridQueryInfo, QueryInfo queryInfo, String uuid,
                                         String contextPath);

    /**
     * 根据uuid获取FlowSchema对象
     *
     * @param uuid
     * @return
     */
    public FlowSchema getFlowSchemaByUuid(String uuid);

    /**
     * 获取日记信息
     *
     * @param uuid
     * @return
     */
    public FlowSchemaLog getFlowSchemaLogByUuid(String uuid);

    public Map<String, String> compareXml(String flowSchemaLogUuid);

    Map<String, String> getLogDiff(String flowSchemaLogUuid);

    /**
     * 办理人修改
     *
     * @param replaceRequest
     */
    void modify(IdentityReplaceRequest replaceRequest);

    /**
     * @param keyword
     * @param pagingInfo
     * @param orderBy
     * @return
     */
    QueryData listLogs(String keyword, PagingInfo pagingInfo, String orderBy);

    /**
     * @param logUuid
     * @return
     */
    UserOperationLog getLogByUuid(String logUuid);
}
