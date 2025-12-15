/*
 * @(#)4/18/25 V1.0
 *
 * Copyright 2025 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.dingtalk.vo;

import com.dingtalk.api.response.OapiV2DepartmentListsubResponse;
import com.dingtalk.api.response.OapiV2UserListResponse;
import com.wellsoft.context.base.BaseObject;
import com.wellsoft.context.util.json.JsonUtils;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import java.util.Collection;
import java.util.Collections;
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
 * 4/18/25.1	    zhulh		4/18/25		    Create
 * </pre>
 * @date 4/18/25
 */
public class DingtalkDepartmentVo extends BaseObject {
    private OapiV2DepartmentListsubResponse.DeptBaseResponse department;

    private List<OapiV2UserListResponse.ListUserResponse> users;

    private List<DingtalkDepartmentVo> children;

    /**
     * @return the department
     */
    public OapiV2DepartmentListsubResponse.DeptBaseResponse getDepartment() {
        return department;
    }

    /**
     * @param department 要设置的department
     */
    public void setDepartment(OapiV2DepartmentListsubResponse.DeptBaseResponse department) {
        this.department = department;
    }

    /**
     * @return the users
     */
    public List<OapiV2UserListResponse.ListUserResponse> getUsers() {
        return users;
    }

    /**
     * @param users 要设置的users
     */
    public void setUsers(List<OapiV2UserListResponse.ListUserResponse> users) {
        this.users = users;
    }

    /**
     * @return the children
     */
    public List<DingtalkDepartmentVo> getChildren() {
        return children;
    }

    /**
     * @param children 要设置的children
     */
    public void setChildren(List<DingtalkDepartmentVo> children) {
        this.children = children;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Extension extends BaseObject {
        private String i18nFieldValues;

        /**
         * @return the i18nFieldValues
         */
        public String getI18nFieldValues() {
            return i18nFieldValues;
        }

        /**
         * @param i18nFieldValues 要设置的i18nFieldValues
         */
        public void setI18nFieldValues(String i18nFieldValues) {
            this.i18nFieldValues = i18nFieldValues;
        }

        public Collection<I18nFieldValue> toI18ns() {
            Collection<I18nFieldValue> list = JsonUtils.toCollection(i18nFieldValues, I18nFieldValue.class);
            return list == null ? Collections.emptyList() : list;
        }

    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class I18nFieldValue extends BaseObject {
        private String name;

        private Map<String, String> i18nValues;

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
         * @return the i18nValues
         */
        public Map<String, String> getI18nValues() {
            return i18nValues;
        }

        /**
         * @param i18nValues 要设置的i18nValues
         */
        public void setI18nValues(Map<String, String> i18nValues) {
            this.i18nValues = i18nValues;
        }
    }

    public static class DeptDirectorLeaderInfo extends BaseObject {
        private Long deptId;
        private Long orgElementUuid;

        /**
         * @return the deptId
         */
        public Long getDeptId() {
            return deptId;
        }

        /**
         * @param deptId 要设置的deptId
         */
        public void setDeptId(Long deptId) {
            this.deptId = deptId;
        }

        /**
         * @return the orgElementUuid
         */
        public Long getOrgElementUuid() {
            return orgElementUuid;
        }

        /**
         * @param orgElementUuid 要设置的orgElementUuid
         */
        public void setOrgElementUuid(Long orgElementUuid) {
            this.orgElementUuid = orgElementUuid;
        }
    }
}
