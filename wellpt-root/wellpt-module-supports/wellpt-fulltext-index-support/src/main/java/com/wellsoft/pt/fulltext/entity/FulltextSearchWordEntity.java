/*
 * @(#)6/13/25 V1.0
 *
 * Copyright 2025 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.fulltext.entity;

import com.wellsoft.context.jdbc.entity.SysEntity;
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
@Table(name = "FULLTEXT_SEARCH_WORD")
@DynamicUpdate
@DynamicInsert
@ApiModel("全文检索词")
public class FulltextSearchWordEntity extends SysEntity {

    @ApiModelProperty("用户ID")
    private String userId;

    @ApiModelProperty("关键词")
    private String keyword;

    @ApiModelProperty("查询次数")
    private Long searchCount;

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
     * @return the keyword
     */
    public String getKeyword() {
        return keyword;
    }

    /**
     * @param keyword 要设置的keyword
     */
    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    /**
     * @return the searchCount
     */
    public Long getSearchCount() {
        return searchCount;
    }

    /**
     * @param searchCount 要设置的searchCount
     */
    public void setSearchCount(Long searchCount) {
        this.searchCount = searchCount;
    }
}
