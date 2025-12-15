/*
 * @(#)6/13/25 V1.0
 *
 * Copyright 2025 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.fulltext.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Description:
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本		修改人		修改日期			修改内容
 * 6/13/25.1	    zhulh		6/13/25		    Create
 * </pre>
 * @date 6/13/25
 */
@Entity
@Table(name = "FULLTEXT_SEARCH_WORD_TERM")
@DynamicUpdate
@DynamicInsert
@ApiModel("全文检索词条目")
public class FulltextSearchWordTermEntity extends com.wellsoft.context.jdbc.entity.Entity {

    @ApiModelProperty("全文检索词UUID")
    private Long searchWordUuid;

    @ApiModelProperty("条目")
    private String term;

    @ApiModelProperty("排序号")
    private Integer sortOrder;

    /**
     * @return the searchWordUuid
     */
    public Long getSearchWordUuid() {
        return searchWordUuid;
    }

    /**
     * @param searchWordUuid 要设置的searchWordUuid
     */
    public void setSearchWordUuid(Long searchWordUuid) {
        this.searchWordUuid = searchWordUuid;
    }

    /**
     * @return the term
     */
    public String getTerm() {
        return term;
    }

    /**
     * @param term 要设置的term
     */
    public void setTerm(String term) {
        this.term = term;
    }

    /**
     * @return the sortOrder
     */
    public Integer getSortOrder() {
        return sortOrder;
    }

    /**
     * @param sortOrder 要设置的sortOrder
     */
    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }
}
