package com.wellsoft.context.component.select2;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.wellsoft.context.jdbc.support.PagingInfo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.annotate.JsonIgnore;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * Description: Select2 Ajax 提交参数
 *
 * @author Xiem
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2016年1月29日.1	Xiem		2016年1月29日		Create
 * </pre>
 * @date 2016年1月29日
 */
@ApiModel("Select2 Ajax 提交参数")
public class Select2QueryInfo {
    @ApiModelProperty("服务")
    private String serviceName;
    @ApiModelProperty("服务方法")
    private String queryMethod;
    @ApiModelProperty("selectionMethod")
    private String selectionMethod;
    @ApiModelProperty("页码")
    private int pageNo;// 页码
    @ApiModelProperty("一页大小")
    private int pageSize;// 一页大小
    @ApiModelProperty("搜索值")
    private String searchValue;// 搜索值
    @JsonIgnore
    private HttpServletRequest request;
    private PagingInfo pageInfo;
    private Map<String, Object> params = Maps.newHashMap();
    private String paramsJSONString;

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public int getPageNo() {
        return pageNo;
    }

    public void setPageNo(int pageNo) {
        this.pageNo = pageNo;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public String getSearchValue() {
        return StringUtils.trimToEmpty(searchValue);
    }

    public void setSearchValue(String searchValue) {
        this.searchValue = searchValue;
    }

    /**
     * 获取分页信息
     *
     * @return
     */
    public PagingInfo getPagingInfo() {
        if (pageInfo == null) {
            pageInfo = new PagingInfo(pageNo, pageSize, false);
        }
        return pageInfo;
    }

    /**
     * 获取透传的参数
     *
     * @param paramsName 参数名
     * @return 参数值
     */
    public String getOtherParams(String paramName) {
        return request.getParameter(paramName);
    }

    /**
     * 获取透传参数如果参数不存在返回默认值
     *
     * @param paramsName   参数名
     * @param defalutValue 默认值
     * @return 参数值
     */
    public String getOtherParams(String paramsName, String defalutValue) {
        String value = request.getParameter(paramsName);
        return StringUtils.isBlank(value) ? defalutValue : value;
    }

    public HttpServletRequest getRequest() {
        return request;
    }

    public void setRequest(HttpServletRequest request) {
        this.request = request;
    }

    /**
     * 通过ID获取Text时，传选中的ID集合
     *
     * @return
     */
    public String[] getIds() {
        return this.request.getParameterValues("selectIds[]");
    }

    /**
     * @return the queryMethod
     */
    public String getQueryMethod() {
        return queryMethod;
    }

    /**
     * @param queryMethod 要设置的queryMethod
     */
    public void setQueryMethod(String queryMethod) {
        this.queryMethod = queryMethod;
    }

    /**
     * @return the selectionMethod
     */
    public String getSelectionMethod() {
        return selectionMethod;
    }

    /**
     * @param selectionMethod 要设置的selectionMethod
     */
    public void setSelectionMethod(String selectionMethod) {
        this.selectionMethod = selectionMethod;
    }

    /**
     * @return the params
     */
    public Map<String, Object> getParams() {
        if (MapUtils.isEmpty(params) && StringUtils.isNotBlank(paramsJSONString)) {
            this.params = new Gson().fromJson(paramsJSONString, Map.class);
            paramsJSONString = null;
        }
        return params;
    }

    /**
     * @param params 要设置的params
     */
    public void setParams(Map<String, Object> params) {
        this.params = params;
    }

    public String getParamsJSONString() {
        return paramsJSONString;
    }

    public void setParamsJSONString(String paramsJSONString) {
        this.paramsJSONString = paramsJSONString;
    }
}
