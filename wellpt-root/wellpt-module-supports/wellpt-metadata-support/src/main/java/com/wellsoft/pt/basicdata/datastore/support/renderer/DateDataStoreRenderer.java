/*
 * @(#)2016年11月1日 V1.0
 *
 * Copyright 2016 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.datastore.support.renderer;

import com.wellsoft.context.util.date.DateUtils;
import com.wellsoft.pt.basicdata.datastore.support.RendererParam;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.util.Date;
import java.util.Map;

/**
 * Description: 如何描述该类
 *
 * @author xiem
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2016年11月1日.1	xiem		2016年11月1日		Create
 * </pre>
 * @date 2016年11月1日
 */
@Component
public class DateDataStoreRenderer extends AbstractDataStoreRenderer {
    private static final String FORMAT_KEY = "format";
    private static final String DEFAULT_FORMAT = "yyyy-MM-dd HH:mm:ss";

    public static void main(String[] args) {
        System.out.println(DateUtils.format(new Date(1590717442339l), DEFAULT_FORMAT));
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.datastore.support.renderer.DataStoreRenderer#getType()
     */
    @Override
    public String getType() {
        return "dateRenderer";
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.datastore.support.renderer.DataStoreRenderer#getName()
     */
    @Override
    public String getName() {
        return "日期渲染器";
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.datastore.support.renderer.AbstractDataStoreRenderer#doRenderData(java.lang.String, java.lang.Object, java.util.Map, com.wellsoft.pt.basicdata.datastore.support.RendererParam)
     */
    @Override
    public Object doRenderData(String columnIndex, Object value, Map<String, Object> rowData, RendererParam param) {
        if (value instanceof Date) {
            String format = param.getString(FORMAT_KEY, DEFAULT_FORMAT);
            return DateUtils.format((Date) value, format);
        } else if (value instanceof String) {
            String format = param.getString(FORMAT_KEY, DEFAULT_FORMAT);
            try {
                return DateUtils.format(DateUtils.parse((String) value), format);
            } catch (ParseException e) {
                if (StringUtils.isNumeric((String) value)) {
                    Date date = new Date(Long.parseLong((String) value));
                    return DateUtils.format(date, format);
                }
                return null;
            }
        } else if (value instanceof Number) {
            String format = param.getString(FORMAT_KEY, DEFAULT_FORMAT);
            Date date = new Date(((Number) value).longValue());
            return DateUtils.format(date, format);
        }
        return null;
    }
}
