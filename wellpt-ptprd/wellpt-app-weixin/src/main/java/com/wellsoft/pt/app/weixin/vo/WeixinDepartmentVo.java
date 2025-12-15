/*
 * @(#)5/21/25 V1.0
 *
 * Copyright 2025 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.weixin.vo;

import com.wellsoft.context.base.BaseObject;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

import java.util.List;
import java.util.Map;

/**
 * Description:
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本		修改人		修改日期			修改内容
 * 5/21/25.1	    zhulh		5/21/25		    Create
 * </pre>
 * @date 5/21/25
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class WeixinDepartmentVo extends BaseObject {
    private static final long serialVersionUID = -483256813491717026L;
    private Department department;

    private List<User> users;

    List<WeixinDepartmentVo> children;

    /**
     * @return the department
     */
    public Department getDepartment() {
        return department;
    }

    /**
     * @param department 要设置的department
     */
    public void setDepartment(Department department) {
        this.department = department;
    }

    /**
     * @return the users
     */
    public List<User> getUsers() {
        return users;
    }

    /**
     * @param users 要设置的users
     */
    public void setUsers(List<User> users) {
        this.users = users;
    }

    /**
     * @return the children
     */
    public List<WeixinDepartmentVo> getChildren() {
        return children;
    }

    /**
     * @param children 要设置的children
     */
    public void setChildren(List<WeixinDepartmentVo> children) {
        this.children = children;
    }

    /**
     *
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Department extends BaseObject {
        private static final long serialVersionUID = 3558224153828567390L;

        private Long id;

        private String name;

        @JsonProperty("parentid")
        private Long parentId;

        private Long order;

        @JsonProperty("department_leader")
        private List<String> departmentLeaders;

        /**
         * @return the id
         */
        public Long getId() {
            return id;
        }

        /**
         * @param id 要设置的id
         */
        public void setId(Long id) {
            this.id = id;
        }

        /**
         * @return the name
         */
        public String getName() {
            return name;
        }

        /**
         * @param name 要设置的name
         */
        public void setName(String name) {
            this.name = name;
        }

        /**
         * @return the parentId
         */
        public Long getParentId() {
            return parentId;
        }

        /**
         * @param parentId 要设置的parentId
         */
        public void setParentId(Long parentId) {
            this.parentId = parentId;
        }

        /**
         * @return the order
         */
        public Long getOrder() {
            return order;
        }

        /**
         * @param order 要设置的order
         */
        public void setOrder(Long order) {
            this.order = order;
        }

        /**
         * @return the departmentLeaders
         */
        public List<String> getDepartmentLeaders() {
            return departmentLeaders;
        }

        /**
         * @param departmentLeaders 要设置的departmentLeaders
         */
        public void setDepartmentLeaders(List<String> departmentLeaders) {
            this.departmentLeaders = departmentLeaders;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class User extends BaseObject {

        private static final long serialVersionUID = -3805644700858439495L;

        private String name;

        private List<Long> department;

        private String position;

        // 激活状态: 1=已激活，2=已禁用，4=未激活，5=退出企业。
        private Integer status;

        private Integer enable;

        private Integer isleader;

        @JsonProperty("extattr")
        private Map<String, Object> extAttr;

        @JsonProperty("hide_mobile")
        private Integer hideMobile;

        private String telephone;

        private List<Long> order;

        @JsonProperty("main_department")
        private Long mainDepartment;

        private String alias;

        @JsonProperty("is_leader_in_dept")
        private List<Integer> isLeaderInDept;

        @JsonProperty("userid")
        private String userId;

        @JsonProperty("direct_leader")
        private List<String> directLeader;

        @JsonProperty("external_profile")
        private Map<String, Object> externalProfile;

        /**
         * @return the name
         */
        public String getName() {
            return name;
        }

        /**
         * @param name 要设置的name
         */
        public void setName(String name) {
            this.name = name;
        }

        /**
         * @return the department
         */
        public List<Long> getDepartment() {
            return department;
        }

        /**
         * @param department 要设置的department
         */
        public void setDepartment(List<Long> department) {
            this.department = department;
        }

        /**
         * @return the position
         */
        public String getPosition() {
            return position;
        }

        /**
         * @param position 要设置的position
         */
        public void setPosition(String position) {
            this.position = position;
        }

        /**
         * @return the status
         */
        public Integer getStatus() {
            return status;
        }

        /**
         * @param status 要设置的status
         */
        public void setStatus(Integer status) {
            this.status = status;
        }

        /**
         * @return the enable
         */
        public Integer getEnable() {
            return enable;
        }

        /**
         * @param enable 要设置的enable
         */
        public void setEnable(Integer enable) {
            this.enable = enable;
        }

        /**
         * @return the isleader
         */
        public Integer getIsleader() {
            return isleader;
        }

        /**
         * @param isleader 要设置的isleader
         */
        public void setIsleader(Integer isleader) {
            this.isleader = isleader;
        }

        /**
         * @return the extAttr
         */
        public Map<String, Object> getExtAttr() {
            return extAttr;
        }

        /**
         * @param extAttr 要设置的extAttr
         */
        public void setExtAttr(Map<String, Object> extAttr) {
            this.extAttr = extAttr;
        }

        /**
         * @return the hideMobile
         */
        public Integer getHideMobile() {
            return hideMobile;
        }

        /**
         * @param hideMobile 要设置的hideMobile
         */
        public void setHideMobile(Integer hideMobile) {
            this.hideMobile = hideMobile;
        }

        /**
         * @return the telephone
         */
        public String getTelephone() {
            return telephone;
        }

        /**
         * @param telephone 要设置的telephone
         */
        public void setTelephone(String telephone) {
            this.telephone = telephone;
        }

        /**
         * @return the order
         */
        public List<Long> getOrder() {
            return order;
        }

        /**
         * @param order 要设置的order
         */
        public void setOrder(List<Long> order) {
            this.order = order;
        }

        /**
         * @return the mainDepartment
         */
        public Long getMainDepartment() {
            return mainDepartment;
        }

        /**
         * @param mainDepartment 要设置的mainDepartment
         */
        public void setMainDepartment(Long mainDepartment) {
            this.mainDepartment = mainDepartment;
        }

        /**
         * @return the alias
         */
        public String getAlias() {
            return alias;
        }

        /**
         * @param alias 要设置的alias
         */
        public void setAlias(String alias) {
            this.alias = alias;
        }

        /**
         * @return the isLeaderInDept
         */
        public List<Integer> getIsLeaderInDept() {
            return isLeaderInDept;
        }

        /**
         * @param isLeaderInDept 要设置的isLeaderInDept
         */
        public void setIsLeaderInDept(List<Integer> isLeaderInDept) {
            this.isLeaderInDept = isLeaderInDept;
        }

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
         * @return the directLeader
         */
        public List<String> getDirectLeader() {
            return directLeader;
        }

        /**
         * @param directLeader 要设置的directLeader
         */
        public void setDirectLeader(List<String> directLeader) {
            this.directLeader = directLeader;
        }

        /**
         * @return the externalProfile
         */
        public Map<String, Object> getExternalProfile() {
            return externalProfile;
        }

        /**
         * @param externalProfile 要设置的externalProfile
         */
        public void setExternalProfile(Map<String, Object> externalProfile) {
            this.externalProfile = externalProfile;
        }

    }

}
