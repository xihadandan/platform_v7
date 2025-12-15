package com.wellsoft.context.jdbc.entity;

import io.swagger.annotations.ApiModelProperty;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.map.ser.std.ToStringSerializer;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.util.ClassUtils;

import javax.persistence.*;
import java.util.Date;

/**
 * 长整型主键的实体类
 */
@MappedSuperclass
public abstract class Entity extends JpaEntity<Long> {

    private static final long serialVersionUID = 5797912101587454625L;

    // UUID
    @ApiModelProperty("数据UUID")
    @JsonSerialize(using = ToStringSerializer.class)
    protected Long uuid;

    // 版本号
    @ApiModelProperty("数据版本号")
    protected Integer recVer;

    // 创建人
    @ApiModelProperty("创建人")
    protected String creator;

    // 创建时间
    @ApiModelProperty("创建时间")
    protected Date createTime;

    // 修改人
    @ApiModelProperty("修改人")
    protected String modifier;

    // 修改时间
    @ApiModelProperty("修改时间")
    protected Date modifyTime;


    public Entity() {
    }

    public Entity(Long uuid) {
        this.uuid = uuid;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "system-snowflake-uuid")
    @GenericGenerator(name = "system-snowflake-uuid", strategy = "com.wellsoft.pt.jpa.support.SnowFlakeUUIDGenerator")
    public Long getUuid() {
        return uuid;
    }

    @Override
    public void setUuid(Long uuid) {
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
     * @see Object#hashCode()
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
     * @see Object#equals(Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != ClassUtils.getUserClass(obj.getClass()))
            return false;
        Entity other = (Entity) obj;
        if (uuid == null) {
            if (other.getUuid() != null)
                return false;
        } else if (!uuid.equals(other.getUuid()))
            return false;
        return true;
    }
}