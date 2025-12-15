package com.wellsoft.context.jdbc.entity;

import java.io.Serializable;
import java.util.Date;

/**
 * Description: 如何描述该类
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2022年11月09日   chenq	 Create
 * </pre>
 */
public abstract class JpaEntity<UUID extends Serializable> implements Serializable {
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

    /**
     * 获取UUID
     *
     * @return
     */
    public abstract UUID getUuid();

    /**
     * 设置UUID
     *
     * @param uuid
     */
    public abstract void setUuid(UUID uuid);


    /**
     * 获取版本号
     *
     * @return
     */
    public abstract Integer getRecVer();

    /**
     * 设置版本号
     *
     * @param recVer
     */
    public abstract void setRecVer(Integer recVer);

    /**
     * 获取创建人
     *
     * @return
     */
    public abstract String getCreator();

    /**
     * 设置创建人
     *
     * @param creator
     */
    public abstract void setCreator(String creator);

    /**
     * 获取创建时间
     *
     * @return
     */
    public abstract Date getCreateTime();

    /**
     * 设置创建时间
     *
     * @param createTime
     */
    public abstract void setCreateTime(Date createTime);

    /**
     * 获取修改人
     *
     * @return
     */
    public abstract String getModifier();

    /**
     * 设置修改人
     *
     * @param modifier
     */
    public abstract void setModifier(String modifier);

    /**
     * 获取修改时间
     *
     * @return
     */
    public abstract Date getModifyTime();

    /**
     * 设置修改时间
     *
     * @param modifyTime
     */
    public abstract void setModifyTime(Date modifyTime);

    /**
     * (non-Javadoc)
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return super.toString();
    }
}
