package com.wellsoft.pt.security.core.userdetails;

import com.google.common.collect.Maps;
import com.wellsoft.pt.multi.org.bean.OrgTreeNodeDto;
import com.wellsoft.pt.multi.org.entity.MultiOrgSystemUnit;
import com.wellsoft.pt.user.dto.UserDetailsVo;
import com.wellsoft.pt.user.entity.UserAccountEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Description: 如何描述该类
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2023年02月03日   chenq	 Create
 * </pre>
 */
public class PlatformUserDetials extends User implements UserDetails {


    private UserDetailsVo userDetailsVo;

    private String tenant;

    private Map<String, Object> custom = Maps.newHashMap(); // 自定义信息

    private UserSystemOrgDetails userSystemOrgDetails = new UserSystemOrgDetails();

    public PlatformUserDetials(UserDetailsVo userDetailsVo, String tenant, Collection<? extends GrantedAuthority> authorities) {
        super(userDetailsVo.getLoginName(), userDetailsVo.getPassword(), true, true, true, true, authorities);
        this.userDetailsVo = userDetailsVo;
        this.tenant = tenant;
    }

    @Override
    public String getCode() {
        return this.userDetailsVo.getUserNo();
    }

    @Override
    public String getUserId() {
        return this.userDetailsVo.getUserId();
    }

    @Override
    public String getUserUuid() {
        return this.userDetailsVo.getUuid();
    }

    @Override
    public String getLoginName() {
        return this.userDetailsVo.getLoginName();
    }

    @Override
    public String getLoginNameLowerCase() {
        return this.userDetailsVo.getLoginName().toLowerCase();
    }

    @Override
    public String getLoginType() {
        throw new UnsupportedOperationException("unsupported");
    }

    @Override
    public String getUserName() {
        return this.userDetailsVo.getUserName();
    }

    @Override
    public String getMainJobName() {
        throw new UnsupportedOperationException("unsupported");
    }

    @Override
    public String getMainDepartmentId() {
        throw new UnsupportedOperationException("unsupported");
//        String mainJobIdPath = this.userDetailsVo.getMainJobIdPath();
//        if (StringUtils.isNotBlank(mainJobIdPath)) {
//            String[] parts = mainJobIdPath.split(Separator.SLASH.getValue());
//            for (int i = parts.length - 1; i >= 0; i--) {
//                if (parts[i].startsWith(IdPrefix.DEPARTMENT.getValue() + Separator.UNDERLINE.getValue())) {
//                    return parts[i];
//                }
//            }
//        }
//        return null;
    }

    @Override
    public String getMainDepartmentName() {
        throw new UnsupportedOperationException("unsupported");
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
        throw new UnsupportedOperationException("unsupported");
    }

    @Override
    public String getMainBusinessUnitName() {
        throw new UnsupportedOperationException("unsupported");
    }

    @Override
    public List<String> getOtherBusinessUniIds() {
        throw new UnsupportedOperationException("unsupported");
    }

    @Override
    public boolean isSuperAdmin() {
        return this.userDetailsVo.getUserType() == UserAccountEntity.Type.SUPER_ADMIN.ordinal();
    }

    @Override
    public boolean isAdmin() {
        return this.userDetailsVo.getUserType() == UserAccountEntity.Type.PT_ADMIN.ordinal()
                || this.userDetailsVo.getUserType() == UserAccountEntity.Type.TENANT_ADMIN.ordinal();
    }

    @Override
    public Object getExtraData(String key) {
        return this.custom.get(key);
    }

    @Override
    public void putExtraData(String key, Object value) {
        this.custom.put(key, value);
    }

    @Override
    public boolean containsExtraData(String key) {
        return this.custom.containsKey(key);
    }

    @Override
    public List<OrgTreeNodeDto> getOtherJobs() {
        throw new UnsupportedOperationException("unsupported");
    }

    @Override
    public String getEmployeeNumber() {
        throw new UnsupportedOperationException("unsupported");
    }

    @Override
    public String getTokenId() {
        throw new UnsupportedOperationException("unsupported");
    }

    @Override
    public void setTokenId(String tokenId) {
        throw new UnsupportedOperationException("unsupported");
    }

    @Override
    public String getSystemUnitId() {
        throw new UnsupportedOperationException("unsupported");
    }

    @Override
    public String getPhotoUuid() {
        return this.userDetailsVo.getAvatar();
    }

    @Override
    public OrgTreeNodeDto getMainJob() {
        throw new UnsupportedOperationException("unsupported");
    }

    public String getMainJobId() {
        return this.userDetailsVo.getMainJobId();
    }

    public List<String> getOtherJobIds() {
//        return this.userDetailsVo.getOtherJobIds();
        throw new UnsupportedOperationException("unsupported");
    }

    @Override
    public MultiOrgSystemUnit getSystemUnit() {
        throw new UnsupportedOperationException("unsupported");
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
    public String getUserIp() {
        return null;
    }

    @Override
    public String getTenant() {
        return this.tenant;
    }

    @Override
    public String getTenantId() {
        throw new UnsupportedOperationException("unsupported");
    }


    @Override
    public String getPassword() {
        return this.userDetailsVo.getPassword();
    }

    @Override
    public String getUsername() {
        return this.userDetailsVo.getUserName();
    }

    @Override
    public boolean isAccountNonExpired() {
        return this.userDetailsVo.isAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return this.userDetailsVo.isAccountNonLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return this.userDetailsVo.isCredentialsNonExpired();
    }

    @Override
    public boolean isEnabled() {
        return this.userDetailsVo.isEnabled();
    }
}
