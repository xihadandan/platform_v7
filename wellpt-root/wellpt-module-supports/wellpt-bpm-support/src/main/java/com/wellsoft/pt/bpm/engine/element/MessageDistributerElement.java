package com.wellsoft.pt.bpm.engine.element;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import java.io.Serializable;
import java.util.List;

/**
 * Description: 分发人员
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2020年08月04日   chenq	 Create
 * </pre>
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class MessageDistributerElement implements Serializable {

    private String distributerType; // 分发人员类型：

    private String distributerTypeName;// 分发人员类型名称

    private String id;//消息模板ID

    private String name;

    private List<UserUnitElement> designee;  //  分发指定人员

    public List<UserUnitElement> getDesignee() {
        return designee;
    }

    public void setDesignee(List<UserUnitElement> designee) {
        this.designee = designee;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDistributerType() {
        return distributerType;
    }

    public void setDistributerType(String distributerType) {
        this.distributerType = distributerType;
    }

    public String getDistributerTypeName() {
        return distributerTypeName;
    }

    public void setDistributerTypeName(String distributerTypeName) {
        this.distributerTypeName = distributerTypeName;
    }
}
