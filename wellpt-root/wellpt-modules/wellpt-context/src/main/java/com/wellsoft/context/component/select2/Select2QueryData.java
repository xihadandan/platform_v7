package com.wellsoft.context.component.select2;

import com.wellsoft.context.jdbc.support.PagingInfo;
import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.context.util.reflection.ReflectionUtils;
import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;

import java.util.*;

/**
 * Description: Select2 ajax查询返回数据
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
public class Select2QueryData {
    @ApiModelProperty("是否更多")
    private boolean more;
    @ApiModelProperty("分页数")
    private int pageNo;
    @ApiModelProperty("返回数据")
    private Set<Select2DataBean> results = new LinkedHashSet<Select2DataBean>();

    public Select2QueryData() {
    }

    public Select2QueryData(PagingInfo pagingInfo) {
        if (pagingInfo != null) {
            this.pageNo = pagingInfo.getCurrentPage();
            this.more = pagingInfo.getTotalPages() > pagingInfo.getCurrentPage();
        }
    }

    public Select2QueryData(Collection<Select2DataBean> results) {
        this.results.addAll(results);
    }

    public Select2QueryData(Collection<Select2DataBean> results, PagingInfo pagingInfo) {
        this(pagingInfo);
        this.results.addAll(results);
    }

    public Select2QueryData(Collection<?> itemList, String idProperty, String textProperty) {
        this(itemList, idProperty, textProperty, null);
    }

    /**
     * Map或者POJO对象
     *
     * @param itemList
     * @param idProperty
     * @param textProperty
     * @param pagingInfo
     */
    public Select2QueryData(Collection<?> itemList, String idProperty, String textProperty, PagingInfo pagingInfo) {
        this(pagingInfo);
        for (Object item : itemList) {
            Object idValue = "";
            Object textValue = "";
            if (item instanceof QueryItem) {
                idValue = ((QueryItem) item).get(QueryItem.getKey(idProperty));
                textValue = ((QueryItem) item).get(QueryItem.getKey(textProperty));
            } else if (item instanceof Map) {
                idValue = ((Map<?, ?>) item).get(idProperty);
                textValue = ((Map<?, ?>) item).get(textProperty);
            } else {
                idValue = ReflectionUtils.invokeGetterMethod(item, idProperty);
                textValue = ReflectionUtils.invokeGetterMethod(item, textProperty);
            }

            addResultData(new Select2DataBean(ObjectUtils.toString(idValue, StringUtils.EMPTY),
                    ObjectUtils.toString(textValue, StringUtils.EMPTY)));
        }
    }

    /**
     * 如何描述该构造方法
     *
     * @param itemList   数组或者List集合
     * @param idIndex
     * @param textIndex
     * @param pagingInfo
     */
    public Select2QueryData(Collection<?> itemList, int idIndex, int textIndex, PagingInfo pagingInfo) {
        this(pagingInfo);
        for (Object item : itemList) {
            Object idValue = "";
            Object textValue = "";
            if (item instanceof Object[]) {
                idValue = ((Object[]) item)[idIndex];
                textValue = ((Object[]) item)[textIndex];
            } else if (item instanceof List) {
                idValue = ((List<?>) item).get(idIndex);
                textValue = ((List<?>) item).get(textIndex);
            } else {
                throw new RuntimeException("不支持的数据类型");
            }
            addResultData(new Select2DataBean(ObjectUtils.toString(idValue, StringUtils.EMPTY),
                    ObjectUtils.toString(textValue, StringUtils.EMPTY)));
        }
    }

    public Select2QueryData(Map<String, String> items, PagingInfo pagingInfo) {
        this(pagingInfo);
        for (String key : items.keySet()) {
            addResultData(new Select2DataBean(key, items.get(key)));
        }
    }

    public Select2QueryData(Map<String, String> items) {
        this(items, null);
    }

    public Select2QueryData(Collection<?> itemList, int idIndex, int textIndex) {
        this(itemList, idIndex, textIndex, null);
    }

    public boolean isMore() {
        return more;
    }

    public void setMore(boolean more) {
        this.more = more;
    }

    public int getPageNo() {
        return pageNo;
    }

    public void setPageNo(int pageNo) {
        this.pageNo = pageNo;
    }

    public Collection<Select2DataBean> getResults() {
        return results;
    }

    public void setResults(Collection<Select2DataBean> results) {
        this.clear();
        this.addResults(results);
    }

    public void addResultData(Select2DataBean result) {
        this.results.add(result);
    }

    public void addResults(Collection<Select2DataBean> results) {
        this.results.addAll(results);
    }

    public void clear() {
        this.results.clear();
    }

}
