package com.wellsoft.oauth2.web.controller;

import com.google.common.collect.Lists;
import com.wellsoft.oauth2.utils.JsonMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The Class DataTable.
 */
public class DataTable {
    public final static String PAGE_JSON_TOTAL = "total";
    public final static String PAGE_JSON_DATA = "items";
    public final static String TOTAL_PAGE = "totalPage";
    public final static String CURRENT_PAGE = "currentPage";
    /**
     * The json mapper.
     */
    private static JsonMapper jsonMapper = JsonMapper.nonEmptyMapper();

    static {
        jsonMapper.getMapper().setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
    }

    /**
     * The page index.
     */
    private int pageIndex = 0;
    /**
     * The page size.
     */
    private int pageSize = 20;
    /**
     * The sort field.
     */
    private String sortField;
    /**
     * The sort order.
     */
    private String sortOrder;

    public DataTable() {
        super();
    }

    public DataTable(int pageIndex, int pageSize, String sortField, Sort.Direction sortOrder) {
        super();
        this.pageIndex = pageIndex;
        this.pageSize = pageSize;
        this.sortField = sortField;
        this.sortOrder = sortOrder.name();
    }

    public DataTable(int pageIndex, int pageSize, String sortField, String sortOrder) {
        super();
        this.pageIndex = pageIndex;
        this.pageSize = pageSize;
        this.sortField = sortField;
        this.sortOrder = sortOrder;
    }

    public String toJson(Page<?> page) {
        return jsonMapper.toJson(new PageJsonInfo(page));
    }

    public String toJson(Page<?> page, ParamCallbak<PageJsonInfo> paramCallbak) {
        PageJsonInfo pageJsonInfo = new PageJsonInfo(page);
        paramCallbak.call(pageJsonInfo, jsonMapper);
        String json = jsonMapper.toJson(pageJsonInfo);
        return paramCallbak.after(json, jsonMapper);
    }

    public String toJson(List<?> data) {
        return jsonMapper.toJson(data);
    }

    public PageRequest toPageRequest() {
        PageRequest pageRequest = null;
        if (StringUtils.isNotBlank(getSortField()) && StringUtils.isNotBlank(getSortOrder())) {
            Sort sort = new Sort(Sort.Direction.fromString(getSortOrder()),
                    StringUtils.replace(getSortField(), "_", "."));
            sort.and(new Sort(Sort.Direction.DESC, "createTime"));
            pageRequest = new PageRequest(getPageIndex(), getPageSize(), sort);
        } else {
            pageRequest = new PageRequest(getPageIndex(), getPageSize());
        }
        return pageRequest;
    }

    public int getPageIndex() {
        return pageIndex;
    }

    public DataTable setPageIndex(int pageIndex) {
        this.pageIndex = pageIndex;
        return this;
    }

    public int getPageSize() {
        return pageSize;
    }

    public DataTable setPageSize(int pageSize) {
        this.pageSize = pageSize;
        return this;
    }

    public String getSortField() {
        return sortField;
    }

    public DataTable setSortField(String sortField) {
        this.sortField = sortField;
        return this;
    }

    public String getSortOrder() {
        return sortOrder;
    }

    public DataTable setSortOrder(String sortOrder) {
        this.sortOrder = sortOrder;
        return this;
    }

    public interface ParamCallbak<T> {
        void call(T t, JsonMapper jsonMapper);

        String after(String json, JsonMapper jsonMapper);
    }

    public static class PageJsonInfo extends HashMap<String, Object> implements
            Map<String, Object> {

        private static final long serialVersionUID = -4149504465694712327L;

        public PageJsonInfo(Page<?> page) {
            super();
            put(PAGE_JSON_TOTAL, page.getTotalElements());
            List<?> content = page.getContent();
            put(PAGE_JSON_DATA, null == content ? Lists.newArrayList() : content);
            put(TOTAL_PAGE, page.getTotalPages());
            put(CURRENT_PAGE, page.getNumber() + 1);
        }

    }

    public static class DefaultParamCallbak<T> implements ParamCallbak<T> {

        @Override
        public void call(T t, JsonMapper jsonMapper) {

        }

        @Override
        public String after(String json, JsonMapper jsonMapper) {
            return null;
        }

    }

}
