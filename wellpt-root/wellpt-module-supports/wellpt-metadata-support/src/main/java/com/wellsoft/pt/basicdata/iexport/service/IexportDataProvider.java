/*
 * @(#)2015-6-16 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.iexport.service;

import com.wellsoft.context.component.tree.TreeNode;
import com.wellsoft.context.jdbc.entity.JpaEntity;
import com.wellsoft.context.service.BaseService;
import com.wellsoft.pt.app.entity.AppFunction;
import com.wellsoft.pt.basicdata.iexport.acceptor.IexportData;
import com.wellsoft.pt.basicdata.iexport.suport.*;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2015-6-16.1	zhulh		2015-6-16		Create
 * </pre>
 * @date 2015-6-16
 */
public interface IexportDataProvider<T extends JpaEntity<UUID>, UUID extends Serializable> extends BaseService {

    String getType();

    IexportData getData(UUID uuid);

    void storeData(IexportData iexportData, boolean newVer) throws Exception;

    IexportMetaData getMetaData();

    /**
     * 获取treeName
     *
     * @param t
     * @return
     */
    String getTreeName(T t);

    /**
     * 批量获取 treeName
     *
     * @param list
     * @return
     */
    Map<UUID, String> getTreeNameMap(Collection<T> list);

    /**
     * 根据uuids查询
     *
     * @return
     */
    List<T> getList(Collection<Serializable> uuids);

    /**
     * 数据检查
     *
     * @param list
     * @return 引用定义 不存在当前系统数据库，或导入文件 的数据uuid
     */
    Set<UUID> dataCheck(Collection<T> list, Map<String, ProtoDataBean> beanMap);

    /**
     * 无实体类关联表数据处理
     * 如：AUDIT_ROLE_PRIVILEGE
     *
     * @return
     */
    JoinTableProcessor<T, UUID> getJoinTableProcessor();

    /**
     * 添加或保存数据
     *
     * @param map      key:uuid
     * @param oldUuids uuid集合
     * @param <P>      父级数据
     * @param <C>      子级数据
     */
    <P extends JpaEntity<UUID>, C extends JpaEntity<UUID>> BusinessProcessor saveOrUpdate(Map<String, ProtoDataBeanTree<T, P, C>> map, Collection<Serializable> oldUuids);

    /**
     * 获取 实体类包含的 fileIds
     *
     * @param t
     * @return
     */
    Set<String> getFileIds(T t);


    /**
     * 添加下一级的 查询参数
     *
     * @param t         当前对象
     * @param parentMap 当前对象作为父级 生成下一级父级key
     * @param hqlMap    key：下一级type  val:ProtoDataHql 查询语句参数对象
     */
    @Deprecated
    void putChildProtoDataHqlParams(T t, Map<String, T> parentMap, Map<String, ProtoDataHql> hqlMap);


    /**
     * 根据 ProtoDataHql 查询当前对象集合
     * 返回 父级类（非当前类）putChildProtoDataHqlParams 方法 生成的parentMap 关联的集合
     * 以确定 当前对象集合的 不同父级对象
     *
     * @param protoDataHql
     * @return
     */
    @Deprecated
    Map<String, List<T>> getParentMapList(ProtoDataHql protoDataHql);

    TreeNode exportAsTreeNode(Serializable uuid);

    IExportEntityStream exportEntityStream(String uuid);

    String entityJsonString(Serializable uuid);

    String entityJsonString(T entity);

    T saveEntityStream(IExportEntityStream stream);

    T entityFromJsonString(String json);

    T getEntity(Serializable uuid);

    public static enum CompareStatus {
        newData, conflict, duplicate
    }

    CompareStatus importEntityCompare(T entity);

    TreeNode exportAsTreeNodeByFunction(AppFunction function);
}
