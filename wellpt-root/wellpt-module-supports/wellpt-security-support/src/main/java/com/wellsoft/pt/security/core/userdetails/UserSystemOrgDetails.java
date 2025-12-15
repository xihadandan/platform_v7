package com.wellsoft.pt.security.core.userdetails;

import com.google.common.collect.Lists;
import com.wellsoft.context.enums.IdPrefix;
import com.wellsoft.context.enums.Separator;
import com.wellsoft.pt.multi.org.bean.OrgTreeNodeDto;
import com.wellsoft.pt.security.access.RequestSystemContextPathResolver;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Description: 如何描述该类
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2024年03月21日   chenq	 Create
 * </pre>
 */
public class UserSystemOrgDetails implements Serializable {


    private static final long serialVersionUID = -2525962462725807866L;

    public UserSystemOrgDetails() {
    }

    public UserSystemOrgDetails(List<OrgDetail> details) {
        this.details = details;
    }


    private List<OrgDetail> details = Lists.newArrayList();

    public List<OrgDetail> getDetails() {
        return details;
    }

    public void setDetails(List<OrgDetail> details) {
        this.details = details;
    }


    /**
     * 返回指定系统下的组织信息
     *
     * @param system
     * @return
     */
    public List<OrgDetail> get(String system) {
        if (StringUtils.isNotBlank(system)) {
            List<OrgDetail> underSysOrgDetails = Lists.newArrayList();
            for (OrgDetail detail : this.details) {
                if (system.equalsIgnoreCase(system)) {
                    underSysOrgDetails.add(detail);
                }
            }
            return underSysOrgDetails;
        }
        return Collections.emptyList();
    }

    public OrgDetail getDefault(String system) {
        if (StringUtils.isNotBlank(system)) {
            for (OrgDetail detail : this.details) {
                // 默认组织会按顺序排在第一位
                if (system.equalsIgnoreCase(system)) {
                    return detail;
                }
            }
        }
        return null;
    }

    /**
     * 返回当前系统的默认组织信息
     *
     * @return
     */
    public OrgDetail currentSystemOrgDetail() {
        OrgDetail detail = this.getDefault(RequestSystemContextPathResolver.system());
        if (detail != null) {
            return detail;
        }
        // 无返回系统组织信息，则返回第一个组织信息
        return this.details.isEmpty() ? null : this.details.get(0);
    }


    public List<String> currentOrAllSystemId() {
        if (CollectionUtils.isEmpty(this.details)) {
            return Collections.emptyList();
        }
        OrgDetail detail = this.getDefault(RequestSystemContextPathResolver.system());
        if (detail != null) {
            return Lists.newArrayList(detail.getSystem());
        }
        return this.details.stream().map(item -> item.getSystem()).collect(Collectors.toList());
    }

    public Set<String> allSystemIds() {
        if (CollectionUtils.isEmpty(this.details)) {
            return Collections.emptySet();
        }

        return this.details.stream().map(item -> item.getSystem()).collect(Collectors.toSet());
    }


    public static class OrgDetail implements Serializable {

        private String system;

        private OrgTreeNodeDto unit;

        private OrgTreeNodeDto mainJob;

        private OrgTreeNodeDto mainDept;

        private List<OrgTreeNodeDto> otherJobs = Lists.newArrayList();

        private List<OrgTreeNodeDto> otherDepts = Lists.newArrayList();

        private List<OrgTreeNodeDto> bizOrgRoles = Lists.newArrayList();

        private Boolean isDefault = false;


        public OrgDetail(OrgTreeNodeDto mainJob, List<OrgTreeNodeDto> otherJobs) {
            this.mainJob = mainJob;
            this.otherJobs = otherJobs;
        }

        public OrgDetail() {
        }

        public OrgTreeNodeDto getMainJob() {
            return mainJob;
        }

        public void setMainJob(OrgTreeNodeDto mainJob) {
            this.mainJob = mainJob;
            if (StringUtils.isNotBlank(mainJob.getEleIdPath())) {
                String[] ids = mainJob.getEleIdPath().split(Separator.SLASH.getValue());
                String[] names = mainJob.getEleNamePath().split(Separator.SLASH.getValue());
                ArrayUtils.reverse(ids);
                ArrayUtils.reverse(names);
                for (int i = 1; i < ids.length; i++) {
                    if (ids[i].startsWith(IdPrefix.DEPARTMENT.getValue() + Separator.UNDERLINE.getValue())) {
                        String[] sub = (String[]) ArrayUtils.subarray(names, i, names.length);
                        ArrayUtils.reverse(sub);
                        this.mainJob.setDeptNamePath(StringUtils.join(sub, Separator.SLASH.getValue()));

                        // 设置主部门信息
                        this.mainDept = new OrgTreeNodeDto();
                        String[] mergeIds = (String[]) ArrayUtils.subarray(ids, i, names.length);
                        ArrayUtils.reverse(mergeIds);
                        this.mainDept.setEleId(ids[i]);
                        this.mainDept.setEleIdPath(StringUtils.join(mergeIds, Separator.SLASH.getValue()));
                        this.mainDept.setName(names[i]);
                        this.mainDept.setEleNamePath(this.mainJob.getDeptNamePath());
                        this.mainDept.setOrgVersionId(this.mainJob.getOrgVersionId());
                        break;
                    }
                }
            }
        }

        public OrgTreeNodeDto getMainDept() {
            return mainDept;
        }

        public void setMainDept(OrgTreeNodeDto mainDept) {
            this.mainDept = mainDept;
        }

        public List<OrgTreeNodeDto> getOtherDepts() {
            return otherDepts;
        }

        public void setOtherDepts(List<OrgTreeNodeDto> otherDepts) {
            this.otherDepts = otherDepts;
        }

        public List<OrgTreeNodeDto> getOtherJobs() {
            return otherJobs;
        }

        public void setOtherJobs(List<OrgTreeNodeDto> otherJobs) {
            this.otherJobs = otherJobs;
        }

        public String getSystem() {
            return system;
        }

        public void setSystem(String system) {
            this.system = system;
        }

        public OrgTreeNodeDto getUnit() {
            return unit;
        }

        public void setUnit(OrgTreeNodeDto unit) {
            this.unit = unit;
        }

        public Boolean getIsDefault() {
            return isDefault;
        }

        public void setIsDefault(Boolean isDefault) {
            isDefault = isDefault;
        }

        public List<OrgTreeNodeDto> getBizOrgRoles() {
            return bizOrgRoles;
        }

        public void setBizOrgRoles(List<OrgTreeNodeDto> bizOrgRoles) {
            this.bizOrgRoles = bizOrgRoles;
        }

        public void addOtherJobs(OrgTreeNodeDto dto) {
            this.otherJobs.add(dto);
            if (StringUtils.isNotBlank(dto.getEleIdPath())) {
                String[] ids = dto.getEleIdPath().split(Separator.SLASH.getValue());
                String[] names = dto.getEleNamePath().split(Separator.SLASH.getValue());
                ArrayUtils.reverse(ids);
                ArrayUtils.reverse(names);
                for (int i = 1; i < ids.length; i++) {
                    if (ids[i].startsWith(IdPrefix.DEPARTMENT.getValue() + Separator.UNDERLINE.getValue())) {
                        String[] sub = (String[]) ArrayUtils.subarray(names, i, names.length);
                        ArrayUtils.reverse(sub);
                        dto.setDeptNamePath(StringUtils.join(sub, Separator.SLASH.getValue()));
                        OrgTreeNodeDto otherDept = new OrgTreeNodeDto();
                        String[] mergeIds = (String[]) ArrayUtils.subarray(ids, i, names.length);
                        ArrayUtils.reverse(mergeIds);
                        otherDept.setEleId(ids[i]);
                        otherDept.setEleIdPath(StringUtils.join(mergeIds, Separator.SLASH.getValue()));
                        otherDept.setName(names[i]);
                        otherDept.setEleNamePath(dto.getDeptNamePath());
                        otherDept.setOrgVersionId(dto.getOrgVersionId());
                        this.otherDepts.add(otherDept);
                        break;
                    }
                }
            }

        }
    }

}
