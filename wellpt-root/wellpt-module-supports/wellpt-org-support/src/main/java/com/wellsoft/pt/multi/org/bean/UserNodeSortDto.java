package com.wellsoft.pt.multi.org.bean;

/**
 * Description: 用户节点信息-增加code参数
 *
 * @author zenghw
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	        修改人		修改日期			修改内容
 * 2021/11/17.1	    zenghw		2021/11/17		    Create
 * </pre>
 * @date 2021/11/17
 */
public class UserNodeSortDto extends UserNode {

    private String code;

    public String getCode() {
        return this.code;
    }

    public void setCode(final String code) {
        this.code = code;
    }
}
