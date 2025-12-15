package com.wellsoft.pt.basicdata.directorydata.entity;

import com.wellsoft.context.annotation.UnCloneable;
import com.wellsoft.context.jdbc.entity.IdEntity;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;

@Entity
@Table(name = "s_data")
@DynamicUpdate
@DynamicInsert
public class Data extends IdEntity {

    private static final long serialVersionUID = -315482359295085633L;

    private String directoryTopUuid;

    private String name;
    @UnCloneable
    private Directory directory;
    @UnCloneable
    @Column(name = "json_data")
    private String jsonData;

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
    @JoinColumn(name = "directory_uuid")
    public Directory getDirectory() {
        return directory;
    }

    public void setDirectory(Directory directory) {
        this.directory = directory;
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

}
