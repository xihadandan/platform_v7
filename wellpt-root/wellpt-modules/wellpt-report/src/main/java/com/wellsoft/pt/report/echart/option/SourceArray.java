package com.wellsoft.pt.report.echart.option;

import com.google.common.collect.Lists;

import java.util.List;

/**
 * Description: 如何描述该类
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2024年06月19日   chenq	 Create
 * </pre>
 */
public class SourceArray implements SourceElement {
    private static final long serialVersionUID = -9033410234592003239L;
    private List<Object> elements = Lists.newArrayList();

    public List<Object> getElements() {
        return elements;
    }

    public void setElements(List<Object> elements) {
        this.elements = elements;
    }
}
