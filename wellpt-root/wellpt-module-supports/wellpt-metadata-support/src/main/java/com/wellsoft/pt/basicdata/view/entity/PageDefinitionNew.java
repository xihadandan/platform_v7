/*
 * @(#)2013-3-22 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.view.entity;

import com.wellsoft.context.annotation.UnCloneable;
import com.wellsoft.context.jdbc.entity.IdEntity;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.LazyToOne;
import org.hibernate.annotations.LazyToOneOption;

import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

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
@Deprecated
//@Entity
//@Table(name = "view_page_definition")
//@DynamicUpdate
//@DynamicInsert
public class PageDefinitionNew extends IdEntity {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = -5516099203718554480L;

    private Boolean isPaging; //是否分页

    private Integer pageNum; //每页的行数

    private Boolean isEveryPage;//是否开启每页分页选项

    private Boolean isCurrentPage;//是否开启第几页选项

    private Boolean isOpenCommonButton;//是否开启公共按钮栏

    @UnCloneable
    private ViewDefinitionNew viewDefinitionNew; // 所属的视图

    /**
     * @return the isOpenCommonButton
     */
    public Boolean getIsOpenCommonButton() {
        return isOpenCommonButton;
    }

    /**
     * @param isOpenCommonButton 要设置的isOpenCommonButton
     */
    public void setIsOpenCommonButton(Boolean isOpenCommonButton) {
        this.isOpenCommonButton = isOpenCommonButton;
    }

    /**
     * @return the isEveryPage
     */
    public Boolean getIsEveryPage() {
        return isEveryPage;
    }

    /**
     * @param isEveryPage 要设置的isEveryPage
     */
    public void setIsEveryPage(Boolean isEveryPage) {
        this.isEveryPage = isEveryPage;
    }

    /**
     * @return the isCurrentPage
     */
    public Boolean getIsCurrentPage() {
        return isCurrentPage;
    }

    /**
     * @param isCurrentPage 要设置的isCurrentPage
     */
    public void setIsCurrentPage(Boolean isCurrentPage) {
        this.isCurrentPage = isCurrentPage;
    }

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
     * @return the viewDefinitionNew
     */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "view_def_uuid", nullable = false)
    @LazyToOne(LazyToOneOption.PROXY)
    @Fetch(FetchMode.SELECT)
    public ViewDefinitionNew getViewDefinitionNew() {
        return viewDefinitionNew;
    }

    /**
     * @param viewDefinitionNew 要设置的viewDefinitionNew
     */
    public void setViewDefinitionNew(ViewDefinitionNew viewDefinitionNew) {
        this.viewDefinitionNew = viewDefinitionNew;
    }

}
