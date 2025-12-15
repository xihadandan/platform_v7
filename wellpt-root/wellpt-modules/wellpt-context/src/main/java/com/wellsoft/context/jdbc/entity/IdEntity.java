package com.wellsoft.context.jdbc.entity;

import io.swagger.annotations.ApiModelProperty;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.util.ClassUtils;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Version;
import java.util.Date;

/**
 * 统一定义id的entity基类.
 * <p>
 * 基类统一定义id的属性名称、数据类型、列名映射及生成策略.
 * 子类可重载getId()函数重定义id的列名映射和生成策略.
 * 时间
 *
 * @author lilin
 */
// JPA 基类的标识，可以对应相应的数据库表
@MappedSuperclass
@Deprecated
public abstract class IdEntity extends BaseEntity {

    public static final String UUID = "uuid";
    public static final String REC_VER = "recVer";
    public static final String CREATOR = "creator";
    public static final String CREATE_TIME = "createTime";
    public static final String MODIFIER = "modifier";
    public static final String MODIFY_TIME = "modifyTime";
    public static final String CREATE_TIME_ASC = "createTime asc";
    public static final String CREATE_TIME_DESC = "createTime desc";
    public static final String[] BASE_FIELDS = new String[]{UUID, CREATOR, CREATE_TIME, MODIFIER, MODIFY_TIME,
            REC_VER};
    private static final long serialVersionUID = -4303763667670820800L;
    // UUID
    @ApiModelProperty("数据UUID")
    protected String uuid;

    // 版本号
    @ApiModelProperty("数据版本号")
    private Integer recVer;

    // 创建人
    @ApiModelProperty("创建人")
    private String creator;

    // 创建时间
    @ApiModelProperty("创建时间")
    private Date createTime;

    // 修改人
    @ApiModelProperty("修改人")
    private String modifier;

    // 修改时间
    @ApiModelProperty("修改时间")
    private Date modifyTime;


    public IdEntity() {
    }

    public IdEntity(String uuid) {
        this.uuid = uuid;
    }

    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "com.wellsoft.pt.jpa.support.CustomUUIDGenerator")
    @Override
    public String getUuid() {
        return uuid;
    }

    @Override
    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    /**
     * @return the recVer
     */
    @Override
    @Version
    public Integer getRecVer() {
        return recVer;
    }

    /**
     * @param recVer 要设置的recVer
     */
    @Override
    public void setRecVer(Integer recVer) {
        this.recVer = recVer;
    }

    /**
     * @return the creator
     */
    @Override
    public String getCreator() {
        return creator;
    }

    /**
     * @param creator 要设置的creator
     */
    @Override
    public void setCreator(String creator) {
        this.creator = creator;
    }

    /**
     * @return the createTime
     */
    @Override
    public Date getCreateTime() {
        return createTime;
    }

    /**
     * @param createTime 要设置的createTime
     */
    @Override
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    /**
     * @return the modifier
     */
    @Override
    public String getModifier() {
        return modifier;
    }

    /**
     * @param modifier 要设置的modifier
     */
    @Override
    public void setModifier(String modifier) {
        this.modifier = modifier;
    }

    /**
     * @return the modifyTime
     */
    @Override
    public Date getModifyTime() {
        return modifyTime;
    }

    /**
     * @param modifyTime 要设置的modifyTime
     */
    @Override
    public void setModifyTime(Date modifyTime) {
        this.modifyTime = modifyTime;
    }


    /**
     * (non-Javadoc)
     *
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((uuid == null) ? 0 : uuid.hashCode());
        return result;
    }

    /**
     * (non-Javadoc)
     *
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != ClassUtils.getUserClass(obj.getClass()))
            return false;
        IdEntity other = (IdEntity) obj;
        if (uuid == null) {
            if (other.getUuid() != null)
                return false;
        } else if (!uuid.equals(other.getUuid()))
            return false;
        return true;
    }
}