package com.wellsoft.pt.multi.org.support.dataprovider;

import org.apache.commons.collections4.map.CaseInsensitiveMap;
import org.apache.commons.lang3.BooleanUtils;

import java.io.Serializable;
import java.util.List;

/**
 * Description: 如何描述该类
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2022年12月12日   chenq	 Create
 * </pre>
 */
public interface OrgSelectProvider {

    /**
     * 组织类型
     *
     * @return
     */
    String type();

    /**
     * 拉取数据
     *
     * @param async 同步或异步
     * @return
     */
    List<Node> fetch(Params params);

    List<Node> fetchByKeys(Params params);

    PageNode fetchUser(Params params);

    List<Node> fetchByTitles(Params params);


    public static class Node implements Serializable {
        private static final long serialVersionUID = -5384414851572578579L;
        private String key;
        private String keyPath; // 路径key：记录顶级节点key到当前key的路径
        private String titlePath; // 标题路径: 记录顶级节点到当前标题的路径
        private String parentKey;
        private String title;
        private String shortTitle;// 短名称
        private String type;// 类型
        private String typeName;
        private String iconClass; // 图标类
        private Object data;
        private String version; //版本号
        private Boolean isLeaf = false;// 是否叶子节点
        private Boolean checkable = true;// 是否可选
        private Boolean halfChecked = false;
        private Boolean disabled = false;//禁用
        private List<Node> children;
        private Boolean fictional = false; // 虚构节点标识: 虚构节点不参与路径等key的获取与计算
        private Boolean asyncPreviewUser = false;

        public String getKey() {
            return this.key;
        }

        public void setKey(final String key) {
            this.key = key;
        }

        public String getTitle() {
            return this.title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public List<Node> getChildren() {
            return this.children;
        }

        public void setChildren(List<Node> children) {
            this.children = children;
        }

        public Boolean getIsLeaf() {
            return this.isLeaf;
        }

        public void setIsLeaf(Boolean leaf) {
            this.isLeaf = leaf;
        }

        public Object getData() {
            return this.data;
        }

        public void setData(final Object data) {
            this.data = data;
        }

        public String getParentKey() {
            return this.parentKey;
        }

        public void setParentKey(final String parentKey) {
            this.parentKey = parentKey;
        }

        public String getType() {
            return this.type;
        }

        public void setType(final String type) {
            this.type = type;
        }

        public Boolean getCheckable() {
            return this.checkable;
        }

        public void setCheckable(final Boolean checkable) {
            this.checkable = checkable;
        }

        public Boolean getDisabled() {
            return this.disabled;
        }

        public void setDisabled(final Boolean disabled) {
            this.disabled = disabled;
        }

        public String getKeyPath() {
            return this.keyPath;
        }

        public void setKeyPath(final String keyPath) {
            this.keyPath = keyPath;
        }

        public String getTitlePath() {
            return this.titlePath;
        }

        public void setTitlePath(final String titlePath) {
            this.titlePath = titlePath;
        }

        public String getShortTitle() {
            return this.shortTitle;
        }

        public void setShortTitle(final String shortTitle) {
            this.shortTitle = shortTitle;
        }

        public String getVersion() {
            return this.version;
        }

        public void setVersion(final String version) {
            this.version = version;
        }

        public Boolean getHalfChecked() {
            return halfChecked;
        }

        public void setHalfChecked(Boolean halfChecked) {
            this.halfChecked = halfChecked;
        }

        public Boolean getAsyncPreviewUser() {
            return asyncPreviewUser;
        }

        public void setAsyncPreviewUser(Boolean asyncPreviewUser) {
            this.asyncPreviewUser = asyncPreviewUser;
        }

        public String getIconClass() {
            return iconClass;
        }

        public void setIconClass(String iconClass) {
            this.iconClass = iconClass;
        }

        public String getTypeName() {
            return typeName;
        }

        public void setTypeName(String typeName) {
            this.typeName = typeName;
        }

        public Boolean getFictional() {
            return fictional;
        }

        public void setFictional(Boolean fictional) {
            this.fictional = fictional;
        }
    }

    public static class PageNode implements Serializable {
        private static final long serialVersionUID = -761937273022793453L;
        private List<Node> nodes;
        private long total = 0l;

        public PageNode() {
        }

        public PageNode(List<Node> nodes, long total) {
            this.nodes = nodes;
            this.total = total;
        }

        public List<Node> getNodes() {
            return nodes;
        }

        public void setNodes(List<Node> nodes) {
            this.nodes = nodes;
        }

        public long getTotal() {
            return total;
        }

        public void setTotal(long total) {
            this.total = total;
        }
    }

    public static class Params extends CaseInsensitiveMap<String, Object> {


        private static final long serialVersionUID = -6587696363619854269L;

        public boolean async() {
            return this.get("async") != null ? BooleanUtils.toBoolean(this.get("async").toString()) : false;
        }

        public String getParentKey() {
            return this.get("parentKey") != null ? this.get("parentKey").toString() : null;
        }


        public String getKeyword() {
            return this.get("keyword") != null ? this.get("keyword").toString() : null;
        }

        public String getOrgVersionId() {
            return this.get("orgVersionId") != null ? this.get("orgVersionId").toString() : null;
        }


        public Long getOrgUuid() {
            return this.get("orgUuid") != null ? Long.parseLong(this.get("orgUuid").toString()) : null;
        }


        public String getOrgElementId() {
            return this.get("orgElementId") != null ? this.get("orgElementId").toString() : null;
        }


        public List<String> getKeys() {
            return this.get("keys") != null ? (List<String>) this.get("keys") : null;
        }

        public List<String> getIncludeKeys() {
            return this.get("includeKeys") != null ? (List<String>) this.get("includeKeys") : null;
        }

        public List<String> getExcludeKeys() {
            return this.get("excludeKeys") != null ? (List<String>) this.get("excludeKeys") : null;
        }

        public List<String> getTitles() {
            return this.get("titles") != null ? (List<String>) this.get("titles") : null;
        }

        public List<String> getCheckedKeys() {
            return this.get("checkedKeys") != null ? (List<String>) this.get("checkedKeys") : null;
        }

        public Integer getPageIndex() {
            return this.optInt("pageIndex");
        }

        public Integer getPageSize() {
            return this.optInt("pageSize");
        }

        public Integer optInt(String key) {
            if (this.containsKey(key) && this.get(key) != null) {
                return Integer.parseInt(this.get(key).toString());
            }
            return null;
        }

        public String optString(String key) {
            if (this.containsKey(key) && this.get(key) != null) {
                return this.get(key).toString();
            }
            return null;
        }


    }


}
