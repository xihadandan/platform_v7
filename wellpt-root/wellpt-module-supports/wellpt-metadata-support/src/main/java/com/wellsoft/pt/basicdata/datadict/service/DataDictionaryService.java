/*
 * @(#)2012-11-15 V1.0
 *
 * Copyright 2012 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.datadict.service;

import com.wellsoft.context.component.tree.TreeNode;
import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.context.util.groovy.GroovyUseable;
import com.wellsoft.pt.basicdata.datadict.bean.DataDictionaryBean;
import com.wellsoft.pt.basicdata.datadict.bean.DataDictionaryDto;
import com.wellsoft.pt.basicdata.datadict.entity.DataDictionary;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

/**
 * Description: 数据字典服务层接口
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本		修改人		修改日期			修改内容
 * 2012-11-15.1	zhulh		2012-11-15		Create
 * </pre>
 * @date 2012-11-15
 */
@GroovyUseable
public interface DataDictionaryService {

    public static final Comparator<DataDictionary> DictAscComparator = new Comparator<DataDictionary>() {
        @Override
        public int compare(DataDictionary o1, DataDictionary o2) {
            if (StringUtils.isBlank(o1.getCode()) || StringUtils.isBlank(o2.getCode())) {
                return 0;
            }
            return o2.getCode().compareTo(o1.getCode());
        }
    };

    public static final Comparator<DataDictionary> DictDescComparator = new Comparator<DataDictionary>() {
        @Override
        public int compare(DataDictionary o1, DataDictionary o2) {
            if (StringUtils.isBlank(o1.getCode()) || StringUtils.isBlank(o2.getCode())) {
                return 0;
            }
            return o1.getCode().compareTo(o2.getCode());
        }
    };

    public static final String ACL_SID = "ROLE_DATA_DICT";

    /**
     * 通过UUID获取数据字典
     *
     * @param string
     * @return
     */
    DataDictionary get(String uid);

    /**
     * 保存编码，若uuid不存在则创建，若存在则新增，返回保存/更新后的实体。使用acl接口存放字典的所有者
     *
     * @param dataDictionary
     * @return
     */
    // DataDictionary saveAcl(DataDictionary dataDictionary);

    /**
     * 根据主键uuid删除指定数据字典，若字典存在子结点则删除
     *
     * @param uuid
     */
    void removeByPk(String uuid);

    /**
     * 根据所有主键uuids删除指定数据字典，若字典存在子结点则删除
     *
     * @param uuids
     */
    void removeAllByPk(Collection<String> uuids);

    /**
     * 异步加载树形结点，维护时不需要考虑ACL权限
     *
     * @param id
     * @return
     */
    List<TreeNode> getAsTreeAsync(String id);

    /**
     * 一次性加载整棵树
     *
     * @param uuid
     * @return
     */
    TreeNode getAllAsTree(String uuid);

    TreeNode getAllDataDicAsTree(String uuid);

    /**
     * 异步加载树形结点，维护时不需要考虑ACL权限(checkAll)
     *
     * @param id
     * @return
     */
    List<TreeNode> getAsTreeAsyncByCheckAll(String id);

    /**
     * 异步加载树形结点，维护时不需要考虑ACL权限(视图专用)
     *
     * @param id
     * @return
     */
    List<TreeNode> getAsTreeAsyncForView(String id);

    /**
     * 从指定类型开始异步加载树形结点，维护时不需要考虑ACL权限
     *
     * @param id
     * @return
     */
    List<TreeNode> getFromTypeAsTreeAsync(String uuid, String type);

    List<TreeNode> getViewTypeAsTreeAsync(String uuid, String type);

    /**
     * 根据UUID获取数据字典
     *
     * @param uuid
     * @return
     */
    DataDictionaryBean getBean(String uuid);

    /**
     * 保存数据字典
     *
     * @param bean 数据字典VO类
     */
    String saveBean(DataDictionaryBean bean);

    /**
     * 删除数据字典
     *
     * @param uuid 数据字典UUID
     */
    void remove(String uuid);

    /**
     * 返回指定ID的字典编码
     *
     * @param id
     * @return
     */
    DataDictionary getByType(String type);

    /**
     * 如何描述该方法
     *
     * @param type
     * @return
     */
    List<DataDictionary> getDataDictionariesByType(String type);

    /**
     * 获取type的数据字典列表
     *
     * @param type 必填
     * @param code 非空时，获取type下code的数据字典列表
     * @return
     */
    List<DataDictionary> getDataDictionariesByTypeCode(String type, String code);

    /**
     * 用于视图-查询条件-单选框-数据字典 add by wujx 20160713
     *
     * @param type
     * @return
     */
    Map<String, Object> getDataDictionariesByType4View(String type);

    /**
     * @param type
     * @return
     */
    QueryItem getKeyValuePair(String sourceCode, String targetCode);

    /**
     * 根据字典类型返回指定下子结点的指定字典编码的数据字典列表
     *
     * @param type
     * @param code
     * @return
     */
    List<DataDictionary> getDataDictionaries(String type, String code);

    /**
     * 根据字典类型返回指定下子结点的指定字典编码的节点名称（树形下拉框配置为取自字典隐藏值配置方法）
     *
     * @param type
     * @param code
     * @return
     */
    Map<String, Object> getDataDictionaryName(String type, String code);

    /**
     * radio,checkbox,select控件使用的字典树，不同点是code返回的是type
     *
     * @param id
     * @return
     */
    List<TreeNode> getAsTreeAsyncForControl(String id);

    /**
     * 职能字典使用，不同点是data返回的是字典UUID
     *
     * @param type
     * @return
     */
    List<TreeNode> getAsTreeAsyncForUuid(String uuid, String type);

    /**
     * 如何描述该方法
     *
     * @param parentType
     * @param code
     * @return
     */
    DataDictionary getByParentTypeAndCode(String parentType, String code);

    List<DataDictionary> getDataDictionariesByParentUuid(String uuid);

    /**
     * 根据数据字典的uuid取得全路径
     *
     * @param type
     * @return
     */
    String getCnAbsolutePath(String uuid, int level);

    /**
     * 判断指类型的数据字典下是否存在指定代码的字典
     *
     * @param code
     * @param type
     * @return
     */
    boolean isExistsByCodeWithInType(String code, String type);

    /**
     * 获取指类型的数据字典下代码的字典
     *
     * @param code
     * @param type
     * @return
     */
    DataDictionary getByCodeWithInType(String code, String type);

    void save(DataDictionary dataDictionary);

    List<DataDictionary> getAll();

    List<QueryItem> query(String qUERY_FOR_CATEGORYPRIVILEGE_TREE, HashMap<String, Object> hashMap,
                          Class<QueryItem> class1);

    List<DataDictionary> findBy(String string, String uuid);

    /**
     * 根据数据字典类型获取字典的key-value字符串
     *
     * @param type
     * @return
     */
    String getDataDictionaryJsonKvByType(String type);

    List<DataDictionary> queryModuleDataDics(Map<String, Object> params);

    void updateSeqByUuids(List<String> uuids);

    public abstract List<TreeNode> getTreeNodeByType(String type, Integer maxLevel);


    /**
     * 移动数据字典节点到其他节点之前
     *
     * @param uuid
     * @param afterUuid 如果该节点为空，则表示是把节点移到第一个
     */
    void moveDataDicAfterOther(String uuid, String afterUuid);

    DataDictionaryDto quickAddDataDic(String name, String afterUuid, String parentUuid);

    void quickDeleteDataDic(String uuid);

    List<DataDictionary> getByNameLikes(String name);

    int countChildren(String uuid);
}
