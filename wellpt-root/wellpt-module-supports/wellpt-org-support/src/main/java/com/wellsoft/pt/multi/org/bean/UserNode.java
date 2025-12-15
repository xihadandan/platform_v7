package com.wellsoft.pt.multi.org.bean;

import com.wellsoft.context.jdbc.support.BaseQueryItem;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.util.Objects;

/**
 * 用户节点信息
 *
 * @author yt
 * @title: UserNode
 * @date 2020/6/17 3:04 下午
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
public class UserNode implements BaseQueryItem {

    private String id;
    private String name;
    private String namePy;
    /**
     * 图标
     * 用户性别 sex=1:man, 0:women
     * sex=null U;
     */
    private String iconSkin;
    /**
     * 节点归属组织版本Id
     * 主要用于 我的领导，下属 等有归属不同组织版本Id的节点数据使用 下次加载传入使用
     */
    private String orgVersionId;

    private String idPath;//id路径
    private String namePath;//名称路径


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIconSkin() {
        return iconSkin;
    }

    public void setIconSkin(String iconSkin) {
        this.iconSkin = iconSkin;
    }

    public String getNamePy() {
        return namePy;
    }

    public void setNamePy(String namePy) {
        this.namePy = namePy;
    }

    public String getOrgVersionId() {
        return orgVersionId;
    }

    public void setOrgVersionId(String orgVersionId) {
        this.orgVersionId = orgVersionId;
    }

    public String getIdPath() {
        return idPath;
    }

    public void setIdPath(String idPath) {
        this.idPath = idPath;
    }

    public String getNamePath() {
        return namePath;
    }

    public void setNamePath(String namePath) {
        this.namePath = namePath;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserNode)) return false;
        UserNode that = (UserNode) o;
        return getId().equals(that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}
