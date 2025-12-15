/*
 * @(#)2016年8月2日 V1.0
 *
 * Copyright 2016 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.function;

import java.util.Map;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2016年8月2日.1	zhulh		2016年8月2日		Create
 * </pre>
 * @date 2016年8月2日
 */
public class SimpleAppFunctionSource extends AbstractAppFunctionSource {

    private static final long serialVersionUID = -3624055405257521976L;
    // 功能信息是否可导出
    boolean exportable;
    // 功能信息ID是否可重复
    boolean repeatable;
    // UUID
    private String uuid;
    // 全名
    private String fullName;
    // 名称
    private String name;
    // ID
    private String id;
    // 编号
    private String code;
    // 动作
    private String action;
    // 数据
    private String data;
    // 分类
    private String category;
    // 功能导出类型
    private String exportType;
    //备注
    private String remark;
    // 其他信息
    private Map<String, Object> extras;

    /**
     * @param uuid
     * @param fullName
     * @param name
     * @param id
     * @param action
     * @param data
     * @param category
     * @param extras
     */
    public SimpleAppFunctionSource(String uuid, String fullName, String name, String id,
                                   String code, String action,
                                   String data, String category, boolean exportable,
                                   String exportType, boolean repeatable,
                                   Map<String, Object> extras) {
        super();
        this.uuid = uuid;
        this.fullName = fullName;
        this.name = name;
        this.id = id;
        this.code = code;
        this.action = action;
        this.data = data;
        this.category = category;
        this.exportable = exportable;
        this.exportType = exportType;
        this.repeatable = repeatable;
        this.extras = extras;
    }

    public SimpleAppFunctionSource(String uuid, String fullName, String name, String id,
                                   String code, String action,
                                   String data, String category, boolean exportable,
                                   String exportType, boolean repeatable,
                                   Map<String, Object> extras, String remark) {
        super();
        this.uuid = uuid;
        this.fullName = fullName;
        this.name = name;
        this.id = id;
        this.code = code;
        this.action = action;
        this.data = data;
        this.category = category;
        this.exportable = exportable;
        this.exportType = exportType;
        this.repeatable = repeatable;
        this.extras = extras;
        this.remark = remark;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.function.AppFunctionSource#getUuid()
     */
    @Override
    public String getUuid() {
        return this.uuid;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.function.AppFunctionSource#getFullName()
     */
    @Override
    public String getFullName() {
        return this.fullName;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.function.AppFunctionSource#getName()
     */
    @Override
    public String getName() {
        return this.name;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.function.AppFunctionSource#getId()
     */
    @Override
    public String getId() {
        return this.id;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.function.AppFunctionSource#getCode()
     */
    @Override
    public String getCode() {
        return this.code;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.function.AppFunctionSource#getAction()
     */
    @Override
    public String getAction() {
        return this.action;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.function.AppFunctionSource#getData()
     */
    @Override
    public String getData() {
        return this.data;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.function.AppFunctionSource#getCategory()
     */
    @Override
    public String getCategory() {
        return this.category;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.function.AppFunctionSource#exportable()
     */
    @Override
    public boolean exportable() {
        return this.exportable;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.function.AppFunctionSource#getExportType()
     */
    @Override
    public String getExportType() {
        return this.exportType;
    }

    /**
     * @return the repeatable
     */
    @Override
    public boolean repeatable() {
        return repeatable;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.function.AppFunctionSource#getExtras()
     */
    @Override
    public Map<String, Object> getExtras() {
        return this.extras;
    }

    @Override
    public String getRemark() {
        return remark;
    }


    /**
     * (non-Javadoc)
     *
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((uuid == null) ? 0 : uuid.hashCode());
        return result;
    }

    /**
     * (non-Javadoc)
     *
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        SimpleAppFunctionSource other = (SimpleAppFunctionSource) obj;
        if (uuid == null) {
            if (other.uuid != null)
                return false;
        } else if (!uuid.equals(other.uuid))
            return false;
        return true;
    }

}
