package com.wellsoft.pt.basicdata.systemtable.bean;

import com.wellsoft.pt.basicdata.systemtable.entity.SystemTable;

import java.util.HashSet;
import java.util.Set;

/**
 * Description: 系统表数据VO类
 *
 * @author zhouyq
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-3-21.1	zhouyq		2013-3-21		Create
 * </pre>
 * @date 2013-3-21
 */
public class SystemTableBean extends SystemTable {
    private static final long serialVersionUID = 1L;
    //改变的属性表列
    private Set<SystemTableAttributeBean> changedAttributes = new HashSet<SystemTableAttributeBean>();
    //改变的关系表列
    private Set<SystemTableRelationshipBean> changedRelationships = new HashSet<SystemTableRelationshipBean>();
    //删除的关系表列
    private Set<SystemTableRelationshipBean> deletedRelationships = new HashSet<SystemTableRelationshipBean>();

    /**
     * @return the changedAttributes
     */
    public Set<SystemTableAttributeBean> getChangedAttributes() {
        return changedAttributes;
    }

    /**
     * @param changedAttributes 要设置的changedAttributes
     */
    public void setChangedAttributes(Set<SystemTableAttributeBean> changedAttributes) {
        this.changedAttributes = changedAttributes;
    }

    /**
     * @return the changedRelationships
     */
    public Set<SystemTableRelationshipBean> getChangedRelationships() {
        return changedRelationships;
    }

    /**
     * @param changedRelationships 要设置的changedRelationships
     */
    public void setChangedRelationships(Set<SystemTableRelationshipBean> changedRelationships) {
        this.changedRelationships = changedRelationships;
    }

    /**
     * @return the deletedRelationships
     */
    public Set<SystemTableRelationshipBean> getDeletedRelationships() {
        return deletedRelationships;
    }

    /**
     * @param deletedRelationships 要设置的deletedRelationships
     */
    public void setDeletedRelationships(Set<SystemTableRelationshipBean> deletedRelationships) {
        this.deletedRelationships = deletedRelationships;
    }

}
