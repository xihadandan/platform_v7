package com.wellsoft.pt.org.dto;

import java.io.Serializable;
import java.util.List;

/**
 * Description: 如何描述该类
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2024年12月03日   chenq	 Create
 * </pre>
 */
public class BizOrgUserDto implements Serializable {

    private Long bizOrgUuid;
    private Long orgUuid;
    private String bizUserId; // 业务组织用户ID
    private String userId;
    private String userPath; // 用户路径：挂载在组织元素下，形成的唯一路径
    private String bizOrgElementId; //  组织元素
    private String elementType;//  组织元素类型

    private List<BizOrgRoleDto> bizOrgRoles;

    public Long getBizOrgUuid() {
        return bizOrgUuid;
    }

    public void setBizOrgUuid(Long bizOrgUuid) {
        this.bizOrgUuid = bizOrgUuid;
    }

    public Long getOrgUuid() {
        return orgUuid;
    }

    public void setOrgUuid(Long orgUuid) {
        this.orgUuid = orgUuid;
    }

    public String getBizUserId() {
        return bizUserId;
    }

    public void setBizUserId(String bizUserId) {
        this.bizUserId = bizUserId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserPath() {
        return userPath;
    }

    public void setUserPath(String userPath) {
        this.userPath = userPath;
    }

    public String getBizOrgElementId() {
        return bizOrgElementId;
    }

    public void setBizOrgElementId(String bizOrgElementId) {
        this.bizOrgElementId = bizOrgElementId;
    }

    public String getElementType() {
        return elementType;
    }

    public void setElementType(String elementType) {
        this.elementType = elementType;
    }

    public List<BizOrgRoleDto> getBizOrgRoles() {
        return bizOrgRoles;
    }

    public void setBizOrgRoles(List<BizOrgRoleDto> bizOrgRoles) {
        this.bizOrgRoles = bizOrgRoles;
    }
}
