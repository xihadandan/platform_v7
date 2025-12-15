package com.wellsoft.pt.security.core.userdetails;

import com.wellsoft.pt.multi.org.bean.OrgTreeNodeDto;
import com.wellsoft.pt.multi.org.entity.MultiOrgSystemUnit;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;
import java.util.List;

/**
 * Description: 如何描述该类
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2020年08月11日   chenq	 Create
 * </pre>
 */
public abstract class AbstractUserDetails extends User implements UserDetails {

    protected String loginType;

    private UserSystemOrgDetails userSystemOrgDetails = new UserSystemOrgDetails();

    public AbstractUserDetails(String username, String password, Collection<? extends GrantedAuthority> authorities) {
        super(username, password, authorities);
    }

    public AbstractUserDetails(String username, String password, boolean enabled, boolean accountNonExpired,
                               boolean credentialsNonExpired, boolean accountNonLocked,
                               Collection<? extends GrantedAuthority> authorities) {
        super(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
    }

    @Override
    public String getCode() {
        return null;
    }

    @Override
    public String getUserId() {
        return null;
    }

    @Override
    public String getUserUuid() {
        return null;
    }

    @Override
    public String getLoginName() {
        return null;
    }

    @Override
    public String getLoginNameLowerCase() {
        return null;
    }

    @Override
    public String getLoginType() {
        return this.loginType;
    }

    public void setLoginType(String loginType) {
        this.loginType = loginType;
    }

    @Override
    public String getUserName() {
        return null;
    }

    @Override
    public String getMainJobName() {
        return null;
    }

    @Override
    public String getMainDepartmentId() {
        return null;
    }

    @Override
    public String getMainDepartmentName() {
        return null;
    }

    @Override
    public String getMainDepartmentPath() {
        return null;
    }

    @Override
    public String getLocalMainDepartmentPath() {
        return null;
    }

    @Override
    public String getMainBusinessUnitId() {
        return null;
    }

    @Override
    public String getMainBusinessUnitName() {
        return null;
    }

    @Override
    public List<String> getOtherBusinessUniIds() {
        return null;
    }

    @Override
    public boolean isSuperAdmin() {
        return false;
    }

    @Override
    public boolean isAdmin() {
        return false;
    }

    @Override
    public Object getExtraData(String key) {
        return null;
    }

    @Override
    public void putExtraData(String key, Object value) {

    }

    @Override
    public boolean containsExtraData(String key) {
        return false;
    }

    @Override
    public List<OrgTreeNodeDto> getOtherJobs() {
        return null;
    }

    @Override
    public String getEmployeeNumber() {
        return null;
    }

    @Override
    public String getTokenId() {
        return null;
    }

    @Override
    public void setTokenId(String tokenId) {

    }

    @Override
    public String getSystemUnitId() {
        return null;
    }

    @Override
    public OrgTreeNodeDto getMainJob() {
        return null;
    }

    @Override
    public MultiOrgSystemUnit getSystemUnit() {
        return null;
    }

    @Override
    public String getTenant() {
        return null;
    }

    @Override
    public UserSystemOrgDetails getUserSystemOrgDetails() {
        return this.userSystemOrgDetails;
    }

    @Override
    public String getLocalUserName() {
        return null;
    }

    @Override
    public String getLocalMainDepartmentName() {
        return null;
    }

    @Override
    public String getTenantId() {
        return null;
    }

    @Override
    public String getPassword() {
        return super.getPassword();
    }

    @Override
    public String getUsername() {
        return super.getUsername();
    }

    @Override
    public String getPhotoUuid() {
        return null;
    }

    @Override
    public String getUserIp() {
        return null;
    }
}
