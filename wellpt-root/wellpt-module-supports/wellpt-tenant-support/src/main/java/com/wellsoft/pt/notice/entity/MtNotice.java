/*
 * @(#)2015-5-25 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.notice.entity;

import com.wellsoft.context.jdbc.entity.CommonEntity;
import com.wellsoft.context.jdbc.entity.IdEntity;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Table;
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
 * 2015-5-25.1	zhulh		2015-5-25		Create
 * </pre>
 * @date 2015-5-25
 */
@Entity
@CommonEntity
@Table(name = "MT_NOTICE")
@DynamicUpdate
@DynamicInsert
public class MtNotice extends IdEntity {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = -3734769051558039912L;

    // 标题
    private String title;
    // 分类名称
    private String categoryName;
    // 分类编号
    private String categoryCode;
    // 责任者
    private String author;
    // 发布者ID
    private String publisher;
    // 发布者名称
    private String publisherName;
    // 发布时间
    private Date publishTime;
    // 发布对象ID
    private String reader;
    // 发布对象名称
    private String readerName;
    // 发布的数据UUID
    private String dataUuid;
    // 置顶状态
    private String topState;
    // 标题颜色
    private String titleColor;
    // 保留时间
    private Date retainTime;
    // 租户ID
    private String tenantId;

    /**
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * @param title 要设置的title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * @return the categoryName
     */
    public String getCategoryName() {
        return categoryName;
    }

    /**
     * @param categoryName 要设置的categoryName
     */
    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    /**
     * @return the categoryCode
     */
    public String getCategoryCode() {
        return categoryCode;
    }

    /**
     * @param categoryCode 要设置的categoryCode
     */
    public void setCategoryCode(String categoryCode) {
        this.categoryCode = categoryCode;
    }

    /**
     * @return the author
     */
    public String getAuthor() {
        return author;
    }

    /**
     * @param author 要设置的author
     */
    public void setAuthor(String author) {
        this.author = author;
    }

    /**
     * @return the publisher
     */
    public String getPublisher() {
        return publisher;
    }

    /**
     * @param publisher 要设置的publisher
     */
    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    /**
     * @return the publisherName
     */
    public String getPublisherName() {
        return publisherName;
    }

    /**
     * @param publisherName 要设置的publisherName
     */
    public void setPublisherName(String publisherName) {
        this.publisherName = publisherName;
    }

    /**
     * @return the publishTime
     */
    public Date getPublishTime() {
        return publishTime;
    }

    /**
     * @param publishTime 要设置的publishTime
     */
    public void setPublishTime(Date publishTime) {
        this.publishTime = publishTime;
    }

    /**
     * @return the reader
     */
    public String getReader() {
        return reader;
    }

    /**
     * @param reader 要设置的reader
     */
    public void setReader(String reader) {
        this.reader = reader;
    }

    /**
     * @return the readerName
     */
    public String getReaderName() {
        return readerName;
    }

    /**
     * @param readerName 要设置的readerName
     */
    public void setReaderName(String readerName) {
        this.readerName = readerName;
    }

    /**
     * @return the dataUuid
     */
    public String getDataUuid() {
        return dataUuid;
    }

    /**
     * @param dataUuid 要设置的dataUuid
     */
    public void setDataUuid(String dataUuid) {
        this.dataUuid = dataUuid;
    }

    /**
     * @return the topState
     */
    public String getTopState() {
        return topState;
    }

    /**
     * @param topState 要设置的topState
     */
    public void setTopState(String topState) {
        this.topState = topState;
    }

    /**
     * @return the titleColor
     */
    public String getTitleColor() {
        return titleColor;
    }

    /**
     * @param titleColor 要设置的titleColor
     */
    public void setTitleColor(String titleColor) {
        this.titleColor = titleColor;
    }

    /**
     * @return the retainTime
     */
    public Date getRetainTime() {
        return retainTime;
    }

    /**
     * @param retainTime 要设置的retainTime
     */
    public void setRetainTime(Date retainTime) {
        this.retainTime = retainTime;
    }

    /**
     * @return the tenantId
     */
    public String getTenantId() {
        return tenantId;
    }

    /**
     * @param tenantId 要设置的tenantId
     */
    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

}
