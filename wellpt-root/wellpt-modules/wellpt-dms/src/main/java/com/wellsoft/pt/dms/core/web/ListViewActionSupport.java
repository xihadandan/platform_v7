/*
 * @(#)Feb 19, 2017 V1.0
 *
 * Copyright 2017 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dms.core.web;

import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.pt.dms.core.convert.ConversionService;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * Feb 19, 2017.1	zhulh		Feb 19, 2017		Create
 * </pre>
 * @date Feb 19, 2017
 */
public abstract class ListViewActionSupport extends ActionSupport {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 8278011451564058722L;

    private List<Object> selection;

    /**
     * @return the selection
     */
    public List<Object> getSelection() {
        return selection;
    }

    /**
     * @param selection 要设置的selection
     */
    public void setSelection(List<Object> selection) {
        this.selection = selection;
    }

    /**
     * @param itemClass
     * @return
     */
    public <ITEM extends Serializable> List<ITEM> getSelection(Class<ITEM> itemClass) {
        if (selection == null || selection.isEmpty()) {
            return Collections.emptyList();
        }
        List<ITEM> list = new ArrayList<ITEM>();
        ConversionService conversionService = ApplicationContextHolder.getBean(ConversionService.class);
        for (Object object : selection) {
            if (conversionService.canConvert(object.getClass(), itemClass)) {
                list.add(conversionService.convert(object, itemClass));
            }
        }
        return list;
    }

}
