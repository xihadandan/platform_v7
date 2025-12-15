/*
 * @(#)2019年3月3日 V1.0
 *
 * Copyright 2019 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.design.support;

import com.wellsoft.pt.app.design.component.UIDesignComponent;

import java.util.Comparator;
import java.util.Map;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2019年3月3日.1	zhulh		2019年3月3日		Create
 * </pre>
 * @date 2019年3月3日
 */
public class WidgetUsageFrequencyComparator implements Comparator<UIDesignComponent> {

    private Map<String, Long> WidgetUsageFrequency;

    /**
     * @param widgetUsageFrequency
     */
    public WidgetUsageFrequencyComparator(Map<String, Long> widgetUsageFrequency) {
        super();
        WidgetUsageFrequency = widgetUsageFrequency;
    }

    /**
     * (non-Javadoc)
     *
     * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
     */
    @Override
    public int compare(UIDesignComponent o1, UIDesignComponent o2) {
        Long frequency1 = WidgetUsageFrequency.get(o1.getType());
        Long frequency2 = WidgetUsageFrequency.get(o2.getType());
        frequency1 = frequency1 == null ? 0l : frequency1;
        frequency2 = frequency2 == null ? 0l : frequency2;
        return frequency1.compareTo(frequency2);
    }

}
