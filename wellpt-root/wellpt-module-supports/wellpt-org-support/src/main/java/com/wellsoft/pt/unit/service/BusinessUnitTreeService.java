package com.wellsoft.pt.unit.service;

import com.wellsoft.context.component.tree.TreeNode;
import com.wellsoft.context.jdbc.support.QueryInfo;
import com.wellsoft.pt.org.entity.User;
import com.wellsoft.pt.unit.bean.BusinessManage;
import com.wellsoft.pt.unit.bean.BusinessTypeBean;
import com.wellsoft.pt.unit.bean.BusinessUnitTreeBean;
import com.wellsoft.pt.unit.entity.BusinessUnitTree;
import com.wellsoft.pt.unit.entity.CommonUnit;

import java.util.List;

/**
 * Description: BusinessUnitTreeService.java
 *
 * @author liuzq
 * @date 2013-11-5
 */
public interface BusinessUnitTreeService {
    public static final String BUSINESS_OWNER = "BUSINESS_OWNER";
    public static final String BUSINESS_RECIPIENT = "BUSINESS_RECIPIENT";
    public static final String BUSINESS_SENDER = "BUSINESS_SENDER";

    /**
     * 租户库查询业务类别
     *
     * @param queryInfo
     * @return
     */
    List<BusinessTypeBean> query(QueryInfo queryInfo);

    /**
     * 根据业务单位树根节点获取业务单位通讯录
     *
     * @param nodeUuid
     * @return
     */
    List<com.wellsoft.context.component.tree.TreeNode> getAsTree(String nodeUuid);

    /**
     * 保存或更新业务单位通讯录节点信息
     *
     * @param bean
     */
    void saveBean(BusinessUnitTreeBean bean);

    /**
     * 根据业务类别获取业务类别信息
     *
     * @param uuid
     * @return
     */
    BusinessTypeBean getBusinessTypeBean(String uuid);

    /**
     * 删除业务单位通讯录节点
     *
     * @param uuid
     */
    void remove(String uuid);

    /**
     * 根据业务单位通讯录节点UUID获取节点信息
     *
     * @param businessUnitTreeUuid
     * @return
     */
    BusinessUnitTreeBean getBean(String uuid);

    /**
     * 将用户ID保存到业务单位里
     *
     * @param businessUnitTreeUuid
     * @param userId
     * @param type
     */
    void saveUserIdToBusinessUnitTree(BusinessUnitTreeBean bean);

    /**
     * 根据业务类别ID和业务单位ID获取执行人员列表
     *
     * @param businessTypeId
     * @param commonUnitId
     * @param type           1表示获取发送人，2表示获取接收人
     * @return
     */
    List<User> getUserList(String businessTypeId, String commonUnitId, String type);

    /**
     * 如何描述该方法
     *
     * @param businessTypeId
     * @param commonUnitId
     * @param type           1表示获取发送人，2表示获取接收人
     * @return
     */
    List<String> getBusinessUnitUserIds(String businessTypeId, String commonUnitId, int type);

    /**
     * 根据业务类别ID和业务单位ID获取指定业务角色的人员ID
     *
     * @param businessTypeId
     * @param commonUnitId
     * @param bizRole
     * @return
     */
    List<String> getBusinessUnitUserIds(String businessTypeId, String unitId, String bizRole);

    /**
     * 根据业务类别ID和业务单位UUID获取业务分支树
     *
     * @param businessTypeId
     * @param commonUnitId
     * @return
     */
    List<TreeNode> getAsTreeBranch(String businessTypeId, String commonUnitId);

    /**
     * 根据业务类别ID和业务单位UUID获取业务单位
     *
     * @param businessTypeId
     * @param unitId
     * @return
     */
    List<BusinessUnitTree> getBusinessUnitManagerById(String businessTypeId, String unitId);

    /**
     * @param businessTypeId
     * @param unitId
     * @param userId
     * @return
     */
    BusinessManage getBusinessManage(String businessTypeId, String unitId, String userId);

    /**
     * @param businessTypeId
     * @param unitId
     * @param userId
     * @return
     */
    List<BusinessManage> getBusinessManage(String businessTypeId, String userId);

    /**
     * 根据业务类型ID及用户ID获取用户所代码的单位列表
     *
     * @param businessTypeId
     * @param userId
     * @return
     */
    List<CommonUnit> getCommonUnitsByBusinessTypeIdAndUserId(String businessTypeId, String userId);

    /**
     * 根据业务类型及单位ID获取类型类型树的所有子结点
     *
     * @param businessType
     * @param commonUnitId
     * @return
     */
    List<BusinessUnitTree> getLeafBusinessUnitTrees(String businessType, String commonUnitId);

    /**
     * 根据UUID获取业务类型单位树的单位所在的全路径
     *
     * @param uuid
     * @return
     */
    String getFullPath(String uuid);

    /**
     * 获取指定业务负责人
     *
     * @param businessTypeId
     * @return
     */
    List<BusinessUnitTree> getBusinessUnitManagerById(String businessTypeId);

}
