/*
 * @(#)2013-1-31 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.context.jdbc.entity;

import io.swagger.annotations.ApiModelProperty;

import javax.persistence.Transient;
import java.io.Serializable;

/**
 * Description: 实体基类接口
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-1-31.1	zhulh		2013-1-31		Create
 * </pre>
 * @date 2013-1-31
 */
@SuppressWarnings("serial")
@Deprecated
public abstract class BaseEntity extends JpaEntity<String> implements Serializable {


    // 附件属性
    @ApiModelProperty("附件属性")
    private String attach;

    /**
     * 获取attach  非持久化属性.
     *
     * @return
     */
    //
    @Transient
    public String getAttach() {
        return attach;
    }

    /**
     * @param attach 要设置的attach
     */
    public void setAttach(String attach) {
        this.attach = attach;
    }

}
