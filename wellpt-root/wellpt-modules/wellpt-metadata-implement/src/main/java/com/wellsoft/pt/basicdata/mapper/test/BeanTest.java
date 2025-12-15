/*
 * @(#)2017年10月12日 V1.0
 *
 * Copyright 2017 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.mapper.test;

import com.wellsoft.pt.dyform.facade.dto.DyFormData;

import java.util.Date;

/**
 * Description: 如何描述该类
 *
 * @author zhongzh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2017年10月12日.1	zhongzh		2017年10月12日		Create
 * </pre>
 * @date 2017年10月12日
 */
public class BeanTest {

    private String id;
    private String name;
    private Boolean bb;
    private Date bdate;
    private DyFormData dydata;

    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id 要设置的id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name 要设置的name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the bb
     */
    public Boolean getBb() {
        return bb;
    }

    /**
     * @param bb 要设置的bb
     */
    public void setBb(Boolean bb) {
        this.bb = bb;
    }

    /**
     * @return the bdate
     */
    public Date getBdate() {
        return bdate;
    }

    /**
     * @param bdate 要设置的bdate
     */
    public void setBdate(Date bdate) {
        this.bdate = bdate;
    }

    /**
     * @return the dydata
     */
    public DyFormData getDydata() {
        return dydata;
    }

    /**
     * @param dydata 要设置的dydata
     */
    public void setDydata(DyFormData dydata) {
        this.dydata = dydata;
    }
}
