package com.wellsoft.pt.fulltext.request;

import com.wellsoft.context.jdbc.support.PagingInfo;
import com.wellsoft.pt.fulltext.enums.QryDateRangeEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.springframework.data.domain.Sort;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

/**
 * Description: 如何描述该类
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2020年09月08日   chenq	 Create
 * </pre>
 */
@ApiModel(value = "索引请求参数")
public class IndexRequestParams implements Serializable {

    private static final long serialVersionUID = 8605206498682372841L;

    @ApiModelProperty(value = "关键字")
    private String keyword;

    @ApiModelProperty(value = "分类编码，多个以分号隔开")
    private String categoryCode;

    @ApiModelProperty(value = "是否统计分类数量")
    private Boolean countCategory;

    @ApiModelProperty(value = "过滤条件")
    private Map<String, Object> filterMap;

    @ApiModelProperty(value = "日期范围字段")
    private String dateRangeField;

    @ApiModelProperty(value = "日期范围类型，ONE_DAY一天内, ONE_WEEK一周内, ONE_MONTH一月内, ONE_YEAR一年内, CUSTOM自定义")
    private QryDateRangeEnum dateRange;

    @ApiModelProperty(value = "日期范围自定义开始时间")
    private Date startTime;

    @ApiModelProperty(value = "日期范围自定义结束时间")
    private Date endTime;

    @ApiModelProperty(value = "结果字段映射", hidden = true)
    private Map<String, String> resultFieldMapping;

    @ApiModelProperty(value = "高亮颜色")
    private String highlightColor;

    @ApiModelProperty(value = "高亮片段的大小")
    private int fragmentSize = 150;

    @ApiModelProperty(value = "分页信息")
    private PagingInfo pagingInfo;

    @ApiModelProperty(value = "排序信息")
    private Order order;

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
     * @return the countCategory
     */
    public Boolean getCountCategory() {
        return countCategory;
    }

    /**
     * @param countCategory 要设置的countCategory
     */
    public void setCountCategory(Boolean countCategory) {
        this.countCategory = countCategory;
    }

    /**
     * @return the filterMap
     */
    public Map<String, Object> getFilterMap() {
        return filterMap;
    }

    /**
     * @param filterMap 要设置的filterMap
     */
    public void setFilterMap(Map<String, Object> filterMap) {
        this.filterMap = filterMap;
    }

    /**
     * @return the dateRangeField
     */
    public String getDateRangeField() {
        return dateRangeField;
    }

    /**
     * @param dateRangeField 要设置的dateRangeField
     */
    public void setDateRangeField(String dateRangeField) {
        this.dateRangeField = dateRangeField;
    }

    /**
     * @return the dateRange
     */
    public QryDateRangeEnum getDateRange() {
        return dateRange;
    }

    /**
     * @param dateRange 要设置的dateRange
     */
    public void setDateRange(QryDateRangeEnum dateRange) {
        this.dateRange = dateRange;
    }

    /**
     * @return the startTime
     */
    public Date getStartTime() {
        return startTime;
    }

    /**
     * @param startTime 要设置的startTime
     */
    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    /**
     * @return the endTime
     */
    public Date getEndTime() {
        return endTime;
    }

    /**
     * @param endTime 要设置的endTime
     */
    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    /**
     * @return the resultFieldMapping
     */
    @JsonIgnore
    public Map<String, String> getResultFieldMapping() {
        return resultFieldMapping;
    }

    /**
     * @param resultFieldMapping 要设置的resultFieldMapping
     */
    public void setResultFieldMapping(Map<String, String> resultFieldMapping) {
        this.resultFieldMapping = resultFieldMapping;
    }

    /**
     * @return the highlightColor
     */
    public String getHighlightColor() {
        return highlightColor;
    }

    /**
     * @param highlightColor 要设置的highlightColor
     */
    public void setHighlightColor(String highlightColor) {
        this.highlightColor = highlightColor;
    }

    /**
     * @return the fragmentSize
     */
    public int getFragmentSize() {
        return fragmentSize;
    }

    /**
     * @param fragmentSize 要设置的fragmentSize
     */
    public void setFragmentSize(int fragmentSize) {
        this.fragmentSize = fragmentSize;
    }

    /**
     * @return the pagingInfo
     */
    public PagingInfo getPagingInfo() {
        return pagingInfo;
    }

    /**
     * @param pagingInfo 要设置的pagingInfo
     */
    public void setPagingInfo(PagingInfo pagingInfo) {
        this.pagingInfo = pagingInfo;
    }

    /**
     * @return the order
     */
    public Order getOrder() {
        return order;
    }

    /**
     * @param order 要设置的order
     */
    public void setOrder(Order order) {
        this.order = order;
    }

    public static class Order implements Serializable {
        private Sort.Direction direction = Sort.Direction.DESC;
        private String property;

        public Order() {
        }

        public Order(Sort.Direction direction, String property) {
            this.direction = direction;
            this.property = property;
        }

        public Sort.Direction getDirection() {
            return direction;
        }

        public void setDirection(Sort.Direction direction) {
            this.direction = direction;
        }

        public String getProperty() {
            return property;
        }

        public void setProperty(String property) {
            this.property = property;
        }


    }
}
