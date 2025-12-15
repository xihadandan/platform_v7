package com.wellsoft.pt.basicdata.directorydata.entity;

import com.wellsoft.context.annotation.UnCloneable;
import com.wellsoft.context.jdbc.entity.IdEntity;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.*;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "s_directory")
@DynamicUpdate
@DynamicInsert
public class Directory extends IdEntity {

    private static final long serialVersionUID = -345482359295085633L;

    private String directoryTopUuid;

    private String name;
    @UnCloneable
    private Directory parent;
    @Column(name = "order_no")
    private Integer orderNo;
    @UnCloneable
    @Column(name = "json_data")
    private String jsonData;
    @UnCloneable
    private List<Directory> children = new ArrayList<Directory>();

    /**
     * @return the directoryTopUuid
     */
    public String getDirectoryTopUuid() {
        return directoryTopUuid;
    }

    /**
     * @param directoryTopUuid 要设置的directoryTopUuid
     */
    public void setDirectoryTopUuid(String directoryTopUuid) {
        this.directoryTopUuid = directoryTopUuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @ManyToOne(targetEntity = Directory.class)
    @JoinColumn(name = "parent_uuid")
    public Directory getParent() {
        return parent;
    }

    public void setParent(Directory parent) {
        this.parent = parent;
    }

    public Integer getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(Integer orderNo) {
        this.orderNo = orderNo;
    }

    /**
     * @return the jsonData
     */
    public String getJsonData() {
        return jsonData;
    }

    /**
     * @param jsonData 要设置的jsonData
     */
    public void setJsonData(String jsonData) {
        this.jsonData = jsonData;
    }

    @OneToMany(fetch = FetchType.LAZY, targetEntity = Directory.class, mappedBy = "parent")
    @Cascade(value = {CascadeType.ALL})
    @LazyCollection(LazyCollectionOption.TRUE)
    @Fetch(FetchMode.SUBSELECT)
    public List<Directory> getChildren() {
        return children;
    }

    public void setChildren(List<Directory> children) {
        this.children = children;
    }

}
