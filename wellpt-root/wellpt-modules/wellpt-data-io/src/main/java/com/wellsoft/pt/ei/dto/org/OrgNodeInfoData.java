package com.wellsoft.pt.ei.dto.org;

/**
 * Description: 用于存放节点信息，在导入时判断是否重复
 *
 * @author liuyz
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2021/10/30.1	liuyz		2021/10/30		Create
 * </pre>
 * @date 2021/10/30
 */
public class OrgNodeInfoData {

    public String eleId;

    public String name;

    public String eleIdPath;

    public String eleNamePath;

    public String type;

    public String getEleId() {
        return eleId;
    }

    public void setEleId(String eleId) {
        this.eleId = eleId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEleIdPath() {
        return eleIdPath;
    }

    public void setEleIdPath(String eleIdPath) {
        this.eleIdPath = eleIdPath;
    }

    public String getEleNamePath() {
        return eleNamePath;
    }

    public void setEleNamePath(String eleNamePath) {
        this.eleNamePath = eleNamePath;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
