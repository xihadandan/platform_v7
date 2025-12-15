package com.wellsoft.pt.basicdata.datanode;

import java.io.Serializable;

/**
 * Description:
 *
 * @author chenq
 * @date 2019/10/21
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2019/10/21    chenq		2019/10/21		Create
 * </pre>
 */
public class DataNode implements Serializable {

    private String nodeName;

    public String getNodeName() {
        return nodeName;
    }

    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }
}
