/*
 * @(#)2016年7月28日 V1.0
 *
 * Copyright 2016 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.org.support;

import com.wellsoft.context.jdbc.support.BaseItem;

/**
 * Description: 如何描述该类
 *
 * @author huanglc
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2016年7月28日.1	huanglc		2016年7月28日		Create
 * </pre>
 * @date 2016年7月28日
 */
public class UserGradeItem extends BaseItem {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = -1219636463200678014L;

    private String userId;

    private String grade;

    /**
     * @return the userId
     */
    public String getUserId() {
        return userId;
    }

    /**
     * @param userId 要设置的userId
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }

    /**
     * @return the grade
     */
    public String getGrade() {
        return grade;
    }

    /**
     * @param grade 要设置的grade
     */
    public void setGrade(String grade) {
        this.grade = grade;
    }

}
