/*
 * @(#)2013-1-11 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.context.component.jqgrid;

import java.util.ArrayList;
import java.util.List;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-1-11.1	zhulh		2013-1-11		Create
 * </pre>
 * @date 2013-1-11
 */
public class JqTreeGridNode {
    private String id;
    private List<Object> cell = new ArrayList<Object>();

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
     * @return the cell
     */
    public List<Object> getCell() {
        return cell;
    }

    /**
     * @param cell 要设置的cell
     */
    public void setCell(List<Object> cell) {
        this.cell = cell;
    }

}
