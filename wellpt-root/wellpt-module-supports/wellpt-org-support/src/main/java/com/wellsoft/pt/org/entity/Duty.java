package com.wellsoft.pt.org.entity;

import com.wellsoft.context.annotation.UnCloneable;
import com.wellsoft.context.jdbc.entity.IdEntity;
import com.wellsoft.context.validator.MaxLength;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
 * Description: 职务实体类
 *
 * @author zhengky
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	       修改人		修改日期	      修改内容
 * 2014-8-22.1  zhengky	2014-8-22	  Create
 * </pre>
 * @date 2014-8-22
 */

//@ApiModel("职务实体")
//@Entity
//@Table(name = "org_duty")
//@DynamicUpdate
//@DynamicInsert
@Deprecated
public class Duty extends IdEntity {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 9126913799558858346L;

    @ApiModelProperty("职务编码")
    @NotBlank
    private String code;

    @ApiModelProperty("职务名称")
    @NotBlank
    private String name;

    @ApiModelProperty("职级")
    @NotBlank
    private String dutyLevel;

    @ApiModelProperty("职位系列名称")
    private String seriesName;

    @ApiModelProperty("职位系列uuid")
    private String seriesUuid;

    @ApiModelProperty("备注")
    @MaxLength(max = Integer.MAX_VALUE)
    private String remark;

    @ApiModelProperty("系统外ID")
    private String externalId;

    @ApiModelProperty("职务id W开头")
    private String id;

    @ApiModelProperty("职级 排序使用")
    private Integer ilevel;

    private String tenantId;
    // 职务对应的岗位
    @UnCloneable
    private Set<Job> jobs = new HashSet<Job>(0);

    public String getTenantId() {
        return tenantId;
    }

    // 职务拥有的角色
    // @UnCloneable
    // private Set<Role> roles = new HashSet<Role>(0);

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    // /**
    // * @return the roles
    // */
    // @ManyToMany(fetch = FetchType.LAZY)
    // @Cascade({ CascadeType.SAVE_UPDATE, CascadeType.MERGE })
    // @JoinTable(name = "ORG_DUTY_ROLE", joinColumns = { @JoinColumn(name =
    // "duty_uuid") }, inverseJoinColumns = { @JoinColumn(name = "role_uuid") })
    // // Fecth策略定义
    // @Fetch(FetchMode.SUBSELECT)
    // public Set<Role> getRoles() {
    // return roles;
    // }
    //
    // /**
    // * @param roles
    // * 要设置的roles
    // */
    // public void setRoles(Set<Role> roles) {
    // this.roles = roles;
    // }

    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDutyLevel() {
        return dutyLevel;
    }

    public void setDutyLevel(String dutyLevel) {
        this.dutyLevel = dutyLevel;
    }

    public String getSeriesName() {
        return seriesName;
    }

    public void setSeriesName(String seriesName) {
        this.seriesName = seriesName;
    }

    public String getSeriesUuid() {
        return seriesUuid;
    }

    public void setSeriesUuid(String seriesUuid) {
        this.seriesUuid = seriesUuid;
    }

    @Column(nullable = false, unique = true)
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return the jobs
     */
    @OneToMany(mappedBy = "duty", fetch = FetchType.LAZY)
    @Cascade({CascadeType.ALL})
    public Set<Job> getJobs() {
        return jobs;
    }

    /**
     * @param jobs 要设置的jobs
     */
    public void setJobs(Set<Job> jobs) {
        this.jobs = jobs;
    }

    public Integer getIlevel() {
        return ilevel;
    }

    public void setIlevel(Integer ilevel) {
        this.ilevel = ilevel;
    }

}
