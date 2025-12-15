/*
 * @(#)2013-3-22 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.dyview.entity;

import com.wellsoft.context.annotation.UnCloneable;
import com.wellsoft.context.jdbc.entity.IdEntity;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;

/**
 * Description: 分页定义类
 *
 * @author wubin
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-3-22.1	wubin		2013-3-22		Create
 * </pre>
 * @date 2013-3-22
 */
@Entity
@Table(name = "dyview_page_definition")
@DynamicUpdate
@DynamicInsert
public class PageDefinition extends IdEntity {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = -5516099203718554480L;

    private Boolean isPaging; //是否分页

    private Integer pageNum; //每页的行数

    @UnCloneable
    private ViewDefinition viewDefinition; // 所属的视图

    /**
     * @return the isPaging
     */
    public Boolean getIsPaging() {
        return isPaging;
    }

    /**
     * @param isPaging 要设置的isPaging
     */
    public void setIsPaging(Boolean isPaging) {
        this.isPaging = isPaging;
    }

    /**
     * @return the pageNum
     */
    public Integer getPageNum() {
        return pageNum;
    }

    /**
     * @param pageNum 要设置的pageNum
     */
    public void setPageNum(Integer pageNum) {
        this.pageNum = pageNum;
    }

    /**
     * @return the viewDefinition
     */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "view_def_uuid", nullable = false)
    public ViewDefinition getViewDefinition() {
        return viewDefinition;
    }

    /**
     * @param viewDefinition 要设置的viewDefinition
     */
    public void setViewDefinition(ViewDefinition viewDefinition) {
        this.viewDefinition = viewDefinition;
    }

}
