package com.wellsoft.pt.basicdata.printtemplate.bean;

import com.wellsoft.pt.basicdata.printtemplate.entity.PrintContents;

/**
 * Description: 如何描述该类
 *
 * @author zhongzh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2018年12月3日.1	zhongzh		2018年12月3日		Create
 * </pre>
 * @date 2018年12月3日
 */
public class PrintContentsBean extends PrintContents {
    private static final long serialVersionUID = 1L;

    private String printContent;

    /**
     * @return the printContent
     */
    public String getPrintContent() {
        return printContent;
    }

    /**
     * @param printContent 要设置的printContent
     */
    public void setPrintContent(String printContent) {
        this.printContent = printContent;
    }
}
