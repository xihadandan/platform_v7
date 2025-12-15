/*
 * @(#)2013-10-12 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.log;

import java.util.Date;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-10-12.1	zhulh		2013-10-12		Create
 * </pre>
 * @date 2013-10-12
 */
public interface OperationLog {

    public static final String ADD = "新增";
    public static final String MODIFY = "修改";
    public static final String DELETE = "删除";

    /**
     * 获取模块ID
     *
     * @return
     */
    String getModuleId();

    /**
     * 设置模块ID
     *
     * @param moduleId
     */
    void setModuleId(String moduleId);

    /**
     * 获取模块名称
     *
     * @return
     */
    String getModuleName();

    /**
     * 设置模块名称
     *
     * @param moduleName
     */
    void setModuleName(String moduleName);

    /**
     * 获取内容类型
     *
     * @return the content
     */
    String getContent();

    /**
     * 设置内容类型
     *
     * @param content
     */
    void setContent(String content);

    /**
     * 获取操作类型(创建、编辑、删除、提交、登录、注销等)
     *
     * @return
     */
    String getOperation();

    /**
     * 设置操作类型(创建、编辑、删除、提交、登录、注销等)
     *
     * @param operation
     */
    void setOperation(String operation);

    /**
     * 获取操作明细
     *
     * @return
     */
    String getDetails();

    /**
     * 设置操作明细
     *
     * @param details
     */
    void setDetails(String details);

    /**
     * 获取操作人ID(用户ID)
     *
     * @return
     */
    String getCreator();

    /**
     * 获取操作人ID(用户ID)
     *
     * @param creator
     */
    void setCreator(String creator);

    /**
     * 获取操作人名称(用户名)
     *
     * @return
     */
    String getUserName();

    /**
     * 设置操作人名称(用户名)
     *
     * @param userName
     */
    void setUserName(String userName);

    /**
     * 获取操作时间
     *
     * @return
     */
    Date getCreateTime();

    /**
     * 设置操作时间
     *
     * @param createTime
     */
    void setCreateTime(Date createTime);
}
