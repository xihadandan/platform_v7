/*
 * @(#)2017年11月22日 V1.0
 *
 * Copyright 2017 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.multi.org.facade.service;

import com.wellsoft.context.component.jqgrid.JqGridQueryInfo;
import com.wellsoft.context.component.select2.Select2QueryData;
import com.wellsoft.context.component.select2.Select2QueryInfo;
import com.wellsoft.context.jdbc.support.QueryData;
import com.wellsoft.context.jdbc.support.QueryInfo;
import com.wellsoft.context.service.BaseService;
import com.wellsoft.pt.multi.org.bean.OrgTreeNode;
import com.wellsoft.pt.multi.org.bean.OrgVersionVo;
import com.wellsoft.pt.multi.org.entity.MultiOrgVersion;

import java.util.List;

/**
 * Description: 如何描述该类
 *
 * @author zyguo
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2017年11月22日.1	zyguo		2017年11月22日		Create
 * </pre>
 * @date 2017年11月22日
 */
public interface MultiOrgVersionFacade extends BaseService {
    /**
     * 添加一个新版本组织
     *
     * @param bean
     * @return
     */
    public OrgVersionVo addMultiOrgVersion(OrgVersionVo vo);

    /**
     * 修改组织版本基本信息
     *
     * @param bean
     * @return
     */
    public OrgVersionVo modifyOrgVersionBaseInfo(OrgVersionVo vo);

    /**
     * 获取一个组织版本信息
     *
     * @param orgVersionUuid
     * @return
     */
    public OrgVersionVo getOrgVersionVo(String orgVersionUuid);

    /**
     * 升级新版本
     *
     * @param sourceVersionUuid
     * @param isSync            是否同步名字
     * @return
     */
    public MultiOrgVersion addNewOrgVersionForUpgrade(String sourceVersionUuid, boolean isSyncName);

    /**
     * 启用组织版本
     *
     * @param orgVersionUuid
     * @return
     */
    public boolean activeOrgVersion(String orgVersionUuid);

    /**
     * 禁用组织版本
     *
     * @param orgVersionUuid
     * @return
     */
    public boolean unactiveOrgVersion(String orgVersionUuid);

    /**
     * jqgrid分页树
     *
     * @param jqGridQueryInfo
     * @param queryInfo
     * @return
     */
    QueryData getForPageAsTree(JqGridQueryInfo jqGridQueryInfo, QueryInfo queryInfo);

    /**
     * 获取所有的组织版本的下拉框数据
     *
     * @param select2QueryInfo
     * @return
     */
    Select2QueryData loadSelectData(Select2QueryInfo select2QueryInfo);

    /**
     * 获取一个组织版本信息
     *
     * @param orgVersionId
     * @return
     */
    public MultiOrgVersion getOrgVersionById(String orgVersionId);

    /**
     * 获取所有可用的组织版本
     *
     * @return
     */
    public List<MultiOrgVersion> getAllActiveOrgVersions();

    /**
     * 获取所有单位的当前启动版本
     *
     * @return
     */
    public List<MultiOrgVersion> queryCurrentActiveVersionListOfAllUnit();

    /**
     * 获取指定单位的当前启动版本
     *
     * @param unitId
     * @return
     */
    List<MultiOrgVersion> queryCurrentActiveVersionListOfSystemUnit(String unitId);

    /**
     * 获取所有单位的版本树
     *
     * @param isAllVersion 是否所有版本，  true : 所有版本， false：当前生效版本
     * @return
     */
    public List<OrgTreeNode> queryVersionTreeOfAllSystemUnit(boolean isAllVersion);

    /**
     * 获取指定系统单位的版本树
     *
     * @param unitId
     * @param isAllVersion 是否所有版本，  true : 所有版本， false：当前生效版本
     * @return
     */
    public OrgTreeNode queryVersionTreeOfUnit(String unitId, boolean isAllVersion);

    /**
     * 获取其他系统单位的版本树，扣除指定的单位和其所包含的子单位
     *
     * @param isAllVersion
     * @return
     */
    public List<OrgTreeNode> queryVersionTreeOfOtherSystemUnit(String unitId, boolean isAllVersion);

    /**
     * 获取指定系统单位的指定类型的当前使用版本, 如果有多个，则使用版本号最大的一个
     *
     * @param systemUnitId
     * @param functionType
     * @return
     */
    public MultiOrgVersion getCurrentActiveVersionListOfUnitAndRootVersionId(String systemUnitId, String functionType);

    /**
     * 变更组织版本对应的自身业务单位ID
     *
     * @param id
     * @param id2
     */
    public void updateSelfBussinessUnitId(String id, String id2);

    /**
     * 获取指定组织类型的当前启用版本
     *
     * @param unitId
     * @param functionType
     * @return
     */
    public String getCurrentActiveVersionByFunctionType(String unitId, String functionType);

    /**
     * 如何描述该方法
     *
     * @param systemUnitId
     * @param id
     * @param name
     */
    public void updateOrgFunctionTypeName(String systemUnitId, String id, String name);

    /**
     * 如何描述该方法
     *
     * @param id
     * @return
     */
    MultiOrgVersion getVersionById(String id);

    /**
     * 如何描述该方法
     *
     * @param orgVersionId
     * @return
     */
    public MultiOrgVersion getCurrentActiveVersionByOrgVersionId(String orgVersionId);

    /**
     * 获取我的子单位的的组织版本
     *
     * @param isAllVersion
     * @return
     */
    List<OrgTreeNode> queryVersionTreeOfMySubUnit(boolean isAllVersion);

    /**
     * 获取单位下默认组织版本
     *
     * @param systemUnitId
     * @return
     */
    public abstract MultiOrgVersion getDefaultVersionBySystemUnitId(String systemUnitId);

}
