package com.wellsoft.pt.unit.service;

import com.wellsoft.context.component.tree.TreeNode;
import com.wellsoft.pt.unit.bean.CommonUnitTreeBean;
import com.wellsoft.pt.unit.entity.CommonUnit;
import org.dom4j.Document;

import java.util.List;

/**
 * Description: CommonUnitTreeService.java
 *
 * @author liuzq
 * @date 2013-11-5
 */
public interface CommonUnitTreeService {
    /**
     * 获取单位通讯录
     *
     * @return
     */
    List<TreeNode> getAsTree(String uuid);

    List<TreeNode> getAsTreeList(String uuid);

    /**
     * 删除单位通讯录单位节点
     *
     * @param uuid 节点
     */
    void remove(String uuid);

    /**
     * 保存或更新单位通讯录节点
     *
     * @param bean
     */
    void saveBean(CommonUnitTreeBean bean);

    /**
     * 根据单位通讯录UUID获取单位节点信息
     *
     * @param unitTreeUuid
     * @return
     */
    CommonUnitTreeBean getBean(String uuid);

    /**
     * 获取单位通讯录树或者集团通讯录树
     *
     * @param unitUuid
     * @param selectType
     * @param excludeTenantId
     * @return
     */
    Document parseCommonUnitTree(String unitUuid, String commonType, String excludeTenantId);

    /**
     * 验证单位ID的唯一性
     *
     * @param uuid
     * @param unitId
     * @return
     */
    boolean validateId(String uuid, String unitId);

    Document leafUnit(String unitId, String optionType, String leafType);

    /**
     * 根据单位ID及业务类型获取下一级子单位
     *
     * @param parentUnitId
     * @param businessTypeId
     * @return
     */
    List<CommonUnit> getCommonUnitsByParentIdAndBusinessTypeId(String parentUnitId, String businessTypeId);

    /**
     * 获取指定业务的所有单位
     *
     * @param businessTypeId
     * @return
     */
    List<CommonUnit> getCommonUnitsByBusinessTypeId(String businessTypeId);

}
